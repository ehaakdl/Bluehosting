package com.blue.hosting.entity.token;



import com.blue.hosting.entity.account.AccountInfoDAO;

import javax.persistence.*;
import java.util.Objects;


@Entity
@Table(name="token_info_tb")
public class TokenInfoDAO {
    @Id
    @Column(name = "jwt_hash", nullable=false, length=256, columnDefinition = "nvarchar2")
    private String mJwtHash;

    public long getmExpireTime() {
        return mExpireTime;
    }

    @Column(name = "Expire_Time", nullable=false, columnDefinition = "long")
    private long mExpireTime;

    @ManyToOne
    @JoinColumn(name = "member_id")
    private AccountInfoDAO mAccountInfo;

    public TokenInfoDAO(String mUsername) {
        this.mAccountInfo = new AccountInfoDAO(mUsername);
    }

    public TokenInfoDAO(String mJwtHash, String mUsername, long mExpireTime) {
        this.mJwtHash = mJwtHash;
        this.mAccountInfo = new AccountInfoDAO(mUsername);
        this.mExpireTime = mExpireTime;
    }

    public TokenInfoDAO() {
    }

    public String getmUsername() {
        return mAccountInfo.getUsername();
    }

    public String getmJwtHash() {
        return mJwtHash;
    }
}
