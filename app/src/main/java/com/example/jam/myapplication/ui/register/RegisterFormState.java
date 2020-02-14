package com.example.jam.myapplication.ui.register;

import android.support.annotation.Nullable;

public class RegisterFormState {
    @Nullable
    private Integer userTypeError;
    @Nullable
    private Integer userNameError;
    @Nullable
    private Integer mobileNumberError;
    @Nullable
    private Integer emailError;
    @Nullable
    private  Integer passwordError;
    @Nullable
    private Integer confirmPassError;
    private boolean isRegFormValid;

    RegisterFormState(@Nullable Integer userTypeError,
                      @Nullable Integer userNameError,
                      @Nullable Integer mobileNumberError,
                      @Nullable Integer emailError,
                      @Nullable Integer passwordError,
                      @Nullable Integer confirmPassError){
        this.userTypeError = userTypeError;
        this.userNameError = userNameError;
        this.mobileNumberError = mobileNumberError;
        this.emailError = emailError;
        this.passwordError = passwordError;
        this.confirmPassError = confirmPassError;
        this.isRegFormValid = false;
    }

    RegisterFormState(boolean isRegFormValid){
        this.userTypeError = null;
        this.userNameError = null;
        this.mobileNumberError = null;
        this.emailError = null;
        this.passwordError = null;
        this.confirmPassError = null;
        this.isRegFormValid = isRegFormValid;
    }

    @Nullable
    Integer getUserTypeError(){return userTypeError;}

    @Nullable
    Integer getUserNameError(){return userNameError;}

    @Nullable
    Integer getMobileNumberError(){return mobileNumberError;}

    @Nullable
    Integer getEmailError(){return emailError;}

    @Nullable
    Integer getPasswordError(){return passwordError;}

    @Nullable
    Integer getConfirmPassError(){return confirmPassError;}

    boolean isRegFormValid(){return isRegFormValid;}


}
