package org.example.debriefrepository.service;

import jakarta.persistence.Column;
import lombok.RequiredArgsConstructor;
import org.example.debriefrepository.entity.Debrief;
import org.example.debriefrepository.repository.DebriefRepository;
import org.example.debriefrepository.repository.GroupRepository;
import org.example.debriefrepository.repository.UserRepository;
import org.example.debriefrepository.types.DebriefInput;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.lang.reflect.Field;
import java.time.ZonedDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Objects;

@Service
@RequiredArgsConstructor
public class DebriefService {

    @Autowired
    private final DebriefRepository debriefRepository;

    @Autowired
    private final UserRepository userRepository;

    @Autowired
    private final GroupRepository groupRepository;

    public Debrief createDebrief(DebriefInput debrief) {
        return debriefRepository.save(mapToDebrief(debrief));
    }

    public List<Debrief> getAllDebriefs() {
        return debriefRepository.findAll();
    }

    public List<Debrief> getDebriefs(Map<String, Object> field) {
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
                    Debrief debrief = debriefRepository.findById(Long.parseLong(value.toString()))
                            .orElseThrow(() -> new IllegalArgumentException("Debrief not found with id: " + value));
                    return List.of(debrief);

                case "date":
                    return debriefRepository.findByDate((ZonedDateTime) value);

                case "user":
                    return debriefRepository.findByUser_Id((String) value);

                case "group":
                    return handleGroupQuery(value);

                case "lessons":
                    //return handleLessonQuery(value);
                    break;

                case "mission":
                    //return handleMissionQuery(value);
                    break;

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
        return null;
    }

    public Boolean deleteDebriefById(Long id) {
        if(debriefRepository.findById(id).orElse(null) == null) {
            return false;
        }
        debriefRepository.deleteById(id);
        return true;
    }

    /* todo: understand how to get the lessons and missions
       and also update the metadata                         */
    public Debrief updateDebrief(Map<String, Object> debriefUpdate) {
        try {
            Debrief existingDebrief = debriefRepository.findById((String)debriefUpdate.get("id"))
                    .orElse(null);
            Arrays.stream(DebriefInput.class.getFields()).forEach(field -> {
                String fieldName = field.getName();
                if (debriefUpdate.containsKey(fieldName)) {
                    field.setAccessible(true);
                    try {
                        Object value = debriefUpdate.get(fieldName);
                        Field dbField = Debrief.class.getDeclaredField(fieldName);
                        Column annotation = dbField.getAnnotation(Column.class);
                        boolean isOptional = annotation.nullable();

                        if (!isOptional && Objects.isNull(value)) {
                            throw new IllegalArgumentException("The field " + fieldName + " is null or empty");
                        }

                        field.set(existingDebrief, value);
                    } catch (IllegalAccessException | NoSuchFieldException e) {
                        throw new RuntimeException(e);
                    }
                }
            });
            return debriefRepository.save(existingDebrief);
        } catch(Exception e) {
            e.printStackTrace();
            throw new RuntimeException("Unexpected error occurred", e);
        }
    }

    private Debrief mapToDebrief(DebriefInput input) {
        Debrief debrief = new Debrief();
        debrief.setContent(input.content());
        debrief.setDate(input.date());
        debrief.setGroup(groupRepository.findByName(input.group()));
        //debrief.setLessons();
        //debrief.setMissions();
        return debrief;
    }

    private List<Debrief> handleGroupQuery(Object group) throws Exception {
        if (!(group instanceof Map)) {
            throw new IllegalArgumentException("Group value must be a Map");
        }

        Map<String, Object> groupMap = (Map<String, Object>) group;

        if (groupMap.containsKey("name")) {
            Object groupName = groupMap.get("name");
            if (groupName != null) {
                return debriefRepository.findByGroupName(groupName.toString());
            }
        }

        if (groupMap.containsKey("id")) {
            Object groupId = groupMap.get("id");
            if (groupId != null) {
                return debriefRepository.findByGroup_Id(groupId.toString());
            }
        }

        throw new IllegalArgumentException("Invalid group fields: Must contain either 'name' or 'id'");
    }

}
