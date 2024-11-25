package com.desafio.controller;

import com.desafio.dto.request.AuthRequestDTO;
import com.desafio.dto.request.UserRequestDTO;
import com.desafio.entity.User;
import com.desafio.service.IUserService;
import com.desafio.service.impl.MyUserDetailsService;
import com.desafio.util.JwtUtil;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * Controller responsible for user authentication and registration.
 * Provides endpoints for user authentication (JWT generation) and registration.
 */
@RestController
@RequestMapping("/api")
public class AuthController {

    private final JwtUtil jwtUtil;
    private final IUserService userService;
    private final MyUserDetailsService userDetailsService;
    private final PasswordEncoder passwordEncoder;

    /**
     * Constructs the AuthController with required dependencies.
     *
     * @param userDetailsService Service for loading user details.
     * @param passwordEncoder    Service for password encryption.
     * @param jwtUtil            Utility for generating JWT tokens.
     * @param userService        Service for user operations.
     */
    @Autowired
    public AuthController(MyUserDetailsService userDetailsService, PasswordEncoder passwordEncoder, JwtUtil jwtUtil,
                          IUserService userService) {
        this.userDetailsService = userDetailsService;
        this.passwordEncoder = passwordEncoder;
        this.jwtUtil = jwtUtil;
        this.userService = userService;
    }

    /**
     * Authenticates a user and generates a JWT token if credentials are valid.
     *
     * @param authRequest DTO containing the user's credentials.
     * @return {@link ResponseEntity} with JWT token or an error message.
     */
    @PostMapping("/signin")
    @Operation(summary = "Authenticate user", description = "Authenticates a user and returns a JWT token.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Authentication successful"),
            @ApiResponse(responseCode = "401", description = "Invalid login or password")
    })
    public ResponseEntity<Map<String, String>> createAuthenticationToken(@RequestBody AuthRequestDTO authRequest) {
        try {
            UserDetails userDetails = userDetailsService.loadUserByUsername(authRequest.getUsername());

            if (passwordEncoder.matches(authRequest.getPassword(), userDetails.getPassword())) {
                User user = userService.findByUsername(authRequest.getUsername());
                String jwt = jwtUtil.generateToken(userDetails.getUsername());
                String fullName = user.getFirstName() + " " + user.getLastName();

                return ResponseEntity.ok(Map.of(
                        "message", "Authentication successful",
                        "token", jwt,
                        "name", fullName
                ));
            }

            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of(
                    "message", "Invalid login or password"
            ));
        } catch (Exception ex) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of(
                    "message", "Invalid login or password"
            ));
        }
    }

    /**
     * Registers a new user in the system.
     *
     * @param userDTO DTO containing the user's details.
     * @return {@link ResponseEntity} with success or error message.
     */
    @PostMapping("/signup")
    @Operation(summary = "Register user", description = "Registers a new user in the system.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "User created successfully"),
            @ApiResponse(responseCode = "400", description = "Missing or invalid fields"),
            @ApiResponse(responseCode = "409", description = "Duplicate email or login"),
            @ApiResponse(responseCode = "500", description = "Server error")
    })
    public ResponseEntity<Map<String, String>> createUser(@RequestBody UserRequestDTO userDTO) {
        try {
            if (userDTO.getEmail() == null || userDTO.getLogin() == null || userDTO.getPassword() == null) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of(
                        "message", "Missing required fields"
                ));
            }

            if (userService.existsByEmail(userDTO.getEmail())) {
                return ResponseEntity.status(HttpStatus.CONFLICT).body(Map.of(
                        "message", "Email already exists"
                ));
            }

            if (userService.existsByLogin(userDTO.getLogin())) {
                return ResponseEntity.status(HttpStatus.CONFLICT).body(Map.of(
                        "message", "Login already exists"
                ));
            }

            userService.save(new User(userDTO));
            return ResponseEntity.status(HttpStatus.CREATED).body(Map.of(
                    "message", "User created successfully"
            ));
        } catch (Exception ex) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of(
                    "message", "Error processing user registration"
            ));
        }
    }
}
