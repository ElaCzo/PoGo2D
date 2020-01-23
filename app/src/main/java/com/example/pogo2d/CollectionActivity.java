package com.example.pogo2d;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Log;
import android.widget.GridView;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class CollectionActivity extends AppCompatActivity {

    public static List<Integer> pkmns = new ArrayList<>();
    public ArrayList<String> pkmnNames = new ArrayList<>();
    private String TAG = "collection";
    private ImageView pokemonImg;
    private ImageView imgview;
    private StorageReference mStorageRef;

    private FirebaseFirestore db;

    private GridView gridView;

    private int i=0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_collection);  //Définition de la vue principale

        db = FirebaseFirestore.getInstance();

        mStorageRef = FirebaseStorage.getInstance().getReference();
    }

    @Override
    protected void onStart() {
        super.onStart();

        Log.e("Resume dans Collection", db.toString());

        pokemonImg = findViewById(R.id.img);

        //gridView = findViewById(R.id.gridView);
        imgview=findViewById(R.id.imageView);

        db.collection("users")
                .document(FirebaseAuth.getInstance().getCurrentUser().getEmail())
                .collection("pokemons")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        Log.e("1Resume dans Collection", db.toString());
                        if (task.isSuccessful()) {
                            Log.e("2Resume dans Collection", db.toString());

                            for (QueryDocumentSnapshot document : task.getResult()) {
                                Log.d(TAG, document.getId() + " => " + document.getData());

                                for (String clePokemon : document.getData().keySet()) {
                                    Log.e("3Resume dans Collection", clePokemon);

                                    final String valPokemon = (String) document.getData().get(clePokemon);

                                    Log.e("1) Collection", valPokemon);

                                    // ImageView in your Activity
                                    //ImageView imageView = findViewById(R.id.imageView);


                                    Log.e("mst Collection", mStorageRef.toString());
                                    Log.e("mst Collection", mStorageRef.getPath());

                                    final StorageReference pokeRef = mStorageRef.child("pokemons/" + valPokemon + ".png");

                                    Log.e("Val Collection", pokeRef.getPath());
                                    Log.e("Val Collection", pokeRef.toString());

                                    pokeRef.getBytes(1024*1024)
                                            .addOnSuccessListener(new OnSuccessListener<byte[]>() {
                                                @Override
                                                public void onSuccess(byte[] bytes) {
                                                    Bitmap img = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
                                                    imgview.setImageBitmap(img);
                                                }
                                            });
                                    /*try {
                                        final File localFile = File.createTempFile(valPokemon, "png");

                                        pokeRef.getFile(localFile).addOnSuccessListener(new OnSuccessListener<FileDownloadTask.TaskSnapshot>() {
                                            @Override
                                            public void onSuccess(FileDownloadTask.TaskSnapshot taskSnapshot) {
                                               //Bitmap bitmap = BitmapFactory.decodeFile(localFile.getAbsolutePath());
                                                //imgview.setImageBitmap(bitmap);

                                                Log.e("4Collection", db.toString());*/

                                                /*pkmns.add(localFile.toURI()); pkmns.add(R.drawable.pikachu);
                                                pkmns.add(R.drawable.pikachu); pkmns.add(R.drawable.pikachu);
                                                pkmns.add(R.drawable.pikachu); pkmns.add(R.drawable.pikachu);
                                                pkmns.add(R.drawable.pikachu); pkmns.add(R.drawable.pikachu);
                                                pkmns.add(R.drawable.pikachu); pkmns.add(R.drawable.pikachu);
                                                pkmns.add(R.drawable.pikachu); pkmns.add(R.drawable.pikachu);
                                                pkmns.add(R.drawable.pikachu); pkmns.add(R.drawable.pikachu);



                                                Object[] list = new Object[2];
                                                list[0]=pkmnNames.toArray(); list[1]=pkmns;

                                                gridView.setAdapter(new CustomAdapter(CollectionActivity.this, list));

                                                gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                                                    public void onItemClick(AdapterView<?> parent, View v,
                                                                            int position, long id) {
                                                        Toast.makeText(getApplicationContext(),
                                                                ((TextView) v).getText(), Toast.LENGTH_SHORT).show();
                                                    }
                                                });*/
                                            /*}
                                        }).addOnFailureListener(new OnFailureListener() {
                                            @Override
                                            public void onFailure(@NonNull Exception exception) {
                                                // Handle any errors

                                                Log.e("ErrorCollection", localFile.getAbsolutePath());
                                            }
                                        });

                                    } catch (Exception e) {
                                    }*/
                                }
                            }
                        } else {
                            Log.d(TAG, "Error getting documents: ", task.getException());
                        }
                    }
                });
    }

}
