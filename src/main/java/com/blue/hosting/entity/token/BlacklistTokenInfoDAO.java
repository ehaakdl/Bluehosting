package com.blue.hosting.entity.token;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "BLACKLIST_LOGIN_TOKEN_TB")
public class BlacklistTokenInfoDAO {

    public BlacklistTokenInfoDAO(String mJwtHash) {
        this.mJwtHash = mJwtHash;
    }

    public BlacklistTokenInfoDAO(){

    }

    @Id
    @Column(name = "jwt_hash", nullable = false, length = 256, columnDefinition = "nvarchar2")
    private String mJwtHash;

    public String getmJwtHash() {
        return mJwtHash;
    }


}
