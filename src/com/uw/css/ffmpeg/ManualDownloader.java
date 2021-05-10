package com.uw.css.ffmpeg;

import com.uw.css.cve.Product;
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
    public static String BASE_URL="http://ffmpeg.org/";
    public static String DOCUMENTATION_DIR="./output/documentation/ffmpeg/";

    public static void main(String[] args) {
        getPackagesList();
    }

    private static void getPackagesList() {
        Document doc;
        try {
            //Get Document object after parsing the html from given url.
            doc = Jsoup.connect(BASE_URL+"documentation.html").get();
            Elements elements = doc.select("div[class=col-md-6] a");
            for(int i=0;i<22;i++){
                Element element = elements.get(i);
                String productName = element.text();
                String url = element.attr("href");
                try {
                    Document documentation = Jsoup.connect(BASE_URL + url).get();
                    String doctext = documentation.select("div[id=page-content-wrapper]").get(0).text();
                    exportTextContentToTxtFile(doctext,productName);
                    System.out.println("documentation downloaded for "+productName);
                }catch (Exception e){
                    e.printStackTrace();
                }
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void exportTextContentToTxtFile(String text,String product) {
        File directory = new File(DOCUMENTATION_DIR);
        if (! directory.exists()){
            directory.mkdir();
        }
        try {
            PrintWriter out = new PrintWriter(new FileWriter(DOCUMENTATION_DIR+product+".txt"));

            out.println(text);
            out.close();
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
