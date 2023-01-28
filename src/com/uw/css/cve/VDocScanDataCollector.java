package com.uw.css.cve;

import com.uw.css.relationshipsDownloader.RelationshipsDownloader;

import java.io.IOException;

public class VDocScanDataCollector {
    public static void main(String[] args) throws IOException {
        if(args.length < 1){
            System.out.println("Argument missing!!");
            System.out.println("Options:");
            System.out.println("--extract-products \t to extract list of all products listed on CVE");
            System.out.println("--extract-vulnerabilities \t to extract list of vulnerability reports listed on CVE");
            System.out.println("--extract-vulnerability-classification \t to extract relationships between vulnerability classes");
            return;
        }
        switch (args[0]){
            case "--help":
                System.out.println("Options:");
                System.out.println("--extract-products \t to extract list of all products listed on CVE");
                System.out.println("--extract-vulnerabilities \t to extract list of vulnerability reports listed on CVE");
                System.out.println("--extract-vulnerability-classification \t to extract relationships between vulnerability classes");
                break;
            case "--extract-products":
                ProductListCreator.run();
                break;
            case "--extract-vulnerabilities":
                VulnerabilityMapCreator.run();
                break;
            case "--extract-vulnerability-classification":
                RelationshipsDownloader.run();
                break;
            default:
                System.out.println("Invalid Argument!");
                System.out.println("Options:");
                System.out.println("--extract-products \t to extract list of all products listed on CVE");
                System.out.println("--extract-vulnerabilities \t to extract list of vulnerability reports listed on CVE");
                System.out.println("--extract-vulnerability-classification \t to extract relationships between vulnerability classes");
        }
    }
}
