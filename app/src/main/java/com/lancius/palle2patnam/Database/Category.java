package com.lancius.palle2patnam.Database;

public class Category {

    //private variables
    int _id;
    String _cat_id;
    String _image;
    String _title;
    String _weight;
    String _qty;
    String _price;
    String _total_price;

    // Empty constructor
    public Category() {

    }

    //constructor
    public Category(String cat_id) {
        this._cat_id = cat_id;
    }

    public Category(String cat_id, String cat_price) {
        this._cat_id = cat_id;
        this._price = cat_price;
    }

    //constructor
    public Category(String cat_id, String qty, String price) {
        this._cat_id = cat_id;
        this._qty = qty;
        this._price = price;
    }

    //    constructor
    public Category(String cat_id, String qty, String price, String total_price) {
        this._cat_id = cat_id;
        this._qty = qty;
        this._price = price;
        this._total_price = total_price;
    }

    public Category(String cat_id, String qty, String price, String image, String weight) {
        this._cat_id = cat_id;
        this._qty = qty;
        this._price = price;
        this._image = image;
        this._weight = weight;
    }


    // constructor
    public Category(int id, String cat_id, String qty, String price, String image, String weight ) {
        this._id = id;
        this._cat_id = cat_id;
        this._image = image;
        this._weight = weight;
        this._qty = qty;
        this._price = price;
    }

    // constructor
    public Category(String cat_id, String qty, String price, String image, String weight, String total_price) {
        this._cat_id = cat_id;
        this._image = image;
        this._total_price = total_price;
        this._weight = weight;
        this._qty = qty;
        this._price = price;
    }

    public int getID() {
        return this._id;
    }

    public void setID(int id) {
        this._id = id;
    }

    public String getCatId() {
        return this._cat_id;
    }

    public void setCatID(String catid) {
        this._cat_id = catid;
    }

    public String getImage() {
        return this._image;
    }

    public void setImage(String image) {
        this._image = image;
    }

    public String getTitle() {
        return this._title;
    }

    public void setTitle(String title) {
        this._title = title;
    }

    public String getWeight() {
        return this._weight;
    }

    public void setWeight(String weight) {
        this._weight = weight;
    }

    public String getQty() {
        return this._qty;
    }

    public void setQty(String qty) {
        this._qty = qty;
    }

    public String getPrice() {
        return this._price;
    }

    public void setPrice(String price) {
        this._price = price;
    }

    public void setTotalPrice(String total_price) {
        this._total_price = total_price;
    }

    public String getTotalPrice() {
        return this.toString();
    }


}
