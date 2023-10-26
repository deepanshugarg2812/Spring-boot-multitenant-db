package com.main.controller;

import com.main.dto.request.UserDto;
import com.main.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.MDC;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping(value = "/user")
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;

    @PostMapping(value = "/add/{id}")
    public ResponseEntity<String> addUser(@PathVariable(name = "id") String id, @RequestBody UserDto userDto) {
        try {
            userService.addUser(userDto);
        } catch (Exception e) {
            log.error("[UserController] error occured", e);
            return ResponseEntity.ok("Failed");
        }
        return ResponseEntity.ok("Success");
    }
}
