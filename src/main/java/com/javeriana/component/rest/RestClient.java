package com.javeriana.component.rest;

import com.javeriana.component.model.request.SyncRequest;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.util.UriComponentsBuilder;
import java.util.logging.Logger;
import java.util.logging.Level;


import java.util.List;
import java.util.Map;

@Service
public class RestClient<T> {

    private static final Logger logger = Logger.getLogger(RestClient.class.getName());


    @Autowired
    private RestTemplate restTemplate;

    public <T> List<T> getApiDataListWithDynamicParams(String URL, Map<String, String> params,ParameterizedTypeReference<List<T>> responseType) {
        UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(URL);

        for (Map.Entry<String, String> entry : params.entrySet()) {
            builder.queryParam(entry.getKey(), entry.getValue());
        }
        String url = builder.build(false).toUriString();
        try {
            ResponseEntity<List<T>> response = restTemplate.exchange(
                    url,
                    HttpMethod.GET,
                    null,
                    responseType);

            return response.getBody();
        }catch (RestClientException ex){
            logger.log(Level.SEVERE,"An error ocurred requesting to API for "+responseType.toString());
            return null;
        }

    }

    public <T> T getApiDataWithDynamicParams(String URL, Map<String, String> params, ParameterizedTypeReference<T> responseType) {
        UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(URL);

        for (Map.Entry<String, String> entry : params.entrySet()) {
            builder.queryParam(entry.getKey(), entry.getValue());
        }

        String url = builder.build(false).toUriString();


        ResponseEntity<T> response = restTemplate.exchange(
                url,
                HttpMethod.GET,
                null,
                responseType);

        return response.getBody();
    }

    public void postGrades(String URL, Map<String, String> params, SyncRequest syncRequest) {
        UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(URL);

        for (Map.Entry<String, String> entry : params.entrySet()) {
            builder.queryParam(entry.getKey(), entry.getValue());
        }
        String url = builder.build(false).toUriString();

        HttpHeaders headers = new HttpHeaders();
        headers.set("Content-Type", "application/json");

        HttpEntity<SyncRequest> requestEntity = new HttpEntity<>(syncRequest, headers);
        restTemplate.exchange(
                url,
                HttpMethod.POST,
                requestEntity,
                SyncRequest.class);

    }



}