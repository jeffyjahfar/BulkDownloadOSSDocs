package com.uw.css.microsoft;

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
    public static String BASE_URL="https://docs.microsoft.com";
    public static String DOCUMENTATION_DIR="./output/documentation/microsoft/";


    public static void main(String[] args) {
        getPackagesList();
    }

    private static void getPackagesList() {
        Document doc;
        try {
            //Get Document object after parsing the html from given url.
            doc = Jsoup.connect(BASE_URL+"/en-us/documentation/").get();
            Elements elements = doc.select("section[id=listings] a");
            Integer count = 0;
            Integer failed = 0;
            for(Element element: elements){
                boolean flag = false;
                String productName = sanitizeProductName( element.text());
                String url = element.attr("href");
                System.out.println("*******"+productName+"*******");
                try {
                    //get datasheet url
                    Document documentation = Jsoup.connect(BASE_URL + url).get();
                    Elements divs = documentation.select("section[id=highlighted-content] div[class=columns]");
                    for (Element div : divs) {
                        String header = div.getElementsByTag("p").get(0).text();
                        if (header.equals("Overview")) {
                            Element link = div.select("h3 a").get(0);
                            String h3href = link.attr("href");
                            if(!h3href.startsWith("http")){
                                h3href = BASE_URL + url + h3href;
                            }
                            try{
                                Document text = Jsoup.connect( h3href).get();
                                String doctext = text.select("div[id=main-column]").text();
                                exportTextContentToTxtFile(doctext, productName);
                                flag = true;
                                count += 1;
                                break;
                            }catch (Exception e){
                                h3href = BASE_URL+link.attr("href");
                                Document text = Jsoup.connect( h3href).get();
                                String doctext = text.select("div[id=main-column]").text();
                                exportTextContentToTxtFile(doctext, productName);
                                flag = true;
                                count += 1;
                                break;
                            }

                        }
                    }
                    if (!flag) {
                        divs = documentation.select("section[id=conceptual-content] div[class=box]");
                        for (Element div : divs) {
                            String header = div.getElementsByTag("p").get(0).text();
                            if (header.equals("get-started")) {
                                Element link = div.select("span a").get(0);
                                String h3href = link.attr("href");
                                Document text = Jsoup.connect(BASE_URL + url + h3href).get();
                                String doctext = text.select("div[id=main-column]").text();
                                exportTextContentToTxtFile(doctext, productName);
                                count += 1;
                                break;
                                    // System.out.println("could not find overview");

                            }

                        }
                    }
                } catch (Exception e){
                   e.printStackTrace();
                    failed+=1;
                }              

            }
            System.out.println("Downloaded "+count);
            System.out.println("Failed "+failed);

        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    public static String sanitizeProductName(String productName){
        String s = productName.replaceAll("[.]", "_");
        s = s.replaceAll("/","_");
        return s;
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
