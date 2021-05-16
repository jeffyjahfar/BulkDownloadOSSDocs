package com.uw.css.google;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.*;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;

public class ManualDownloader {
    public static String BASE_URL="https://developers.google.com/";
    public static String DOCUMENTATION_DIR="./output/documentation/google/";

    public static void main(String[] args) {
        getPackagesList();
    }

    private static void getPackagesList() {
        Document doc;
        Integer count = 0;
        Integer failed = 0;
        try {
            //Get Document object after parsing the html from given url.
            File input = new File("src/com/uw/css/google/google.html");
            doc = Jsoup.parse(input, "UTF-8", BASE_URL);
//            doc = Jsoup.connect(BASE_URL+"products").get();
            Elements elements = doc.select("devsite-filter a[data-category=Product Catalog]");
            for(Element element: elements){
                try{
                    String url = element.attr("href");
                    String productName = sanitizeProductName(element.text());
                    System.out.println("*****"+productName+"*****");
                    Document guide = Jsoup.connect(url).get();
                    Elements sections = guide.select("a[class=devsite-nav-title]");
                    String doctext = "";
                    doctext +=guide.select("article[class=devsite-article]").text();
                    for(Element section: sections){
                        try{
                            String sectiontext = Jsoup.connect(section.attr("href")).get().select("article[class=devsite-article]").text();
                            doctext = doctext+sectiontext;
                            doctext +="\n";
                        }catch (Exception e){

                        }
                    }
                    exportTextContentToTxtFile(doctext,productName);
                    count+=1;
                }catch (Exception e){
                    e.printStackTrace();
                    failed+=1;
                }

            }

        } catch (IOException e) {
            e.printStackTrace();
        }
        System.out.println("Downloaded "+count);
        System.out.println("Failed "+failed);
    }

    public static String sanitizeProductName(String s){
        return s.replaceAll("/","_");
    }

    public static void exportTextContentToTxtFile(String text,String product) throws IOException {
        File directory = new File(DOCUMENTATION_DIR);
        if (! directory.exists()){
            directory.mkdir();
        }
        PrintWriter out = new PrintWriter(new FileWriter(DOCUMENTATION_DIR+product+".txt"));
        out.println(text);
        out.close();
    }

    public static void exportContentToTxtFile(String manualUrl,String product){
        File directory = new File(DOCUMENTATION_DIR);
        if (! directory.exists()){
            directory.mkdir();
        }
        InputStream inputStream = null;
        try {
            inputStream = new URL(manualUrl).openStream();
            Files.copy(inputStream, Paths.get(DOCUMENTATION_DIR+product+".txt"), StandardCopyOption.REPLACE_EXISTING);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
