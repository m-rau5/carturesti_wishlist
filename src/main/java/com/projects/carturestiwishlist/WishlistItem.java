package com.projects.carturestiwishlist;

public class WishlistItem {
    int itemId;
    String itemName;
    String username = "";
    String itemLink = "";
    int basePrice,lowestPrice,currentPrice;
    Boolean onSale;

    public WishlistItem(int ID, String itemName,
                        int basePrice, int lowestPrice, int currentPrice) {
        this.itemId = ID;
        this.itemName = itemName;
        this.basePrice= basePrice;
        this.lowestPrice = lowestPrice;
        this.currentPrice = currentPrice;
        if(currentPrice - basePrice < 0 && currentPrice != 0)
            this.onSale= true;
        else this.onSale = false;
    }

    public void setAdminValues(String username,String itemLink){
        this.username = username;
        this.itemLink = itemLink;
    }

    public void setItemId(int itemId) {
        this.itemId = itemId;
    }

    public void setItemName(String itemName) {
        this.itemName = itemName;
    }

    public void setBasePrice(int basePrice) {
        this.basePrice = basePrice;
    }

    public void setLowestPrice(int lowestPrice) {
        this.lowestPrice = lowestPrice;
    }

    public void setCurrentPrice(int currentPrice) {
        this.currentPrice = currentPrice;
    }

    public void setOnSale(Boolean onSale) {
        this.onSale = onSale;
    }



    public int getItemId() {
        return itemId;
    }

    public String getItemName() {
        return itemName;
    }

    public int getBasePrice() {
        return basePrice;
    }

    public int getLowestPrice() {
        return lowestPrice;
    }

    public int getCurrentPrice() {
        return currentPrice;
    }

    public Boolean getOnSale() {
        return onSale;
    }

    public String getUsername() {
        return username;
    }

    public String getItemLink() {
        return itemLink;
    }

}
