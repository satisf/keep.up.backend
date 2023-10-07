package com.moeller.keep.up.backend.handler;

import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyRequestEvent;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyResponseEvent;
import com.moeller.keep.up.backend.model.User;
import com.moeller.keep.up.backend.service.CognitoService;
import com.moeller.keep.up.backend.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

import java.util.function.Function;

@Component
public class RequestHandler implements Function<APIGatewayProxyRequestEvent, APIGatewayProxyResponseEvent> {

    private UserService userService;
    private CognitoService cognitoService;
    private UserHandler userHandler;
    private ChallengeHandler challengeHandler;

    @Autowired
    public void setUserService(UserService userService) {
        this.userService = userService;
    }

    @Autowired
    public void setCognitoService(CognitoService cognitoService) {
        this.cognitoService = cognitoService;
    }

    @Autowired
    public void setUserHandler(UserHandler userHandler){
        this.userHandler = userHandler;
    }
    @Autowired
    public void setChallengeHandler(ChallengeHandler challengeHandler) {
        this.challengeHandler = challengeHandler;
    }

    @Override
    public APIGatewayProxyResponseEvent apply(APIGatewayProxyRequestEvent apiGatewayProxyRequestEvent) {
        final long timeStart = System.currentTimeMillis();
        APIGatewayProxyResponseEvent response;

        try {
            String authToken = apiGatewayProxyRequestEvent.getHeaders().get("Authorization");
            if (authToken == null) {
                response =new APIGatewayProxyResponseEvent().withStatusCode(HttpStatus.UNAUTHORIZED.value());
                final long timeEnd = System.currentTimeMillis();
                System.out.println("Requestzeit unauthorized: " + (timeEnd - timeStart) + " Millisecs.");
                return response;
            }
            Boolean isValid = cognitoService.isTokenValid(authToken);
            if(!isValid){
                response = new APIGatewayProxyResponseEvent().withStatusCode(HttpStatus.UNAUTHORIZED.value());
                final long timeEnd = System.currentTimeMillis();
                System.out.println("Requestzeit invalid token: " + (timeEnd - timeStart) + " Millisecs.");
                return response;
            }
            String userName = cognitoService.getUsername(authToken);
            String userSub = cognitoService.getSub(authToken);
            User user = userService.findOrCreateUser(userSub, userName);

            String[] path = apiGatewayProxyRequestEvent.getPath().substring(1).split("/");

            switch(path[0]) {
                case "users": {
                    System.out.println("case users");
                    response = userHandler.handle(apiGatewayProxyRequestEvent, user);
                    final long timeEnd = System.currentTimeMillis();
                    System.out.println("Requestzeit users: " + (timeEnd - timeStart) + " Millisecs.");
                    return response;
                }
                case "challenges": {
                    System.out.println("case challenges");
                    response = challengeHandler.handle(apiGatewayProxyRequestEvent, user);
                    final long timeEnd = System.currentTimeMillis();
                    System.out.println("Requestzeit challenges: " + (timeEnd - timeStart) + " Millisecs.");
                    return response;
                }
            }
            final long timeEnd = System.currentTimeMillis();
            System.out.println("Requestzeit: " + (timeEnd - timeStart) + " Millisecs.");
            return new APIGatewayProxyResponseEvent().withBody(path.toString());
        } catch (Exception e) {
            e.printStackTrace();
            final long timeEnd = System.currentTimeMillis();
            System.out.println("Requestzeit error: " + (timeEnd - timeStart) + " Millisecs.");
            return new APIGatewayProxyResponseEvent().withStatusCode(HttpStatus.UNAUTHORIZED.value());
        }
    }

}

