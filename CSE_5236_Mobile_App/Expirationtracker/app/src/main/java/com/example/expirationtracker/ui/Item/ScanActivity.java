package com.example.expirationtracker.ui.Item;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;
import com.example.expirationtracker.AppStatus;
import com.example.expirationtracker.R;
import com.example.expirationtracker.ui.NavActivity;
import com.yzq.zxinglibrary.android.CaptureActivity;
import com.yzq.zxinglibrary.bean.ZxingConfig;
import com.yzq.zxinglibrary.common.Constant;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ScanActivity extends AppCompatActivity {

    private int REQUEST_CODE_SCAN = 1;
    private String msg;
    private String mCategoryId;
    private TextView mResult;
    private boolean stopThread;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scan);
        ActionBar actionBar = this.getSupportActionBar();
        Objects.requireNonNull(actionBar).setDisplayHomeAsUpEnabled(true);
        if (!AppStatus.getInstance(this).isOnline()) {
            Toast.makeText(this, "Network connection issue",
                    Toast.LENGTH_SHORT).show();
        }else {
            Intent itemIntent = this.getIntent();
            mCategoryId = itemIntent.getStringExtra("categoryId");
            mResult = this.findViewById(R.id.scan_result);
            mResult.setText(R.string.scan_search_waiting);
            Intent intent = new Intent(this, CaptureActivity.class);
            // set scan config
            ZxingConfig config = new ZxingConfig();
            config.setShowbottomLayout(false);
            config.setPlayBeep(true);
            config.setShake(true);
            config.setShowAlbum(false);
            config.setShowFlashLight(false);
            intent.putExtra(Constant.INTENT_ZXING_CONFIG, config);
            //check allow camera
            if (Build.VERSION.SDK_INT >= 23) {
                int checkCallPhonePermission = ContextCompat.checkSelfPermission(this,
                        Manifest.permission.CAMERA);
                if (checkCallPhonePermission != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA}, 222);
                } else {
                    startActivityForResult(intent, REQUEST_CODE_SCAN);
                }
            } else {
                startActivityForResult(intent, REQUEST_CODE_SCAN);
            }
        }
    }
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults){
        if(grantResults[0] == 0) {
            Intent intent = new Intent(this, ScanActivity.class);
            intent.putExtra("categoryId", mCategoryId);
            intent.putExtra("content", "ITEM_EDIT");
            startActivity(intent);
        }else{
            Intent intent = new Intent(ScanActivity.this, NavActivity.class);
            intent.putExtra("operation", "Add");
            intent.putExtra("categoryId",mCategoryId);
            intent.putExtra("content", "ITEM_EDIT");
            startActivity(intent);
        }
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE_SCAN && resultCode == RESULT_OK) {
            if (data != null) {
                String content = data.getStringExtra(Constant.CODED_CONTENT);
                final String html = "https://www.barcodelookup.com/"+content;
                new Thread() {
                    public void run() {
                        if(!stopThread){
                            try {
                                msg = getNameFromWeb(html);
                                String pattern = "(<meta name=\"description\"[^-]*)(- )([^\"]*)";
                                Pattern r = Pattern.compile(pattern);
                                Matcher m = r.matcher(msg);
                                if (m.find( )) {
                                    msg =  m.group(3);
                                    Intent intent = new Intent(ScanActivity.this, NavActivity.class);
                                    intent.putExtra("operation", "Scan");
                                    intent.putExtra("categoryId",mCategoryId);
                                    intent.putExtra("itemName",msg);
                                    intent.putExtra("content", "ITEM_EDIT");
                                    startActivity(intent);
                                    ScanActivity.this.finish();
                                } else {
                                    runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            mResult.setText(R.string.scan_search_fail);
                                        }
                                    });
                                    Intent intent = new Intent(ScanActivity.this, NavActivity.class);
                                    intent.putExtra("operation", "Add");
                                    intent.putExtra("categoryId",mCategoryId);
                                    intent.putExtra("content", "ITEM_EDIT");
                                    startActivity(intent);
                                    ScanActivity.this.finish();
                                }
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                    }
                }.start();
            }
        }
    }

    public String getNameFromWeb(String path) throws Exception {
        URL url = new URL(path);
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setConnectTimeout(5000);
        conn.setRequestMethod("GET");
        if (conn.getResponseCode() == 200) {
            InputStream in = conn.getInputStream();
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            byte[] buffer = new byte[300];
            int len = 0;
            while((len = in.read(buffer)) != -1)
            {
                out.write(buffer,0,len);
            }
            in.close();
            byte[] data = out.toByteArray();
            return new String(data, StandardCharsets.UTF_8);
        }
        return null;
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        Intent intent = new Intent(this, NavActivity.class);
        intent.putExtra("content", "ITEM_LIST");
        intent.putExtra("categoryId",mCategoryId);
        startActivity(intent);
        this.finish();
        return super.onOptionsItemSelected(item);
    }
    @Override
    public void onDestroy() {
        super.onDestroy();
        this.finish();
        stopThread = true;
    }
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        return keyCode == KeyEvent.KEYCODE_BACK;
    }
}

