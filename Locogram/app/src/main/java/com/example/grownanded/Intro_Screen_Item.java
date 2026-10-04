package com.example.grownanded;

public class Intro_Screen_Item {

    String Title, Description , Description02,Description03,Description04,Description05;
    int ScreenImg;


    public Intro_Screen_Item(String title, String description,String description02,String description03,String description04,String description05, int screenImg) {
        Title = title;
        Description = description;
        Description02 = description02;
        Description03 = description03;
        Description04 = description04;
        Description05 = description05;

        ScreenImg = screenImg;
    }

    public String getDescription03() {
        return Description03;
    }

    public void setDescription03(String description03) {
        Description03 = description03;
    }

    public String getDescription04() {
        return Description04;
    }

    public void setDescription04(String description04) {
        Description04 = description04;
    }

    public String getDescription05() {
        return Description05;
    }

    public void setDescription05(String description05) {
        Description05 = description05;
    }

    public String getDescription02() {
        return Description02;
    }

    public void setDescription02(String description02) {
        Description02 = description02;
    }

    public void setTitle(String title) {
        Title = title;
    }

    public void setDescription(String description) {
        Description = description;
    }

    public void setScreenImg(int screenImg) {
        ScreenImg = screenImg;
    }

    public String getTitle() {
        return Title;
    }

    public String getDescription() {
        return Description;
    }

    public int getScreenImg() {
        return ScreenImg;
    }
}
