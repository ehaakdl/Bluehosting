package com.blue.hosting.entity;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository("accountInfoRepo")
public interface AccountInfoRepo extends JpaRepository<AccountInfoDAO, String>{
    @Override
    Optional<AccountInfoDAO> findById(String id);
}
