package com.test.crawling;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.util.HashSet;
import java.util.Set;

import org.apache.log4j.Logger;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

/* Extracts the data from the list of URLs. It will also extract all the links on a web page 
 * and feed them into the Extractor to get the specific data that we want.
 */
public class Extractor {

	private static final Logger logger = Logger.getLogger(Extractor.class);
	
	private Set<String> discoveredURLList = new HashSet<String>();
	private Set<String> outputURL = new HashSet<String>();
	
	public Extractor()
	{
		
	}
	
	public Set<String> getOutputURLList()
	{
		return this.outputURL;
	}
	
	/* check whether the given web page is html or not. If it is, then parse the page and find all other urls 
	 * discovered on that page.
	 * While parsing the page, check for the given keyword if it present or not.
	 * If the required keyword is present, save the url in a output list.
	 */
	public Set<String> processWebPage(String requestUrl, String keyword) throws IOException
	{
		URL url = new URL(requestUrl);
		System.out.println("Processing Webpage");
		URLConnection conn = url.openConnection();
		String contentType = conn.getContentType();
		System.out.println("Content-Type: " + contentType);
		if (contentType.contains("text/html") || contentType.contains("text/xhtml")) 
		{
			Document document = Jsoup.connect(requestUrl).get();

			if (document.text().contains(keyword)) {
				outputURL.add(requestUrl);
			}
			Elements urls = document.select("a[href]");
			for (Element elem : urls) {
				String discoveredUrl = elem.attr("abs:href");
				System.out.println("dis url: " + discoveredUrl);
				logger.info("Discovered URLs : " + discoveredUrl);
				discoveredURLList.add(discoveredUrl);
			}
		}
		return discoveredURLList;
	}
	
	/* set maxRedirectCount to avoid infinite redirect loops. 
	 * SET maxRedirectCount to 3. 
	 */
	private String getContentType(String requestUrl, int maxRedirectCount) throws IOException
	{
		URL url  = new URL(requestUrl);
		HttpURLConnection connection = (HttpURLConnection) url.openConnection();
		connection.setRequestMethod("HEAD");
		if(isRedirect(connection.getResponseCode()) && maxRedirectCount > 0)
		{
			String redirectUrl = connection.getHeaderField("Location");
			return getContentType(redirectUrl, --maxRedirectCount);
		}
		String contentType = connection.getContentType();
		return contentType;
	}

	private boolean isRedirect(int statusCode)
	{
		if(statusCode != HttpURLConnection.HTTP_OK)
		{
			if(statusCode == HttpURLConnection.HTTP_MOVED_TEMP || statusCode == HttpURLConnection.HTTP_MOVED_PERM ||
					statusCode == HttpURLConnection.HTTP_SEE_OTHER)
			{
				return true;
			}
		}
		return false;
	}
}
