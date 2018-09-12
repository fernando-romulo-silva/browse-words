package br.com.fernando.browsewords;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import com.gargoylesoftware.htmlunit.BrowserVersion;
import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.html.HtmlPage;
import com.gargoylesoftware.htmlunit.html.HtmlSpan;

public class Main {

    public static void main(String[] args) throws Exception {

        final WebClient webClient = new WebClient(BrowserVersion.FIREFOX_60);

        try (webClient) {

            webClient.getOptions().setJavaScriptEnabled(false);
            webClient.getOptions().setCssEnabled(false);

            // webClient.getOptions().getProxyConfig().setProxyHost("127.0.0.1");
            // webClient.getOptions().getProxyConfig().setProxyPort(8888);

            Stream.iterate(1, x -> x <= 20, x -> x++) //
                .forEach((x) -> {

                });

            final List<HtmlSpan> spans = webClient.<HtmlPage>getPage("https://quizlet.com/286499785/english-words-0016-flash-cards") //
                .getByXPath("//span[contains(@class,'lang-en')]");

            final List<String> wordsPage = spans.stream() //
                .map(elt -> elt.getTextContent()) //
                .collect(Collectors.toList());

            System.out.println(wordsPage);

        }
    }
}
