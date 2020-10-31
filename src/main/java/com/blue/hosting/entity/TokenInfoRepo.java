package com.blue.hosting.entity;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository("tokeInfoRepo")
public interface TokenInfoRepo extends JpaRepository<TokenInfoDTO, String> {
}
