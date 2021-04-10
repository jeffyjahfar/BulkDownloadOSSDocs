package com.uw.css.cve;

public class Product {
    String name;
    Integer productId;
    Integer vendorId;
    String url;
    Integer noVulnerabilities;
    String vendor;

    public Product(String name, Integer productId, Integer vendorId, String url, Integer noVulnerabilities, String vendor) {
        this.name = name;
        this.productId = productId;
        this.vendorId = vendorId;
        this.url = url;
        this.noVulnerabilities = noVulnerabilities;
        this.vendor = vendor;
    }

    @Override
    public String toString() {
        return  name + "," + url +"," + noVulnerabilities+","+productId+","+vendorId;
    }
}
