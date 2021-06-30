package com.example.expirationtracker;

import com.example.expirationtracker.model.User;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
public class UserModelUnitTest {
    private User mUser = new User("1@gmail.com", "jack");
    @Test
    public void get_user_name_isCorrect() throws Exception {
        assertEquals("1@gmail.com", mUser.getUserName());
    }
    @Test
    public void get_name_isCorrect() throws Exception {
        assertEquals("jack", mUser.getName());
    }
}