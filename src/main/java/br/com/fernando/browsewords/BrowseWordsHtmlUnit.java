package br.com.fernando.browsewords;

import java.net.URL;
import java.util.stream.Collectors;

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

        final var globalMap = ArrayListMultimap.<String, String> create();

        try (webClient) {

            final var webRequest = new WebRequest(new URL(BrowseWordsUtils.URL), HttpMethod.GET);
            webRequest.setAdditionalHeader("Accept", "*/*");
            webRequest.setAdditionalHeader("Content-Type", "application/json");

            final var jsonString = webClient.<UnexpectedPage> getPage(webRequest) //
                    .getWebResponse() //
                    .getContentAsString();

            final var urlStudySets = BrowseWordsUtils.getUrlFromJson01(jsonString);

            for (final var urlStudySet : urlStudySets) {

                final var spans = webClient.<HtmlPage> getPage(urlStudySet) //
                        .<HtmlSpan> getByXPath("//span[contains(@class,'TermText')]");

                spans.stream() //
                        .map(HtmlSpan::getTextContent) //
                        .filter(x -> !"...".equals(x)) //
                        .collect(Collectors.toList())//
                        .forEach(w -> globalMap.put(w, urlStudySet));
                
                // TimeUnit.SECONDS.sleep(2);
            }
        }

        BrowseWordsUtils.printWordsNotInSite(globalMap.asMap());

        BrowseWordsUtils.printRepeatedWordsInSite(globalMap.asMap());
    }
}
