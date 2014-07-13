package io.sensable.model;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by simonmadine on 12/07/2014.
 */
public class User implements Parcelable {

    private String username;
    private String email;
    private String accessToken;

    public User() {
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getAccessToken() {
        return accessToken;
    }

    public void setAccessToken(String accessToken) {
        this.accessToken = accessToken;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(username);
        dest.writeString(email);
        dest.writeString(accessToken);

    }
}