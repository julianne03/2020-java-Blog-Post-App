package com.example.azangazang;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class RegisterActivity extends AppCompatActivity {

    private EditText reg_email;
    private EditText reg_pass;
    private EditText reg_confirm_pass;
    private Button reg_btn;
    private Button reg_login;
    private ProgressBar reg_progress;

    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        mAuth = FirebaseAuth.getInstance();

        reg_email = (EditText) findViewById(R.id.reg_email);
        reg_pass = (EditText) findViewById(R.id.reg_password);
        reg_confirm_pass = (EditText) findViewById(R.id.reg_confirm_password);
        reg_btn = (Button) findViewById(R.id.reg_reg_btn);
        reg_login = (Button) findViewById(R.id.reg_login);
        reg_progress = (ProgressBar) findViewById(R.id.reg_progressbar);

        androidx.appcompat.app.ActionBar ab = getSupportActionBar();
        ab.hide();

        reg_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String email = reg_email.getText().toString();
                String pass = reg_pass.getText().toString();
                String confirm_pass = reg_confirm_pass.getText().toString();

                if(!TextUtils.isEmpty(email) && !TextUtils.isEmpty(pass) && !TextUtils.isEmpty(confirm_pass)) {
                    if(pass.equals(confirm_pass)) {

                        reg_progress.setVisibility(View.VISIBLE);
                        mAuth.createUserWithEmailAndPassword(email,pass).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                if(task.isSuccessful()) {
                                    sendToMain();
                                } else {
                                    String errorMessage = task.getException().getMessage();
                                    Toast.makeText(RegisterActivity.this, "Error: "+errorMessage,Toast.LENGTH_LONG).show();
                                }
                                reg_progress.setVisibility(View.INVISIBLE);
                            }
                        });

                    } else {
                        Toast.makeText(RegisterActivity.this, "다시 입력한 비밀번호와 기존 비밀번호가 일치하지 않습니다.",Toast.LENGTH_LONG).show();
                    }
                }
            }
        });

        reg_login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent loginIntent = new Intent(RegisterActivity.this,LoginActivity.class);
                startActivity(loginIntent);
                finish();
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();

        FirebaseUser currentUser = mAuth.getCurrentUser();
        if(currentUser != null) {
            sendToMain();
        }
    }

    private void sendToMain() {
        Intent mainIntent = new Intent(RegisterActivity.this,MainActivity.class);
        startActivity(mainIntent);
        finish();
    }
}