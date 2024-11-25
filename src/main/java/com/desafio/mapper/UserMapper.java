package com.desafio.mapper;

import java.util.List;

import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.RequestParam;

import com.desafio.dto.response.UserResponseDTO;
import com.desafio.entity.User;
import com.desafio.service.IUserService;

@Component
public class UserMapper {

    private final IUserService userService;

    public UserMapper(IUserService userService) {
        this.userService = userService;
    }

    public ResponseEntity<List<UserResponseDTO>> getAllUsers(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "id") String sortBy) {
        try {
            List<UserResponseDTO> users = userService.findAll(page, size, sortBy);

            if (users.isEmpty()) {
                return ResponseEntity
                        .noContent()
                        .build();
            }

            HttpHeaders headers = new HttpHeaders();
            headers.add("X-Total-Count", String.valueOf(userService.getTotalCount()));
            headers.add("X-Page-Number", String.valueOf(page));
            headers.add("X-Page-Size", String.valueOf(size));

            return ResponseEntity
                    .ok()
                    .headers(headers)
                    .body(users);
        } catch (IllegalArgumentException e) {
            return ResponseEntity
                    .badRequest()
                    .build();
        } catch (Exception e) {
            return ResponseEntity
                    .internalServerError()
                    .build();
        }
    }

}
