package org.example.debriefrepository.service;

import lombok.RequiredArgsConstructor;
import org.example.debriefrepository.entity.User;
import org.example.debriefrepository.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class UserService {

    @Autowired
    private final UserRepository userRepository;

    public User createUser(User user) {
        userRepository.save(user);
        return user;
    }

    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    public User getUserById(Long id) {
        return userRepository.findById(id).orElse(null);
    }

    public Boolean deleteById(Long id) {
        if (getUserById(id) == null)
            return false;
        userRepository.deleteById(id);
        return true;
    }

    public User updateById(Long id, User user) {
        User existingUser = getUserById(id);
        if (existingUser != null) {
            existingUser.setMissions(user.getMissions());
            existingUser.setDebriefs(user.getDebriefs());
            existingUser.setGroup(user.getGroup());
            existingUser.setPassword(user.getPassword());
            existingUser.setServiceType(user.getServiceType());
            existingUser.setFirstName(user.getFirstName());
            existingUser.setLastName(user.getLastName());
            userRepository.save(existingUser);
        }
        return existingUser;
    }
}
