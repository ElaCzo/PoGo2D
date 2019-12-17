package com.example.pogo2d;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class CollectionActivity extends AppCompatActivity {

    private String TAG = "collection";

    public static String[] osNameList = {
            "Android",
            "iOS",
            "Linux",
            "MacOS",
            "MS DOS",
            "Symbian",
            "Windows 10",
            "Windows XP",
    };

    private ImageView pokemonImg;

    private static final Integer[] items = { R.drawable.pikachu,
            R.drawable.pikachu, R.drawable.pikachu, R.drawable.pikachu, R.drawable.pikachu,
            R.drawable.pikachu, R.drawable.pikachu, R.drawable.pikachu, R.drawable.pikachu};

    public static int[] osImages = {
            R.drawable.pikachu, R.drawable.pikachu, R.drawable.pikachu, R.drawable.pikachu};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_collection);  //Définition de la vue principale

        FirebaseFirestore db = FirebaseFirestore.getInstance();

        /*Button ButtonConnexion = (Button) findViewById(R.id.button1);   //Appel du "button1"
        ButtonConnexion.setOnClickListener(new View.OnClickListener()      //Creation du listener sur ce bouton
        {
            public void onClick(View actuelView)    //au clic sur le bouton
            {
                Intent intent = new Intent(CollectionActivity.this, MainActivity.class);  //Lancer l'activité DisplayVue
                startActivity(intent);    //Afficher la vue
            }
        });*/

        GridView gridView = (GridView) findViewById(R.id.gridView);
        pokemonImg = (ImageView) findViewById(R.id.img);

        gridView = (GridView) findViewById(R.id.gridView);
        gridView.setAdapter(new CustomAdapter(this, osNameList, osImages));


        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View v,
                                    int position, long id) {
                Toast.makeText(getApplicationContext(),
                        ((TextView) v).getText(), Toast.LENGTH_SHORT).show();
            }
        });


        // Create a new user with a first and last name
        Map<String, Object> user = new HashMap<>();
        user.put("pseudo", "bla");
        user.put("last", "Lovelace");
        user.put("born", 1815);

        // Add a new document with a generated ID
        db.collection("utilisateur")
                .add(user)
                .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                    @Override
                    public void onSuccess(DocumentReference documentReference) {
                        Log.d(TAG, "DocumentSnapshot added with ID: " + documentReference.getId());
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.w(TAG, "Error adding document", e);
                    }
                });
    }

}
