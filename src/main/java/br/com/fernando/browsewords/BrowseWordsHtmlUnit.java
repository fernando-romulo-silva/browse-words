package br.com.fernando.browsewords;

import static br.com.fernando.browsewords.util.BrowseWordsUtils.URL;
import static br.com.fernando.browsewords.util.BrowseWordsUtils.printRepeatedWordsInSite;
import static br.com.fernando.browsewords.util.BrowseWordsUtils.printWordsNotInSite;
import static com.gargoylesoftware.htmlunit.BrowserVersion.FIREFOX_78;
import static com.gargoylesoftware.htmlunit.HttpMethod.GET;
import static java.util.concurrent.TimeUnit.MILLISECONDS;
import static java.util.concurrent.TimeUnit.SECONDS;
import static java.util.stream.Collectors.toList;

import java.net.URL;

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

	final var webClient = new WebClient(FIREFOX_78);
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

	    final var urlStudySets = BrowseWordsUtils.getUrlFromJson02(jsonString);

	    watch.stop();
	    System.out.println("JsonPath Time Elapsed: " + watch.getTime(MILLISECONDS) + " ms");
	    watch.reset();

	    watch.start();

	    for (final var urlStudySet : urlStudySets) {

		final var spans = webClient.<HtmlPage>getPage(urlStudySet) //
			.<HtmlSpan>getByXPath("//span[contains(@class,'TermText')]");

		spans.stream() //
			.map(HtmlSpan::getTextContent) //
			.filter(x -> !"...".equals(x)) //
			.collect(toList())//
			.forEach(w -> globalMap.put(w, urlStudySet));
	    }

	    watch.stop();
	    System.out.println("URL with XPath: " + urlStudySets.size() + " requests, Time Elapsed: " + watch.getTime(SECONDS) + " s");
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

	printWordsNotInSite(globalMap.asMap());

	printRepeatedWordsInSite(globalMap.asMap());
    }
}
