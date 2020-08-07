package com.uzair.sinchcallingsdk;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class LoginScreen extends AppCompatActivity {

    private FirebaseAuth firebaseAuth;
    private EditText userEmail , userPassword;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_screen);

        userEmail =  findViewById(R.id.userEmail);
        userPassword = findViewById(R.id.userPassword);

        firebaseAuth = FirebaseAuth.getInstance();

    }

    public void dontHaveAccount(View view) {

        startActivity(new Intent(this , SignUpScreen.class));
        this.finish();
    }

    public void LoginButton(View view)
    {

        String email = userEmail.getText().toString();
        String password =  userPassword.getText().toString();

        if(!email.isEmpty() && !password.isEmpty())
        {
            firebaseAuth.signInWithEmailAndPassword(email , password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {

                    if(task.isSuccessful())
                    {
                        startActivity(new Intent(LoginScreen.this, MainActivity.class));
                        LoginScreen.this.finish();
                    }
                    else
                    {
                        Toast.makeText(LoginScreen.this, task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                    }

                }
            });

        }
        else
        {
            Toast.makeText(this, "Require all fields", Toast.LENGTH_SHORT).show();
        }

    }
}
