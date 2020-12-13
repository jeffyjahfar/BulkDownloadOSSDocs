package com.uw.css.gnu;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;

public class ProductListCreator {
    public static List<Product> getProductList(String url,Integer vendorId){
        Document doc;
        List<Product> products = new ArrayList<>();
        try {
            //Get Document object after parsing the html from given url.
            doc = Jsoup.connect(url).get();
            Elements elements = doc.select("div[id=pagingb]").get(0).getElementsByTag("a");
            int pages = elements.size();
            for(int i=1;i<=pages;i++){
                String url1 = "https://www.cvedetails.com/product-list/product_type-/vendor_id-"+vendorId+"/firstchar-/page-"+i+"/products.html";
                doc = Jsoup.connect(url1).get();
                Elements productByVendor = doc.select("table[class=listtable] tr");
                int size = productByVendor.size();
                for(int j=2;j<size;j++){
                    Elements children = productByVendor.get(j).getElementsByTag("a");
                    String text = children.get(0).text();
                    String href = children.get(2).attr("href");
                    Integer productId = Integer.valueOf(href.split("/")[3].split("-")[1]);
                    Integer numVulnerabilities = Integer.valueOf(children.get(2).text());
                    Product product = new Product(text,productId,vendorId,href,numVulnerabilities);
                    products.add(product);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return products;
    }
    public static void main(String[] args) {
        String url = "https://www.cvedetails.com/product-list/vendor_id-452/Mozilla.html";
        String vendor = "Mozilla";
        Integer vendorId = 452;
        List<Product> products = getProductList(url,vendorId);
        exportProductsToCsv(products,vendor);
    }

    private static void exportProductsToCsv(List<Product> products,String vendor) {
        try {
            PrintWriter out = new PrintWriter(new FileWriter("./output/"+vendor+".csv"));

            out.println("Product,URL,Num_Vulnerabilities");
            for(Product product : products) {
                out.println(product.toString());
            }

            out.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
