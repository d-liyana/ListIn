package com.dinu.listin.Model;

public class BestDeal {
    private String menu_id, item_id, name, image;

    public BestDeal() {
    }

    public BestDeal(String menu_id, String item_id, String name, String image) {
        this.menu_id = menu_id;
        this.item_id = item_id;
        this.name = name;
        this.image = image;
    }

    public String getMenu_id() {
        return menu_id;
    }

    public void setMenu_id(String menu_id) {
        this.menu_id = menu_id;
    }


    public String getItem_id() {
        return item_id;
    }

    public void setItem_id(String item_id) {
        this.item_id = item_id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }
}
