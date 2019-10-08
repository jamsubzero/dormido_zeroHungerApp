package com.example.jam.myapplication;

import android.app.Activity;
import android.os.Bundle;
import android.provider.DocumentsContract;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class LoginFragment extends Fragment implements View.OnClickListener{

    Button regbtn;

    public LoginFragment(){}

    public static LoginFragment newInstance(){
        LoginFragment fragment = new LoginFragment();
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
         View rootView = inflater.inflate(R.layout.layout_login, container, false);
         regbtn = rootView.findViewById(R.id.reg_btn);
         regbtn.setOnClickListener(new View.OnClickListener() {
             @Override
             public void onClick(View v) {
                 //Log.d("RegbtbTEst","OK");
                 //Toast.makeText(v.getContext(),"Test",Toast.LENGTH_LONG).show();
                 goto_register();
             }
         });
        return rootView;
    }

    @Override
    public void onClick(View v) {

    }
    private void goto_register(){
        RegisterFragment registerFragment = new RegisterFragment();
        getActivity().getSupportFragmentManager().beginTransaction()
                .replace(R.id.frame, registerFragment, "findThisFragment")
                .addToBackStack(null)
                .commit();
    }
}
