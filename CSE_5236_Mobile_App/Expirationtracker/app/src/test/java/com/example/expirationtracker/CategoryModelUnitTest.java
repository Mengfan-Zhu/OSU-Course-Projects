package com.example.expirationtracker;

import com.example.expirationtracker.model.Category;
import com.example.expirationtracker.model.User;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
public class CategoryModelUnitTest {
    private Category mCategory = new Category("Food", "1 week before", "everyday", "15:00");
    @Test
    public void get_name_isCorrect() throws Exception {
        assertEquals("Food", mCategory.getName());
    }
    @Test
    public void get_begin_isCorrect() throws Exception {
        assertEquals("1 week before", mCategory.getBegin());
    }
    @Test
    public void get_frequency_isCorrect() throws Exception {
        assertEquals("everyday", mCategory.getFrequency());
    }
    @Test
    public void get_time_isCorrect() throws Exception {
        assertEquals("15:00", mCategory.getTime());
    }
}