package br.com.fernando.browsewords.findwords;

import static br.com.fernando.browsewords.util.BrowseWordsUtilsHtmlUnit.URL;
import static com.gargoylesoftware.htmlunit.BrowserVersion.FIREFOX;
import static com.gargoylesoftware.htmlunit.HttpMethod.GET;
import static java.util.concurrent.TimeUnit.MILLISECONDS;
import static org.apache.commons.lang3.RegExUtils.removePattern;
import static org.apache.commons.lang3.StringUtils.containsIgnoreCase;
import static org.apache.commons.lang3.StringUtils.containsNone;
import static org.apache.commons.lang3.StringUtils.isNotBlank;
import static org.apache.commons.lang3.StringUtils.split;
import static org.apache.commons.lang3.StringUtils.trim;

import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;

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

public class FindWordsHtmlUnit {
    
    private static final Logger LOGGER = LoggerFactory.getLogger(FindWordsHtmlUnit.class);

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
			    .filter(f -> containsIgnoreCase(f, "english-words")) //
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
				.toList()
				.forEach(w -> globalMap.put(w, urlStudySet));
	    }

	    watch.stop();
	    LOGGER.info("URL with XPath: {} requests, Time Elapsed: {}", urlStudySets.size(), watch.getTime(MILLISECONDS));
	    watch.reset();
	}

	final var wordsPath = Paths.get(BrowseWordsUtilsHtmlUnit.class.getClassLoader().getResource("find-words.txt").toURI());

	try (final var stream = Files.lines(wordsPath)) { // reading file
	    
	    final var words = stream.filter(s -> isNotBlank(s)) //
			    .map(s -> trim(removePattern(s, "\\(.*"))) //
			    .distinct() //
			    .toList();

	    for (final var word : words) {
		final var w = trim(split(word, ':')[0].toLowerCase());

		if (globalMap.containsKey(w)) {
		    LOGGER.info("{} ( {} ) ", word, globalMap.get(w));
		} else {
		    LOGGER.info("{}", word);
		}
	    }
	}
    }

}
