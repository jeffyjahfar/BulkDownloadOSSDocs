package com.uw.css.ibm;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import javax.xml.crypto.Data;
import java.io.*;
import java.lang.reflect.Type;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;

public class ManualDownloader {
    public static String BASE_URL="https://www.ibm.com";
    public static String API_URL = "https://www.ibm.com/docs/api/v1/product/";
    public static String CONTENT_API="https://www.ibm.com/docs/api/v1/content/";
    public static String DOCUMENTATION_DIR="./output/documentation/ibm/";
    public static String SUBSECTION_BASE_URL = "https://www.ibm.com/docs/api/v1/content/STCTNLZ/com.ibm.storage.etc.doc/";
    public static String breadcrumb_URL = "https://www.ibm.com/docs/api/v1/breadcrumb/";
    public static void main(String[] args) {
        getPackagesList();
        getJsonBasedProductList();
    }

    private static void getJsonBasedProductList() {
        Integer count = 0;
        Integer failed = 0;
        InputStream jsoninput = null;
        try {
            jsoninput = new URL("https://www.ibm.com/docs/api/v1/products").openStream();
            Reader reader = new InputStreamReader(jsoninput, "UTF-8");
            JsonArray jsonArray = new Gson().fromJson(reader, JsonArray.class);
            for(JsonElement product: jsonArray){
                JsonObject product1 = product.getAsJsonObject();
                String productName = product1.get("name").getAsString();
                System.out.println("*****"+productName+"*****");
                String productId = product1.get("productUrlKey").getAsString();
                try{
                    InputStream productJsonInput = new URL(breadcrumb_URL + productId + "?lang=en").openStream();
                    Reader productReader = new InputStreamReader(productJsonInput, "UTF-8");
                    JsonObject productDocObject = new Gson().fromJson(productReader, JsonObject.class);
                    JsonElement jsonElement = productDocObject.get("breadcrumb").getAsJsonObject().get("topicBreadcrumb");
                    JsonArray topicBreadCrump = null;
                    if (jsonElement!=null){
                        topicBreadCrump = jsonElement.getAsJsonArray();
                    }

                    String doctext = "";
                    Boolean flag = false;
                    for(JsonElement jsonObject: topicBreadCrump){
                        String label = jsonObject.getAsJsonObject().get("label").getAsString();
                        if(label.equalsIgnoreCase("Overview") || label.equalsIgnoreCase("User Guide") || label.equalsIgnoreCase("Getting started") || label.contains("documentation") || label.contains("guide")){
                            flag = true;
                            String docURL = jsonObject.getAsJsonObject().get("href").getAsString();
                            Document document = Jsoup.connect(CONTENT_API + docURL).get();
                            doctext += document.select("p[class=shortdesc]").text();
                            Elements sublinks = document.select("div[class=related-links] a");
                            for(Element sublink: sublinks){
                                String[] split1 = docURL.split("/");
                                String suburl = docURL.replaceFirst(split1[split1.length-1],sublink.attr("href"));
                                doctext += Jsoup.connect(CONTENT_API+suburl).get().text();
                            }
                            if(flag){
                                break;
                            }
                        }
                    }
                    if(!flag){
                        throw new Exception("documentation not found");
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

    private static void getPackagesList() {
        Document doc;
        Integer count = 0;
        Integer failed = 0;
            try {
                File input = new File("src/com/uw/css/ibm/ibm.html");
                doc = Jsoup.parse(input, "UTF-8", BASE_URL);
                Elements elements = doc.select("div[class=ibmdocs-results] a");
                for (Element element: elements) {
                    try{
                        String url = element.attr("href");
                        String[] split = url.split("/");
                        String productId = split[split.length-1];
                        String productName = sanitizeProductName(element.text());
                        System.out.println("*****"+productName+"*****");
                        InputStream jsoninput = null;
                        try {
                            jsoninput = new URL(API_URL + productId + "?lang=en").openStream();
                        }catch (FileNotFoundException e){
                            try{
                                productId = split[split.length-2];
                                jsoninput = new URL(API_URL + productId + "?lang=en").openStream();
                            }catch (FileNotFoundException e1){
                                productId = split[split.length-2]+"/"+split[split.length-1];
                                productId = URLEncoder.encode(productId, StandardCharsets.UTF_8.toString());
                                jsoninput = new URL(API_URL + productId + "?lang=en").openStream();
                            }
                        }
                        Reader reader = new InputStreamReader(jsoninput, "UTF-8");
                        JsonObject productDoc = new Gson().fromJson(reader, JsonObject.class);
                        JsonElement jsonElement = productDoc.get("properties").getAsJsonObject().get("journey");
                        JsonArray jsonArray = null;
                        if (jsonElement!=null){
                            jsonArray = jsonElement.getAsJsonArray();
                        }else{
                            productId = productDoc.get("taxonomy").getAsJsonObject().get("versions").getAsJsonArray().get(0).getAsJsonObject().get("productUrlKey").getAsString();
                            productId = URLEncoder.encode(productId, StandardCharsets.UTF_8.toString());
                            jsoninput = new URL(API_URL + productId+"?lang=en").openStream();
                            reader = new InputStreamReader(jsoninput, "UTF-8");
                            productDoc = new Gson().fromJson(reader, JsonObject.class);
                            jsonElement = productDoc.get("properties").getAsJsonObject().get("journey");
                            jsonArray = jsonElement.getAsJsonArray();
                        }

                        String doctext = "";
                        Boolean flag = false;
                        for(JsonElement jsonObject: jsonArray){
                            String label = jsonObject.getAsJsonObject().get("label").getAsString();
                            if(label.equals("Overview") || label.equals("User Guide") || label.equals("Getting started")){
                                flag = true;
                                String docURL = jsonObject.getAsJsonObject().get("href").getAsString();
                                Document document = Jsoup.connect(CONTENT_API + docURL).get();
                                doctext += document.select("p[class=shortdesc]").text();
                                Elements sublinks = document.select("div[class=related-links] a");
                                for(Element sublink: sublinks){
                                    String[] split1 = docURL.split("/");
                                    String suburl = docURL.replaceFirst(split1[split1.length-1],sublink.attr("href"));
                                    doctext += Jsoup.connect(CONTENT_API+suburl).get().text();
                                }
                            }
                        }
                        if(!flag){
                             throw new Exception("documentation not found");
                        }
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
