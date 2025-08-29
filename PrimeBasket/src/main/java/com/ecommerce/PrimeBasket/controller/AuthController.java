package com.ecommerce.PrimeBasket.controller;

import com.ecommerce.PrimeBasket.config.AppConstants;
import com.ecommerce.PrimeBasket.model.AppRole;
import com.ecommerce.PrimeBasket.model.Role;
import com.ecommerce.PrimeBasket.model.User;
import com.ecommerce.PrimeBasket.payload.AuthenticationResult;
import com.ecommerce.PrimeBasket.repository.RoleRepository;
import com.ecommerce.PrimeBasket.repository.UserRepository;
import com.ecommerce.PrimeBasket.security.jwt.JwtUtils;
import com.ecommerce.PrimeBasket.security.request.LoginRequest;
import com.ecommerce.PrimeBasket.security.request.SignupRequest;
import com.ecommerce.PrimeBasket.security.response.MessageResponse;
import com.ecommerce.PrimeBasket.security.response.UserInfoResponse;
import com.ecommerce.PrimeBasket.security.services.UserDetailsImplementation;
import com.ecommerce.PrimeBasket.sevice.AuthService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    @Autowired
    AuthService authService;

    @PostMapping("/signin")
    public ResponseEntity<?> authenticateUser(@RequestBody LoginRequest loginRequest) {
        AuthenticationResult result = authService.login(loginRequest);
        return ResponseEntity.ok().header(HttpHeaders.SET_COOKIE,
                        result.getJwtCookie().toString())
                .body(result.getResponse());
    }

    @PostMapping("/signup")
    public ResponseEntity<?> registerUser(@Valid @RequestBody SignupRequest signUpRequest) {
        return authService.register(signUpRequest);
    }

    @GetMapping("/username")
    public String currentUserName(Authentication authentication){
        if (authentication != null)
            return authentication.getName();
        else
            return "";
    }


    @GetMapping("/user")
    public ResponseEntity<?> getUserDetails(Authentication authentication){
        return ResponseEntity.ok().body(authService.getCurrentUserDetails(authentication));
    }

    @PostMapping("/signout")
    public ResponseEntity<?> signoutUser(){
        ResponseCookie cookie = authService.logoutUser();
        return ResponseEntity.ok().header(HttpHeaders.SET_COOKIE,
                        cookie.toString())
                .body(new MessageResponse("You've been signed out!"));
    }
}