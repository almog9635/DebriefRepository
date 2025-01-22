package org.example.debriefrepository.repository;

import org.example.debriefrepository.entity.Debrief;
import org.example.debriefrepository.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Set;

public interface UserRepository extends JpaRepository<User, Long> {
    List<User> findByFirstName(String firstName);
    List<User> findByLastName(String lastName);
    List<User> findByRank(String rank);
    List<User> findByServiceType(String serviceType);
    List<User> findByGroupName(String groupName);

    List<User> findByGroup_Id(String groupId);
    List<User> findByDebriefs(Set<Debrief> debriefs);
}