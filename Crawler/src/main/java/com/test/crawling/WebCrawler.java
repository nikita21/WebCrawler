package com.test.crawling;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.apache.log4j.Logger;

public class WebCrawler {
	
	private static final Logger logger = Logger.getLogger(WebCrawler.class);
	
	private static Set<String> urlVisited = new HashSet<String>();
	private static List<String> urlToVisit = new ArrayList<String>();
	private static String domainName;
	
	public static void main(String[] args)
	{
		String url = null;
		String keyword = "java";
		System.out.println(url);
		if(args.length == 2)
		{
			url = args[0];
			keyword = args[1];
			urlToVisit.add(url);
		}
		
		try {
			domainName = getDomainName(url);
		} catch (MalformedURLException e1) {
			e1.printStackTrace();
			logger.error("Error while fetching domain name fron url" + e1.getMessage());
		}
		
		Extractor extract = new Extractor();
		
		while(urlToVisit.size() > 0)
		{
			String requestUrl = getNextURL();
			if(!urlVisited.contains(requestUrl))
			{
				Set<String> discoveredURLs;
				urlVisited.add(requestUrl);
				if(requestUrl.contains(domainName))
				{
					try {
						discoveredURLs = extract.processWebPage(requestUrl, keyword);
						urlToVisit.addAll(discoveredURLs);
					} catch (IOException e) {
						e.printStackTrace();
						logger.error("Error while processing the current webpage" + e.getMessage());
					}
				}
				else
				{
					logger.info("Requested URL belong to other site");
				}
			}
		}
		
		Set<String> resultSet = extract.getOutputURLList();
		for(String outputURL : resultSet)
		{
			System.out.println(outputURL);
		}
	}
	
	private static String getNextURL()
	{
		String nextUrl = null;
		Iterator<String> iterator = urlToVisit.iterator();
		if(iterator.hasNext())
		{
			nextUrl = iterator.next();
			iterator.remove();
		}
		System.out.println("getNextURL: " + nextUrl);
		return nextUrl;
	}
	
	private static String getDomainName(String url) throws MalformedURLException
	{
		URL u = new URL(url);
		String hostName = u.getHost();
		logger.info("Domain Name: " + hostName);
		System.out.println("Domain Name: " + hostName);
		return hostName;
	}

}
