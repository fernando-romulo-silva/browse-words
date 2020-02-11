package br.com.fernando.browsewords;

import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import org.apache.commons.lang3.time.StopWatch;

import com.gargoylesoftware.htmlunit.BrowserVersion;
import com.gargoylesoftware.htmlunit.HttpMethod;
import com.gargoylesoftware.htmlunit.UnexpectedPage;
import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.WebRequest;
import com.gargoylesoftware.htmlunit.html.HtmlPage;
import com.gargoylesoftware.htmlunit.html.HtmlSpan;
import com.google.common.collect.ArrayListMultimap;

import br.com.fernando.browsewords.util.BrowseWordsUtils;

public class BrowseWordsHtmlUnit {

    public static void main(String[] args) throws Exception {

	final var webClient = new WebClient(BrowserVersion.FIREFOX_60);
	webClient.getOptions().setJavaScriptEnabled(false);
	webClient.getOptions().setCssEnabled(false);

	final var globalMap = ArrayListMultimap.<String, String>create();
	final var watch = new StopWatch();

	try (webClient) {

	    final var webRequest = new WebRequest(new URL(BrowseWordsUtils.URL), HttpMethod.GET);
	    webRequest.setAdditionalHeader("Accept", "*/*");
	    webRequest.setAdditionalHeader("Content-Type", "application/json");

	    watch.start();

	    final var jsonString = webClient.<UnexpectedPage>getPage(webRequest) //
	        .getWebResponse() //
	        .getContentAsString();

	    watch.stop();
	    System.out.println("URL principal, Time Elapsed: " + watch.getTime(TimeUnit.MILLISECONDS) + " ms");
	    watch.reset();

	    watch.start();

	    final var urlStudySets = BrowseWordsUtils.getUrlFromJson02(jsonString);

	    watch.stop();
	    System.out.println("JsonPath Time Elapsed: " + watch.getTime(TimeUnit.MILLISECONDS) + " ms");
	    watch.reset();

	    watch.start();

	    for (final var urlStudySet : urlStudySets) {

		final var spans = webClient.<HtmlPage>getPage(urlStudySet) //
		    .<HtmlSpan>getByXPath("//span[contains(@class,'TermText')]");

		spans.stream() //
		    .map(HtmlSpan::getTextContent) //
		    .filter(x -> !"...".equals(x)) //
		    .collect(Collectors.toList())//
		    .forEach(w -> globalMap.put(w, urlStudySet));
	    }

	    watch.stop();
	    System.out.println("URL with XPath: " + urlStudySets.size() + " requests, Time Elapsed: " + watch.getTime(TimeUnit.SECONDS) + " s");
	    watch.reset();
	}

//	final Path wordsFile = Paths.get(BrowseWordsUtils.class.getClassLoader().getResource("words.txt").toURI());
//
//	final Set<String> wordsOnSite = globalMap.keySet();
//
//	final List<String> wordsInFile = Files.lines(wordsFile) // reading file
//	    .map(s -> s.toLowerCase().trim()) //
//	    .filter(w -> !wordsOnSite.contains(w.toLowerCase())) // only words that not in site
//	    .distinct() //
//	    .collect(Collectors.toList());

//	final var normalizedWords = new ArrayList<>();
//
//	for (final String word : wordsInFile) {
//	    // https://translate.google.com/#view=home&op=translate&sl=en&tl=pt&text=prevent
//	    // <span class="gt-card-ttl-txt" style="direction: ltr;">prevent</span>
//	    HtmlSpan htmlSpan = webClient.<HtmlPage>getPage("https://translate.google.com/#view=home&op=translate&sl=en&tl=pt&text=" + word) //
//	        .<HtmlSpan>getFirstByXPath("//span[contains(@class,'gt-card-ttl-txt')]");
//
//	    normalizedWords.add(htmlSpan.getTextContent());
//
//	    // spans.stream() //
//	    // .map(HtmlSpan::getTextContent) //
//	}

	BrowseWordsUtils.printWordsNotInSite(globalMap.asMap());

	BrowseWordsUtils.printRepeatedWordsInSite(globalMap.asMap());
    }
}
