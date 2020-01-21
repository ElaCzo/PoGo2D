package com.example.pogo2d;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CollectionActivity extends AppCompatActivity {

    private String TAG = "collection";

    public static String[] pkmnNames = {
            "Pikachu", "Pikachu", "Pikachu", "Pikachu",
            "Pikachu", "Pikachu", "Pikachu", "Pikachu"
    };

    private ImageView pokemonImg;

    public static List<Integer> pkmns = new ArrayList<>();

    private StorageReference mStorageRef;


    private FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_collection);  //Définition de la vue principale

        db = FirebaseFirestore.getInstance();

        mStorageRef = FirebaseStorage.getInstance().getReference();
    }

    @Override
    protected void onResume() {
        super.onResume();

         Log.e("Resume dans Collection", db.toString());


        db.collection("blue")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                Log.d(TAG, document.getId() + " => " + document.getData());


                                for(String clePokemon : document.getData().keySet()) {
                                    String valPokemon = (String) document.getData().get(clePokemon);

                                    StorageReference pikaRef = mStorageRef.child("images/"+valPokemon+".jpg");

                                    try {
                                        File localFile = File.createTempFile(valPokemon, "jpg");

                                        pikaRef.getFile(localFile).addOnSuccessListener(new OnSuccessListener<FileDownloadTask.TaskSnapshot>() {
                                            @Override
                                            public void onSuccess(FileDownloadTask.TaskSnapshot taskSnapshot) {
                                                // Local temp file has been created
                                            }
                                        }).addOnFailureListener(new OnFailureListener() {
                                            @Override
                                            public void onFailure(@NonNull Exception exception) {
                                                // Handle any errors
                                            }
                                        });

                                    } catch (Exception e) {}
                                }
                            }
                        } else {
                            Log.d(TAG, "Error getting documents: ", task.getException());
                        }
                    }
                });


        pkmns.add(R.drawable.pikachu); pkmns.add(R.drawable.pikachu);
        pkmns.add(R.drawable.pikachu); pkmns.add(R.drawable.pikachu);
        pkmns.add(R.drawable.pikachu); pkmns.add(R.drawable.pikachu);
        pkmns.add(R.drawable.pikachu); pkmns.add(R.drawable.pikachu);
        pkmns.add(R.drawable.pikachu); pkmns.add(R.drawable.pikachu);
        pkmns.add(R.drawable.pikachu); pkmns.add(R.drawable.pikachu);
        pkmns.add(R.drawable.pikachu); pkmns.add(R.drawable.pikachu);


        /*GridView gridView = (GridView) findViewById(R.id.gridView);
        pokemonImg = (ImageView) findViewById(R.id.img);

        gridView = (GridView) findViewById(R.id.gridView);

        Object[] list = new Object[2];
        list[0]=pkmnNames; list[1]=pkmns;

        gridView.setAdapter(new CustomAdapter(this, list));

        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View v,
                                    int position, long id) {
                Toast.makeText(getApplicationContext(),
                        ((TextView) v).getText(), Toast.LENGTH_SHORT).show();
            }
        });*/
    }

}
