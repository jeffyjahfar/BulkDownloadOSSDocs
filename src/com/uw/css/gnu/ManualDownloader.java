package com.uw.css.gnu;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

public class ManualDownloader {
    public static String GNU_URL="https://www.gnu.org/manual/manual.html";

    public static void main(String[] args) {
        getPackagesList();
    }

    private static void getPackagesList() {
        Document doc;
        List<String> packageUrls = new ArrayList<>();
        try {
            //Get Document object after parsing the html from given url.
            doc = Jsoup.connect(GNU_URL).get();
            Elements elements = doc.select("div[class=cat] a[href]");
            for(Element element: elements){
                packageUrls.add(element.baseUri());
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static String getFullDesc(String url) {
        String desc = "";
        Document doc;
        try {
            //Get Document object after parsing the html from given url.
            doc = Jsoup.connect(url).get();
            Elements elements = doc.select("div[class=heading],div[class=indent]");
            for(Element element: elements){
                desc += element.text();
                desc += "\n";
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
        return desc;
    }
}
