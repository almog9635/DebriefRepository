package org.example.debriefrepository.repository;

import org.example.debriefrepository.entity.User;
import org.example.debriefrepository.types.Rank;
import org.example.debriefrepository.types.ServiceType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User, String> {
    List<User> findByFirstName(String firstName);
    List<User> findByLastName(String lastName);
    List<User> findByRank(Rank rank);
    List<User> findByServiceType(ServiceType serviceType);
    List<User> findByGroupName(String groupName);
    List<User> findByGroupId(String groupId);

    @Query("SELECT DISTINCT u FROM User u " +
            "JOIN u.roles ur " +
            "JOIN ur.role r " +
            "WHERE r.name = :name")
    List<User> findByRolesName(@Param("name") String name);
}