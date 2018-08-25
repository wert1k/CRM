package com.ewp.crm.service.impl;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.config.CookieSpecs;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;

import java.io.IOException;

@Service
@PropertySource("file:./slackAPI.properties")
public class SlackAPIServiceImpl {

    private GoogleApiServiceImpl googleApiService;

    private String botToken;

    private String SLACK_API_METHOD_TEMPLATE = "https://slack.com/api/";

    @Autowired
    public SlackAPIServiceImpl(Environment env, GoogleApiServiceImpl googleApiService){
        this.googleApiService = googleApiService;
        this.botToken = env.getRequiredProperty("slack.api.bot.token");
    }

    public void takeUserInSlackAndSendToGSheet(String userId) throws IOException, JSONException {
        String request = SLACK_API_METHOD_TEMPLATE + "users.info?"
                + "token=" + botToken
                + "&user=" + userId
                + "&include_locale=" + true;
        HttpGet httpGetClient = new HttpGet(request);
        HttpClient httpClient = HttpClients.custom()
                .setDefaultRequestConfig(RequestConfig.custom()
                        .setCookieSpec(CookieSpecs.STANDARD).build()).build();
        HttpResponse response = httpClient.execute(httpGetClient);
        String result = EntityUtils.toString(response.getEntity());
        JSONObject json = new JSONObject(result);
        if (json.getBoolean("ok")) {
            JSONObject user = json.getJSONObject("user");
            JSONObject profile = user.getJSONObject("profile");
            String realName = profile.getString("real_name_normalized");
            String email = profile.getString("email");
            googleApiService.addToTable(realName, email);
        }
    }
}
