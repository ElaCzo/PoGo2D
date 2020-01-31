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
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.ListResult;
import com.google.firebase.storage.StorageReference;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import static android.widget.Toast.LENGTH_LONG;
import static android.widget.Toast.LENGTH_SHORT;

public class MainActivity extends AppCompatActivity {

    /* EditTexts */
    EditText email;
    EditText mdp;
    /* Buttons */
    Button jouer;
    Button OK;
    Button inscription;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        /* EditTexts */
        email = findViewById(R.id.email);
        mdp = findViewById(R.id.mdp);

        /* Buttons */
        jouer = findViewById(R.id.jouer);
        OK = findViewById(R.id.ok);
        inscription = findViewById(R.id.inscription);

        OK.setVisibility(View.GONE);

        jouer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                jouer.setEnabled(false);
                inscription.setEnabled(false);
                signIn(email.getText().toString(), mdp.getText().toString());
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
        if (!validateForm()) {
            return;
        }

        FirebaseAuth.getInstance().createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {

                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            final FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                            // Add a new document with a generated ID

                            Map<String, Object> data = new HashMap<>();
                            data.put("number", 0);

                            FirebaseFirestore.getInstance().collection("users")
                                    .document(user.getEmail())
                                    .set(data)
                                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void aVoid) {
                                            Toast.makeText(
                                                    MainActivity.this,
                                                    "Inscription réussie ! \nCliquez sur Jouer",
                                                    LENGTH_LONG).show();
                                        }
                                    })
                                    .addOnFailureListener(new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception e) {
                                        }
                                    });
                        } else {
                            // If sign in fails, display a message to the user.
                            Toast.makeText(MainActivity.this, "Authentication failed.",
                                    Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    public void switchToCollectionActivity(){
        Toast.makeText(this, "Chargement terminé", LENGTH_SHORT).show();
        Intent intent = new Intent(MainActivity.this, CollectionActivity.class);
        jouer.setEnabled(true);
        inscription.setEnabled(true);
        startActivity(intent);
    }

    private void signIn(String email, String password) {
        if (!validateForm()) {
            return;
        }

        FirebaseAuth.getInstance().signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, change activity

                            FirebaseStorage.getInstance().getReference().child("pokemons/")
                                    .listAll()
                                    .addOnSuccessListener(new OnSuccessListener<ListResult>() {
                                        @Override
                                        public void onSuccess(ListResult listResult) {

                                            Toast.makeText(MainActivity.this,"Chargement en cours...", LENGTH_LONG).show();
                                            if(Pokemon.getPokemons().size() == 0) {
                                                Pokemon.init(listResult, 0, MainActivity.this);
                                            } else {
                                                Ash.init(MainActivity.this);
                                            }

                                        }
                                    })
                                    .addOnFailureListener(new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception e) {
                                            Log.e("Pokemon", "Erreur accès Storage");
                                        }
                                    });

                        } else {
                            // If sign in fails, display a message to the user.
                            Toast.makeText(MainActivity.this, "Authentication failed.",
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
