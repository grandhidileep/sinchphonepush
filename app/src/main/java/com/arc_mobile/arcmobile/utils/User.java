package com.arc_mobile.arcmobile.utils;

/**
 * Created by dgrandhi on 29/10/16.
 */

public class User {
    private String userMobile ;
    private String userCountry;
    private String userId;
    private String userName;
    private String userGcmId;

    public String getUserMobile() {
        return userMobile;
    }

    public void setUserMobile(String userMobile) {
        this.userMobile = userMobile;
    }

    public String getUserCountry() {
        return userCountry;
    }

    public void setUserCountry(String userCountry) {
        this.userCountry = userCountry;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getUserGcmId() {
        return userGcmId;
    }

    public void setUserGcmId(String userGcmId) {
        this.userGcmId = userGcmId;
    }

    @Override
    public String toString() {
        return "User{" +
                "userMobile='" + userMobile + '\'' +
                ", userCountry='" + userCountry + '\'' +
                ", userId='" + userId + '\'' +
                ", userName='" + userName + '\'' +
                ", userGcmId= "+userGcmId+
                '}';
    }

}
