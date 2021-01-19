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
