package com.blue.hosting.entity;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository("tokenInfoRepo")
public interface TokenInfoRepo extends JpaRepository<TokenInfoDAO, String> {
    @Override
    Optional<TokenInfoDAO> findById(String accountId);
}
