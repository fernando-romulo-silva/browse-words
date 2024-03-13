package br.com.fernando.browsewords.findwords;

import static com.gargoylesoftware.htmlunit.BrowserVersion.CHROME;
import static com.gargoylesoftware.htmlunit.HttpMethod.GET;
import static java.util.concurrent.TimeUnit.MILLISECONDS;
import static org.apache.commons.lang3.RegExUtils.removePattern;
import static org.apache.commons.lang3.StringUtils.containsIgnoreCase;
import static org.apache.commons.lang3.StringUtils.containsNone;
import static org.apache.commons.lang3.StringUtils.isNotBlank;
import static org.apache.commons.lang3.StringUtils.split;
import static org.apache.commons.lang3.StringUtils.trim;

import java.io.IOException;
import java.net.InetAddress;
import java.net.Socket;
import java.net.URL;
import java.net.UnknownHostException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.security.KeyManagementException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.UnrecoverableKeyException;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;

import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import org.apache.commons.lang3.time.StopWatch;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.gargoylesoftware.htmlunit.CookieManager;
import com.gargoylesoftware.htmlunit.NicelyResynchronizingAjaxController;
import com.gargoylesoftware.htmlunit.SilentCssErrorHandler;
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

	final var webClient = new WebClient(CHROME);
	webClient.getOptions().setUseInsecureSSL(true);	
//	webClient.getOptions().setJavaScriptEnabled(false);
//	webClient.getOptions().setCssEnabled(false);
	webClient.getOptions().setCssEnabled(true);
	webClient.getOptions().setJavaScriptEnabled(true);
	webClient.getOptions().setThrowExceptionOnScriptError(false);
	webClient.getOptions().setThrowExceptionOnFailingStatusCode(false);
	
        webClient.getCache().clear();
        webClient.getOptions().setUseInsecureSSL(true);
        webClient.getOptions().setCssEnabled(false);
        webClient.getOptions().setJavaScriptEnabled(true);
        webClient.getOptions().setRedirectEnabled(true);
        webClient.getOptions().setDownloadImages(false);
        webClient.getOptions().setThrowExceptionOnScriptError(false);
        webClient.getOptions().setThrowExceptionOnFailingStatusCode(false);
        
        webClient.setAjaxController(new NicelyResynchronizingAjaxController());
        webClient.setCssErrorHandler(new SilentCssErrorHandler());	
	
	webClient.setAjaxController(new NicelyResynchronizingAjaxController());
	webClient.waitForBackgroundJavaScript(10_000);
	
	webClient.getCookieManager().setCookiesEnabled(true);
	webClient.getCookieManager().clearCookies();
	webClient.setCookieManager(new CookieManager());
        
//	webClient.getCache().clear();
//	webClient.getCache().setMaxSize(0);

	final var globalMap = ArrayListMultimap.<String, String>create();
	final var watch = new StopWatch();


	try (webClient) {

	    // https://quizlet.com/br/429494297/english-multi-words-0006-flash-cards/
	    // https://quizlet.com/webapi/3.9/feed/65138028/created-sets
	    // cloudflare 
	    final var webRequest = new WebRequest(new URL("https://quizlet.com/webapi/3.9/feed/65138028/created-sets"), GET);
	    webRequest.setAdditionalHeader("Accept", "*/*");
	    webRequest.setAdditionalHeader("Content-Type", "application/json");

	    watch.start();

	    final var jsonString = webClient.<HtmlPage>getPage(webRequest) //
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
