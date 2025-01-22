package org.example.debriefrepository.service;

import lombok.RequiredArgsConstructor;
import org.example.debriefrepository.entity.Group;
import org.example.debriefrepository.entity.User;
import org.example.debriefrepository.repository.GroupRepository;
import org.example.debriefrepository.repository.UserRepository;
import org.example.debriefrepository.types.GroupInput;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class GroupService {

    private final GroupRepository groupRepository;
    private final UserRepository userRepository;

    public List<Group> findAll() {
        return groupRepository.findAll();
    }

    public Group getGroup(Map<String, Object> field) {
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
                    return groupRepository.findById(Long.parseLong(value.toString()))
                            .orElseThrow(() -> new IllegalArgumentException("The group id " + value + " is not found"));

                case "name":
                    return groupRepository.findByName((String) value);
            }
        }catch (IllegalArgumentException e) {
            System.err.println("Validation Error: " + e.getMessage());
            throw e;
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("Unexpected error occurred", e);
        }
        return null;
    }

    public Group update(Map<String, Object> input) {
        try{
            Group existingGroup = groupRepository.findById((Long)input.get("id"))
                    .orElse(null);
            if(existingGroup != null) {
                for (Map.Entry<String, Object> entry : input.entrySet()) {
                    String key = entry.getKey();
                    Object value = entry.getValue();
                    switch (key) {
                        case "name":
                            existingGroup.setName((String) value);
                            break;

                        case "commander":
                            existingGroup.setCommander((userRepository.findById(Long.parseLong(value.toString()))
                                    .orElse(null)));
                            break;

                        case "users":

                            break;

                        default:
                            throw new IllegalArgumentException("Unsupported key: " + key);
                    }
                }
                return existingGroup;
            }
            return existingGroup;
        } catch (Error error){
            error.printStackTrace();
            throw new RuntimeException("Unexpected error occurred");
        }

    }

    public Group create(GroupInput groupInput) {
        Group group = new Group();
        group.setName(groupInput.name());
        group.setCommander(null);
        if(groupInput.commander() != null) {
            group.setCommander(userRepository.findById(groupInput.commander()).orElse(null));
        }
        return groupRepository.save(group);
    }

    public Boolean deleteById(Long id) {
        if(groupRepository.existsById(id)) {
            groupRepository.deleteById(id);
            return true;
        }
        return false;
    }

    /* todo: to map the object to users,
        also to do it in to the roles in user service */
    private Set<User> handleUsers(Set<Long> users) {
        return null;
    }

}
