package com.blue.hosting.entity.token;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository("blacklistTokenInfoTb")
public interface BlacklistTokenInfoTb extends JpaRepository<BlacklistTokenInfoDAO, String> {

}
