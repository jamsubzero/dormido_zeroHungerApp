package com.example.jam.myapplication.ui.register;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.ViewModel;
import android.content.Context;
import android.content.res.Resources;
import android.util.Log;
import android.util.Patterns;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.example.jam.myapplication.MySingleton;
import com.example.jam.myapplication.R;

import org.json.JSONException;
import org.json.JSONObject;

public class RegisterViewModel extends ViewModel {

    private MutableLiveData<RegisterFormState> regFormState = new MutableLiveData<>();
    private MutableLiveData<RegistrationResult> regResult = new MutableLiveData<>();

    LiveData<RegisterFormState> getRegistrationFormState(){return regFormState;}
    LiveData<RegistrationResult> getRegistrationResult(){return regResult;}

    public void submit(JSONObject params, String url, Context context){

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest
                (Request.Method.POST, url, params, new Response.Listener<JSONObject>() {

                    @Override
                    public void onResponse(JSONObject response) {
                        Log.d("Response", response.toString());
                        int status = 0;
                        String message = "";
                        try {
                            status = response.getInt("status");
                            message = response.getString("message");
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                        if (status == 200){
                            regResult.setValue(new RegistrationResult(
                                    R.string.registration_success,
                                    null
                            ));
                        }else if (status == 11){
                            regResult.setValue(new RegistrationResult(
                                    message,
                                    message,
                                    null
                            ));
                        }else if(status == 12){

                            regResult.setValue(new RegistrationResult(
                                    message,
                                    null,
                                    message
                            ));
                        }else if(status == 13){
                            regResult.setValue(new RegistrationResult(
                                    message,
                                    null,
                                    null
                            ));
                        }
                    }
                }, new Response.ErrorListener() {

                    @Override
                    public void onErrorResponse(VolleyError error) {
                        // TODO: Handle error
                        Log.d("Response Error", error.toString());
                        regResult.setValue(new RegistrationResult(
                                null,
                                R.string.no_server
                        ));
                    }
                });

        MySingleton.getInstance(context).addToRequestQueue(jsonObjectRequest);

    }

    public void regDataChanged(
            int type,
            String name,
            String mobile,
            String email,
            String password,
            String confirmpass
    ){
        if(!isTypeValid(type)){
            regFormState.setValue(new RegisterFormState(
                    R.string.invalid_user_type,
                    null,
                    null,
                    null,
                    null,
                    null
                    ));
        }else if(!isNameValid(name)){
            regFormState.setValue(new RegisterFormState(
                    null,
                    R.string.name_empty,
                    null,
                    null,
                    null,
                    null
            ));
        }else if(!isMobileValid(mobile)){
            regFormState.setValue(new RegisterFormState(
                    null,
                    null,
                    R.string.invalid_mobile,
                    null,
                    null,
                    null
            ));
        }else if(!isEmailValid(email)){
            regFormState.setValue(new RegisterFormState(
                    null,
                    null,
                    null,
                    R.string.invalid_email,
                    null,
                    null
            ));
        }else if(!isPasswordValid(password)){
            regFormState.setValue(new RegisterFormState(
                    null,
                    null,
                    null,
                    null,
                    R.string.invalid_password,
                    null
            ));
        }else if(!isConfirmPasswordValid(password,confirmpass)){
            regFormState.setValue(new RegisterFormState(
                    null,
                    null,
                    null,
                    null,
                    null,
                    R.string.password_dont_match
            ));
        }else{
            regFormState.setValue(new RegisterFormState(true));
        }

    }

    private boolean isNameValid(String name){
        return name.length() > 0;
    }

    private boolean isTypeValid(int type){
        return type !=0;
    }

    private boolean isMobileValid(String mobile){
        return mobile != null && mobile.length() == 11;
    }

    private boolean isEmailValid(String email){
        Boolean x = email.contains("@");
        Log.d("Email", x.toString());
        if(email.contains("@")){
            Boolean y = Patterns.EMAIL_ADDRESS.matcher(email).matches();
            Log.d("Patterns", y.toString());
            return Patterns.EMAIL_ADDRESS.matcher(email).matches();
        }else {
            Boolean z = email.isEmpty();
            Log.d("Email Empty", z.toString());
            return email.isEmpty();
        }
    }

    private boolean isPasswordValid(String password){
        return password.length() >= 8;
    }

    private boolean isConfirmPasswordValid(String password, String confirmPass){
        return password.equals(confirmPass);
    }
}
