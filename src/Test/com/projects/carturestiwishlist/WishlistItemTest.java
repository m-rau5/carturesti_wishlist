package com.projects.carturestiwishlist;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class WishlistItemTest {
    //test setting basic details of items
    @Test
    public void testWishlistItem() {
        int ID = 1;
        String itemName = "WishlistItem";
        int basePrice = 100;
        int lowestPrice = 80;
        int currentPrice = 90; //curr < base => on sale
        WishlistItem whsItem = new WishlistItem(ID, itemName, basePrice, lowestPrice, currentPrice);

        assertEquals(ID, whsItem.getItemId());
        assertEquals(itemName, whsItem.getItemName());
        assertEquals(basePrice, whsItem.getBasePrice());
        assertEquals(lowestPrice, whsItem.getLowestPrice());
        assertEquals(currentPrice, whsItem.getCurrentPrice());
        assertEquals(true, whsItem.getOnSale());
    }

    //test setting details only admin sees
    @Test
    public void testSetAdminValues() {
        WishlistItem whsItem = new WishlistItem(1, "WishlistItemAdmin", 100, 80, 90);
        String username = "admin";
        String itemLink = "https://carturesti.ro/";

        whsItem.setAdminValues(username, itemLink);

        assertEquals(username, whsItem.getUsername());
        assertEquals(itemLink, whsItem.getItemLink());
    }

    //setter/getter tester (for views)
    @Test
    public void testSetters() {
        WishlistItem whsItem = new WishlistItem(1, "WishlistItemSetter", 100, 80, 90);
        int newItemId = 2;
        String newItemName = "WishlistItemSetter";
        int newBasePrice = 110;
        int newLowestPrice = 90;
        int newCurrentPrice = 100;
        boolean newOnSale = false;

        whsItem.setItemId(newItemId);
        whsItem.setItemName(newItemName);
        whsItem.setBasePrice(newBasePrice);
        whsItem.setLowestPrice(newLowestPrice);
        whsItem.setCurrentPrice(newCurrentPrice);
        whsItem.setOnSale(newOnSale);

        assertEquals(newItemId, whsItem.getItemId());
        assertEquals(newItemName, whsItem.getItemName());
        assertEquals(newBasePrice, whsItem.getBasePrice());
        assertEquals(newLowestPrice, whsItem.getLowestPrice());
        assertEquals(newCurrentPrice, whsItem.getCurrentPrice());
        assertEquals(newOnSale, whsItem.getOnSale());
    }
}