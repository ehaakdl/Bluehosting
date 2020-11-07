package com.blue.hosting.security;

public enum eRoleName {
    ROLE_USER("ROLE_USER"),
    ROLE_ADMIN("ROLE_ADMIN");

    public String getmRole() {
        return mRole;
    }

    String mRole;
    eRoleName(String mRole) {
        this.mRole = mRole;
    }
}
