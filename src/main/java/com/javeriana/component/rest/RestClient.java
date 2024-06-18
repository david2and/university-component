package com.javeriana.component.rest;

import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.util.UriComponentsBuilder;


import java.util.List;
import java.util.Map;

@Service
public class RestClient<T> {

    @Autowired
    private RestTemplate restTemplate;

    public <T> List<T> getApiDataListWithDynamicParams(String URL, Map<String, String> params,ParameterizedTypeReference<List<T>> responseType) {
        UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(URL);

        for (Map.Entry<String, String> entry : params.entrySet()) {
            builder.queryParam(entry.getKey(), entry.getValue());
        }
        String url = builder.build(false).toUriString();

        ResponseEntity<List<T>> response = restTemplate.exchange(
                url,
                HttpMethod.GET,
                null,
                responseType);

        return response.getBody();
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



}