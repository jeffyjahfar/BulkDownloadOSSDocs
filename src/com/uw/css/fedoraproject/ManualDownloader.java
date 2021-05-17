package com.uw.css.fedoraproject;

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
    public static String BASE_URL="https://docs.fedoraproject.org/en-US";
    public static String DOCUMENTATION_DIR="./output/documentation/fedoraproject/";

    public static void main(String[] args) {
        getPackagesList();
    }

    private static void getPackagesList() {
        Document doc;
        Integer count = 0;
        Integer failed = 0;
            try {
                doc = Jsoup.connect(BASE_URL+"/docs/").get();
                Elements elements = doc.select("div[class=\"homepage-section homepage-section-user-docs\"] a");
                for (int i = 0; i < elements.size(); i++) {
                    Element element = elements.get(i);
                    {
                        try{
                            String url = element.attr("href");
                            String productName = sanitizeProductName(element.select("h3").text());
                            System.out.println("*****"+productName+"*****");
                            url = url.replaceFirst("..",BASE_URL);
                            Elements navbar = Jsoup.connect(url).get().select("ul[class=nav-list] li[data-depth=3] a");
                            String doctext = "";
                            if(navbar.size()==0){
                                navbar = Jsoup.connect(url).get().select("ul[class=nav-list] li[data-depth=1] a");
                                for(Element navitem: navbar){
                                    try {
                                        doctext += Jsoup.connect(url + navitem.attr("href")).get().select("article[class=doc]").text();
                                    }catch (Exception e){

                                    }
                                }
                            }else{
                                for(Element navitem: navbar){
                                    try {
                                        doctext += Jsoup.connect(url + navitem.attr("href")).get().select("article[class=doc]").text();
                                    }catch (Exception e){

                                    }
                                }
                            }

                            exportTextContentToTxtFile(doctext,productName);
                            count+=1;
                        }catch (Exception e){
                            e.printStackTrace();
                            failed+=1;
                        }

                    }
                }

            } catch (Exception e) {
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

    public static String post2Parse(String command) {

        String outputString;

        System.out.println(command);
        String result="";
        Process curlProc;
        try {
            curlProc = Runtime.getRuntime().exec(command);

            DataInputStream curlIn = new DataInputStream(
                    curlProc.getInputStream());

            while ((outputString = curlIn.readLine()) != null) {
                result+=outputString;
            }

        } catch (IOException e1) {
            // TODO Auto-generated catch block
            e1.printStackTrace();
        }
        return result;
    }
}
