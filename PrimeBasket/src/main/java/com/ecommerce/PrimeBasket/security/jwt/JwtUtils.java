package com.ecommerce.PrimeBasket.security.jwt;

import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.UnsupportedJwtException;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.security.Key;
import java.util.Date;

@Component
public class JwtUtils {
    private  static final Logger logger = LoggerFactory.getLogger(JwtUtils.class);

    @Value("${spring.app.jwtExpirationMs}")
    private int jwtExpirationMs;

    @Value("${spring.app.jwtSecret}")
    private String jwtSecret;

    //Getting JWT from header
    public String getJwtFromHeader(HttpServletRequest request){
        String bearer=request.getHeader("Authorization");
        logger.debug("Authorization Header: {}", bearer);
        if(bearer!=null && bearer.startsWith("Bearer ")){
            return bearer.substring(7);
        }
        return null;
    }
    //Generating token from username
    public String generateTTokenFromUsername(UserDetails userDetails){
        String username=userDetails.getUsername();
        return Jwts.builder().subject(username)
                .issuedAt(new Date()).expiration(new Date(new Date().getTime() + jwtExpirationMs))
                .signWith(key()).compact();
    }

    //Getting username from JWT token
    public String getUsernameFromJwtToken(String token){
        return Jwts.parser().verifyWith((SecretKey) key()).build().parseSignedClaims(token).getPayload().getSubject();
    }

    //Generate signing key
    public Key key(){
        return Keys.hmacShaKeyFor(Decoders.BASE64.decode(jwtSecret));
    }

    //Validate JWT token
    public boolean validateJwtToken(String authToken){
        try{
            System.out.println("Validate");
            Jwts.parser().verifyWith((SecretKey) key()).build().parseSignedClaims(authToken);
            return true;
        }catch (MalformedJwtException e){
            logger.error("Invalid JWT token:{}", e.getMessage());
        }catch (ExpiredJwtException e){
            logger.error("Expired JWT token:{}", e.getMessage());
        }catch(UnsupportedJwtException e){
            logger.error("Unsupported JWT token:{}", e.getMessage());
        }catch (IllegalArgumentException e){
            logger.error("JWT claims string is empty:{}",e.getMessage());
        }
        return false;
    }
}
