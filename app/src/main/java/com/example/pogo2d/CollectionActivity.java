package com.example.pogo2d;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Log;
import android.widget.GridView;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;

public class CollectionActivity extends AppCompatActivity {

    private StorageReference mStorageRef;
    private FirebaseFirestore db;

    private GridView gridView;
    private ArrayList<String> pkmnNames = new ArrayList<>();
    private ArrayList<Bitmap> pokeArrayList = new ArrayList();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_collection);

        db = FirebaseFirestore.getInstance();
        mStorageRef = FirebaseStorage.getInstance().getReference();

        gridView = findViewById(R.id.gridView);

        Object[] list = new Object[2];
        list[0] = pkmnNames;
        list[1] = pokeArrayList;

        gridView.setAdapter(new CustomAdapter(CollectionActivity.this, list));

        db.collection("users")
                .document(FirebaseAuth.getInstance().getCurrentUser().getEmail())
                .collection("pokemons")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {

                            for (QueryDocumentSnapshot document : task.getResult()) {

                                for (String clePokemon : document.getData().keySet()) {

                                    final String valPokemon = (String) document.getData().get(clePokemon);

                                    final StorageReference pokeRef = mStorageRef.child("pokemons/" + valPokemon + ".png");

                                    pokeRef.getBytes(1024 * 1024)
                                            .addOnSuccessListener(new OnSuccessListener<byte[]>() {
                                                @Override
                                                public void onSuccess(byte[] bytes) {
                                                    Bitmap img = BitmapFactory
                                                            .decodeByteArray(bytes,
                                                                    0,
                                                                    bytes.length);
                                                    img=Bitmap
                                                            .createScaledBitmap(img,
                                                                    img.getWidth()*3,
                                                                    img.getHeight()*3,
                                                                    false);
                                                    pkmnNames.add(valPokemon);
                                                    pokeArrayList.add(img);
                                                    ((CustomAdapter)gridView
                                                            .getAdapter())
                                                            .notifyDataSetChanged();
                                                }
                                            });
                                }
                            }

                        } else {
                            Log.d("CollectionActivity", "Error getting documents: ", task.getException());
                        }
                    }
                });
    }

}
