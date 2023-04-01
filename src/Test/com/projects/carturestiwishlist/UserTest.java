package com.projects.carturestiwishlist;

import org.junit.jupiter.api.Test;

import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;


public class UserTest {
    //test setting username and isAdmin boolean
    @Test
    public void testUser() {
        String username = "User";
        Boolean isAdmin = true;
        User user = new User(username, isAdmin);

        assertEquals(username, user.getUsername());
        assertEquals(isAdmin, user.isAdmin);
    }

    //test adding and removing of items in the list of items
    @Test
    public void testAddAndRemoveWhsItem() {
        User user = new User("User", false);
        WishlistItem item1 = new WishlistItem(1, "Item 1", 100, 80, 90);
        WishlistItem item2 = new WishlistItem(2, "Item 2", 110, 90, 100);
        WishlistItem item3 = new WishlistItem(3, "Item 3", 120, 100, 0);

        user.addWhsItem(item1);
        user.addWhsItem(item2);
        user.addWhsItem(item3);
        ArrayList<WishlistItem> whsItems = user.getWhsItems();
        assertEquals(3, whsItems.size());
        assertTrue(whsItems.contains(item1));
        assertTrue(whsItems.contains(item2));
        assertTrue(whsItems.contains(item3));

        user.removeWhsItem(2);
        whsItems = user.getWhsItems();
        assertEquals(2, whsItems.size());
        assertTrue(whsItems.contains(item1));
        assertFalse(whsItems.contains(item2));
        assertTrue(whsItems.contains(item3));
    }
}