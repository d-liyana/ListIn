package com.dinu.listin.Model;

import java.util.List;

public class Item {
    private String id,name,image,description;
    private Long price;
    List<Addon> addon;
    List<Size> size;


    //For Cart
    private Size userSelectedSize;

    public Item() {
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
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

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Long getPrice() {
        return price;
    }

    public void setPrice(Long price) {
        this.price = price;
    }

    public List<Addon> getAddon() {
        return addon;
    }

    public void setAddon(List<Addon> addon) {
        this.addon = addon;
    }

    public List<Size> getSize() {
        return size;
    }

    public void setSize(List<Size> size) {
        this.size = size;
    }

    public Size getUserSelectedSize() {
        return userSelectedSize;
    }

    public void setUserSelectedSize(Size userSelectedSize) {
        this.userSelectedSize = userSelectedSize;
    }
}
