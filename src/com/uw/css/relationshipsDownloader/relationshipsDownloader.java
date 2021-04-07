package com.uw.css.relationshipsDownloader;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Scanner;

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
            if (doc.select("div#relevant_table").size() > 0) {
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
                } else if (!n.getID().equals(ID)) {
                    if (!n.getParents().contains(ID)) {
                        n.setParents(ID);
                    }
                } else {
                    n.setParents("0");
                }
            }
            else {
                n.setParents("0");
            }
        }
        catch (IOException e) {
            e.printStackTrace();
        }
        return n;
    }

    public static HashMap<String, ArrayList<String>> categorizeData(Node n) {
        HashMap<String, ArrayList<String>> data = new HashMap<>();
        data.put("ID", new ArrayList<>(Arrays.asList(n.getID())));
        data.put("Ancestors", n.getParents());
        return data;
    }

    public static void writeToCSV(ArrayList<HashMap<String, ArrayList<String>>> data) throws FileNotFoundException {
        PrintWriter p = new PrintWriter("src\\com\\uw\\css\\relationshipsDownloader\\CWE_DATA");
        StringBuilder sb = new StringBuilder();

        sb.append("ID, Parent");
        sb.append("\n");

        for (HashMap<String, ArrayList<String>> h : data) {
            String ID = h.get("ID").get(0);
            String parentList = h.get("Ancestors").toString();
            String parents = parentList.substring(1, parentList.length() - 1);
            sb.append(ID);
            sb.append(',');
            sb.append(parents);
            sb.append('\n');
        }
        p.write(sb.toString());
        p.flush();
        p.close();
    }

    public static void main(String[] args) throws IOException {
        ArrayList<HashMap<String, ArrayList<String>>> relationshipData = new ArrayList<>();
        File IDs = new File("src\\com\\uw\\css\\relationshipsDownloader\\CWE_IDs");
        Scanner idReader = new Scanner(IDs);

        while (idReader.hasNextLine()) {
            String ID = idReader.nextLine();
            System.out.println(ID);
            Node n = new Node();
            n.setID(ID);
            Node data = getRelationships(n, ID);
            relationshipData.add(categorizeData(data));
        }

        writeToCSV(relationshipData);

        idReader.close();
    }
}
