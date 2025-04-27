package org.example.debriefrepository.service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.example.debriefrepository.entity.Role;
import org.example.debriefrepository.entity.User;
import org.example.debriefrepository.entity.UserRole;
import org.example.debriefrepository.repository.RoleRepository;
import org.example.debriefrepository.repository.UserRepository;
import org.example.debriefrepository.types.input.UserInput;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserService extends GenericService<User, UserInput> {

    @Autowired
    private final UserRepository userRepository;

    @Autowired
    private final RoleRepository roleRepository;

    private static final Logger logger = LoggerFactory.getLogger(UserService.class);

    public User createUser(UserInput userInput) {
        User user = new User();
        List<String> skippedFields = new ArrayList<>();
        skippedFields.add("id");
        try {
            return userRepository.save(setFields(user, userInput, skippedFields));
        } catch (Exception e) {
            logger.error(e.getMessage());
            e.printStackTrace();
        }
        throw new RuntimeException("Error creating user");
    }


    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    public List<User> getUser(Map<String, Object> input){
        return super.getEntities(input);
    }

    @Transactional
    public Boolean deleteById(String id) {
        if (userRepository.findById(id).isEmpty())
            return false;
        try {
            userRepository.deleteById(id);
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }

        return true;
    }

    public User update(UserInput userInput) {
        String userId = userInput.id();
        List<String> skippedFields = new ArrayList<>();
        skippedFields.add("id");
        if (userId == null || userId.trim().isEmpty()) {
            throw new IllegalArgumentException("User ID cannot be null or empty");
        }
        try {
            User existingUser = userRepository.findById(userId)
                    .orElseThrow(() -> new IllegalArgumentException("User not found with ID: " + userId));
            return userRepository.save(setFields(existingUser, userInput, skippedFields));
        } catch (IllegalArgumentException e) {
            logger.error(e.getMessage());
            e.printStackTrace();
        }
        throw new RuntimeException("Error modifying user");
    }

    private User setFields(User user, UserInput input, List<String> skippedFields) {
        Map<String, Function<Object, Object>> customProcessors = new HashMap<>();
        customProcessors.put("roles", value -> {
            List<Role> roles;
            try {
                List<?> values = (List<?>) value;
                roles = values.stream()
                        .map(id -> roleRepository.findById(id.toString())
                                .orElseThrow(() -> new IllegalArgumentException("Role not found with ID: " + id.toString())))
                        .toList();

                user.getRoles().clear();
                List<UserRole> newRoles = roles.stream()
                        .map(role -> {
                            UserRole userRole = new UserRole();
                            userRole.setRole(role);
                            userRole.setUser(user);
                            return userRole;
                        })
                        .collect(Collectors.toCollection(ArrayList::new));
                return user.getRoles().addAll(newRoles);
            } catch (IllegalArgumentException e) {
                logger.error(e.getMessage());
                throw new RuntimeException(e.getMessage());
            }
        });
        return super.setFields(user, input, customProcessors, skippedFields);
    }

}
