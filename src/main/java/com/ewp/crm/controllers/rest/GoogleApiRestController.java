package com.ewp.crm.controllers.rest;

import com.ewp.crm.service.impl.GoogleApiServiceImpl;
import com.ewp.crm.service.impl.SlackAPIServiceImpl;
import org.json.JSONException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;

@RestController
public class GoogleApiRestController {

    @Autowired
    private GoogleApiServiceImpl googleApiService;

    @Autowired
    private SlackAPIServiceImpl slackAPIService;

    @RequestMapping(value = "/google", method = RequestMethod.GET)
    public ResponseEntity createNewTable(@RequestParam String name) throws IOException, JSONException {
//        googleApiService.createNewTable(name);
//        googleApiService.updateTable();
//        googleApiService.readTable();
//        googleApiService.addToTable();
        slackAPIService.takeUserInSlackAndSendToGSheet(name);
        return ResponseEntity.ok().build();
//                .body(googleApiService.createNewTable(name));
    }
}
