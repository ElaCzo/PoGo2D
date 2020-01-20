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

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
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

        FirebaseFirestore db = FirebaseFirestore.getInstance();

        mStorageRef = FirebaseStorage.getInstance().getReference();

        File localFile = null;
        try {
            localFile = File.createTempFile("images", "jpg");
        } catch (IOException e) {
            e.printStackTrace();
        }

        pkmns.add(R.drawable.pikachu); pkmns.add(R.drawable.pikachu);
        pkmns.add(R.drawable.pikachu); pkmns.add(R.drawable.pikachu);
        pkmns.add(R.drawable.pikachu); pkmns.add(R.drawable.pikachu);
        pkmns.add(R.drawable.pikachu); pkmns.add(R.drawable.pikachu);
        pkmns.add(R.drawable.pikachu); pkmns.add(R.drawable.pikachu);
        pkmns.add(R.drawable.pikachu); pkmns.add(R.drawable.pikachu);
        pkmns.add(R.drawable.pikachu); pkmns.add(R.drawable.pikachu);

        StorageReference riversRef = mStorageRef.child("images/rivers.jpg");
        riversRef.getFile(localFile)
                .addOnSuccessListener(new OnSuccessListener<FileDownloadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(FileDownloadTask.TaskSnapshot taskSnapshot) {
                        // Successfully downloaded data to local file
                        // ...
                    }
                }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                // Handle failed download
                // ...
            }
        });

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

        Object[] list = new Object[2];
        list[0]=pkmnNames; list[1]=pkmns;

        gridView.setAdapter(new CustomAdapter(this, list));

        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View v,
                                    int position, long id) {
                Toast.makeText(getApplicationContext(),
                        ((TextView) v).getText(), Toast.LENGTH_SHORT).show();
            }
        });





        //////////////////////////////////////////////////////////////////////////////////////
        //////////////////////////////////////////////////////////////////////////////////////

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
