
// UserController.java
package com.uber.uber.controller;

import com.uber.uber.model.UserSchema;
import com.uber.uber.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

import java.lang.String;
@RestController
@RequestMapping("/api/users")
public class UserController {
    @Autowired
    private UserService userService;

    @GetMapping("/health-check")
    public String healthCheck() {
        return "User service is up and running";
    }

    @PostMapping("/register")
    public ResponseEntity<?> registerUser(@RequestBody UserSchema user) {
        try {
            UserSchema registeredUser = userService.registerUser(user);
            Map<String, String> response = new HashMap<>();
            response.put("id", registeredUser.getStringId());
            response.put("message", "User registered successfully");
            return ResponseEntity.ok(response);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (IllegalStateException e) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("An error occurred during registration");
        }
    }

    @GetMapping("/login")
    public String loginUser(@RequestBody UserSchema user) {
        try {
            System.out.println(user.getEmail()+" "+user.getPassword());
//            UserSchema existingUser = userService.loginUser(email, pass);
            return "User logged in successfully";
        } catch (IllegalArgumentException e) {
            return e.getMessage();
        } catch (Exception e) {
            return "An error occurred during login";
        }
    }

}