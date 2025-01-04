package org.example.debriefrepository.service;

import lombok.RequiredArgsConstructor;
import org.example.debriefrepository.entity.Group;
import org.example.debriefrepository.entity.Mission;
import org.example.debriefrepository.entity.User;
import org.example.debriefrepository.repository.*;
import org.example.debriefrepository.types.input.UserInput;
import org.example.debriefrepository.types.update.UserUpdate;
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
        return userRepository.save(mapToUser(userInput));
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

    /* todo: missions update */
    public User updateById(UserUpdate userUpdate) {
        User existingUser = getUserById(userUpdate.id());
        if (existingUser != null) {
            User user = mapToUser(userUpdate);
            existingUser.setRoles((user.getRoles()));
            existingUser.setGroup(user.getGroup());
            existingUser.setPassword(user.getPassword());
            existingUser.setServiceType(user.getServiceType());
            existingUser.setFirstName(user.getFirstName());
            existingUser.setLastName(user.getLastName());
            if(user.getMissions() != null)
                existingUser.setMissions(user.getMissions());
            userRepository.save(existingUser);
        }
        return existingUser;
    }

    private User mapToUser(UserInput input) {
        User user = new User();
        user.setFirstName(input.firstName());
        user.setLastName(input.lastName());
        user.setPassword(input.password());
        user.setServiceType(input.serviceType());
        user.setRoles(input.roles().stream()
                .map(roleInput -> roleRepository.findByRoleName(roleInput.roleName()))
                .toList());
        Group group = groupRepository.findByName(input.group().name());
        user.setGroup(group);
        return user;
    }

    private User mapToUser(UserUpdate update) {
        User user = new User();
        user.setFirstName(update.firstName());
        user.setLastName(update.lastName());
        user.setPassword(update.password());
        user.setServiceType(update.serviceType());
        user.setRoles(update.roles().stream()
                .map(roleInput -> roleRepository.findByRoleName(roleInput.roleName()))
                .toList());
        Group group = groupRepository.findByName(update.group().name());
        user.setGroup(group);
        if (update.missions() != null) {
            user.setMissions(update.missions().stream()
                    .map(missionInput -> {
                        Mission mission = new Mission();
                        mission.setContent(missionInput.content());
                        mission.setStartDate(missionInput.startDate());
                        mission.setDeadline(missionInput.deadline());
                        mission.setUser(getUserById(missionInput.userId()));
                        return mission;
                    })
                    .collect(Collectors.toSet()));
        }
        return user;
    }
}
