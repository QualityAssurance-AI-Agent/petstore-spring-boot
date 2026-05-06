package io.swagger.petstore.controller;

import io.swagger.petstore.exception.BadRequestException;
import io.swagger.petstore.model.User;
import io.swagger.petstore.service.UserService;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.OffsetDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/user")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping(
            consumes = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE,
                        MediaType.APPLICATION_FORM_URLENCODED_VALUE},
            produces = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE})
    public User createUser(@RequestBody User user) {
        return userService.create(user);
    }

    @PostMapping(value = "/createWithList",
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE})
    public List<User> createWithList(@RequestBody List<User> users) {
        userService.createBatch(users);
        return users;
    }

    @GetMapping(value = "/login",
            produces = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE})
    public ResponseEntity<String> login(@RequestParam(value = "username", required = false) String username,
                                        @RequestParam(value = "password", required = false) String password) {
        if (!userService.login(username, password)) {
            throw new BadRequestException("Invalid username/password supplied");
        }
        String token = "logged in user session:" + UUID.randomUUID();
        HttpHeaders headers = new HttpHeaders();
        headers.add("X-Rate-Limit", "5000");
        headers.add("X-Expires-After",
                OffsetDateTime.now().plusHours(1).format(DateTimeFormatter.ISO_OFFSET_DATE_TIME));
        return ResponseEntity.ok().headers(headers).body(token);
    }

    @GetMapping("/logout")
    public void logout() {
        // no-op, matches the reference server
    }

    @GetMapping(value = "/{username}",
            produces = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE})
    public User getUserByName(@PathVariable("username") String username) {
        return userService.getByUsername(username);
    }

    @PutMapping(value = "/{username}",
            consumes = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE,
                        MediaType.APPLICATION_FORM_URLENCODED_VALUE})
    public void updateUser(@PathVariable("username") String username, @RequestBody User user) {
        userService.update(username, user);
    }

    @DeleteMapping("/{username}")
    public void deleteUser(@PathVariable("username") String username) {
        userService.delete(username);
    }
}
