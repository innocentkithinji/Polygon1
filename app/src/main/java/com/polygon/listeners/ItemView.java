package com.polygon.listeners;

/**
 * Created by innocen on 5/19/2017.
 */

public class ItemView {
    private String  Amount;
    private String ComparePrice;
    private String Image;
    private String Name;
    private String Price;
    private String OwnerID;



    private String Place;

    public ItemView() {
    }

    public ItemView(String amount, String comparePrice, String image, String name, String price) {
        Amount = amount;
        ComparePrice = comparePrice;
        Image = image;
        Name = name;
        Price = price;
    }

    public String getAmount() {
        return Amount;
    }

    public void setAmount(String amount) {
        Amount = amount;
    }

    public String getComparePrice() {
        return ComparePrice;
    }

    public void setComparePrice(String comparePrice) {
        ComparePrice = comparePrice;
    }

    public String getImage() {
        return Image;
    }

    public void setImage(String image) {
        Image = image;
    }

    public String getName() {
        return Name;
    }

    public void setName(String name) {
        Name = name;
    }

    public String getPlace() {
        return Place;
    }

    public void setPlace(String place) {
        Place = place;
    }

    public String getPrice() {
        return Price;
    }

    public void setPrice(String price) {
        Price = price;
    }

    public String getOwnerID() {
        return OwnerID;
    }

    public void setOwnerID(String ownerID) {
        OwnerID = ownerID;
    }
}
