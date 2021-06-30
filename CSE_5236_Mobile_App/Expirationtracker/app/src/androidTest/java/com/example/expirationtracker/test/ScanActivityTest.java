package com.example.expirationtracker.test;

import android.content.Intent;
import android.view.View;

import androidx.test.espresso.intent.rule.IntentsTestRule;
import androidx.test.rule.ActivityTestRule;

import com.example.expirationtracker.R;
import androidx.test.espresso.intent.Intents;
import com.example.expirationtracker.ui.Item.ScanActivity;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static junit.framework.TestCase.assertNotNull;

public class ScanActivityTest {

    @Rule
    public IntentsTestRule<ScanActivity> mIntentsRule  = new IntentsTestRule<ScanActivity>(ScanActivity.class);
    private ScanActivity mActivity;

    @Before
    public void setUp() throws Exception {
        mActivity = mIntentsRule.getActivity();
    }

    @Test
    public void testLaunch(){
        View view = mActivity.findViewById(R.id.scanAct);
        assertNotNull(view);
    }

    @After
    public void tearDown() throws Exception {
        mActivity = null;
    }
}