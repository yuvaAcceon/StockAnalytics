package io.endeavour.stocks.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.support.BasicAuthenticationInterceptor;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class LoginService {
    private String loginURL;
    private RestTemplate restTemplate;
    //@Value annotation reads values from application.properties and injects it into Java variables
    public LoginService(@Value("${client.stock-calculations.url}") String baseURL,
                        @Value("${client.login.username}") String userName,
                        @Value("${client.login.password}") String password){
        loginURL = baseURL + "/login";

        //RestTemplate is a client that can generate a HttpRequest to be sent to another webservice and read the response that was sent
        restTemplate = new RestTemplate();

        //Add an interceptor to the RestTemplate to ensure that any call going through this restTemplate will have
        // n Basic Authentication header added
        restTemplate.getInterceptors().add(new BasicAuthenticationInterceptor(userName, password));
    }

    /**
     * Generates the Bearer Token to be used by the CumulativeReturn calculation API's
     * @return String
     */
    public String getBearerToken(){
        //RestTemplate exchange method will fire the actual Webservice call to the remote API. Its input parameters include:
        // 1) The URL of the Webservice call to hit
        // 2) The Http Method used to hit the remote API
        // 3) The Request object to be sent to the remote API (null in our case)
        // 4) How should the response received back be converted to. (String in our case)
        ResponseEntity<String> response = restTemplate.exchange(loginURL, HttpMethod.POST, null, String.class);
        String token = response.getBody();
        return token;
    }























}
