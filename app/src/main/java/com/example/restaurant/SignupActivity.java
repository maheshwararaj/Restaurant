package com.example.restaurant;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import java.util.HashMap;
import java.util.Map;

public class SignupActivity extends AppCompatActivity {

    private EditText editTextName, editTextEmail, editTextPassword;
    private Button buttonSignup;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        // Initialize Firebase Auth
        mAuth = FirebaseAuth.getInstance();

        editTextName = findViewById(R.id.name);
        editTextEmail = findViewById(R.id.email2);
        editTextPassword = findViewById(R.id.password2);
        buttonSignup = findViewById(R.id.signup);

        buttonSignup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String name = editTextName.getText().toString().trim();
                String email = editTextEmail.getText().toString().trim();
                String password = editTextPassword.getText().toString().trim();

                if (name.isEmpty()) {
                    editTextName.setError("Name is required");
                    editTextName.requestFocus();
                    return;
                }

                if (email.isEmpty()) {
                    editTextEmail.setError("Email is required");
                    editTextEmail.requestFocus();
                    return;
                }

                if (password.isEmpty()) {
                    editTextPassword.setError("Password is required");
                    editTextPassword.requestFocus();
                    return;
                }

                if (password.length() < 6) {
                    editTextPassword.setError("Password should be at least 6 characters");
                    editTextPassword.requestFocus();
                    return;
                }

                mAuth.createUserWithEmailAndPassword(email, password)
                        .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                if (task.isSuccessful()) {
                                    // Sign in success, update UI with the signed-in user's information
                                    Toast.makeText(SignupActivity.this, "Signup successful.", Toast.LENGTH_SHORT).show();

                                    String userId = mAuth.getCurrentUser().getUid();

                                    // Access Firestore instance
                                    FirebaseFirestore db = FirebaseFirestore.getInstance();

                                    // Create a new user document with the user's ID as the document ID
                                    Map<String, Object> user = new HashMap<>();
                                    user.put("name", name);
                                    user.put("email", email);

                                    // Add the user document to the 'users' collection
                                    db.collection("users").document(userId)
                                            .set(user)
                                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                @Override
                                                public void onSuccess(Void aVoid) {
                                                    // Document added successfully
                                                    Toast.makeText(SignupActivity.this, "User document created in Firestore.", Toast.LENGTH_SHORT).show();
                                                    // You can add further actions here if needed
                                                }
                                            })
                                            .addOnFailureListener(new OnFailureListener() {
                                                @Override
                                                public void onFailure(@NonNull Exception e) {
                                                    // Handle errors
                                                    Toast.makeText(SignupActivity.this, "Error creating user document: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                                                }
                                            });

                                            startActivity(new Intent(SignupActivity.this, MainActivity.class));


                                } else {
                                    // If sign in fails, display a message to the user.
                                    Toast.makeText(SignupActivity.this, "Signup failed. " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
            }
        });
    }
}
