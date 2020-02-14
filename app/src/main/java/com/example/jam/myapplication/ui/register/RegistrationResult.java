package com.example.jam.myapplication.ui.register;

import android.support.annotation.Nullable;

class RegistrationResult {
    @Nullable
    private Integer success;
    @Nullable
    private Integer error;
    @Nullable
    private String customError;

    @Nullable
    private String mobileNumberExistError;
    @Nullable
    private String emailExistError;

    RegistrationResult(@Nullable Integer success,
                       @Nullable Integer error){
        this.success = success;
        this.error = error;
    }

    RegistrationResult(@Nullable String customError,
                       @Nullable String mobileNumberExistError,
                       @Nullable String emailExistError){
        this.customError = customError;
        this.mobileNumberExistError = mobileNumberExistError;
        this.emailExistError = emailExistError;
    }

    @Nullable
    Integer getSuccess(){return success;}

    @Nullable
    Integer getError(){return error;}

    @Nullable
    String getCustomError(){return customError;}

    @Nullable
    String getMobileNumberExistError(){return mobileNumberExistError;}

    @Nullable
    String getEmailExistError(){return emailExistError;}
}
