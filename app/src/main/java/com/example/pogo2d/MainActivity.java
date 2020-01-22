package com.example.pogo2d;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
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
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class MainActivity extends AppCompatActivity {

    /* EditTexts */
    EditText pseudo;
    EditText email;
    EditText mdp;
    /* Buttons */
    Button jouer;
    Button OK;
    Button inscription;
    private String TAG = "auth";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        /* EditTexts */
        //pseudo = findViewById(R.id.pseudo);
        email = findViewById(R.id.email);
        mdp = findViewById(R.id.mdp);

        /* Buttons */
        jouer = findViewById(R.id.jouer);
        OK = findViewById(R.id.ok);
        inscription = findViewById(R.id.inscription);

        OK.setVisibility(View.GONE);
        //email.setVisibility(View.GONE);


        // regarder stocker auth firebase.
        // recup ds autre act
        // pattern singleton

        jouer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                signIn(email.getText().toString(), mdp.getText().toString());
                FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                if (user != null) {

                } else {
                    // No user is signed in
                }

            }
        });

        inscription.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                OK.setVisibility(View.VISIBLE);
                //email.setVisibility(View.VISIBLE);
                jouer.setVisibility(View.GONE);
                inscription.setVisibility(View.GONE);
            }
        });

        OK.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                OK.setVisibility(View.GONE);
                //email.setVisibility(View.GONE);
                jouer.setVisibility(View.VISIBLE);
                inscription.setVisibility(View.VISIBLE);

                createAccount(email.getText().toString(), mdp.getText().toString());
            }
        });
    }

    @Override
    public void onStart() {
        super.onStart();
    }

    private void createAccount(String email, String password) {
        Log.d(TAG, "createAccount:" + email);
        if (!validateForm()) {
            return;
        }

        Log.d(TAG, "1createUserWithEmail:success");

        FirebaseAuth.getInstance().createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        Log.d(TAG, "2createUserWithEmail:success");

                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d(TAG, "createUserWithEmail:success");
                            final FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                            // Add a new document with a generated ID

                            Globals.setDB(FirebaseFirestore.getInstance());

                            Map<String, Object> data = new HashMap<>();
                            data.put("number", 0);

                            FirebaseFirestore.getInstance().collection("users")
                                    .document(user.getEmail())
                                    .set(data)
                                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void aVoid) {
                                            Log.i(TAG, "Success adding document");
                                        }
                                    })
                                    .addOnFailureListener(new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception e) {
                                            Log.w(TAG, "Error adding document", e);
                                        }
                                    });
                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w(TAG, "createUserWithEmail:failure", task.getException());
                            Toast.makeText(MainActivity.this, "Authentication failed.",
                                    Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    private void signIn(String email, String password) {
        Log.d(TAG, "signIn:" + email);
        if (!validateForm()) {
            return;
        }

        FirebaseAuth.getInstance().signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d(TAG, "signInWithEmail:success");
                            Globals.setMAuth(Globals.getInstance().getMAuth());

                            Intent intent = new Intent(MainActivity.this, CollectionActivity.class);
                            startActivity(intent);
                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w(TAG, "signInWithEmail:failure", task.getException());
                            Toast.makeText(MainActivity.this, "Authentication failed.",
                                    Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    private void signOut() {
        Globals.getInstance().getMAuth().signOut();
    }

    private void sendEmailVerification() {
        // Disable button
        //findViewById(R.id.verifyEmailButton).setEnabled(false);

        // Send verification email
        final FirebaseUser user = Globals.getInstance().getMAuth().getCurrentUser();
        user.sendEmailVerification()
                .addOnCompleteListener(this, new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        // [START_EXCLUDE]
                        // Re-enable button
                        //findViewById(R.id.verifyEmailButton).setEnabled(true);

                        if (task.isSuccessful()) {
                            Toast.makeText(MainActivity.this,
                                    "Verification email sent to " + user.getEmail(),
                                    Toast.LENGTH_SHORT).show();
                        } else {
                            Log.e(TAG, "sendEmailVerification", task.getException());
                            Toast.makeText(MainActivity.this,
                                    "Failed to send verification email.",
                                    Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    private boolean validateForm() {
        boolean valid = true;

        String emailText = email.getText().toString();
        if (TextUtils.isEmpty(emailText)) {
            email.setError("Required.");
            valid = false;
        } else {
            email.setError(null);
        }

        String password = mdp.getText().toString();
        if (TextUtils.isEmpty(password)) {
            mdp.setError("Required.");
            valid = false;
        } else {
            mdp.setError(null);
        }

        return valid;
    }
}
