package com.uw.css.MicroFocus;

import java.io.BufferedInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.List;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

public class ManualDownloader {
    public static String MF_URL="https://www.microfocus.com/documentation/archive/";
    public static String BASE_URL="https://www.microfocus.com/";
    public static String DOCUMENTATION_DIR="./output/documentation/microfocus/";

    public static void main(String[] args) {
        getPackagesList();
    }

    private static void getPackagesList() {
        Document doc;
        int count = 0;
        int failed = 0;
        // Placeholders to help skip certain items
        String[] prevItem = {" ", " "};
        String[] currentItem = {" ", " "};

        try {
            //Get Document object after parsing the html from given url.
            doc = Jsoup.connect(MF_URL).get();
            Elements elements = doc.select("div[class=container] tr[class=language language-english] td a[class=language language-english]");    
            for(Element element: elements){
                prevItem = currentItem;
                currentItem = element.text().split(" ");

                if(!(prevItem[0].equalsIgnoreCase(currentItem[0]) && prevItem[1].equalsIgnoreCase(currentItem[1]))){
                String productName = element.text();
                String url = element.attr("href");
                try {
                    // Sometimes use base URL here
                    Document documentation = Jsoup.connect(MF_URL + url).get();
                    Elements links = documentation.getElementsByClass("language language-english");
                    String linkToKeep  = "";
                    for(Element link:links){
                        String linkUrl = link.attr("href");
                        if(linkUrl.endsWith(".pdf")){
                            if(linkUrl.contains("guide") || linkUrl.contains("Guide")){
                                if(linkToKeep.isEmpty() || linkUrl.contains("Developer's") || linkUrl.contains("user") ||
                                linkUrl.contains("administration")){
                                    linkToKeep = linkUrl;
                                }
                            }
                        }
                    }

                    if(!linkToKeep.isEmpty()){
                         exportContentToTxtFile(BASE_URL.substring(0,BASE_URL.length()-1)+linkToKeep,productName);
                         count += 1;
                    } 
                }catch (Exception e){
                    //e.printStackTrace();
                    System.out.println(productName);
                    failed += 1;
                }
            }
            }

        } catch (IOException e) {
            //e.printStackTrace();
            failed += 1;
        }

        System.out.println("Failed: " + failed);
        System.out.println("Succeeded: " + count);
    }

    public static void exportContentToTxtFile(String manualUrl,String product){
        InputStream inputStream = null;
        try {
            inputStream = new URL(manualUrl).openStream();
            Files.copy(inputStream, Paths.get(DOCUMENTATION_DIR+product+".pdf"), StandardCopyOption.REPLACE_EXISTING);
        } catch (IOException e) {
            //e.printStackTrace();
        }
    }
}
