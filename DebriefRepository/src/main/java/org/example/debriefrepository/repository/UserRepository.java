package org.example.debriefrepository.repository;

import org.example.debriefrepository.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, Long> {
}