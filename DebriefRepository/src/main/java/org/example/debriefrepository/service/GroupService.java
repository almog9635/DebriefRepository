package org.example.debriefrepository.service;

import lombok.RequiredArgsConstructor;
import org.example.debriefrepository.entity.Group;
import org.example.debriefrepository.repository.GroupRepository;
import org.example.debriefrepository.types.input.GroupInput;
import org.example.debriefrepository.types.update.GroupUpdate;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class GroupService {

    private final GroupRepository groupRepository;

    public List<Group> findAll() {
        return groupRepository.findAll();
    }

    public Group findById(Long id) {
        return groupRepository.findById(id).orElse(null);
    }

    public Group findByName(String name) {
        return groupRepository.findByName(name);
    }

    public Group update(GroupUpdate groupUpdate) {
        Group existingGroup = findById(groupUpdate.id());
        if(existingGroup != null) {
            existingGroup.setName(groupUpdate.name());
            //existingGroup.setCommander(groupUpdate.Commander());
            return groupRepository.save(existingGroup);
        }
        return existingGroup;
    }

    public Group create(GroupInput groupInput) {
        Group group = new Group();
        group.setName(groupInput.name());
        return groupRepository.save(group);
    }

    public Boolean deleteById(Long id) {
        if(groupRepository.existsById(id)) {
            groupRepository.deleteById(id);
            return true;
        }
        return false;
    }
}
