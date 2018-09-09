package br.com.fernando.browsewords;

import com.gargoylesoftware.htmlunit.BrowserVersion;
import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.html.HtmlPage;

public class Main {

    
    
    public static void main(String[] args) throws Exception {
	
	@SuppressWarnings("resource")
	final WebClient webClient = new WebClient(BrowserVersion.FIREFOX_60);
	
	final HtmlPage page = webClient.getPage("https://quizlet.com/286499785/english-words-0016-flash-cards/");
	
	// UIDiv SetPage-termsWrapper
	// SetPageTerm-wordText
    }
}
