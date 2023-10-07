package com.moeller.keep.up.backend.handler;

import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyRequestEvent;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyResponseEvent;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.moeller.keep.up.backend.model.*;
import com.moeller.keep.up.backend.service.FriendshipService;
import com.moeller.keep.up.backend.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

@Component
public class UserHandler {

    UserService userService;

    FriendshipService friendshipService;

    @Autowired
    public void setUserService(UserService userService){
        this.userService = userService;
    }

    @Autowired
    public void setFriendshipService(FriendshipService friendshipService) { this.friendshipService = friendshipService;}

    public APIGatewayProxyResponseEvent handle(APIGatewayProxyRequestEvent apiGatewayProxyRequestEvent, User user) throws JsonProcessingException {
        String[] path = apiGatewayProxyRequestEvent.getPath().substring(1).split("/");

        // GET users
        if (apiGatewayProxyRequestEvent.getHttpMethod().equals(HttpMethod.GET.name()) && path.length == 1) {
            return getUser(user);
        }
        // GET friendships
        if (apiGatewayProxyRequestEvent.getHttpMethod().equals(HttpMethod.GET.name()) && path[1].equals("friendships")) {
            return getFriendships(user);
        }

        switch (path[1]) {
            case "friendship": return handleFriendship(user, path);
            default: return new APIGatewayProxyResponseEvent().withBody("default");
        }
    }

    private APIGatewayProxyResponseEvent getUser(User user) throws JsonProcessingException {
        return new APIGatewayProxyResponseEvent().withStatusCode(HttpStatus.OK.value()).withBody(
                new ObjectMapper().writeValueAsString(UserDTO.fromUser(user)));
    }
    private APIGatewayProxyResponseEvent getFriendships(User user) throws JsonProcessingException {
        return new APIGatewayProxyResponseEvent().withStatusCode(HttpStatus.OK.value()).withBody(
                new ObjectMapper().writeValueAsString(friendshipService.getAllFriendshipsForUser(user)
                        .stream()
                        .map(friendship -> FriendshipDTO.fromFriendship(friendship, user))
                        .toArray()));
    }

    private APIGatewayProxyResponseEvent handleFriendship(User user, String[] path) throws JsonProcessingException {
        User friend = userService.findUserOrThrow(path[2]);

        System.out.println("handle friendship: " + user.getUsername() + " and " + friend.getUsername());

        switch (path[3]) {
            case "offer": {
                Friendship friendship = friendshipService.createFriendship(user, friend);
                return new APIGatewayProxyResponseEvent().withStatusCode(200).withBody(
                        new ObjectMapper().writeValueAsString(friendship)
                );
            }
//            case "accept": {
//                friendshipService.acceptFriendship(user, friend);
//                return new APIGatewayProxyResponseEvent().withStatusCode(200);
//            }
//            case "deny": {
//                userService.denyFriendship(user, friend);
//                return new APIGatewayProxyResponseEvent().withStatusCode(200);
//            }
            default:
                return new APIGatewayProxyResponseEvent().withStatusCode(404);
        }

    }
}
