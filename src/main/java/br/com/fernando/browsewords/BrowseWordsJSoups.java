package br.com.fernando.browsewords;

import java.io.IOException;
import java.net.URL;
import java.util.List;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import com.jayway.jsonpath.JsonPath;

public class BrowseWordsJSoups {

    public static void main(String[] args) throws IOException {
        final String jsonString = Jsoup.connect("https://quizlet.com/webapi/3.2/feed/65138028/created-sets?perPage=100&query=&sort=alphabetical&seenCreatedSetIds=&filters%5Bsets%5D%5BisPublished%5D=true&include%5Bset%5D%5B%5D=creator") //
                .ignoreContentType(true)//
                .execute() //
                .body(); //
        
        final List<String> urlStudySets = JsonPath.read(jsonString, "$..set[*]._webUrl");

        for (final String urlStudySet : urlStudySets) {
            
            Document document = Jsoup.parse(new URL(urlStudySet), 10000);
            
            final Elements spans = document.select("//a/@href");
                    
                    
                    //.getByXPath("//span[contains(@class,'lang-en')]");
        }
        
        System.out.println("Title : " + jsonString);
    }

}
