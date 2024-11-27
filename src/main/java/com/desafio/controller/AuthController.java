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

import java.util.HashMap;
import java.util.Map;

/**
 * Controller responsible for handling user authentication and registration.
 * Provides endpoints for user login (JWT generation) and account creation.
 */
@RestController
@RequestMapping("/api")
public class AuthController {

    /**
     * Service for generating JWT tokens.
     */
    private final JwtUtil jwtUtil;

    /**
     * Service for user-related operations.
     */
    private final IUserService userService;

    /**
     * Service for loading user details.
     */
    private final MyUserDetailsService userDetailsService;

    /**
     * Encoder for hashing and verifying passwords.
     */
    private final PasswordEncoder passwordEncoder;

    /**
     * Constructs the AuthController with its dependencies.
     *
     * @param userDetailsService Service for loading user details.
     * @param passwordEncoder    Service for password encryption and verification.
     * @param jwtUtil            Utility for generating JWT tokens.
     * @param userService        Service for user management.
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
     * Authenticates a user and generates a JWT token.
     * If the credentials are invalid, an appropriate error message is returned.
     *
     * @param authRequest DTO containing the username and password.
     * @return A JWT token if the credentials are valid, or an error response if not.
     */
    @PostMapping("/users/signin")
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
                String fullName = String.format("%s %s", user.getFirstName(), user.getLastName());

                Map<String, String> response = new HashMap<>();
                response.put("message", "Authentication successful");
                response.put("token", jwt);
                response.put("name", fullName);

                return ResponseEntity.ok(response);
            }

            return createErrorResponse("Invalid login or password", HttpStatus.UNAUTHORIZED);
        } catch (Exception ex) {
            return createErrorResponse("Invalid login or password", HttpStatus.UNAUTHORIZED);
        }
    }

    /**
     * Registers a new user in the system.
     *
     * @param userDTO DTO containing the user's registration details.
     * @return Success message if registration is successful or error messages for validation failures.
     */
    @PostMapping("/users/signup")
    @Operation(summary = "Register user", description = "Registers a new user in the system.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "User created successfully"),
            @ApiResponse(responseCode = "400", description = "Missing or invalid fields"),
            @ApiResponse(responseCode = "409", description = "Duplicate email or login"),
            @ApiResponse(responseCode = "500", description = "Server error")
    })
    public ResponseEntity<Map<String, String>> createUser(@RequestBody UserRequestDTO userDTO) {
        try {
            if (isMissingFields(userDTO)) {
                return createErrorResponse("Missing required fields", HttpStatus.BAD_REQUEST);
            }

            if (userService.existsByEmail(userDTO.getEmail())) {
                return createErrorResponse("Email already exists", HttpStatus.CONFLICT);
            }

            if (userService.existsByLogin(userDTO.getLogin())) {
                return createErrorResponse("Login already exists", HttpStatus.CONFLICT);
            }

            userService.save(new User(userDTO));
            return ResponseEntity.status(HttpStatus.CREATED).body(Map.of("message", "User created successfully"));
        } catch (Exception ex) {
            return createErrorResponse("Error processing user registration", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * Checks if the required fields for user registration are missing.
     *
     * @param userDTO The DTO containing user registration data.
     * @return True if required fields are missing, false otherwise.
     */
    private boolean isMissingFields(UserRequestDTO userDTO) {
        return userDTO.getEmail() == null || userDTO.getEmail().isEmpty()
                || userDTO.getLogin() == null || userDTO.getLogin().isEmpty()
                || userDTO.getPassword() == null || userDTO.getPassword().isEmpty();
    }

    /**
     * Creates a standardized error response with a message and HTTP status.
     *
     * @param message The error message to include in the response.
     * @param status  The HTTP status for the response.
     * @return A ResponseEntity containing the error message and status.
     */
    private ResponseEntity<Map<String, String>> createErrorResponse(String message, HttpStatus status) {
        return ResponseEntity.status(status).body(Map.of("message", message));
    }
}
