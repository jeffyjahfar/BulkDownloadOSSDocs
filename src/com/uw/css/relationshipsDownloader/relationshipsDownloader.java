package com.uw.css.relationshipsDownloader;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.*;
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
            if (doc.select("div#relevant_table").size() > 0) {
                Elements rows = doc.select("div#relevant_table").get(0).select("tr");
                ArrayList<Element> relatives = new ArrayList<>();

                for (Element row : rows) {
                    if (row.child(0).text().equals("ChildOf")) { relatives.add(row); }
                }
                if (relatives.size() > 0) {
                    for (Element row : relatives) {
                        getRelationships(n, row.child(2).text());
                    }
                }
                else if (!n.getID().equals(ID)) {
                    if (!n.getParents().contains(ID)) { n.setParents(ID); }
                }
                else { n.setParents("0"); }
            }
            else { n.setParents("0"); }
        }
        catch (IOException e) {
            e.printStackTrace();
        }
        return n;
    }

    public static HashMap<String, ArrayList<String>> categorizeData(Node n) {
        HashMap<String, ArrayList<String>> data = new HashMap<>();
        data.put("ID", new ArrayList<>(Collections.singletonList(n.getID())));
        data.put("Ancestors", n.getParents());
        return data;
    }

    // Writes data to csv file at specified path
    public static void writeToCSV(ArrayList<HashMap<String, ArrayList<String>>> data, int numRows) throws FileNotFoundException {
        PrintWriter p = new PrintWriter("com/uw/css/relationshipsDownloader/freshPillarRelationships.csv");
        StringBuilder sb = new StringBuilder();

        sb.append("ID,");
        for (int i = 1; i < numRows; i++) {
            sb.append("Parent ");
            sb.append(i);
            sb.append(',');
        }
        sb.append("Parent ");
        sb.append(numRows);
        sb.append("\n");

        for (HashMap<String, ArrayList<String>> h : data) {
            String ID = h.get("ID").get(0);
            String parentList = h.get("Ancestors").toString();
            String parents = parentList.substring(1, parentList.length() - 1);
            int numParents = h.get("Ancestors").size();
            sb.append(ID);
            sb.append(',');
            sb.append(parents);
            for (int i = 0; i < numRows - numParents - 1; i++) {
                sb.append(',');
                sb.append("0");
            }
            if (numRows - numParents > 0) { sb.append(",0"); }
            sb.append('\n');
        }
        p.write(sb.toString());
        p.flush();
        p.close();
    }

    // Reads cwe ids from a text file at specified path.
    // Assumes ids are on separate lines with no empty lines in the file.
    public static void main(String[] args) throws IOException {
        ArrayList<HashMap<String, ArrayList<String>>> relationshipData = new ArrayList<>();
        File IDs = new File("com/uw/css/relationshipsDownloader/CWE_IDs");
        Scanner idReader = new Scanner(IDs);
        int maxNumberOfParents = 0;

        while (idReader.hasNextLine()) {
            String ID = idReader.nextLine();
            Node n = new Node();
            n.setID(ID);
            Node data = getRelationships(n, ID);
            int numParents = data.getParents().size();
            if (numParents > maxNumberOfParents) { maxNumberOfParents = numParents; }
            relationshipData.add(categorizeData(data));
        }
        writeToCSV(relationshipData, maxNumberOfParents);
        idReader.close();
    }
}
