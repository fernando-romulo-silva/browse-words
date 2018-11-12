package br.com.fernando.browsewords;

import java.net.URL;
import java.util.List;
import java.util.stream.Collectors;

import com.gargoylesoftware.htmlunit.BrowserVersion;
import com.gargoylesoftware.htmlunit.HttpMethod;
import com.gargoylesoftware.htmlunit.UnexpectedPage;
import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.WebRequest;
import com.gargoylesoftware.htmlunit.html.HtmlPage;
import com.gargoylesoftware.htmlunit.html.HtmlSpan;
import com.google.common.collect.ArrayListMultimap;

public class BrowseWordsHtmlUnit {

    public static void main(String[] args) throws Exception {

        final WebClient webClient = new WebClient(BrowserVersion.FIREFOX_60);
        webClient.getOptions().setJavaScriptEnabled(false);
        webClient.getOptions().setCssEnabled(false);

        final ArrayListMultimap<String, String> globalMap = ArrayListMultimap.create();

        try (webClient) {

            final WebRequest webRequest = new WebRequest(new URL(BrowseWordsUtils.URL), HttpMethod.GET);
            webRequest.setAdditionalHeader("Accept", "*/*");
            webRequest.setAdditionalHeader("Content-Type", "application/json");

            final String jsonString = webClient.<UnexpectedPage> getPage(webRequest) //
                    .getWebResponse() //
                    .getContentAsString();

            final List<String> urlStudySets = BrowseWordsUtils.getUrlFromJson(jsonString);

            for (final String urlStudySet : urlStudySets) {

                final List<HtmlSpan> spans = webClient.<HtmlPage> getPage(urlStudySet) //
                        .getByXPath("//span[contains(@class,'lang-en')]");

                spans.stream() //
                        .map(HtmlSpan::getTextContent) //
                        .collect(Collectors.toList())//
                        .forEach(w -> globalMap.put(w, urlStudySet));
            }
        }

        BrowseWordsUtils.printWordsNotInSite(globalMap.asMap());

        BrowseWordsUtils.printRepeatedWordsInSite(globalMap.asMap());
    }
}
