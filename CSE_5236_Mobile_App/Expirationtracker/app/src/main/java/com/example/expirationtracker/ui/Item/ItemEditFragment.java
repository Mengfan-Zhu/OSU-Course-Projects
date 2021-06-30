package com.example.expirationtracker.ui.Item;

import android.Manifest;
import android.app.Activity;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import android.provider.CalendarContract;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;
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
import java.time.ZoneId;
import java.util.Calendar;
import java.util.Objects;


public class ItemEditFragment extends Fragment implements View.OnClickListener{
    private Activity mActivity;
    private FirebaseAuth mAuth;
    private DatabaseReference mItemReference;
    private ValueEventListener mCategoryListener;
    private DatabaseReference mCategoryReference;
    private View mView;
    private String mCategoryId;
    private Intent mIntent;
    private String mName;
    private String mQuantity;
    private String mDescription;
    private String mDate;
    private Category mCategory;
    private long mEventId;

    public ItemEditFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @return A new instance of fragment ItemEditFragment.
     */
    public static ItemEditFragment newInstance() {
        return new ItemEditFragment();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        mView = inflater.inflate(R.layout.fragment_item_edit, container, false);
        mAuth = FirebaseAuth.getInstance();
        mActivity = getActivity();
        mIntent = Objects.requireNonNull(mActivity).getIntent();
        mCategoryId = mIntent.getStringExtra("categoryId");
        if (mIntent.getStringExtra("operation") != null) {
            // deal with edit
            if (Objects.requireNonNull(mIntent.getStringExtra("operation")).equals("Edit")) {
                ((EditText) mView.findViewById(R.id.text_item_name)).setText(mIntent.getStringExtra("itemName"));
                String date = mIntent.getStringExtra("itemExpirationDate");
                ((DatePicker) mView.findViewById(R.id.date_picker)).updateDate(Integer.parseInt(Objects.requireNonNull(date).substring(0, 4)), Integer.parseInt(date.substring(4, 6)) - 1, Integer.parseInt(date.substring(6, 8)));
                ((TextView) mView.findViewById((R.id.quantity))).setText(mIntent.getStringExtra("itemQuantity"));
                ((EditText) mView.findViewById(R.id.description)).setText(mIntent.getStringExtra("itemDescription"));
            } else if (Objects.requireNonNull(mIntent.getStringExtra("operation")).equals("Scan")) {
                ((EditText) mView.findViewById(R.id.text_item_name)).setText(mIntent.getStringExtra("itemName"));
            }
        }
        // save
        Button saveButton = mView.findViewById(R.id.btn_item_save);
        saveButton.setOnClickListener(this);
        return mView;
    }
    private void addReminder(String operation) {
        // get date
        int year = Integer.parseInt(mDate.substring(0, 4));
        int month = Integer.parseInt(mDate.substring(4, 6)) - 1;
        int day = Integer.parseInt(mDate.substring(6, 8));
        mCategoryReference = FirebaseDatabase.getInstance().getReference().child("categories").child(Objects.requireNonNull(mAuth.getUid())).child(mCategoryId);
        mCategoryListener = new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        // Get category
                        mCategory = dataSnapshot.getValue(Category.class);
                        // get time
                        String[] time = Objects.requireNonNull(mCategory).getTime().split(":");
                        int hour = Integer.parseInt(time[0]);
                        int minute = Integer.parseInt(time[1]);
                        // set calendar id
                        long calendar_id = 1;
                        // set up time
                        Calendar start = Calendar.getInstance();
                        switch(mCategory.getBegin()){
                            case "1 day before":
                                start.set(year, month, day-1, hour, minute);
                                break;
                            case "3 days before":
                                start.set(year, month, day-3, hour, minute);
                                break;
                            case "1 week before":
                                start.set(year, month, day-7, hour, minute);
                                break;
                            case "2 weeks before":
                                start.set(year, month, day-14, hour, minute);
                                break;
                            case "1 month before":
                                start.set(year, month-1, day, hour, minute);
                                break;
                        }
                        // set recurrence rule
                        String rrule = "";
                        switch (mCategory.getFrequency()) {
                            // 每天
                            case "everyday":
                                rrule = "FREQ=DAILY;UNTIL=" + mDate + "T235959Z";
                                break;
                            // 每周
                            case "2 days":
                                rrule = "FREQ=DAILY;INTERVAL=2;UNTIL=" + mDate + "T235959Z";
                                break;
                            // 每两周
                            case "3 days":
                                rrule = "FREQ=DAILY;INTERVAL=3;UNTIL=" + mDate + "T235959Z";
                                break;
                            // 每月
                            case "1 week":
                                rrule = "FREQ=WEEKLY;UNTIL=" + mDate + "T235959Z";
                                break;
                            // 每年
                            case "2 weeks":
                                rrule = "FREQ=WEEKLY;INTERVAL=2;UNTIL=" + mDate + "T235959Z";
                                break;
                            default:
                                break;
                        }
                        // get timezone
                        String timezone = "America/New_York";
                        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
                            timezone = ZoneId.systemDefault().toString();
                        }
                    // add new event and reminder
                    if (operation.equals("Add")) {
                        // set event
                        ContentValues event = new ContentValues();
                        event.put(CalendarContract.Events.TITLE, "Expiration tracker reminder");
                        event.put(CalendarContract.Events.DESCRIPTION, mName + " will be expired on " + mDate);
                        event.put(CalendarContract.Events.CALENDAR_ID, calendar_id);
                        event.put(CalendarContract.Events.DTSTART, start.getTimeInMillis());
                        event.put(CalendarContract.Events.DTEND, start.getTimeInMillis());
                        event.put(CalendarContract.Events.RRULE, rrule);
                        event.put(CalendarContract.Events.HAS_ALARM, 1);
                        event.put(CalendarContract.Events.EVENT_TIMEZONE, timezone);
                        ContentResolver context = mActivity.getContentResolver();
                        int checkCalenderPermission = ContextCompat.checkSelfPermission(mActivity,
                                Manifest.permission.WRITE_CALENDAR);
                        Uri newEvent = null;
                        Uri newReminder = null;
                        if (checkCalenderPermission != PackageManager.PERMISSION_GRANTED) {
                            ActivityCompat.requestPermissions(mActivity, new String[]{Manifest.permission.WRITE_CALENDAR}, 2);

                        } else {
                            newEvent = context.insert(CalendarContract.Events.CONTENT_URI, event);
                            mEventId = Long.parseLong(Objects.requireNonNull(Objects.requireNonNull(newEvent).getLastPathSegment()));
                            ContentValues reminder = new ContentValues();
                            reminder.put(CalendarContract.Reminders.EVENT_ID, mEventId);
                            reminder.put(CalendarContract.Reminders.MINUTES, 0);
                            reminder.put(CalendarContract.Reminders.METHOD, CalendarContract.Reminders.METHOD_ALERT);
                            newReminder = context.insert(CalendarContract.Reminders.CONTENT_URI, reminder);
                            if(newReminder == null){
                                return;
                            }
                        }
                        mItemReference.push().setValue(new Item(mName, mDate, Integer.parseInt(mQuantity), mDescription, mEventId));
                    }
                    else{
                        // update event and reminder
                        ContentResolver cr = mActivity.getContentResolver();
                        ContentValues event = new ContentValues();
                        event.put(CalendarContract.Events.TITLE, "Expiration tracker reminder");
                        event.put(CalendarContract.Events.DESCRIPTION, mName + " will be expired on " + mDate);
                        event.put(CalendarContract.Events.DTSTART, start.getTimeInMillis());
                        event.put(CalendarContract.Events.DTEND, start.getTimeInMillis());
                        event.put(CalendarContract.Events.RRULE, rrule);
                        Uri updateEvent = ContentUris.withAppendedId(CalendarContract.Events.CONTENT_URI, mEventId);
                        int rows = cr.update(updateEvent, event, null, null);
                        Log.e("mEventId", String.valueOf(mEventId));
                        Log.e("reminder", "Rows updated: " + rows);
                    }
//                    return;
                }
                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                }
            };
        mCategoryReference.addListenerForSingleValueEvent(mCategoryListener);
    }
    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.btn_item_save) {
            mName = ((EditText) mView.findViewById(R.id.text_item_name)).getText().toString();
            if (mName.length() == 0) {
                Toast.makeText(mActivity.getApplicationContext(), "Name cannot be empty",
                        Toast.LENGTH_SHORT).show();
                ((EditText) mView.findViewById(R.id.text_item_name)).setText("");
            } else {
                DatePicker datePicker = mView.findViewById(R.id.date_picker);
                int day = datePicker.getDayOfMonth();
                int month = datePicker.getMonth() + 1;
                int year = datePicker.getYear();
                mDate = "" + year;
                if (month < 10) {
                    mDate = mDate + "0" + month;
                } else {
                    mDate = mDate + month;
                }
                if (day < 10) {
                    mDate = mDate + "0" + day;
                } else {
                    mDate = mDate + day;
                }
                mQuantity = ((TextView) mView.findViewById(R.id.quantity)).getText().toString();
                mDescription = ((EditText) mView.findViewById(R.id.description)).getText().toString();
                mItemReference = FirebaseDatabase.getInstance().getReference().child("items").child(Objects.requireNonNull(mAuth.getUid())).child(mCategoryId);
                if ((Objects.requireNonNull(mIntent.getStringExtra("operation"))).equals("Edit")) {
                    String itemId = mIntent.getStringExtra("itemId");
                    mEventId = Long.parseLong(Objects.requireNonNull(mIntent.getStringExtra("eventId")));
                    mItemReference.child(Objects.requireNonNull(itemId)).setValue(new Item(mName, mDate, Integer.parseInt(mQuantity), mDescription, mEventId));
                    addReminder("Edit");
                } else {
                    addReminder("Add");
                }
                Intent newIntent = new Intent(mActivity, NavActivity.class);
                if (Objects.requireNonNull(mIntent.getStringExtra("content")).equals("ITEM_EDIT_FROM_HOME")) {
                    newIntent.putExtra("content", "HOME");
                } else {
                    newIntent.putExtra("content", "ITEM_LIST");
                }
                newIntent.putExtra("categoryId", mCategoryId);
                startActivity(newIntent);
            }
        }
    }
    @Override
    public void onStop() {
        super.onStop();
        // Remove post value event listener
        if (mCategoryListener != null) {
            mCategoryReference.removeEventListener(mCategoryListener);
            mCategoryListener = null;
        }
        Runtime.getRuntime().gc();
    }
}
