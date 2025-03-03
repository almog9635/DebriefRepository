package org.example.debriefrepository.types;

import lombok.RequiredArgsConstructor;
import org.example.debriefrepository.entity.*;
import org.example.debriefrepository.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
@RequiredArgsConstructor
public class EntityFetcher {
    @Autowired
    private final DebriefRepository debriefRepository;

    @Autowired
    private final GroupRepository groupRepository;

    @Autowired
    private final UserRepository userRepository;

    @Autowired
    private final RoleRepository roleRepository;

    @Autowired
    private final MissionRepository missionRepository;

    @Autowired
    private final LessonRepository lessonRepository;

    private final Map<Class<? extends BaseEntity>, JpaRepository<? extends BaseEntity, String>> REPOSITORY_MAP;

    @Autowired
    public EntityFetcher(DebriefRepository debriefRepository1, GroupRepository groupRepository1, UserRepository userRepository1, RoleRepository roleRepository1, MissionRepository missionRepository1, LessonRepository lessonRepository1, GroupRepository groupRepository, RoleRepository roleRepository, MissionRepository missionRepository,
                         UserRepository userRepository, DebriefRepository debriefRepository, LessonRepository lessonRepository) {
        this.debriefRepository = debriefRepository1;
        this.groupRepository = groupRepository1;
        this.userRepository = userRepository1;
        this.roleRepository = roleRepository1;
        this.missionRepository = missionRepository1;
        this.lessonRepository = lessonRepository1;

        this.REPOSITORY_MAP = Map.of(
                Group.class, groupRepository,
                Role.class, roleRepository,
                Mission.class, missionRepository,
                User.class, userRepository,
                Debrief.class, debriefRepository,
                Lesson.class, lessonRepository
        );
    }

    public JpaRepository<? extends BaseEntity, String> getRepository(Class<? extends BaseEntity> entityClass) {
        return REPOSITORY_MAP.get(entityClass);
    }
}

