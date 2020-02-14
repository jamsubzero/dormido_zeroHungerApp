package com.example.jam.myapplication.ui.login;

import android.support.annotation.Nullable;

/**
 * Authentication result : success (user details) or error message.
 */
class LoginResult {
    @Nullable
    private LoggedInUserView success;
    @Nullable
    private Integer error;
    @Nullable
    private Integer noServer;

    LoginResult(@Nullable Integer error,
                @Nullable Integer noServer) {
        this.error = error;
        this.noServer = noServer;
    }

    LoginResult(@Nullable LoggedInUserView success) {
        this.success = success;
    }

    @Nullable
    LoggedInUserView getSuccess() {
        return success;
    }

    @Nullable
    Integer getError() {
        return error;
    }

    @Nullable
    Integer getNoServer(){return noServer;}
}
