package com.finconnect.auth_service.repository;

import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import com.finconnect.auth_service.entity.Users;

@Repository
public interface UsersRepository extends JpaRepository<Users, UUID>{
    
    Optional<Users> findByEmail(String email);

}
