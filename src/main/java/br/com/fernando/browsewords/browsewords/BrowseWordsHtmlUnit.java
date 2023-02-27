package br.com.fernando.browsewords.browsewords;

import static br.com.fernando.browsewords.util.BrowseWordsUtilsHtmlUnit.URL;
import static br.com.fernando.browsewords.util.BrowseWordsUtilsHtmlUnit.printRepeatedWordsInSite;
import static br.com.fernando.browsewords.util.BrowseWordsUtilsHtmlUnit.printWordsNotInSite;
import static com.gargoylesoftware.htmlunit.BrowserVersion.FIREFOX;
import static com.gargoylesoftware.htmlunit.HttpMethod.GET;
import static java.util.concurrent.TimeUnit.MILLISECONDS;
import static java.util.concurrent.TimeUnit.SECONDS;
import static org.apache.commons.lang3.RegExUtils.removePattern;
import static org.apache.commons.lang3.StringUtils.containsIgnoreCase;
import static org.apache.commons.lang3.StringUtils.containsNone;
import static org.apache.commons.lang3.StringUtils.isNotBlank;
import static org.apache.commons.lang3.StringUtils.trim;

import java.net.URL;

import org.apache.commons.lang3.time.StopWatch;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.gargoylesoftware.htmlunit.UnexpectedPage;
import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.WebRequest;
import com.gargoylesoftware.htmlunit.html.HtmlPage;
import com.gargoylesoftware.htmlunit.html.HtmlSpan;
import com.google.common.collect.ArrayListMultimap;

import br.com.fernando.browsewords.util.BrowseWordsUtilsHtmlUnit;

public class BrowseWordsHtmlUnit {
    
    private static final Logger LOGGER = LoggerFactory.getLogger(BrowseWordsHtmlUnit.class);

    public static void main(String[] args) throws Exception {

	
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
	    LOGGER.info("URL principal, Time Elapsed: {} ms", watch.getTime(MILLISECONDS));
	    watch.reset();

	    watch.start();

	    final var urlStudySets = BrowseWordsUtilsHtmlUnit.getUrlFromJson02(jsonString) //
			    .stream() //
			    .filter(f -> containsIgnoreCase(f, "multi-word")) //
//			    .filter(f -> containsIgnoreCase(f, "english words")) //
			    .toList();

	    watch.stop();
	    LOGGER.info("JsonPath Time Elapsed: {} ms", watch.getTime(MILLISECONDS));
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
			.toList() //
			.forEach(w -> globalMap.put(w, urlStudySet));
	    }

	    watch.stop();
	    LOGGER.info("URL with XPath: {} requests, Time Elapsed: {} s", urlStudySets.size(), watch.getTime(SECONDS));
	    watch.reset();
	}

	printWordsNotInSite(globalMap.asMap());

	printRepeatedWordsInSite(globalMap.asMap());
    }
}
