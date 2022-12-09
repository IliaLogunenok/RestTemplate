package com.kata.example.resttemplate;

import com.kata.example.resttemplate.entity.User;
import jakarta.annotation.PostConstruct;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.*;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.util.List;

@Component
public class Communication {

    private final String URL = "http://94.198.50.185:7081/api/users";
    private List<String> cookies;
    private final RestTemplate restTemplate;

    public Communication(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    @PostConstruct
    private void initData() {
        ResponseEntity<List<User>> usersList = getUsersList();
        System.out.println(usersList.getBody());

        User newUser = new User(3, "James", "Brown", 25);
        ResponseEntity<String> responseSaveUser = saveUser(newUser);
        System.out.println(responseSaveUser.getBody());

        newUser.setName("Thomas");
        newUser.setLastName("Shelby");
        ResponseEntity<String> responseEditUser = editUser(newUser);
        System.out.println(responseEditUser.getBody());

        ResponseEntity<String> responseDeleteUser = deleteUser(3);
        System.out.println(responseDeleteUser.getBody());

    }

    public ResponseEntity<List<User>> getUsersList() {
        ResponseEntity<List<User>> responseEntity =
                restTemplate.exchange(URL, HttpMethod.GET, null,
                        new ParameterizedTypeReference<>() {
                        });

        cookies = responseEntity.getHeaders().get("Set-Cookie");
        System.out.println(cookies);
        return responseEntity;
    }

    public ResponseEntity<String> saveUser(User user) {

        HttpHeaders headers = new HttpHeaders();
        headers.set("Cookie", String.join(";", cookies));
        headers.setAccept(List.of(MediaType.APPLICATION_JSON));
        System.out.println(headers);

        HttpEntity<User> httpEntity = new HttpEntity<>(user, headers);
        return restTemplate.postForEntity(URL, httpEntity, String.class);
    }

    public ResponseEntity<String> editUser(User user) {

        HttpHeaders headers = new HttpHeaders();
        headers.set("Cookie", String.join(";", cookies));
        headers.setAccept(List.of(MediaType.APPLICATION_JSON));

        HttpEntity<User> httpEntity = new HttpEntity<>(user, headers);
        return restTemplate.exchange(URL, HttpMethod.PUT, httpEntity, String.class);
    }

    public ResponseEntity<String> deleteUser(int id) {

        HttpHeaders headers = new HttpHeaders();
        headers.set("Cookie", String.join(";", cookies));
        headers.setAccept(List.of(MediaType.APPLICATION_JSON));

        HttpEntity<User> httpEntity = new HttpEntity<>(headers);
        return restTemplate.exchange(URL + "/" + id, HttpMethod.DELETE, httpEntity, String.class);
    }
}

