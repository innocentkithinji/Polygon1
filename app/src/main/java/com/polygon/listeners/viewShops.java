package com.polygon.listeners;

/**
 * Created by innocen on 5/8/2017.
 */

public class viewShops {
    private String Image;
    private String Name;
    private String City;
    private String Closing;
    private String Opening;
    private String Place;
    private String ShopId;

    public viewShops() {
    }

    public String getName() {
        return Name;
    }

    public void setName(String name) {
        Name = name;
    }
    public String getShopId() {
        return ShopId;
    }

    public void setShopId(String shopId) {
        this.ShopId = shopId;
    }


    public String getImage() {
        return Image;
    }

    public void setImage(String image) {
        Image = image;
    }


    public String getClosing() {
        return Closing;
    }

    public void setClosing(String closing) {
        Closing = closing;
    }

    public String getOpening() {
        return Opening;
    }

    public void setOpening(String opening) {
        Opening = opening;
    }

    public String getPlace() {
        return Place;
    }

    public void setPlace(String place) {
        Place = place;
    }

    public String getCity() {
        return City;
    }

    public void setCity(String city) {
        City = city;
    }
}
