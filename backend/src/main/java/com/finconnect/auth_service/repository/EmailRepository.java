package com.finconnect.auth_service.repository;

import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import com.finconnect.auth_service.entity.Email;

@Repository
public interface EmailRepository extends JpaRepository<Email, UUID> {

}
