package com.handicape.MarketCreators;

import java.io.Serializable;

public class Product implements Serializable{
    private String name_product;
    private String price_product;
    private String number_of_product;
    private String name_owner_product;
    private String address_owner_product;
    private String details_product;
    private String url_image_product;

    public Product(String name_product, String price_product, String number_of_product,
                   String name_owner_product, String address_owner_product, String details_product
            , String url_image_product) {
        this.name_product = name_product;
        this.price_product = price_product;
        this.number_of_product = number_of_product;
        this.name_owner_product = name_owner_product;
        this.address_owner_product = address_owner_product;
        this.details_product = details_product;
        this.url_image_product = url_image_product;
    }

    public String getName_product() {
        return name_product;
    }

    public void setName_product(String name_product) {
        this.name_product = name_product;
    }

    public String getPrice_product() {
        return price_product;
    }

    public void setPrice_product(String price_product) {
        this.price_product = price_product;
    }

    public String getNumber_of_product() {
        return number_of_product;
    }

    public void setNumber_of_product(String number_of_product) {
        this.number_of_product = number_of_product;
    }

    public String getName_owner_product() {
        return name_owner_product;
    }

    public void setName_owner_product(String name_owner_product) {
        this.name_owner_product = name_owner_product;
    }

    public String getAddress_owner_product() {
        return address_owner_product;
    }

    public void setAddress_owner_product(String address_owner_product) {
        this.address_owner_product = address_owner_product;
    }

    public String getDetails_product() {
        return details_product;
    }

    public void setDetails_product(String details_product) {
        this.details_product = details_product;
    }

    public String getUrl_image_product() {
        return url_image_product;
    }

    public void setUrl_image_product(String url_image_product) {
        this.url_image_product = url_image_product;
    }

}
