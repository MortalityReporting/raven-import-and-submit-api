package edu.gatech.chai.Mapping.Service;

import org.springframework.http.ResponseEntity;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

public class NightingaleSubmissionService {

	private RestTemplate restTemplate;
	
	public NightingaleSubmissionService() {
		this.restTemplate = new RestTemplate();
	}
	
	public ResponseEntity<String> submitRecord(String POSTendpoint, String VRDRJson) throws ResourceAccessException, RestClientException{
		System.out.println("Submitting to nightingale at endpoint:" + POSTendpoint);
		ResponseEntity<String> POSTresponse
		  = restTemplate.postForEntity(POSTendpoint, VRDRJson, String.class);
		return POSTresponse;
	}

}