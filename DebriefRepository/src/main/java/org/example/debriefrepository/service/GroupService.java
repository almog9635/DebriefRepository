package org.example.debriefrepository.service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.example.debriefrepository.entity.Group;
import org.example.debriefrepository.repository.GroupRepository;
import org.example.debriefrepository.types.input.GroupInput;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.*;

@Service
@RequiredArgsConstructor
public class GroupService extends GenericService<Group, GroupInput> {

    @Autowired
    private final GroupRepository groupRepository;
    
    private static final Logger logger = LoggerFactory.getLogger(UserService.class);

    public List<Group> findAll() {
        return groupRepository.findAll();
    }

    /**
     * Retrieves a group by filtering on a chosen field.
     * Iterates over the provided map, dynamically builds a repository method name and invokes it.
     */
    public Group getGroup(Map<String, Object> chosenField) {
        return super.getEntities(chosenField).getFirst();
    }

    public Group update(GroupInput input) {
        try {
            String groupId = input.id();
            Group existingGroup = groupRepository.findById(groupId)
                    .orElseThrow(() -> new IllegalArgumentException("Group not found with ID: " + groupId));
            return groupRepository.save(setFields(existingGroup, input));
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            throw new RuntimeException("Error updating group", e);
        }
    }

    public Group create(GroupInput groupInput) {
        return groupRepository.save(setFields(new Group(), groupInput));
    }

    private Group setFields(Group group, GroupInput input) {
        List<String> skippedFields = new ArrayList<>();
        skippedFields.add("id");
        return super.setFields(group, input, null, skippedFields);
    }

    @Transactional
    public Boolean deleteById(String id) {
        if (groupRepository.findById(id).isEmpty())
            return false;
        try {
            groupRepository.deleteById(id);
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }

        return true;
    }
}
