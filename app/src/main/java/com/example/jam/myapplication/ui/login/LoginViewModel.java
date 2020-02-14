package com.example.jam.myapplication.ui.login;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.ViewModel;
import android.content.Context;
import android.util.Log;
import android.util.Patterns;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.example.jam.myapplication.MySingleton;
import com.example.jam.myapplication.data.LoginRepository;
import com.example.jam.myapplication.R;
import com.example.jam.myapplication.data.Result;
import com.example.jam.myapplication.data.model.LoggedInUser;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class LoginViewModel extends ViewModel {

    private MutableLiveData<LoginFormState> loginFormState = new MutableLiveData<>();
    private MutableLiveData<LoginResult> loginResult = new MutableLiveData<>();
    private LoginRepository loginRepository;

    LoginViewModel(LoginRepository loginRepository) {
        this.loginRepository = loginRepository;
    }

    LiveData<LoginFormState> getLoginFormState() {
        return loginFormState;
    }

    LiveData<LoginResult> getLoginResult() {
        return loginResult;
    }

    public void login(String username, String password, Context context, String url) {
        // can be launched in a separate asynchronous job
//        Result<LoggedInUser> result = loginRepository.login(username, password);

//        if (result instanceof Result.Success) {
//            LoggedInUser data = ((Result.Success<LoggedInUser>) result).getData();
//            loginResult.setValue(new LoginResult(new LoggedInUserView(data.getDisplayName())));
//        } else {
//            loginResult.setValue(new LoginResult(R.string.login_failed));
//        }

        JSONObject params = new JSONObject();

        try {
            params.put("emailNumber", username);
            params.put("password", password);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(
                Request.Method.GET, url, params, new Response.Listener<JSONObject>() {

            int status = 0;
            String message = "";

            @Override
            public void onResponse(JSONObject response) {
                Log.d("onResponse", response.toString());
                try {
                    status = response.getInt("status");
                    message = response.getString("message");

                    if (status == 200){
                        JSONArray dataJson = response.getJSONArray("data");
                        JSONObject dataObj = dataJson.getJSONObject(0);

                        Integer id = dataObj.getInt("id");
                        String user = dataObj.getString("name");
                        Integer type = dataObj.getInt("type");
                        String email = dataObj.getString("email");
                        String emailNumber = "";

                        if (email != null){
                            emailNumber = email;
                        }else{
                            emailNumber = dataObj.getString("mobile");
                        }

                        Result<LoggedInUser> result = loginRepository.login(id.toString(), user, emailNumber, type);

                        if (result instanceof Result.Success) {
                        LoggedInUser data = ((Result.Success<LoggedInUser>) result).getData();
                        loginResult.setValue(new LoginResult(new LoggedInUserView(
                                data.getUserId(),
                                data.getDisplayName(),
                                data.getEmailNum(),
                                data.getUserType()
                        )));
                        }

                    }else{
                        loginResult.setValue(new LoginResult(
                            R.string.invalid_user_or_pass,
                                null
                        ));
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.d("Login Volley Error", error.toString());
                loginResult.setValue(new LoginResult(
                        null,
                        R.string.no_server
                ));
            }
        });
        MySingleton.getInstance(context).addToRequestQueue(jsonObjectRequest);
    }

    public void loginDataChanged(String username, String password) {
        if (!isUserNameValid(username)) {
            loginFormState.setValue(new LoginFormState(R.string.invalid_username, null));
        } else if (!isPasswordValid(password)) {
            loginFormState.setValue(new LoginFormState(null, R.string.invalid_password));
        } else {
            loginFormState.setValue(new LoginFormState(true));
        }
    }

    // A placeholder username validation check
    private boolean isUserNameValid(String username) {
        if (username == null) {
            return false;
        }
        if (username.contains("@")) {
            return Patterns.EMAIL_ADDRESS.matcher(username).matches();
        } else {
            return !username.trim().isEmpty();
        }
    }

    // A placeholder password validation check
    private boolean isPasswordValid(String password) {
        return password != null && password.trim().length() >= 8;
    }
}
