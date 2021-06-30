package com.example.expirationtracker.ui.Category;

import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import android.app.Activity;
import android.provider.CalendarContract;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TimePicker;
import android.widget.Toast;
import com.example.expirationtracker.R;
import com.example.expirationtracker.model.Category;
import com.example.expirationtracker.model.Item;
import com.example.expirationtracker.ui.NavActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import java.util.Calendar;
import java.util.Objects;


public class CategoryEditFragment extends Fragment implements View.OnClickListener{
    private Activity mActivity;
    private FirebaseAuth mAuth;
    private DatabaseReference mItemReference;
    private View mView;
    private String mBegin;
    private String mFrequency;
    private String mHourRemindingTime;
    private String mMinuteRemindingTime;
    private String mCategoryId;
    private ValueEventListener mItemListener;
    private String mOperation;
    public CategoryEditFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @return A new instance of fragment CategoryEditFragment.
     */
    public static CategoryEditFragment newInstance() {
        return new CategoryEditFragment();
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        mView = inflater.inflate(R.layout.fragment_category_edit, container, false);
        mAuth = FirebaseAuth.getInstance();
        mActivity = getActivity();
        final Intent intent = Objects.requireNonNull(getActivity()).getIntent();
        mCategoryId = intent.getStringExtra("categoryId");
        mOperation = intent.getStringExtra("operation");
        if(intent.getStringExtra("operation") != null){
            // edit mode
            if(Objects.requireNonNull(intent.getStringExtra("operation")).equals("Edit")){
                // get current contents
                ((EditText)mView.findViewById(R.id.text_category_name)).setText(intent.getStringExtra("categoryName"));
                int pos = 0;
                switch (Objects.requireNonNull(intent.getStringExtra("categoryBegin"))){
                    case "1 day before":
                        pos = 0;
                        break;
                    case "3 days before":
                        pos = 1;
                        break;
                    case "1 week before":
                        pos = 2;
                        break;
                    case "2 weeks before":
                        pos = 3;
                        break;
                    case "1 month before":
                        pos = 4;
                        break;
                }
                ((Spinner)mView.findViewById(R.id.notification_setting)).setSelection(pos);
                switch (Objects.requireNonNull(intent.getStringExtra("categoryFrequency"))){
                    case "2 days":
                        ((RadioButton)mView.findViewById(R.id.btn_1)).setChecked(false);
                        ((RadioButton)mView.findViewById(R.id.btn_2)).setChecked(true);
                        break;
                    case "3 days":
                        ((RadioButton)mView.findViewById(R.id.btn_1)).setChecked(false);
                        ((RadioButton)mView.findViewById(R.id.btn_3)).setChecked(true);
                        break;
                    case "1 week":
                        ((RadioButton)mView.findViewById(R.id.btn_1)).setChecked(false);
                        ((RadioButton)mView.findViewById(R.id.btn_4)).setChecked(true);
                        break;
                    case "2 weeks":
                        ((RadioButton)mView.findViewById(R.id.btn_1)).setChecked(false);
                        ((RadioButton)mView.findViewById(R.id.btn_5)).setChecked(true);
                        break;
                }
                String[] s = Objects.requireNonNull(intent.getStringExtra("categoryTime")).split(":");
                ((TimePicker) mView.findViewById(R.id.time_picker)).setCurrentHour(Integer.parseInt(s[0]));
                ((TimePicker) mView.findViewById(R.id.time_picker)).setCurrentMinute(Integer.parseInt(s[1]));
            }
        }
        // save
        Button saveButton = mView.findViewById(R.id.btn_save);
        saveButton.setOnClickListener(this);
        return mView;
    }

    private void updateReminder(){
        mItemReference = FirebaseDatabase.getInstance().getReference().child("items").child(Objects.requireNonNull(mAuth.getUid())).child(mCategoryId);
        mItemListener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot currentSnapshot : dataSnapshot.getChildren()) {
                    // Get category
                    Item item = currentSnapshot.getValue(Item.class);
                    // set up time
                    Calendar start = Calendar.getInstance();
                    String date = Objects.requireNonNull(item).getExpirationDate();
                    int year = Integer.parseInt(date.substring(0, 4));
                    int month = Integer.parseInt(date.substring(4, 6)) - 1;
                    int day = Integer.parseInt(date.substring(6, 8));
                    // get time
                    int hour = Integer.parseInt(mHourRemindingTime);
                    int minute = Integer.parseInt(mMinuteRemindingTime) + 10;
                    switch (mBegin) {
                        case "1 day before":
                            start.set(year, month, day - 1, hour, minute);
                            break;
                        case "3 days before":
                            start.set(year, month, day - 3, hour, minute);
                            break;
                        case "1 week before":
                            start.set(year, month, day - 7, hour, minute);
                            break;
                        case "2 weeks before":
                            start.set(year, month, day - 14, hour, minute);
                            break;
                        case "1 month before":
                            start.set(year, month - 1, day, hour, minute);
                            break;
                    }
                    // set recurrence rule
                    String rrule = "";
                    switch (mFrequency) {
                        // 每天
                        case "everyday":
                            rrule = "FREQ=DAILY;UNTIL=" + date + "T235959Z";
                            break;
                        // 每周
                        case "2 days":
                            rrule = "FREQ=DAILY;INTERVAL=2;UNTIL=" + date + "T235959Z";
                            break;
                        // 每两周
                        case "3 days":
                            rrule = "FREQ=DAILY;INTERVAL=3;UNTIL=" + date + "T235959Z";
                            break;
                        // 每月
                        case "1 week":
                            rrule = "FREQ=WEEKLY;UNTIL=" + date + "T235959Z";
                            break;
                        // 每年
                        case "2 weeks":
                            rrule = "FREQ=WEEKLY;INTERVAL=2;UNTIL=" + date + "T235959Z";
                            break;
                        default:
                            break;
                    }
                    ContentResolver cr = mActivity.getContentResolver();
                    ContentValues event = new ContentValues();
                    event.put(CalendarContract.Events.DTSTART, start.getTimeInMillis());
                    event.put(CalendarContract.Events.DTEND, start.getTimeInMillis());
                    event.put(CalendarContract.Events.RRULE, rrule);
                    Uri updateEvent = ContentUris.withAppendedId(CalendarContract.Events.CONTENT_URI, item.getEventId());
                    cr.update(updateEvent, event, null, null);

                }
//                    return;
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        };
        mItemReference.addValueEventListener(mItemListener);
    }

    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.btn_save) {
            String name = ((EditText) mView.findViewById(R.id.text_category_name)).getText().toString();
            if (name.length() == 0) {
                Toast.makeText(mActivity.getApplicationContext(), "Name cannot be empty",
                        Toast.LENGTH_SHORT).show();
                ((EditText) mView.findViewById(R.id.text_category_name)).setText("");
            } else {
                mBegin = ((Spinner) mView.findViewById(R.id.notification_setting)).getSelectedItem().toString();
                int selectedId = ((RadioGroup) mView.findViewById(R.id.frequency)).getCheckedRadioButtonId();
                mFrequency = ((RadioButton) mView.findViewById(selectedId)).getText().toString();
                TimePicker time = mView.findViewById(R.id.time_picker);
                mHourRemindingTime = Integer.toString(time.getCurrentHour());
                mMinuteRemindingTime = Integer.toString(time.getCurrentMinute());
                if (mHourRemindingTime.length() == 1) {
                    mHourRemindingTime = "0" + mHourRemindingTime;
                }
                if (mMinuteRemindingTime.length() == 1) {
                    mMinuteRemindingTime = "0" + mMinuteRemindingTime;
                }
                DatabaseReference categoryReference = FirebaseDatabase.getInstance().getReference().child("categories").child(Objects.requireNonNull(mAuth.getUid()));
                Category c = new Category(name, mBegin, mFrequency, mHourRemindingTime + ":" + mMinuteRemindingTime);
                if (mOperation.equals("Edit")) {
                    categoryReference.child(mCategoryId).setValue(c);
                    updateReminder();
                } else {
                    categoryReference.push().setValue(c);
                }
                Intent newIntent = new Intent(mActivity, NavActivity.class);
                newIntent.putExtra("content", "CATEGORY_LIST");
                startActivity(newIntent);
            }
        }
    }
    @Override
    public void onStop() {
        super.onStop();
        // Remove post value event listener
        if (mItemListener != null) {
            mItemReference.removeEventListener(mItemListener);
            mItemListener = null;
        }
        Runtime.getRuntime().gc();
    }
}
