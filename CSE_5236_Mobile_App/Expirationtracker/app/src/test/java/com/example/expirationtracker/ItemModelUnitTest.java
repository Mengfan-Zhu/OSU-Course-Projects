package com.example.expirationtracker;

import com.example.expirationtracker.model.Item;
import com.example.expirationtracker.model.User;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
public class ItemModelUnitTest {
    private Item mItem = new Item("bread","20200417",2,"breakfast",10);
    @Test
    public void get_name_isCorrect() throws Exception {
        assertEquals("bread", mItem.getName());
    }
    @Test
    public void get_date_isCorrect() throws Exception {
        assertEquals("20200417", mItem.getExpirationDate());
    }
    @Test
    public void get_quantity_isCorrect() throws Exception {
        assertEquals(2, mItem.getQuantity());
    }
    @Test
    public void get_description_isCorrect() throws Exception {
        assertEquals("breakfast", mItem.getDescription());
    }
    @Test
    public void get_id_isCorrect() throws Exception {
        assertEquals(10, mItem.getEventId());
    }
}