package com.bezkoder.springjwt.controllers;

import com.bezkoder.springjwt.models.User;
import com.bezkoder.springjwt.payload.request.TokenValidationRequest;
import com.bezkoder.springjwt.payload.response.TokenValidationResponse;
import com.bezkoder.springjwt.repository.RoleRepository;
import com.bezkoder.springjwt.repository.UserRepository;
import com.bezkoder.springjwt.security.jwt.JwtUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

@CrossOrigin(origins = "*",maxAge = 3600)
@RestController
@RequestMapping("/api/auth")
public class ValidAuthController {

    @Autowired
    AuthenticationManager authenticationManager;

    @Autowired
    UserRepository userRepository;

    @Autowired
    RoleRepository roleRepository;

    @Autowired
    PasswordEncoder passwordEncoder;

    @Autowired
    JwtUtils jwtUtils;

    @PostMapping("/validate")
    public ResponseEntity<TokenValidationResponse> validateToken(@RequestBody TokenValidationRequest request) {
        TokenValidationResponse response = new TokenValidationResponse();

        String token = request.getToken();
        if (token == null || token.isEmpty()) {
            response.setValid(false);
            response.setError("Token is missing");
            return ResponseEntity.badRequest().body(response);
        }

        try {
            if (!jwtUtils.validateJwtToken(token)) {
                response.setValid(false);
                response.setError("Invalid or expired token");
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
            }

            String username = jwtUtils.getUserNameFromJwtToken(token);
            User user = userRepository.findByUsername(username)
                    .orElseThrow(() -> new RuntimeException("User not found"));

            response.setValid(true);
            response.setUsername(username);
            response.setRoles(
                    user.getRoles().stream()
                            .map(role -> role.getName().name())
                            .toList()
            );
            response.setExpiresAt(jwtUtils.getExpirationDateFromJwtToken(token));
            return ResponseEntity.ok(response);

        } catch (Exception ex) {
            response.setValid(false);
            response.setError(ex.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

}
