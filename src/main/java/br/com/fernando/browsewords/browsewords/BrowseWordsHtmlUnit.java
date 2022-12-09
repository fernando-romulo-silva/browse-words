package br.com.fernando.browsewords.browsewords;

import static br.com.fernando.browsewords.util.BrowseWordsUtils.URL;
import static br.com.fernando.browsewords.util.BrowseWordsUtils.printRepeatedWordsInSite;
import static br.com.fernando.browsewords.util.BrowseWordsUtils.printWordsNotInSite;
import static com.gargoylesoftware.htmlunit.BrowserVersion.FIREFOX;
import static com.gargoylesoftware.htmlunit.HttpMethod.GET;
import static java.util.concurrent.TimeUnit.MILLISECONDS;
import static java.util.concurrent.TimeUnit.SECONDS;
import static java.util.stream.Collectors.toList;
import static org.apache.commons.lang3.RegExUtils.removePattern;
import static org.apache.commons.lang3.StringUtils.containsIgnoreCase;
import static org.apache.commons.lang3.StringUtils.containsNone;
import static org.apache.commons.lang3.StringUtils.isNotBlank;
import static org.apache.commons.lang3.StringUtils.trim;

import java.net.URL;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.apache.commons.lang3.time.StopWatch;

import com.gargoylesoftware.htmlunit.UnexpectedPage;
import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.WebRequest;
import com.gargoylesoftware.htmlunit.html.HtmlPage;
import com.gargoylesoftware.htmlunit.html.HtmlSpan;
import com.google.common.collect.ArrayListMultimap;

import br.com.fernando.browsewords.util.BrowseWordsUtils;

public class BrowseWordsHtmlUnit {

    public static void main(String[] args) throws Exception {

	Logger.getLogger("com.gargoylesoftware").setLevel(Level.OFF); 
	
	final var webClient = new WebClient(FIREFOX);
	webClient.getOptions().setJavaScriptEnabled(false);
	webClient.getOptions().setCssEnabled(false);

	final var globalMap = ArrayListMultimap.<String, String>create();
	final var watch = new StopWatch();

	try (webClient) {

	    final var webRequest = new WebRequest(new URL(URL), GET);
	    webRequest.setAdditionalHeader("Accept", "*/*");
	    webRequest.setAdditionalHeader("Content-Type", "application/json");

	    watch.start();

	    final var jsonString = webClient.<UnexpectedPage>getPage(webRequest) //
		    .getWebResponse() //
		    .getContentAsString();

	    watch.stop();
	    System.out.println("URL principal, Time Elapsed: " + watch.getTime(MILLISECONDS) + " ms");
	    watch.reset();

	    watch.start();

	    final var urlStudySets = BrowseWordsUtils.getUrlFromJson02(jsonString) //
			    .stream() //
			    .filter(f -> containsIgnoreCase(f, "english-words")) //
//			    .filter(f -> containsIgnoreCase(f, "English")) //
			    .collect(toList());

	    watch.stop();
	    System.out.println("JsonPath Time Elapsed: " + watch.getTime(MILLISECONDS) + " ms");
	    watch.reset();

	    watch.start();

	    for (final var urlStudySet : urlStudySets) {

		final var spans = webClient //
				.<HtmlPage>getPage(urlStudySet) //
			        .<HtmlSpan>getByXPath("//span[contains(@class,'TermText')]");

		spans.stream() //
			.map(HtmlSpan::getTextContent) //
			.map(w -> trim(removePattern(w, "\\(.*"))) //
			.filter(x -> containsNone(x, "...")) //
			.filter(x -> containsNone(x, "[")) //
			.filter(x -> isNotBlank(x)) //
			.collect(toList())//
			.forEach(w -> globalMap.put(w, urlStudySet));
	    }

	    watch.stop();
	    System.out.println("URL with XPath: " + urlStudySets.size() + " requests, Time Elapsed: " + watch.getTime(SECONDS) + " s");
	    watch.reset();
	}

	printWordsNotInSite(globalMap.asMap());

	printRepeatedWordsInSite(globalMap.asMap());
    }
}
