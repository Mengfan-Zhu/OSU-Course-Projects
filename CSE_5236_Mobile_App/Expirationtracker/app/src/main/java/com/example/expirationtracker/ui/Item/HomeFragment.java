package com.example.expirationtracker.ui.Item;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import android.provider.CalendarContract;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import com.example.expirationtracker.R;
import com.example.expirationtracker.model.Item;
import com.example.expirationtracker.ui.NavActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import static androidx.constraintlayout.widget.Constraints.TAG;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link HomeFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class HomeFragment extends Fragment implements View.OnClickListener{
    private DatabaseReference mItemReference;
    private Activity mActivity;
    private LinearLayout mItemLayout;
    private ValueEventListener mItemListener;
    private Query mItemQuery;
    private final int MAXSIZE = 1000;
    private Button[] mEditButtons = new Button[MAXSIZE];
    private Button[] mDeleteButtons = new Button[MAXSIZE];
    private Item[] mItems = new Item[MAXSIZE];
    private String[] mItemIds = new String[MAXSIZE];
    private String[] mCategoryIds = new String[MAXSIZE];
    private int mCount = 0;

    private View mView;
    public HomeFragment() {
        // Required empty public constructor
    }
    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @return A new instance of fragment HomeFragment.
     */
    public static HomeFragment newInstance() {
        return new HomeFragment();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        mActivity = getActivity();
        View view = inflater.inflate(R.layout.fragment_home, container, false);
        ScrollView itemList = view.findViewById(R.id.home_layout);
        itemList.setFillViewport(true);
        mItemLayout = new LinearLayout(mActivity);
        mItemLayout.setPadding(10,10,10,400);
        mItemLayout.setOrientation(LinearLayout.VERTICAL);
        itemList.addView(mItemLayout);
        return view;
    }
    @Override
    public void onStart(){
        super.onStart();
        FirebaseAuth auth = FirebaseAuth.getInstance();
        if(auth.getUid()!=null){
            mItemReference = FirebaseDatabase.getInstance().getReference().child("items").child(auth.getUid());
            mItemQuery = mItemReference.orderByChild("name");
            showItemList();
        }
    }
    private void showItemList(){
        mItemListener = new ValueEventListener() {
            @SuppressLint("SimpleDateFormat")
            SimpleDateFormat sdf = new SimpleDateFormat(getString(R.string.date_format));
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                // clear previous view
                mActivity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        mItemLayout.removeAllViews();
                    }
                });
                List<Item> nonExpired = new ArrayList<>();
                List<Item> expired = new ArrayList<>();
                Map<Item, String[]> expiredId = new HashMap<>();
                Map<Item, String[]> nonExpiredId = new HashMap<>();
                for (DataSnapshot currentCategory : dataSnapshot.getChildren()) {
                    final String categoryId = currentCategory.getKey();
                    for(DataSnapshot currentSnapshot : currentCategory.getChildren()){
                        final String itemId = currentSnapshot.getKey();
                        final Item item = currentSnapshot.getValue(Item.class);
                        String[] id = new String[2];
                        id[0] = itemId;
                        id[1] = categoryId;
                        Date expDate = null;
                        Date currDate = null;
                        try {
                            expDate = sdf.parse(Objects.requireNonNull(item).getExpirationDate());
                            currDate = sdf.parse(sdf.format(new Date() ));
                        } catch (ParseException e) {
                            e.printStackTrace();
                        }
                        if(Objects.requireNonNull(expDate).compareTo(currDate) > 0 ){
                            nonExpired.add(item);
                            nonExpiredId.put(item, id);
                        }else{
                            expired.add(item);
                            expiredId.put(item, id);
                        }
                    }
                }
                addListView(expired,expiredId,true);
                addListView(nonExpired,nonExpiredId,false);
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.e(TAG, "FAIL TO UPDATE");
            }
        };
        mItemQuery.addValueEventListener(mItemListener);
    }
    public void addListView(List<Item> list, Map<Item, String[]> idList, boolean isExpired){
        mActivity.runOnUiThread(new Runnable() {
            @SuppressLint("DefaultLocale")
            @Override
            public void run() {
                Collections.sort(list, new Comparator<Item>(){
                    @Override
                    public int compare(Item i1, Item i2) {
                        return (Integer.parseInt(i1.getExpirationDate()) - Integer.parseInt(i2.getExpirationDate()));
                    }
                });
                TextView itemList = new TextView(mActivity);
                itemList.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT));
                if(isExpired){
                    itemList.setText(R.string.expired_items);
                    itemList.setTextColor(Color.parseColor("#FF0000"));
                }else{
                    itemList.setText(R.string.not_expired_items);
                }
                itemList.setTextSize(TypedValue.COMPLEX_UNIT_SP, 25);
                mItemLayout.addView(itemList);
                for(Item item : list){
                    String itemId = Objects.requireNonNull(idList.get(item))[0];
                    String categoryId = Objects.requireNonNull(idList.get(item))[1];
                    mItemIds[mCount] = itemId;
                    mItems[mCount] = item;
                    mCategoryIds[mCount] = categoryId;
                    LinearLayout itemContent = new LinearLayout(mActivity);
                    LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
                            LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                    layoutParams.setMargins(10, 10, 10, 10);
                    itemContent.setOrientation(LinearLayout.VERTICAL);
                    itemContent.setLayoutParams(layoutParams);
                    itemContent.setDividerPadding(10);
                    itemContent.setBackgroundResource(R.drawable.bg_item);
                    itemContent.setClickable(true);
                    // TextView for name
                    TextView name = new TextView(mActivity);
                    name.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT));
                    name.setText(item.getName());
                    name.setTextSize(TypedValue.COMPLEX_UNIT_SP, 25);
                    itemContent.addView(name);
                    // TextView for contents
                    TextView contents = new TextView(mActivity);
                    contents.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT));
                    contents.setText(String.format("Expiration Date: %s\n" +
                            "Quantity: %d\n" +
                            "Description: %s", item.getExpirationDate(), item.getQuantity(), item.getDescription()));
                    itemContent.addView(contents);
                    if(isExpired){
                        name.setTextColor(Color.parseColor("#FF0000"));
                    }
                    // linearLayout for buttons
                    LinearLayout buttonsLayout = new LinearLayout(mActivity);
                    buttonsLayout.setOrientation(LinearLayout.HORIZONTAL);
                    buttonsLayout.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT));
                    // edit button
                    mEditButtons[mCount] = new Button(mActivity);
                    mEditButtons[mCount].setText(R.string.edit);
                    mEditButtons[mCount].setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT));
                    mEditButtons[mCount].setOnClickListener(HomeFragment.this);
                    mEditButtons[mCount].setId(mCount);
                    buttonsLayout.addView(mEditButtons[mCount]);
                    // delete button
                    mDeleteButtons[mCount] = new Button(mActivity);
                    mDeleteButtons[mCount].setText(R.string.delete);
                    mDeleteButtons[mCount].setId(MAXSIZE + mCount);
                    mDeleteButtons[mCount].setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT));
                    mDeleteButtons[mCount].setOnClickListener(HomeFragment.this);
                    buttonsLayout.addView(mDeleteButtons[mCount]);
                    itemContent.addView(buttonsLayout);
                    mItemLayout.addView(itemContent);
                    mCount++;
                }
            }
        });
    }
    @Override
    public void onClick(View view) {
        for (int i = 0; i < mCount; i++) {
            if (view.getId() == mEditButtons[i].getId()) {
                Intent intent = new Intent(mActivity, NavActivity.class);
                intent.putExtra("itemName",mItems[i].getName());
                intent.putExtra("itemExpirationDate",mItems[i].getExpirationDate());
                intent.putExtra("itemQuantity",Integer.toString(mItems[i].getQuantity()));
                intent.putExtra("itemDescription",mItems[i].getDescription());
                intent.putExtra("itemId",mItemIds[i]);
                intent.putExtra("eventId",Long.toString(mItems[i].getEventId()));
                intent.putExtra("categoryId",mCategoryIds[i]);
                intent.putExtra("operation","Edit");
                intent.putExtra("content", "ITEM_EDIT_FROM_HOME");
                startActivity(intent);
            }
        }
        for (int i = 0; i < mCount; i++) {
            if (view.getId() == mDeleteButtons[i].getId()) {
                ContentResolver cr = mActivity.getContentResolver();
                Uri deleteEvent = ContentUris.withAppendedId(CalendarContract.Events.CONTENT_URI, mItems[i].getEventId());
                cr.delete(deleteEvent, null, null);
                mItemReference.child(mCategoryIds[i]).child(mItemIds[i]).removeValue();
                mItemLayout.removeAllViews();
            }
        }
    }
    @Override
    public void onStop() {
        super.onStop();
        // Remove post value event listener
        if (mItemListener != null) {
            mItemQuery.removeEventListener(mItemListener);
            mItemListener = null;
        }
        Runtime.getRuntime().gc();
    }
}
