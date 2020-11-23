package com.blue.hosting.utils.token;

public class TokenAttribute {
    public static final String ID_CLAIM= "id";
    public static final String IAT_CLAIM= "iat";
    public static final String SECRETKEY = "key";
    public static final String CHARSET = "UTF-8";
    public static final String HS256 = "HS256";
    public static final String JWT = "JWT";
    public static final long ACCESS_EXPIRETIME = 1000 * 900;
    public static final long REFRESH_EXPIRETIME = 1000 * 1800;
    public static final String ALG_HEADER = "alg";
    public static final String TYP_HEADER = "typ";
    public static final String ACCESS_TOKEN = "ACCESS_TOKEN";
    public static final String REFRESH_TOKEN = "REFRESH_TOKEN";
}
