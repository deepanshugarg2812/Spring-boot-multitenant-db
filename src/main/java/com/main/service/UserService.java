package com.main.service;

import com.main.dto.request.UserDto;
import org.springframework.stereotype.Service;


public interface UserService {
    /**
     *
     * @param userDto
     */
    void addUser(UserDto userDto);
}
