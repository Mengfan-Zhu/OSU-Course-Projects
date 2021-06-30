package com.example.expirationtracker.ui.Setting;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import com.example.expirationtracker.R;
import com.example.expirationtracker.model.User;
import com.example.expirationtracker.ui.NavActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Objects;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link NameSettingFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class NameSettingFragment extends Fragment implements View.OnClickListener{
    private Activity mActivity;
    private FirebaseAuth mAuth;
    private DatabaseReference mUserReference;
    private ValueEventListener mUserListener;
    private View mView;

    public NameSettingFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @return A new instance of fragment NameSettingFragment.
     */
    public static NameSettingFragment newInstance() {
        return new NameSettingFragment();
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        mView = inflater.inflate(R.layout.fragment_name_setting, container, false);
        mActivity = getActivity();
        mAuth = FirebaseAuth.getInstance();
        mUserReference = FirebaseDatabase.getInstance().getReference().child("users").child(Objects.requireNonNull(mAuth.getUid()));
        Button saveButton = mView.findViewById(R.id.btn_name_setting_save);
        saveButton.setOnClickListener(this);
        // Inflate the layout for this fragment
        return mView;
    }

    @Override
    public void onStart(){
        super.onStart();
        mUserListener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                // get user info
                User u = dataSnapshot.getValue(User.class);
                mActivity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        ((EditText) mView.findViewById((R.id.name_setting_text))).setText(Objects.requireNonNull(u).getName());
                    }
                });
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        };
        mUserReference.addListenerForSingleValueEvent(mUserListener);
    }

    @Override
    public void onClick(View view) {
        if(view.getId() == R.id.btn_name_setting_save){
            String inputName = ((EditText) mView.findViewById(R.id.name_setting_text)).getText().toString();
            if(inputName.length() == 0){
                Toast.makeText(mActivity.getApplicationContext(), "Name cannot be empty",
                        Toast.LENGTH_SHORT).show();
                ((EditText)mView.findViewById(R.id.name_setting_text)).setText("");
            }else{
                FirebaseDatabase.getInstance().getReference().child("users").child(Objects.requireNonNull(mAuth.getUid())).child("name").setValue(inputName);
                Intent newIntent = new Intent(mActivity, NavActivity.class);
                newIntent.putExtra("content", "SETTING");
                startActivity(newIntent);
            }
        }
    }

    @Override
    public void onStop() {
        super.onStop();
        // Remove post value event listener
        if (mUserListener != null) {
            mUserReference.removeEventListener(mUserListener);
            mUserListener = null;
        }
        Runtime.getRuntime().gc();
    }

}
