package com.uw.css.cve;

public class Product {
    String name;
    Integer productId;
    Integer vendorId;
    String url;
    Integer noVulnerabilities;

    public Product(String name, Integer productId, Integer vendorId, String url, Integer noVulnerabilities) {
        this.name = name;
        this.productId = productId;
        this.vendorId = vendorId;
        this.url = url;
        this.noVulnerabilities = noVulnerabilities;
    }

    @Override
    public String toString() {
        return  name + "," + url +"," + noVulnerabilities+","+productId+","+vendorId;
    }
}
