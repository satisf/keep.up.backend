package com.moeller.keep.up.backend.service;

import com.auth0.jwk.Jwk;
import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.auth0.jwk.UrlJwkProvider;
import com.auth0.jwk.GuavaCachedJwkProvider;
import org.springframework.stereotype.Service;

import java.security.interfaces.RSAKey;

import java.net.URL;

@Service
public class CognitoService {

    public Boolean isTokenValid(String token) throws Exception {

        // Decode the key and set the kid
        DecodedJWT decodedJwtToken = JWT.decode(token);
        String kid = decodedJwtToken.getKeyId();

        UrlJwkProvider http = new UrlJwkProvider(new URL(System.getenv("JWKS_URL")));
        // Let's cache the result from Cognito for the default of 10 hours
        GuavaCachedJwkProvider provider = new GuavaCachedJwkProvider(http);
        Jwk jwk = provider.get(kid);

        Algorithm algorithm = Algorithm.RSA256((RSAKey) jwk.getPublicKey());
        JWTVerifier verifier = JWT.require(algorithm)
                .withIssuer((System.getenv("JWT_TOKEN_ISSUER")))
                .build(); //Reusable verifier instance
        DecodedJWT jwt = verifier.verify(token);

        return (jwt != null);
    }

    public String getUsername(String idToken) {
        return JWT.decode(idToken).getClaim("cognito:username").asString();
    }

    public String getSub(String idToken) {
        return JWT.decode(idToken).getClaim("sub").asString();
    }

}
