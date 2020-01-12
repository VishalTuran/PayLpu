package com.csemaster.paylpu.Modals;

public class CartModel {
    String name;
    int quantity;
    int price;
    String shopName;
    String uId;
    String imageUrl;

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    //Empty constuctor for firebase
    public CartModel() {

    }

    public CartModel(String name, int quantity, int price, String shopName, String uId,String imageUrl) {
        this.name = name;
        this.quantity = quantity;
        this.price = price;
        this.shopName = shopName;
        this.uId = uId;
        this.imageUrl=imageUrl;
    }

    public String getShopName() {
        return shopName;
    }

    public void setShopName(String shopName) {
        this.shopName = shopName;
    }

    public String getuId() {
        return uId;
    }

    public void setuId(String uId) {
        this.uId = uId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public int getPrice() {
        return price;
    }

    public void setPrice(int price) {
        this.price = price;
    }
}
