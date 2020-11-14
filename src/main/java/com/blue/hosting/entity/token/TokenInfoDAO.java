package com.blue.hosting.entity.token;



import javax.persistence.*;


@Entity
@Table(name="token_info_tb")
public class TokenInfoDAO {
    @Id
    @Column(name = "jwt_hash", nullable=false, length=256, columnDefinition = "nvarchar2")
    private String mJwtHash;


    @Column(name = "member_id", nullable=false, length=15, columnDefinition = "nvarchar2")
    private String mUsername;

    public TokenInfoDAO(String mUsername) {
        this.mUsername = mUsername;
    }

    public TokenInfoDAO(String mJwtHash, String mUsername) {
        this.mJwtHash = mJwtHash;
        this.mUsername = mUsername;
    }

    public TokenInfoDAO() {
    }

    public String getmUsername() {
        return mUsername;
    }

    public String getmJwtHash() {
        return mJwtHash;
    }
}
