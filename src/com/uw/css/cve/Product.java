package com.uw.css.cve;

public class Product {
    String name;
    Integer productId;
    Integer vendorId;
    String url;
    Integer noVulnerabilities;
    String vendorName;
    String productType;

    public Product(String name, Integer productId, Integer vendorId, String url, Integer noVulnerabilities, String vendorName, String productType) {
        this.name = name;
        this.productId = productId;
        this.vendorId = vendorId;
        this.url = url;
        this.noVulnerabilities = noVulnerabilities;
        this.vendorId = vendorId;
        this.vendorName = vendorName;
        this.productType = productType;
    }

    @Override
    public String toString() {
        return  name + "," + url +"," + noVulnerabilities+","+productId+","+vendorId+","+vendorName+","+productType;
    }


}
