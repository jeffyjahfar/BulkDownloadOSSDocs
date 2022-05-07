package com.uw.css.redhat;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
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
    public static String BASE_URL="https://access.redhat.com";
    public static String DOCUMENTATION_DIR="./output/documentation/redhat/";

    public static void main(String[] args) {
        getPackagesList();
    }

    private static void getPackagesList() {
        Document doc;
        Integer count = 0;
        Integer failed = 0;
            try {
                doc = Jsoup.connect(BASE_URL+"/products/").get();
                Elements elements = doc.select("a[class=\"product-docs\"]");
                for (Element element: elements) {
                        try{
                            String url = element.attr("href");
                            if(!url.startsWith("http")){
                                url = BASE_URL+url;
                            }
                            InputStream jsoninput = new URL( url).openStream();
                            Reader reader = new InputStreamReader(jsoninput, "UTF-8");
                            JsonObject productDoc = new Gson().fromJson(reader, JsonObject.class);
                            String productName = sanitizeProductName(productDoc.get("name").getAsString());
                            System.out.println("*****"+productName+"*****");
                            String doctext = productDoc.get("description").getAsString();
                            doctext += Jsoup.connect(productDoc.get("homepage").getAsString()).get().text();

                            exportTextContentToTxtFile(doctext,productName);
                            count+=1;
                        }catch (Exception e){
                            e.printStackTrace();
                            failed+=1;
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
