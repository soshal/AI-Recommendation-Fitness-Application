package com.fitness.user_service;

import com.fitness.user_service.dto.RegisterRequest;
import com.fitness.user_service.dto.UserResponse;
import com.fitness.user_service.model.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

@Service

public class UserService {



    private final UserRepository userRepository;

    public  UserService(UserRepository userRepository){

        this.userRepository = userRepository;

    }

    public UserResponse register(RegisterRequest request) {
        if (userRepository.existsByEmail(request.getEmail())) {

            User euser = userRepository.findByEmail(request.getEmail());


            UserResponse response = new UserResponse();




            response.setId(euser.getId());

            response.setEmail(euser.getEmail());
            response.setPassword(euser.getPassword());
            response.setFirstName(euser.getFirstName());
            response.setLastName(euser.getLastName());
            response.setCreatedAt(euser.getCreatedAt());
            response.setUpdatedAt(euser.getUpdatedAt());
        }

        User user = new User();
        user.setEmail(request.getEmail());
        user.setPassword(request.getPassword());
        user.setFirstName(request.getFirstName());
        user.setLastName(request.getLastName());

        User savedUser = userRepository.save(user);
        UserResponse uresponse = new UserResponse();
        uresponse.setId(savedUser.getId());
        uresponse.setEmail(savedUser.getEmail());
        uresponse.setPassword(savedUser.getPassword());
        uresponse.setFirstName(savedUser.getFirstName());
        uresponse.setLastName(savedUser.getLastName());
        uresponse.setCreatedAt(savedUser.getCreatedAt());
        uresponse.setUpdatedAt(savedUser.getUpdatedAt());


        return uresponse;
    }

    public UserResponse getUserProfile(String userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        return mapToUserResponse(user);
    }

    private UserResponse mapToUserResponse(User user) {
        UserResponse response = new UserResponse();
        response.setId(user.getId());
        response.setEmail(user.getEmail());
        response.setPassword(user.getPassword());
        response.setFirstName(user.getFirstName());
        response.setLastName(user.getLastName());
        response.setCreatedAt(user.getCreatedAt());
        response.setUpdatedAt(user.getUpdatedAt());
        return response;
    }



}
