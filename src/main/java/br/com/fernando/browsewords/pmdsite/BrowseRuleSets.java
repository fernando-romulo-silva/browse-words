package br.com.fernando.browsewords.pmdsite;

import static com.gargoylesoftware.htmlunit.BrowserVersion.FIREFOX_78;
import static com.gargoylesoftware.htmlunit.HttpMethod.GET;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.apache.commons.lang3.time.StopWatch;

import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.WebRequest;
import com.google.common.collect.ArrayListMultimap;

public class BrowseRuleSets {

    public BrowseRuleSets() throws MalformedURLException {
	
	// https://pmd.github.io/pmd-6.40.0/pmd_rules_java.html
	Logger.getLogger("com.gargoylesoftware").setLevel(Level.OFF); 
	
	final var webClient = new WebClient(FIREFOX_78);
	webClient.getOptions().setJavaScriptEnabled(false);
	webClient.getOptions().setCssEnabled(false);

	final var globalMap = ArrayListMultimap.<String, String>create();
	final var watch = new StopWatch();

	try (webClient) {
	    
	    // 
	    final var webRequest = new WebRequest(new URL("https://pmd.github.io/pmd-6.40.0/pmd_rules_java.html#best-practices"), GET);
	    
	    
	}
    }

}
