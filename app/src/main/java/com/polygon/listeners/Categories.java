package com.polygon.listeners;

/**
 * Created by innocen on 3/10/2017.
 */

public class Categories {
    private String Image;
    private String Title;

    public Categories(){}

    public Categories(String image, String title) {
        Image = image;
        Title = title;
    }

    public String getImage() {
        return Image;
    }

    public String getTitle() {
        return Title;
    }

    public void setImage(String image) {
        Image = image;
    }

    public void setTitle(String title) {
        Title = title;
    }
}
