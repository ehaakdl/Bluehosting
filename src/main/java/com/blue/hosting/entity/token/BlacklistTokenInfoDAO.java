package com.blue.hosting.entity.token;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.util.Objects;

@Entity
@Table(name = "BLACKLIST_TOKEN_TB")
public class BlacklistTokenInfoDAO {

    public BlacklistTokenInfoDAO(String mJwtHash, long mExpireTime) {
        this.mJwtHash = mJwtHash;
        this.mExpireTime = mExpireTime;
    }

    public BlacklistTokenInfoDAO(){

    }

    @Id
    @Column(name = "jwt_hash", nullable = false, length = 256, columnDefinition = "nvarchar2")
    private String mJwtHash;

    public long getmExpireTime() {
        return mExpireTime;
    }

    @Column(name = "Expire_Time", nullable = false, columnDefinition = "long")
    private long mExpireTime;


    public String getmJwtHash() {
        return mJwtHash;
    }


}
