package com.blue.hosting.entity.account;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository("accountInfoRepo")
public interface AccountInfoRepo extends JpaRepository<AccountInfoDAO, String>{
    @Override
    Optional<AccountInfoDAO> findById(String id);

    List<AccountInfoDAO> findByEmail(String email);

    boolean existsByEmail(String email);
}
