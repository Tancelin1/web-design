package com.example.exercice11.exo11;

import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.ArrayList;
import java.util.List;

import static org.springframework.web.reactive.function.server.ServerResponse.*;

@Component
public class UserHandler {

    private final List<User> users = new ArrayList<>();
    private Long currentId = 1L;

    public Mono<ServerResponse> getAllUsers(ServerRequest request) {
        return ok().body(Flux.fromIterable(users), User.class);
    }

    public Mono<ServerResponse> getUserById(ServerRequest request) {
        Long id = Long.parseLong(request.pathVariable("id"));

        return Flux.fromIterable(users)
                .filter(user -> user.getId().equals(id))
                .next()
                .flatMap(user -> ok().bodyValue(user))
                .switchIfEmpty(notFound().build());
    }

    public Mono<ServerResponse> addUser(ServerRequest request) {
        return request.bodyToMono(User.class)
                .map(user -> {
                    user.setId(currentId++);
                    return user;
                })
                .doOnNext(users::add)
                .flatMap(user -> ok().bodyValue(user));
    }

    public Mono<ServerResponse> updateUser(ServerRequest request) {
        Long id = Long.parseLong(request.pathVariable("id"));

        return request.bodyToMono(User.class)
                .flatMap(updatedUser -> {
                    for (User user : users) {
                        if (user.getId().equals(id)) {
                            user.setName(updatedUser.getName());
                            user.setEmail(updatedUser.getEmail());
                            user.setActive(updatedUser.isActive());
                            return ok().bodyValue(user);
                        }
                    }
                    return notFound().build();
                });
    }

    public Mono<ServerResponse> deleteUser(ServerRequest request) {
        Long id = Long.parseLong(request.pathVariable("id"));

        boolean removed = users.removeIf(user -> user.getId().equals(id));

        return removed ? noContent().build() : notFound().build();
    }
}