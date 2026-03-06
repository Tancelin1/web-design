package com.example.exercice11.exo11;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.ServerResponse;

import static org.springframework.web.reactive.function.server.RequestPredicates.*;
import static org.springframework.web.reactive.function.server.RouterFunctions.route;

@Configuration
public class UserRouter {

    @Bean
    public RouterFunction<ServerResponse> userRoutes(UserHandler userHandler) {
        return route(GET("/api/users"), userHandler::getAllUsers)
                .andRoute(GET("/api/users/{id}"), userHandler::getUserById)
                .andRoute(POST("/api/users"), userHandler::addUser)
                .andRoute(PUT("/api/users/{id}"), userHandler::updateUser)
                .andRoute(DELETE("/api/users/{id}"), userHandler::deleteUser);
    }
}