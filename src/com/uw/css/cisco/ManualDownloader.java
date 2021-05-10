package com.uw.css.cisco;

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
    public static String BASE_URL="https://www.cisco.com";
    public static String DOCUMENTATION_DIR="./output/documentation/cisco/";

    // sample links
    // https://www.webex.com/content/dam/wbx/us/documents/pdf/Webex-Calling-benefits.pdf
    //https://www.cisco.com/c/en/us/products/collateral/conferencing/webex-meeting-center/datasheet-c78-740969.html


    public static void main(String[] args) {
        getPackagesList();
    }

    private static void getPackagesList() {
        Document doc;
        try {
            //Get Document object after parsing the html from given url.
            doc = Jsoup.connect(BASE_URL+"/c/en/us/products/a-to-z-series-index.html#all").get();
            Elements elements = doc.select("div[id=res-listing-az] div[class=list] a");
            Integer count = 0;
            Integer failed = 0;
            for(Element element: elements){
                String productName = element.text();
                String url = element.attr("href");
                System.out.println("*******"+productName+"*******");
                try {
                    //get datasheet url
                    String datasheetUrlListingUrl = url.replaceFirst("index.html", "datasheet-listing.html");
                    Document documentationList = Jsoup.connect(BASE_URL + datasheetUrlListingUrl).get();
                    Elements datasheetUrlListing = documentationList.select("ul[class=listing] a");
                    if (datasheetUrlListing.size() > 0) {
                        String datasheetUrl = datasheetUrlListing.get(0).attr("href");
                        if(datasheetUrl.endsWith(".pdf")){
                            exportContentToTxtFile(BASE_URL + datasheetUrl,productName);
                        }else{
                            String doctext = Jsoup.connect(BASE_URL + datasheetUrl).get().select("div[class=col wide document]").get(0).text();
                            exportTextContentToTxtFile(doctext, productName);
                        }
                        count+=1;
                        continue;
                    }
                }catch (Exception e){
                    System.out.println("could not find datasheet. Looking for at a glance");
                }
                try {
                    //get at a glance url
                    String atAGlanceURLList = url.replaceFirst("index.html", "at-a-glance-listing.html");
                    Document documentationList = Jsoup.connect(BASE_URL + atAGlanceURLList).get();
                    Elements atAGlanceURL = documentationList.select("ul[class=listing] a");
                    if (atAGlanceURL.size() > 0) {
                        String datasheetUrl = atAGlanceURL.get(0).attr("href");
                        if(datasheetUrl.endsWith(".pdf")){
                            exportContentToTxtFile(BASE_URL + datasheetUrl,productName);
                        }else{
                        String doctext = Jsoup.connect(BASE_URL + datasheetUrl).get().select("div[class=col wide document]").get(0).text();
                        exportTextContentToTxtFile(doctext, productName);
                        }
                        count+=1;
                        continue;
                    }
                }catch (Exception e) {
                    System.out.println("could not find at a glance. Looking for pdf");
                }

//                    String doctext = documentation.select("div[id=page-content-wrapper]").get(0).text();

                failed+=1;

            }
            System.out.println("Downloaded "+count);
            System.out.println("Failed "+failed);

        } catch (IOException e) {
            e.printStackTrace();
        }

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
