package com.dhbw.backend.repository;

import com.dhbw.backend.model.Users;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<Users, Long> {
    
    // Automatisch generierte Suche nach der E-Mail (z.B. für den Login)
    Optional<Users> findByEmail(String email);
    
    // Automatisch generierte Suche nach dem Benutzernamen
    Optional<Users> findByUsername(String username);
}