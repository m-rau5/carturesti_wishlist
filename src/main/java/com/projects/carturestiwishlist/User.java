package com.projects.carturestiwishlist;

import java.util.ArrayList;

public class User {

    String username;
    ArrayList<WishlistItem> whsItems = new ArrayList<WishlistItem>();
    Boolean isAdmin;
    Boolean updated = false;

    public User(String username,Boolean isAdmin){
        this.username = username;
        this.isAdmin = isAdmin;
    }

    public void addWhsItem(WishlistItem item){
        if(!whsItems.contains(item)){
            whsItems.add(item);
        }
    }

    public void removeWhsItem(int ID){
        for(WishlistItem item : whsItems){
            if(item.itemId == ID){
                whsItems.remove(item);
                break;
            }
        }
    }

    public ArrayList<WishlistItem> getWhsItems (){
        return whsItems;
    }

    public String getUsername() {
        return username;
    }
}
