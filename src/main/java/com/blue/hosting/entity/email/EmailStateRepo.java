package com.blue.hosting.entity.email;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository("emailStateRepo")
public interface EmailStateRepo extends JpaRepository<EmailStateDAO, String>{
    @Override
    Optional<EmailStateDAO> findById(String email);


}
