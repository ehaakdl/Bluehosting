package com.blue.hosting.entity;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface AccountInfoRepo extends JpaRepository<AccountInfoTb, String>{
    @Override
    Optional<AccountInfoTb> findById(String id);
}
