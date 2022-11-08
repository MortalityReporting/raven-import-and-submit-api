package edu.gatech.chai.Submission.Service;

import org.apache.commons.codec.binary.Base64;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.HttpStatusCodeException;
import org.springframework.web.client.RestTemplate;

import ca.uhn.fhir.rest.api.Constants;

@Service
public class SubmitBundleService {
	private RestTemplate rest;
	@Value("${fhircms.url}")
	private String url;
	@Value("${fhircms.basicAuth.username}")
	private String basicUsername;
	@Value("${fhircms.basicAuth.password}")
	private String basicPassword;
	
	public SubmitBundleService() {
		rest = new RestTemplate();
	}
	
	public ResponseEntity<String> submitBundle(String bundle) throws HttpStatusCodeException{
		String auth = basicUsername + ":" + basicPassword;
		String encodedAuth = Base64.encodeBase64String(auth.getBytes(Constants.CHARSET_US_ASCII));
		MultiValueMap<String, String> headers = new LinkedMultiValueMap<String, String>();
		headers.add("Authorization", "Basic " + encodedAuth);
		headers.add("Content-Type", "application/fhir+json");
		HttpEntity<String> entity = new HttpEntity<String>(bundle, headers);
		ResponseEntity<String> response = rest.postForEntity(url, entity, String.class);
		return response;
	}
	
	public RestTemplate getRest() {
		return rest;
	}
	public void setRest(RestTemplate rest) {
		this.rest = rest;
	}
	public String getUrl() {
		return url;
	}
	public void setUrl(String url) {
		this.url = url;
	}
	public String getBasicUsername() {
		return basicUsername;
	}
	public void setBasicUsername(String basicUsername) {
		this.basicUsername = basicUsername;
	}
	public String getBasicPassword() {
		return basicPassword;
	}
	public void setBasicPassword(String basicPassword) {
		this.basicPassword = basicPassword;
	}
		
}