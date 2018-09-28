package br.com.fernando.browsewords;

import java.io.IOException;

import org.jsoup.Jsoup;

public class BrowseWordsJSoups {

    public static void main(String[] args) throws IOException {
        String doc = Jsoup.connect("https://quizlet.com/webapi/3.2/feed/65138028/created-sets?perPage=100&query=&sort=alphabetical&seenCreatedSetIds=&filters%5Bsets%5D%5BisPublished%5D=true&include%5Bset%5D%5B%5D=creator") //
                .header("Accept", "*/*") //
                .header("Content-Type", "application/json") //
                .get() //
                .body() //
                .text();

        System.out.println("Title : " + doc);
    }

}
