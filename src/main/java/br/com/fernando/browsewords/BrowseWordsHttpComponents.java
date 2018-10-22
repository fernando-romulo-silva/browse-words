package br.com.fernando.browsewords;

import java.util.ArrayList;
import java.util.List;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.cookie.Cookie;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.protocol.HTTP;

import javax.xml.namespace.NamespaceContext;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;

import org.w3c.dom.Document;


// https://www.baeldung.com/java-xpath
// https://examples.javacodegeeks.com/core-java/xml/xpath/java-xpathfactory-example/
public class BrowseWordsHttpComponents {

    public static void main(String[] args) throws Exception {
	
	
	
	
	FileInputStream fileIS = new FileInputStream(this.getFile());
	
	DocumentBuilderFactory builderFactory = DocumentBuilderFactory.newInstance();
	DocumentBuilder builder = builderFactory.newDocumentBuilder();
	Document xmlDocument = builder.parse(fileIS);
	XPath xPath = XPathFactory.newInstance().newXPath();
	String expression = "/Tutorials/Tutorial";
	nodeList = (NodeList) xPath.compile(expression).evaluate(xmlDocument, XPathConstants.NODESET);

	
	
	
	
	
	HttpClientBuilder httpClientBuilder = HttpClientBuilder.create();

	DefaultHttpClient httpclient = new DefaultHttpClient();

	HttpGet httpget = new HttpGet("https://portal.sun.com/portal/dt");

	HttpResponse response = httpclient.execute(httpget);
	HttpEntity entity = response.getEntity();

	System.out.println("Login form get: " + response.getStatusLine());
	if (entity != null) {
	    entity.consumeContent();
	}
	System.out.println("Initial set of cookies:");
	List<Cookie> cookies = httpclient.getCookieStore().getCookies();
	if (cookies.isEmpty()) {
	    System.out.println("None");
	} else {
	    for (int i = 0; i < cookies.size(); i++) {
		System.out.println("- " + cookies.get(i).toString());
	    }
	}

	HttpPost httpost = new HttpPost("https://portal.sun.com/amserver/UI/Login?" + "org=self_registered_users&" + "goto=/portal/dt&" + "gotoOnFail=/portal/dt?error=true");

	List<NameValuePair> nvps = new ArrayList<NameValuePair>();
	nvps.add(new BasicNameValuePair("IDToken1", "username"));
	nvps.add(new BasicNameValuePair("IDToken2", "password"));

	httpost.setEntity(new UrlEncodedFormEntity(nvps, HTTP.UTF_8));

	response = httpclient.execute(httpost);
	entity = response.getEntity();

	System.out.println("Login form get: " + response.getStatusLine());
	if (entity != null) {
	    entity.consumeContent();
	}

	System.out.println("Post logon cookies:");
	cookies = httpclient.getCookieStore().getCookies();
	if (cookies.isEmpty()) {
	    System.out.println("None");
	} else {
	    for (int i = 0; i < cookies.size(); i++) {
		System.out.println("- " + cookies.get(i).toString());
	    }
	}

    }
}
