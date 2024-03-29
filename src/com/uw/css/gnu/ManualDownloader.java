package com.uw.css.gnu;

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
    public static String GNU_URL="https://www.gnu.org/manual/manual.html";
    public static String BASE_URL="https://www.gnu.org";
    public static String DOCUMENTATION_DIR="./output/documentation/gnu";

    public static void main(String[] args) {
        getPackagesList();
    }

    private static void getPackagesList() {
        Document doc;
        try {
            //Get Document object after parsing the html from given url.
            doc = Jsoup.connect(GNU_URL).get();
            Elements elements = doc.select("div[class=cat] dt a");
            for(Element element: elements){
                String productName = element.text();
                String url = element.attr("href");
                try {
                    Document documentation = Jsoup.connect(BASE_URL + url).get();
                    Elements links = documentation.getElementsByTag("a");
                    for(Element link:links){
                        String linkUrl = link.attr("href");
                        if(linkUrl.endsWith(".txt")){
                            exportContentToTxtFile(BASE_URL+url+linkUrl,productName);
                        }
                    }
                }catch (Exception e){

                }
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void exportContentToTxtFile(String manualUrl,String product){
        InputStream inputStream = null;
        try {
            inputStream = new URL(manualUrl).openStream();
            Files.copy(inputStream, Paths.get(DOCUMENTATION_DIR+product+".txt"), StandardCopyOption.REPLACE_EXISTING);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
