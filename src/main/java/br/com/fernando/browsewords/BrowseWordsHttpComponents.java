package br.com.fernando.browsewords;

import org.apache.http.HttpEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;

package org.apache.http.client.fluent;

public class BrowseWordsHttpComponents {

    public static void main(String[] args) throws Exception {

        Request.Get("http://targethost/homepage").execute().returnContent();

        final CloseableHttpClient httpclient = HttpClients.createDefault();
        final HttpGet httpGet = new HttpGet(BrowseWordsConsts.URL);
        final CloseableHttpResponse response1 = httpclient.execute(httpGet);

        try {
            System.out.println(response1.getStatusLine());

            HttpEntity entity1 = response1.getEntity();
            // do something useful with the response body and ensure it is fully consumed
            EntityUtils.consume(entity1);

        } finally {
            response1.close();
        }

    }
}
