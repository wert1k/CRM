package com.ewp.crm.controllers.rest;

import com.ewp.crm.service.impl.GoogleApiServiceImpl;
import com.ewp.crm.service.impl.SlackAPIServiceImpl;
import com.sun.jmx.snmp.SnmpTimeticks;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;

@Controller
public class SlackApiRestController {

    @Autowired
    private SlackAPIServiceImpl slackAPIService;


    @RequestMapping(value = "/slack/oauth", method = RequestMethod.POST)
    public ResponseEntity oauth2(@RequestBody String requestData) throws IOException {
        try {
            JSONObject jsonObject = new JSONObject(requestData);
            String type = (String) jsonObject.get("type");
            if (type.equals("url_verification")) {
                String challenge = (String) jsonObject.get("challenge");
                return ResponseEntity.ok().contentType(MediaType.TEXT_PLAIN).body(challenge);
            }
            if (type.equals("event_callback")){
                JSONObject event = jsonObject.getJSONObject("event");
                if (event.getString("type").equals("member_joined_channel")) {
                    String userId = event.getString("user");
                    slackAPIService.takeUserInSlackAndSendToGSheet(userId);
                }
                return ResponseEntity.ok(HttpStatus.OK);
            }
            return ResponseEntity.badRequest().build();
        } catch (JSONException e) {
            return ResponseEntity.noContent().build();
        }
    }
}
