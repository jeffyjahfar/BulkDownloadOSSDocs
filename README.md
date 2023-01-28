# BulkDownloadOSSDocs

## 1. To download data from CVE, there are two steps. First, you download a list of all products under a vendor. Second, the program uses that list to download all vulnerabilities in those products. 

### A. To download the list of products by a vendor, execute the Main method of com/uw/css/cve/ProductListCreator.java
In the Main method, the String variable "vendor" needs to be initialized with the name of the Vendor as per the CVE Details website. All the vendor names can be browsed through on this link: https://www.cvedetails.com/vendor.php

image.png

### B. To download all vulnerabilities of all products in the list created in Step A, execute the Main method of com/uw/css/cve/VulnerabilityMapCreator.java

In the Main method, the following three variables are to be set:
String url = "https://www.cvedetails.com/product-list/vendor_id-20/Novell.html";
String vendor = "Novell";
Integer vendorId = 20;

Vendor name is the same as used in Step A
vendorId is the integer identifier for the vendor that is visible in the URL as vendor_id

image.png

URL is the link to the Products listing under the respective vendor page (for example, in the above image, by clicking on Products right below the heading A-shop: Vulnerability Statistics)
image.png

## 2. To download documentation files:
This needs to be customized for each vendor you are trying to download documentations from. For example, for GNU, execute the Main method in com/uw/css/gnu/ManualDownloader.java
The URL variables needed for this specific program were the base URL and the GNU Manuals page URL. 

Documentation downloader logic for each vendors is maintained in a different directory under com.uw.css. 

To run the jar file if the latest vulnerability reports or product metadata needs to be extracted from CVE:

1. Install Java (openjdk version "14.0.1")
2. Ensure Java is added to the system path and the command "java -version" prints out the above version of Java
3. Download the BulkDownloadOSSDocs.jar from the archive or the latest version from Github
4. To extract product metadata: Execute command "java -jar BulkDownloadOSSDocs.jar --extract-products". This will create a directory "output" next to the jar file with a subdirectory vendors. The vendors subdirectory will contain a CSV file for each vendorID containing list of products from that vendor.

Expected directory structure:
|-BulkDownloadOSSDocs.jar
|-output
|--vendors
|---1.csv
|---6.csv


5. To extract latest vulnerability reports: Execute command "java -jar BulkDownloadOSSDocs.jar --extract-vulnerabilities". This will create a subdirectory "vulnerabilities" under the directory "output". The "vulnerabilities" directory will have a subdirectory per vendor containing a CSV file per product of the vendor. Each CSV file will contain all vulnerability reports for the specific product.

Expected directory structure:
|-BulkDownloadOSSDocs.jar
|-output
|--vulnerabilities
|---6
|----2598.csv
|----4708.csv

To Modify the source code:
The relevant classes for each functionality are as follows:

1. Entry point of the JAR - com.uw.css.cve.VDocScanDataCollector
2. Class for extracting Product and Vendor Metadata -  com.uw.css.cve.ProductListCreator
3. Class for extracting Vulnerability Reports of a specific Product(s)/Vendor(s) - com.uw.css.cve.VulnerabilityMapCreator
4. Specification Document Downloader: The documentation downloader is different for each vendor. Under com.uw.css package, each vendor has a module Containing a ManualDownloader.java file
   For example, to customize the downloader for GNU website, modify com.uw.css.gnu.ManualDownloader class