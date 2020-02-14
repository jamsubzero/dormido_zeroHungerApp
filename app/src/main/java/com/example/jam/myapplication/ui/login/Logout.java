package com.example.jam.myapplication.ui.login;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.provider.Settings;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.example.jam.myapplication.MainActivity;
import com.example.jam.myapplication.R;

import kotlinx.coroutines.GlobalScope;

public class Logout extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_logout);

        Intent intent = new Intent(this, MainActivity.class);

        Thread logout = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    getWindow().getDecorView().setSystemUiVisibility(
                            View.SYSTEM_UI_FLAG_HIDE_NAVIGATION |
                                    View.SYSTEM_UI_FLAG_FULLSCREEN
                    );

                    SharedPreferences sharedPreferences = getSharedPreferences("user", Context.MODE_PRIVATE);
                    SharedPreferences.Editor editor = sharedPreferences.edit();

                    editor.clear();
                    editor.commit();

                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        });
        logout.start();
        startActivity(intent);
        Toast.makeText(getApplicationContext(), "You have logged out", Toast.LENGTH_LONG).show();
        finish();
    }
}
