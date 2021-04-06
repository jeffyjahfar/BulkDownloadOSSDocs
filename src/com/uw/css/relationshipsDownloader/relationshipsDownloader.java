package com.uw.css.relationshipsDownloader;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import java.util.ArrayList;
import java.io.IOException;

public class relationshipsDownloader {
    private static class Node {
        private String ID;
        private ArrayList<String> parents;
        public Node() {
            ID = "";
            parents = new ArrayList<>();
        }
        public ArrayList<String> getParents() {
            return parents;
        }
        public String getID() {
            return ID;
        }
        public void setParents(String s) { parents.add(s); }
        public void setID(String ID) { this.ID = ID; }
    }

    public static Node getRelationships(Node n, String ID) {
        try {
            String url = "https://cwe.mitre.org/data/definitions/" + ID + ".html";
            Document doc = Jsoup.connect(url).get();
            Elements rows = doc.select("div#relevant_table").get(0).select("tr");
            ArrayList<Element> relatives = new ArrayList<>();

            for (Element row : rows) {
                if (row.child(0).text().equals("ChildOf")) {
                    relatives.add(row);
                }
            }
            if (relatives.size() > 0) {
                for (Element row : relatives) {
                    getRelationships(n, row.child(2).text());
                }
            }
            else { n.setParents(ID); }
        }
        catch (IOException e) {
            e.printStackTrace();
        }
        return n;
    }

    public static void main(String[] args) {
        String ID = "707";
        Node n = new Node();
        n.setID(ID);
        getRelationships(n, ID);

        System.out.println(n.getParents());
    }
}
