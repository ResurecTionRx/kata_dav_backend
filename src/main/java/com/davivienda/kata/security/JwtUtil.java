package com.davivienda.kata.security;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.auth0.jwt.interfaces.JWTVerifier;
import org.springframework.stereotype.Component;

import java.util.Date;

@Component
public class JwtUtil {

    private static final String SECRET = "mi_clave_secreta"; // ⚠️ Puedes moverlo a application.properties
    private static final long EXPIRATION_TIME = 86400000; // 1 día en milisegundos
    private static final String ROLE_CLAIM = "role";

    public String generateToken(String email, String role) {
        return JWT.create()
                .withSubject(email)
                .withClaim(ROLE_CLAIM, role)                    // 👈 añade el claim
                .withExpiresAt(new Date(System.currentTimeMillis() + EXPIRATION_TIME))
                .sign(Algorithm.HMAC256(SECRET));
    }

    public String validateTokenAndRetrieveSubject(String token) throws JWTVerificationException {
        JWTVerifier verifier = JWT.require(Algorithm.HMAC256(SECRET)).build();
        DecodedJWT jwt = verifier.verify(token);
        return jwt.getSubject();
    }
    public String extractEmail(String token) {
        return JWT.require(Algorithm.HMAC256(SECRET))
                .build()
                .verify(token)
                .getSubject();
    }

    /* Obtiene el rol */
    public String extractRole(String token) {
        return JWT.require(Algorithm.HMAC256(SECRET))
                .build()
                .verify(token)
                .getClaim(ROLE_CLAIM)
                .asString();
    }
}
