package com.identity.reconciliation.controller;

import com.identity.reconciliation.service.ContactService;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/")
public class IdentityController {

    @Autowired
    public ContactService contactService;

    @PostMapping("identify")
    public ResponseEntity<String> getIdentity(@RequestBody String contact) {
        return ResponseEntity.status(HttpStatus.OK).body(contactService.identifyContact(new JSONObject(contact)));
    }

}
