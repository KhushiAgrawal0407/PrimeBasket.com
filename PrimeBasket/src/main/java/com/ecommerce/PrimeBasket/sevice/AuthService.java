package com.ecommerce.PrimeBasket.sevice;

import com.ecommerce.PrimeBasket.payload.AuthenticationResult;
import com.ecommerce.PrimeBasket.security.request.LoginRequest;
import com.ecommerce.PrimeBasket.security.request.SignupRequest;
import com.ecommerce.PrimeBasket.security.response.MessageResponse;
import com.ecommerce.PrimeBasket.security.response.UserInfoResponse;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;

public interface AuthService {
    AuthenticationResult login(LoginRequest loginRequest);

    ResponseEntity<MessageResponse> register(SignupRequest signUpRequest);

    UserInfoResponse getCurrentUserDetails(Authentication authentication);

    ResponseCookie logoutUser();
}
