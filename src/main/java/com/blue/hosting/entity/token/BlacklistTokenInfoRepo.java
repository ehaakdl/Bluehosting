package com.blue.hosting.entity.token;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository("blacklistTokenInfoRepo")
public interface BlacklistTokenInfoRepo extends JpaRepository<BlacklistTokenInfoDAO, String> {

}
