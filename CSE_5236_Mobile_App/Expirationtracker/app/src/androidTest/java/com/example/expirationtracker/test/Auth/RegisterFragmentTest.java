package com.example.expirationtracker.test.Auth;

import android.view.View;
import android.widget.LinearLayout;

import androidx.test.rule.ActivityTestRule;

import com.example.expirationtracker.R;
import com.example.expirationtracker.ui.Authentication.AuthActivity;
import com.example.expirationtracker.ui.Authentication.RegisterFragment;
import com.google.firebase.auth.FirebaseAuth;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import static androidx.test.platform.app.InstrumentationRegistry.getInstrumentation;
import static junit.framework.TestCase.assertNotNull;

public class RegisterFragmentTest {

    @Rule
    public ActivityTestRule<AuthActivity> mActivityTestRule = new ActivityTestRule<AuthActivity>(AuthActivity.class);
    private AuthActivity mActivity = null;
    @Before
    public void setUp() throws Exception {
        mActivity = mActivityTestRule.getActivity();
    }

    @Test
    public void testLaunch(){
        if(FirebaseAuth.getInstance().getCurrentUser()==null) {
            FirebaseAuth.getInstance().signOut();
            LinearLayout container = mActivity.findViewById(R.id.registerFrag);
            assertNotNull(container);
            RegisterFragment test = new RegisterFragment();
            mActivity.getSupportFragmentManager().beginTransaction().add(container.getId(), test).commitAllowingStateLoss();
            getInstrumentation().waitForIdleSync();
            View view = test.getView();
            assertNotNull(view);
        }
    }

    @After
    public void tearDown() throws Exception {
        mActivity = null;
    }
}