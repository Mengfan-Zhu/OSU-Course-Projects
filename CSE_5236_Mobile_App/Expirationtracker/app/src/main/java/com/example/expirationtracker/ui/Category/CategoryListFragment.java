package com.example.expirationtracker.ui.Category;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.CalendarContract;
import android.util.Log;
import android.util.TypedValue;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.view.LayoutInflater;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.LinearLayout.LayoutParams;

import com.example.expirationtracker.R;
import com.example.expirationtracker.model.Category;

import com.example.expirationtracker.model.Item;
import com.example.expirationtracker.ui.NavActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Objects;


public class CategoryListFragment extends Fragment implements View.OnClickListener {
    private FirebaseAuth mAuth;
    private DatabaseReference mCategoryReference;
    private ValueEventListener mCategoryListener;
    private Query mCategoryQuery;
    private LinearLayout mCategoryLayout;
    private Activity mActivity;
    private final int MAXSIZE = 200;
    private Button[] mEditButtons = new Button[MAXSIZE];
    private Button[] mDeleteButtons = new Button[MAXSIZE];
    private Category[] mCategorys = new Category[MAXSIZE];
    private String[] mItemIds = new String[MAXSIZE];
    private String[] mCategoryIds = new String[MAXSIZE];
    private LinearLayout[] mCategoryContent = new LinearLayout[MAXSIZE];
    private int mCount = 0;
    private String TAG = "Category List Fragment";
    private ArrayList<View.OnClickListener> mEditListeners = new ArrayList<View.OnClickListener>();
    private View view;
    private Runnable r;

    public CategoryListFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     * @return A new instance of fragment CategoryListFragment.
     */
    public static CategoryListFragment newInstance() {
        return new CategoryListFragment();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        mActivity = getActivity();
        view = inflater.inflate(R.layout.fragment_category_list, container, false);
        // set up firebase reference
        mAuth = FirebaseAuth.getInstance();

        // set up layouts
        ScrollView categoryList = view.findViewById(R.id.category_layout);
        mCategoryLayout = new LinearLayout(mActivity);
        mCategoryLayout.setPadding(10,10,10,400);
        mCategoryLayout.setOrientation(LinearLayout.VERTICAL);
        categoryList.addView(mCategoryLayout);
        // set up buttons
        Button addButton = view.findViewById(R.id.btn_add_category);
        addButton.setOnClickListener(this);
        return view;
    }
    @Override
    public void onStart(){
        super.onStart();
        mCategoryReference = FirebaseDatabase.getInstance().getReference().child("categories").child(Objects.requireNonNull(mAuth.getUid()));
        mCategoryQuery = FirebaseDatabase.getInstance().getReference().child("categories").child(Objects.requireNonNull(mAuth.getUid())).orderByChild("name");
        showCategoryList();
    }
    public void showCategoryList(){
        mCategoryListener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                mActivity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        mCategoryLayout.removeAllViews();
                    }
                });
                // add each layout
                for (DataSnapshot currentSnapshot : dataSnapshot.getChildren()) {
                    final String categoryId = currentSnapshot.getKey();
                    final Category category = currentSnapshot.getValue(Category.class);
                    mCategorys[mCount] = category;
                    mCategoryIds[mCount] = categoryId;
                    // linearLayout for one category content
                    mActivity.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            mCategoryContent[mCount] = new LinearLayout(view.getContext());
                            mCategoryContent[mCount].setOrientation(LinearLayout.VERTICAL);
                            LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
                                    LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                            layoutParams.setMargins(10, 10, 10, 10);
                            mCategoryContent[mCount].setLayoutParams(layoutParams);
                            mCategoryContent[mCount].setDividerPadding(10);
                            mCategoryContent[mCount].setBackgroundResource(R.drawable.bg_item);
                            mCategoryContent[mCount].setClickable(true);
                            mCategoryContent[mCount].setOnClickListener(CategoryListFragment.this);
                            mCategoryContent[mCount].setId(2*MAXSIZE + mCount);
                            // TextView for name
                            TextView name = new TextView(view.getContext());
                            name.setLayoutParams(new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT));
                            name.setText(Objects.requireNonNull(category).getName());
                            name.setTextSize(TypedValue.COMPLEX_UNIT_SP, 25);
                            mCategoryContent[mCount].addView(name);
                            // TextView for contents
                            TextView contents = new TextView(view.getContext());
                            contents.setLayoutParams(new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT));
                            contents.setText(String.format("Begin: %s\nFrequency: %s\nTime: %s", category.getBegin(), category.getFrequency(), category.getTime()));
                            mCategoryContent[mCount].addView(contents);
                            // linearLayout for buttons
                            LinearLayout buttonsLayout = new LinearLayout(view.getContext());
                            buttonsLayout.setOrientation(LinearLayout.HORIZONTAL);
                            buttonsLayout.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT));
                            // edit button
                            mEditButtons[mCount] = new Button(view.getContext());
                            mEditButtons[mCount].setText(R.string.edit);
                            mEditButtons[mCount].setLayoutParams(new LayoutParams(LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT));
                            mEditButtons[mCount].setOnClickListener(CategoryListFragment.this);
                            mEditButtons[mCount].setId(mCount);
                            buttonsLayout.addView(mEditButtons[mCount]);
                            // delete button
                            mDeleteButtons[mCount] = new Button(view.getContext());
                            mDeleteButtons[mCount].setText(R.string.delete);
                            mDeleteButtons[mCount].setLayoutParams(new LayoutParams(LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT));
                            mDeleteButtons[mCount].setOnClickListener(CategoryListFragment.this);
                            mDeleteButtons[mCount].setId(MAXSIZE + mCount);
                            buttonsLayout.addView(mDeleteButtons[mCount]);
                            mCategoryContent[mCount].addView(buttonsLayout);
                            mCategoryLayout.addView( mCategoryContent[mCount]);
                            mCount++;
                        }
                    });
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.e(TAG, "FAIL TO UPDATE");
            }

        };
        mCategoryQuery.addValueEventListener(mCategoryListener);
    }

    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.btn_add_category) {
            Intent intent = new Intent(mActivity, NavActivity.class);
            intent.putExtra("content", "CATEGORY_EDIT");
            intent.putExtra("operation", "Add");
            startActivity(intent);
        }
        for (int i = 0; i < mCount; i++) {
            if (view.getId() == mEditButtons[i].getId()) {
                Intent intent = new Intent(mActivity, NavActivity.class);
                intent.putExtra("content", "CATEGORY_EDIT");
                intent.putExtra("categoryName",mCategorys[i].getName());
                intent.putExtra("categoryFrequency",mCategorys[i].getFrequency());
                intent.putExtra("categoryTime",mCategorys[i].getTime());
                intent.putExtra("categoryBegin",mCategorys[i].getBegin());
                intent.putExtra("categoryId",mCategoryIds[i]);
                intent.putExtra("operation","Edit");
                startActivity(intent);
            }
        }
        for (int i = 0; i < mCount; i++) {
            if (view.getId() == mDeleteButtons[i].getId()) {
                mCategoryReference.child(Objects.requireNonNull(mCategoryIds[i])).removeValue();
                DatabaseReference itemReference = FirebaseDatabase.getInstance().getReference().child("items").child(Objects.requireNonNull(mAuth.getUid())).child(mCategoryIds[i]);
                itemReference.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        for (DataSnapshot currentSnapshot : dataSnapshot.getChildren()) {
                            Item item = currentSnapshot.getValue(Item.class);
                            ContentResolver cr = mActivity.getContentResolver();
                            Uri deleteEvent = ContentUris.withAppendedId(CalendarContract.Events.CONTENT_URI, Objects.requireNonNull(item).getEventId());
                            cr.delete(deleteEvent, null, null);
                        }

                    }
                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
                itemReference.removeValue();
            }
        }
        for (int i = 0; i < mCount; i++) {
            if (view.getId() == mCategoryContent[i].getId()) {
                Intent intent = new Intent(mActivity, NavActivity.class);
                intent.putExtra("content", "ITEM_LIST");
                intent.putExtra("categoryId",mCategoryIds[i]);
                startActivity(intent);
            }
        }
    }
    @Override
    public void onStop() {
        super.onStop();
        // Remove post value event listener
        if (mCategoryListener != null) {
            mCategoryQuery.removeEventListener(mCategoryListener);
            mCategoryListener = null;
        }
        Runtime.getRuntime().gc();
    }
}
