package com.uw.css.novell;

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
    public static String BASE_URL="https://www.novell.com";
    public static String DOCUMENTATION_DIR="./output/documentation/novell/";

    public static void main(String[] args) {
        getPackagesList();
    }

    private static void getPackagesList() {
        Document doc;
        Integer count = 0;
        Integer failed = 0;
        try {
            //Get Document object after parsing the html from given url.
            doc = Jsoup.connect(BASE_URL+"/documentation").get();
            Elements elements = doc.select("tr td[class=prodlistrow] a");
            for(Element element: elements){
                try{
                    String url = element.attr("href");
                    String productName = sanitizeProductName(element.text());
                    System.out.println("*****"+productName+"*****");
                    Document documentation = Jsoup.connect(BASE_URL+"/documentation/"+url).get();
                    Elements links = documentation.select("a[class=htmlpdf]");
                    for(Element link:links){
                        String linkUrl = link.attr("href");
                        if(linkUrl.endsWith(".pdf")){
                            if(linkUrl.contains("admin") || linkUrl.contains("Admin")){
                                exportContentToTxtFile(documentation.baseUri()+linkUrl,productName);
                                break;
                            }
                        }
                    }
                    
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
        PrintWriter out = new PrintWriter(new FileWriter(DOCUMENTATION_DIR+product+".pdf"));
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
            Files.copy(inputStream, Paths.get(DOCUMENTATION_DIR+product+".pdf"), StandardCopyOption.REPLACE_EXISTING);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
