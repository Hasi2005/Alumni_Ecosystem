package com.example.referral_service.job;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

@Component
public class JobServiceClient {

    private final RestTemplate restTemplate;

    @Value("${job.internal.base-url:http://job-service:8083}")
    private String jobInternalBaseUrl;

    public JobServiceClient(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    public boolean jobExists(Long jobId) {
        if (jobId == null) return false;
        try {
            ResponseEntity<Void> resp = restTemplate.exchange(
                    jobInternalBaseUrl + "/api/jobs/" + jobId + "/exists",
                    HttpMethod.GET,
                    null,
                    Void.class
            );
            return resp.getStatusCode().is2xxSuccessful();
        } catch (Exception ex) {
            return false;
        }
    }
}

