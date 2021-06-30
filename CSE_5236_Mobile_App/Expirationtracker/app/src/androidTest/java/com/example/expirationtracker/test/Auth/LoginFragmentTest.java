package com.example.expirationtracker.test.Auth;

import android.view.View;
import android.widget.RelativeLayout;

import androidx.test.rule.ActivityTestRule;

import com.example.expirationtracker.R;
import com.example.expirationtracker.ui.Authentication.AuthActivity;
import com.example.expirationtracker.ui.Authentication.LoginFragment;
import com.google.firebase.auth.FirebaseAuth;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import static androidx.test.platform.app.InstrumentationRegistry.getInstrumentation;
import static junit.framework.TestCase.assertNotNull;

public class LoginFragmentTest {

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
            RelativeLayout container = mActivity.findViewById(R.id.loginFrag);
            assertNotNull(container);
            LoginFragment test = new LoginFragment();
            mActivity.getSupportFragmentManager().beginTransaction().add(container.getId(), test).commitAllowingStateLoss();
            getInstrumentation().waitForIdleSync();
            View view = test.getView();
            assertNotNull((view));

        }

    }

    @After
    public void tearDown() throws Exception {
        mActivity = null;
    }
}