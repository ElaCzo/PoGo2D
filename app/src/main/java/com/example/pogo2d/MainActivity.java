package com.example.pogo2d;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        /* EditTexts */
        final EditText pseudo = findViewById(R.id.pseudo);
        final EditText email = findViewById(R.id.email);
        final EditText mdp = findViewById(R.id.mdp);

        /* Buttons */
        final Button jouer = findViewById(R.id.jouer);
        final Button OK  = findViewById(R.id.ok);
        final Button inscription =  findViewById(R.id.inscription);


        OK.setVisibility(View.GONE);
        email.setVisibility(View.GONE);


        inscription.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                OK.setVisibility(View.VISIBLE);
                email.setVisibility(View.VISIBLE);
                jouer.setVisibility(View.GONE);
                inscription.setVisibility(View.GONE);
            }
        });

        OK.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                OK.setVisibility(View.GONE);
                email.setVisibility(View.GONE);
                jouer.setVisibility(View.VISIBLE);
                inscription.setVisibility(View.VISIBLE);
            }
        });
    }
}
