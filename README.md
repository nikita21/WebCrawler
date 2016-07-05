# WebCrawler
 Crawling the current web page based on Robots.txt file.
 Extracting the html document and check whether the required keyword is present or not.
 If present, save it in the output list and traverse the next URL in the list.
 If not, get the next URL from the list and parse the html page using Jsoup library.
 

 Compile the maven project using the following command : mvn clean install
 Run the main class using : mvn exec:java
