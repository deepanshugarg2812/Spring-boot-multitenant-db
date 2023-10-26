package com.main.service.impl;

import com.main.dto.request.UserDto;
import com.main.entity.User;
import com.main.repo.UserRepository;
import com.main.service.UserService;
import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@RequiredArgsConstructor
@Service
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;

    @Override
    public void addUser(UserDto userDto) {
        log.info("[addUser] request came for add user {}", userDto);
        User user = new User();
        user.setName(userDto.getName());
        user.setId(userDto.getId());
        user = userRepository.save(user);
        log.info("saved user info is {}", user);
    }
}
