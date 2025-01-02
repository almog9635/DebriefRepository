package org.example.debriefrepository.service;

import lombok.RequiredArgsConstructor;
import org.example.debriefrepository.entity.Group;
import org.example.debriefrepository.entity.Mission;
import org.example.debriefrepository.entity.Role;
import org.example.debriefrepository.entity.User;
import org.example.debriefrepository.input.UserInput;
import org.example.debriefrepository.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserService {

    @Autowired
    private final UserRepository userRepository;

    @Autowired
    private final GroupRepository groupRepository;

    @Autowired
    private final RoleRepository roleRepository;

    @Autowired
    private final DebriefRepository debriefRepository;

    @Autowired
    private final LessonRepository lessonRepository;

    @Autowired
    private final MissionRepository missionRepository;

    public User createUser(UserInput userInput) {
        User user = new User();
        user.setFirstName(userInput.firstName());
        user.setLastName(userInput.lastName());
        user.setPassword(userInput.password());
        user.setServiceType(userInput.serviceType());

        // Map RoleInput to Role entities

        user.setRoles(extractRoles(userInput));

        user.setGroup(findGroupByName(userInput.group().groupName()));
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

    public User updateById(Long id, UserInput userInput) {
        User existingUser = getUserById(id);
        if (existingUser != null) {
            existingUser.setRoles(extractRoles(userInput));
            //map the missions
            //List<Mission> missions = userInput.missions();
            //existingUser.setMissions(userInput.missions());

            existingUser.setGroup(findGroupByName(userInput.group().groupName()));
            existingUser.setPassword(userInput.password());
            existingUser.setServiceType(userInput.serviceType());
            existingUser.setFirstName(userInput.firstName());
            existingUser.setLastName(userInput.lastName());
            userRepository.save(existingUser);
        }
        return existingUser;
    }

    private Group findGroupByName(String name) {
        return groupRepository.findByName(name);
    }

    private List<Role> extractRoles(UserInput userInput) {
        return userInput.roles().stream()
                .map(roleInput -> roleRepository.findByRoleName(roleInput.roleName()))
                .collect(Collectors.toList());
    }
}
