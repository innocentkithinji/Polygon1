package com.polygon.listeners;

/**
 * Created by innocen on 5/19/2017.
 */

public class ItemView {
    private String Name;
    private String Place;
    private String Price;
    private String CompPrice;
    private String Image;

    public ItemView() {
    }

    public ItemView(String name, String place, String price, String compPrice) {
        Name = name;
        Place = place;
        Price = price;
        CompPrice = compPrice;
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

    public String getCompPrice() {
        return CompPrice;
    }

    public void setCompPrice(String compPrice) {
        CompPrice = compPrice;
    }

    public String getImage() {
        return Image;
    }

    public void setImage(String image) {
        Image = image;
    }
}
