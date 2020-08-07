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
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;
import java.util.Map;

public class SignUpScreen extends AppCompatActivity {


    private EditText userEmail , userPassowrd , userName;
    private FirebaseAuth firebaseAuth;
    private DatabaseReference databaseReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up_screen);

        userEmail  = findViewById(R.id.edUserEmail);
        userPassowrd  = findViewById(R.id.edUserPassword);
        userName  = findViewById(R.id.edUserName);

        firebaseAuth = FirebaseAuth.getInstance();
        databaseReference = FirebaseDatabase.getInstance().getReference().child("Users");

    }

     // click on register button
    public void registerUser(View view)
    {
        final String name = userName.getText().toString();
        final String email = userEmail.getText().toString();
        final String password = userPassowrd.getText().toString();

        if(!name.isEmpty() && !email.isEmpty() && !password.isEmpty()) {


            firebaseAuth.createUserWithEmailAndPassword(email , password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {

                    if(task.isSuccessful())
                    {
                        Map<String, String> data = new HashMap<>();
                        data.put("userName", name);
                        data.put("userPassword", password);
                        data.put("userEmail", email);
                        data.put("uid", firebaseAuth.getCurrentUser().getUid());

                        databaseReference.child(firebaseAuth.getCurrentUser().getUid())
                                .setValue(data);

                    }else
                    {
                        Toast.makeText(SignUpScreen.this, task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                    }

                }
            });

        }
        else
        {
            Toast.makeText(this, "All Fields are Required", Toast.LENGTH_SHORT).show();
        }

    }

    // click on text already have account
    public void alreadHaveAccount(View view)
    {
        startActivity(new Intent(this , LoginScreen.class));
        this.finish();
    }
}
