package com.evan.transaction.remote;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import javax.annotation.PostConstruct;

@Component
public class GtcRestClient {

    @Autowired
    private RestTemplate restTemplate;

    @Autowired
    private static GtcRestClient gtcRestClient;

    @PostConstruct
   public void init() {
        gtcRestClient = this;
        gtcRestClient.restTemplate = this.restTemplate;
    }

    public static void rollbackExecute(String groupId,String serviceUrl) {
        gtcRestClient.restTemplate.postForEntity(serviceUrl + "/global-rollback", groupId, Boolean.class);
    }

    public static void commitExecute(String groupId,String serviceUrl) {
        gtcRestClient.restTemplate.postForEntity(serviceUrl + "/global-commit", groupId, Boolean.class);
    }
}
