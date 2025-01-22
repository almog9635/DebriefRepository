package org.example.debriefrepository.service;

import jakarta.persistence.Column;
import lombok.RequiredArgsConstructor;
import org.example.debriefrepository.entity.*;
import org.example.debriefrepository.repository.*;
import org.example.debriefrepository.types.UserInput;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.lang.reflect.Field;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Objects;
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

    public List<User> getUser(Map<String, Object> field) {
        try {
            if (field == null || field.isEmpty()) {
                throw new IllegalArgumentException("The field map is null or empty");
            }

            if (field.size() > 1) {
                throw new IllegalArgumentException("The field map contains more than one field");
            }
            Map.Entry<String, Object> entry = field.entrySet().iterator().next();
            String key = entry.getKey();
            Object value = entry.getValue();

            if (value == null) {
                throw new IllegalArgumentException("The value for key " + key + " is null");
            }

            switch (key) {
                case "id":
                    User user = userRepository.findById(Long.parseLong(value.toString()))
                            .orElseThrow(() -> new IllegalArgumentException("User not found with id: " + value));
                    return List.of(user);

                case "firstName":
                    return userRepository.findByFirstName(value.toString());

                case "lastName":
                    return userRepository.findByLastName(value.toString());

                case "serviceType":
                    return userRepository.findByServiceType(value.toString());

                case "rank":
                    return userRepository.findByRank(value.toString());

                case "group":
                    return handleGroupQuery(value);

                case "debrief":
                    return handleDebriefQuery(value);

                case "name":
                    return handleRoleQuery(value);

                case "mission":
                    return handleMissionQuery(value);

                default:
                    throw new IllegalArgumentException("Unsupported key: " + key);
            }
        } catch (IllegalArgumentException e) {
            System.err.println("Validation Error: " + e.getMessage());
            throw e;
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("Unexpected error occurred", e);
        }
    }


    public Boolean deleteById(Long id) {
        if (userRepository.findById(id) == null)
            return false;
        userRepository.deleteById(id);
        return true;
    }

    public User update(Map<String, Object> userInput) {
        try {
            User existingUser = userRepository.findById((Long)userInput.get("id"))
                    .orElse(null);
            Arrays.stream(UserInput.class.getFields()).forEach(field -> {
                String fieldName = field.getName();
                if (userInput.containsKey(fieldName)) {
                    field.setAccessible(true);
                    try {
                        Object value = userInput.get(fieldName);
                        Field dbField = User.class.getDeclaredField(fieldName);
                        Column annotation = dbField.getAnnotation(Column.class);
                        boolean isOptional = annotation.nullable();

                        if (!isOptional && Objects.isNull(value)) {
                            throw new IllegalArgumentException("The field " + fieldName + " is null or empty");
                        }

                        field.set(existingUser, value);
                    } catch (IllegalAccessException | NoSuchFieldException e) {
                        throw new RuntimeException(e);
                    }
                }
            });
                return userRepository.save(existingUser);
        } catch (Error error) {
            error.printStackTrace();
            return null;
        }
    }

    private User mapToUser(UserInput input) {
        User user = new User();
        user.setFirstName(input.firstName());
        user.setLastName(input.lastName());
        user.setPassword(input.password());
        user.setServiceType(input.serviceType());
        user.setRank(input.rank());
        user.setRoles(input.roles().stream()
                .map(roleInput -> roleRepository.findByName(roleInput.name()))
                .collect(Collectors.toSet()));
        Group group = groupRepository.findByName(input.group());
        user.setGroup(group);
        return user;
    }

    private List<User> handleGroupQuery(Object group) throws Exception {
        if (!(group instanceof Map)) {
            throw new IllegalArgumentException("Group value must be a Map");
        }

        Map<String, Object> groupMap = (Map<String, Object>) group;

        if (groupMap.containsKey("name")) {
            Object groupName = groupMap.get("name");
            if (groupName != null) {
                return userRepository.findByGroupName(groupName.toString());
            }
        }

        if (groupMap.containsKey("id")) {
            Object groupId = groupMap.get("id");
            if (groupId != null) {
                return userRepository.findByGroup_Id(groupId.toString());
            }
        }

        throw new IllegalArgumentException("Invalid group fields: Must contain either 'name' or 'id'");
    }

    private List<User> handleDebriefQuery(Object debrief) throws Exception {
        if (!(debrief instanceof Map)) {
            throw new IllegalArgumentException("Debrief value must be a Map");
        }

        Map<String, Object> debriefMap = (Map<String, Object>) debrief;

        if (debriefMap.containsKey("id")) {
            Object debriefid = debriefMap.get("id");
            if (debriefid != null) {
                Debrief existingDebrief = debriefRepository.findById((Long)debriefid)
                        .orElseThrow(() -> new IllegalArgumentException("debrief not found with id: " + debriefid));
                return List.of(existingDebrief.getUser());
            }
        }

        if (debriefMap.containsKey("date")) {
            Object debriefDate = debriefMap.get("date");
            if (debriefDate != null) {
                List<Debrief> existingDebrief = debriefRepository.findByDate((LocalDate)debriefDate);
                if (existingDebrief != null) {
                    return existingDebrief.stream().map(Debrief::getUser).collect(Collectors.toList());
                }
            }
        }

        throw new IllegalArgumentException("Invalid debrief fields: Must contain either: 'id' or 'date'");
    }

    private List<User> handleMissionQuery(Object mission) throws Exception {
        if (!(mission instanceof Map)) {
            throw new IllegalArgumentException("Mission value must be a Map");
        }

        Map<String, Object> missionMap = (Map<String, Object>) mission;

        if (missionMap.containsKey("id")) {
            Object missionId = missionMap.get("id");
            if (missionId != null) {
                Mission existingMission = missionRepository.findById((Long)missionId)
                        .orElseThrow(() -> new IllegalArgumentException("mission not found with id: " + missionId));
                return List.of(existingMission.getUser());
            }
        }

        if (missionMap.containsKey("startDate")) {
            Object missionDate = missionMap.get("startDate");
            if (missionDate != null) {
                List<Mission> missions = missionRepository.findByStartDate((LocalDate)missionDate);
                if (missions != null) {
                    return missions.stream().map(Mission::getUser).collect(Collectors.toList());
                }
            }
        }

        if (missionMap.containsKey("deadline")) {
            Object missionDate = missionMap.get("deadline");
            if (missionDate != null) {
                List<Mission> missions = missionRepository.findByDeadline((LocalDate)missionDate);
                if (missions != null) {
                    return missions.stream().map(Mission::getUser).collect(Collectors.toList());
                }
            }
        }

        throw new IllegalArgumentException("Invalid mission fields: Must contain either 'id' or 'start date' or 'deadline'");
    }

    private List<User> handleRoleQuery(Object role) throws Exception {

        if (!(role instanceof Map)) {
            throw new IllegalArgumentException("Role value must be a Map");
        }

        Map<String, Object> roleMap = (Map<String, Object>) role;

        if (roleMap.containsKey("id")) {
            Object roleId = roleMap.get("id");
            if (roleId != null) {
                Role existingRole = roleRepository.findById((Long) roleId)
                        .orElseThrow(() -> new IllegalArgumentException("role not found with id: " + roleId));
                return existingRole.getUsers().stream().toList();
            }
        }

        if (roleMap.containsKey("name")) {
            Object roleName = roleMap.get("name");
            if (roleName != null) {
                Role existingRole = roleRepository.findByName((String)roleName);
                return existingRole.getUsers().stream().toList();
            }
        }

        throw new IllegalArgumentException("Invalid role fields: Must contain either 'id' or 'name'");
    }

}
