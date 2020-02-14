package com.example.jam.myapplication.ui.register;

import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModel;
import android.arch.lifecycle.ViewModelProvider;
import android.arch.lifecycle.ViewModelProviders;
import android.graphics.Color;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.example.jam.myapplication.R;

import org.json.JSONException;
import org.json.JSONObject;

public class RegisterActivity extends AppCompatActivity {

    private RegisterViewModel registerViewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_register);
        registerViewModel = ViewModelProviders.of(this).get(RegisterViewModel.class);

        final Spinner userTypeSpinner = findViewById(R.id.usertype_spinner);
        final EditText nameEditText = findViewById(R.id.username_et);
        final EditText mobileEditText = findViewById(R.id.mobile_number_et);
        final EditText emailEditText = findViewById(R.id.email_et);
        final EditText passwordEdiText = findViewById(R.id.password_et);
        final EditText confirmPassEditText = findViewById(R.id.confirm_pass_et);
        final Button submitButton = findViewById(R.id.submit_btn);

        registerViewModel.getRegistrationFormState().observe(this, new Observer<RegisterFormState>() {
            @Override
            public void onChanged(@Nullable RegisterFormState registerFormState) {
                if( registerFormState == null){
                    return;
                }
                submitButton.setEnabled(registerFormState.isRegFormValid());
                Boolean x = registerFormState.isRegFormValid();
                Log.d("isRegFormValid", x.toString());

                if (registerFormState.getUserNameError() != null){
                    nameEditText.setError(getString(registerFormState.getUserNameError()));
                }else if (registerFormState.getUserTypeError() != null){
                    TextView spinnerError = (TextView) userTypeSpinner.getSelectedView();
                    spinnerError.setError("");
                    spinnerError.setTextColor(Color.RED);
                    spinnerError.setText(getString(registerFormState.getUserTypeError()));
                }else if (registerFormState.getMobileNumberError() != null){
                    mobileEditText.setError(getString(registerFormState.getMobileNumberError()));
                }else if (registerFormState.getEmailError() != null){
                    emailEditText.setError(getString(registerFormState.getEmailError()));
                }else if(registerFormState.getPasswordError() != null){
                    passwordEdiText.setError(getString(registerFormState.getPasswordError()));
                }else if(registerFormState.getConfirmPassError() != null){
                    confirmPassEditText.setError(getString(registerFormState.getConfirmPassError()));
                }
            }
        });

        registerViewModel.getRegistrationResult().observe(this, new Observer<RegistrationResult>() {
            @Override
            public void onChanged(@Nullable RegistrationResult registrationResult) {
                if (registrationResult == null){
                    return;
                }
                //hide progress bar
                hideProgressBar();

                if (registrationResult.getError() != null){
                    Toast.makeText(getApplicationContext(), registrationResult.getError(), Toast.LENGTH_LONG).show();
                }
                if (registrationResult.getCustomError() != null){
                    if (registrationResult.getMobileNumberExistError() != null){
                        mobileEditText.setError(registrationResult.getMobileNumberExistError());
                        Toast.makeText(getApplicationContext(), registrationResult.getCustomError(), Toast.LENGTH_LONG).show();
                    }else if (registrationResult.getEmailExistError() != null){
                        emailEditText.setError(registrationResult.getEmailExistError());
                        Toast.makeText(getApplicationContext(), registrationResult.getCustomError(), Toast.LENGTH_LONG).show();
                    }else {
                        Toast.makeText(getApplicationContext(), registrationResult.getCustomError(), Toast.LENGTH_LONG).show();
                    }

                }
                if (registrationResult.getSuccess() != null){
                    Toast.makeText(getApplicationContext(), getResources().getString(R.string.registration_success), Toast.LENGTH_LONG).show();
                    finish();
                }

            }
        });

        TextWatcher afterTextChangedListener = new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {}

            @Override
            public void afterTextChanged(Editable s) {
                registerViewModel.regDataChanged(
                        userTypeSpinner.getSelectedItemPosition(),
                        nameEditText.getText().toString(),
                        mobileEditText.getText().toString(),
                        emailEditText.getText().toString(),
                        passwordEdiText.getText().toString(),
                        confirmPassEditText.getText().toString()
                );
            }
        };


        userTypeSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            int firstRun = 0; //removes error on first run of registration activity
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (firstRun++ > 1){
                    registerViewModel.regDataChanged(
                            userTypeSpinner.getSelectedItemPosition(),
                            nameEditText.getText().toString(),
                            mobileEditText.getText().toString(),
                            emailEditText.getText().toString(),
                            passwordEdiText.getText().toString(),
                            confirmPassEditText.getText().toString()
                    );
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        nameEditText.addTextChangedListener(afterTextChangedListener);
        mobileEditText.addTextChangedListener(afterTextChangedListener);
        emailEditText.addTextChangedListener(afterTextChangedListener);
        passwordEdiText.addTextChangedListener(afterTextChangedListener);
        confirmPassEditText.addTextChangedListener(afterTextChangedListener);

        submitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //show progressbar
                showProgressBar();

                //register
                String url = "http://zerop.ml/agri/register.php";
                JSONObject params = new JSONObject();

                try {
                    params.put("type", userTypeSpinner.getSelectedItemPosition());
                    params.put("name", nameEditText.getText().toString());
                    params.put("mobile",  mobileEditText.getText().toString());
                    params.put("email", emailEditText.getText().toString());
                    params.put("password", confirmPassEditText.getText().toString());
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                registerViewModel.submit(params,url,getApplicationContext());
            }
        });
    }

    public void showProgressBar(){
        ProgressBar progressBar = findViewById(R.id.regLoading);

        progressBar.setVisibility(View.VISIBLE);
        //to avoid touching while loading
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
    }

    public void hideProgressBar(){
        Log.d("ProgressBar", "GONE");
        ProgressBar progressBar = findViewById(R.id.regLoading);

        progressBar.setVisibility(View.GONE);
        getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
    }
}
