package br.com.fernando.browsewords.checkbase;

import static com.gargoylesoftware.htmlunit.BrowserVersion.FIREFOX;
import static com.gargoylesoftware.htmlunit.HttpMethod.GET;
import static org.apache.commons.lang3.StringUtils.containsIgnoreCase;
import static org.apache.commons.lang3.StringUtils.equalsIgnoreCase;

import java.io.IOException;
import java.net.URL;

import com.gargoylesoftware.htmlunit.FailingHttpStatusCodeException;
import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.WebRequest;
import com.gargoylesoftware.htmlunit.html.HtmlDivision;
import com.gargoylesoftware.htmlunit.html.HtmlItalic;
import com.gargoylesoftware.htmlunit.html.HtmlPage;

public class CheckBaseWordHtmlUnit {

    /**
     * Check if the word is a verb, if true return its conjuction.
     * 
     * @param word
     * @return
     * @throws FailingHttpStatusCodeException
     * @throws IOException
     */
    public static String conjugation(final String word) throws FailingHttpStatusCodeException, IOException {

	final var webClient = new WebClient(FIREFOX);
	webClient.getOptions().setJavaScriptEnabled(false);
	webClient.getOptions().setCssEnabled(false);

	try (webClient) {

	    final var stringUrl = new StringBuilder("https://conjugacao.reverso.net/conjugacao-ingles-verbo-").append(word).append(".html").toString();
	    final var webRequest = new WebRequest(new URL(stringUrl), GET);

	    final var page = webClient.<HtmlPage>getPage(webRequest);

	    final var htmlString = page //
			    .getWebResponse() //
			    .getContentAsString();
	    
	    if (containsIgnoreCase(htmlString, "AVISO: Este verbo") || containsIgnoreCase(htmlString, "para o verbo inserido")) {
		
		return word;

	    } else {

		final var rootWords = page.<HtmlDivision>getFirstByXPath("//div[contains(@class,'word-wrap')]");

		final var imperativeOne = rootWords.<HtmlItalic>getFirstByXPath("//p[text() = 'Present']/following-sibling::ul/li/i[2]");
		final var textImperativeOne = imperativeOne.asNormalizedText();

		final var imperativeTwo = rootWords.<HtmlItalic>getFirstByXPath("//p[text() = 'Present']/following-sibling::ul/li[3]/i[2]");
		final var textImperativeTwo = imperativeTwo.asNormalizedText();

		final var gerund = rootWords.<HtmlItalic>getFirstByXPath("//p[text() = 'Present continuous']/following-sibling::ul/li/i[3]");
		final var textGerund = gerund.asNormalizedText();

		final var ulParticiple = rootWords.<HtmlItalic>getFirstByXPath("//p[text() = 'Present perfect']/following-sibling::ul/li/i[3]");
		final var textParticiple = ulParticiple.asNormalizedText();

		final var past = rootWords.<HtmlItalic>getFirstByXPath("//p[text() = 'Preterite']/following-sibling::ul/li/i[2]");
		final var textPast = past.asNormalizedText();

		final var text = new StringBuilder() //
				.append(textImperativeOne) //
				.append(" (") //
				.append(equalsIgnoreCase(textImperativeOne, textImperativeTwo) ? "" : textImperativeTwo + ", ")
				.append(textPast) //
				.append(", "); //

		if (!equalsIgnoreCase(textPast, textParticiple)) {
		    text.append(textParticiple).append(", ");
		}

		text.append(textGerund).append(")");

		if (!equalsIgnoreCase(textImperativeOne, word)) {
		    text.append(" - ").append(word);
		}

		return text.toString();
	    }

	} catch (final Exception ex) {
	    return word;
	}
    }
}
