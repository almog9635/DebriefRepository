package org.example.debriefrepository.service;

import lombok.RequiredArgsConstructor;
import org.example.debriefrepository.entity.Role;
import org.example.debriefrepository.repository.RoleRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class RoleService {

    @Autowired
    private final RoleRepository roleRepository;

    public List<Role> findAll() {
        return roleRepository.findAll();
    }

    public Role getRole(Map<String, Object> field) {
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
                    Role role = roleRepository.findById(value.toString())
                            .orElseThrow(() -> new IllegalArgumentException("Debrief not found with id: " + value));
                    return role;

                case "name":
                    return roleRepository.findByName((String) value);

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

    public Role save(Role role) {
        return roleRepository.save(role);
    }
}
