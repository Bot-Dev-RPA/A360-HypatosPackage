package com.automationanywhere.botcommand.Utils;

import com.automationanywhere.botcommand.exception.BotCommandException;
import org.apache.http.HttpEntity;
import org.apache.http.client.CookieStore;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPatch;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.BasicCookieStore;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.util.EntityUtils;
import org.json.JSONObject;

import java.io.IOException;


public class HTTPRequest {
    private static final CookieStore cookieStore = new BasicCookieStore();
    public static final RequestConfig requestConfig = RequestConfig.custom()
            .setSocketTimeout(30 * 1000)
            .build();
    public static final CloseableHttpClient httpClient = HttpClientBuilder
            .create()
            .setDefaultCookieStore(cookieStore)
            .setDefaultRequestConfig(requestConfig)
            .build();

    public static String Request(String url, String auth) throws IOException {
        HttpGet request = new HttpGet(url);
        request.setHeader("Authorization", auth);

        try (CloseableHttpResponse response = httpClient.execute(request)) {
            HttpEntity entity = response.getEntity();
            if(response.getStatusLine().getStatusCode() != 200)
                throw new BotCommandException("Hypatos could not complete the action error code: "+response.getStatusLine().getStatusCode() +"error details: "+ response);
            return entity != null ? EntityUtils.toString(entity) : null;
        }


    }


    public static String httpPatch(String url, String auth, JSONObject jsonBody) throws IOException {

        StringEntity jsonStringEntity = new StringEntity(jsonBody.toString(), ContentType.APPLICATION_JSON);
        HttpPatch request = new HttpPatch(url);
        request.setHeader("Authorization", auth);
        request.setHeader("Content-Type", "application/json");
        request.setEntity(jsonStringEntity);

        try (CloseableHttpResponse response = httpClient.execute(request)) {
            HttpEntity entity = response.getEntity();
            if(response.getStatusLine().getStatusCode() != 200)
                throw new BotCommandException("Hypatos could not complete the action error code: "+response.getStatusLine().getStatusCode() +"error details: "+ response);
            return entity != null ? EntityUtils.toString(entity) : null;
        }

    }

}
