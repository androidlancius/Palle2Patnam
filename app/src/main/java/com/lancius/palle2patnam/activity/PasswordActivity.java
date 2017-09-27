package com.lancius.palle2patnam.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.lancius.palle2patnam.R;

/**
 * Created by lancius on 7/3/2017.
 */

public class PasswordActivity extends AppCompatActivity {
    Toolbar toolbar;
    EditText oldPasswordEt,newPasswordEt,confirmPasswordEt;
    Button submit;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_password);
        toolbar=(Toolbar)findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        oldPasswordEt=(EditText)findViewById(R.id.input_old_password);
        newPasswordEt=(EditText)findViewById(R.id.input_new_password);
        confirmPasswordEt=(EditText)findViewById(R.id.input_conform_password);
        submit=(Button)findViewById(R.id.button_submit);
        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i=new Intent(getApplicationContext(),MainActivity.class);
                startActivity(i);
            }
        });
    }
}
