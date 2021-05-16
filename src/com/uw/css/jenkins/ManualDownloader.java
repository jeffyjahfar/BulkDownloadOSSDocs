package com.uw.css.jenkins;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;

public class ManualDownloader {
    public static String BASE_URL="https://plugins.jenkins.io/";
    public static String DOCUMENTATION_DIR="./output/documentation/jenkins/";

    public static void main(String[] args) {
//        getPackagesList();
        getMainUserGuide();
    }

    private static void getMainUserGuide() {
        Document doc;
        Integer count = 0;
        Integer failed = 0;
        try {
            doc = Jsoup.connect("https://www.jenkins.io/doc/").get();
            String doctext = "";
            doctext +=doc.select("div[class=col-lg-9]").text();
            Elements sections = doc.select("div[class=col-lg-3] a");
            for(Element section: sections){
                try{
                    String sectiontext = Jsoup.connect("https://www.jenkins.io"+section.attr("href")).get().select("div[class=col-lg-9]").text();
                    doctext = doctext+sectiontext;
                    doctext +="\n";
                }catch (Exception e){

                }
            }
            exportTextContentToTxtFile(doctext,"Jenkins");
            count+=1;

        } catch (Exception e) {
            e.printStackTrace();
        }


        System.out.println("Downloaded "+count);
        System.out.println("Failed "+failed);
    }

    private static void getPackagesList() {
        Document doc;
        Integer count = 0;
        Integer failed = 0;
            try {
                doc = Jsoup.connect("https://updates.jenkins-ci.org/download/plugins/").get();

                Elements elements = doc.select("tr td a");
                for (int i = 1; i < elements.size(); i++) {
                    Element element = elements.get(i);
                    {
                        try{
                            String url = element.attr("href");
                            String productName = sanitizeProductName(element.text());
                            System.out.println("*****"+productName+"*****");
                            String doctext = Jsoup.connect(BASE_URL+url).get().select("div[class=content]").get(0).text();
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
