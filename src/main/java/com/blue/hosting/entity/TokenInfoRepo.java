package com.blue.hosting.entity;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface TokenInfoRepo extends JpaRepository<TokenInfoTb, String> {
    @Query(value = "SELECT token_info_secret_key_parse_seq.nextval FROM dual", nativeQuery = true)
    int getTokenInfoSecrtKeyParseSeqNextValue();
}
