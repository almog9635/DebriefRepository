package org.example.debriefrepository.repository;

import org.example.debriefrepository.entity.Role;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RoleRepository extends JpaRepository<Role, String> {

    Role findByName(String roleName);
}
