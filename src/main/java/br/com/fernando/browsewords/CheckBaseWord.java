package br.com.fernando.browsewords;

import static com.gargoylesoftware.htmlunit.BrowserVersion.FIREFOX_78;
import static com.gargoylesoftware.htmlunit.HttpMethod.GET;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.StopWatch;

import com.gargoylesoftware.htmlunit.FailingHttpStatusCodeException;
import com.gargoylesoftware.htmlunit.UnexpectedPage;
import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.WebRequest;
import com.gargoylesoftware.htmlunit.html.HtmlDivision;
import com.gargoylesoftware.htmlunit.html.HtmlPage;
import com.gargoylesoftware.htmlunit.html.HtmlSpan;
import com.google.common.collect.ArrayListMultimap;

public class CheckBaseWord {

    // https://translate.google.com.br/?hl=pt-BR&sl=en&tl=pt&text=compelling

    public static void main(String[] args) throws FailingHttpStatusCodeException, IOException {

	final var webClient = new WebClient(FIREFOX_78);
	webClient.getOptions().setJavaScriptEnabled(false);
	webClient.getOptions().setCssEnabled(false);

	final var globalMap = ArrayListMultimap.<String, String>create();

	try (webClient) {

	    final var webRequest = new WebRequest(new URL("https://conjugacao.reverso.net/conjugacao-ingles-verbo-woven.html"), GET);

	    final var page = webClient.<HtmlPage>getPage(webRequest);

	    final var jsonString = page //
		    .getWebResponse() //
		    .getContentAsString();

	    if (StringUtils.contains(jsonString, "AVISO: Este verbo")) {
		System.out.println(jsonString);

	    } else {

		final var a = page.<HtmlDivision>getFirstByXPath("//div[contains(@class,'verb-forms-wrap')]");
		System.out.println(a);
	    }

	    //

	    System.out.println(jsonString);

	}
    }

}
