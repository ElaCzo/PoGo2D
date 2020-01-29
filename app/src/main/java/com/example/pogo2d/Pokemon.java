package com.example.pogo2d;

import android.util.Log;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.ListResult;
import com.google.firebase.storage.StorageReference;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

public class Pokemon {
    public static ArrayList<Pokemon> pokemons = initPokemons();
    private String nom;
    private int num;
    private File fichier;

    protected Pokemon(int num){
        this.num=num;
        this.nom=getPokemons().get(num).nom;
        this.fichier=getPokemons().get(num).fichier;
    }

    public static ArrayList<Pokemon> getPokemons() {
        return pokemons;
    }

    private Pokemon(int num, String nom, File fichier) {
        this.num = num;
        this.nom = nom;
        this.fichier = fichier;
    }

    private static ArrayList<Pokemon> initPokemons() {
        final ArrayList<Pokemon> res = new ArrayList<>();

        Task<ListResult> task = FirebaseStorage.getInstance().getReference().child("pokemons")
                .listAll()
                .addOnSuccessListener(new OnSuccessListener<ListResult>() {
                    @Override
                    public void onSuccess(ListResult listResult) {
                        for (final StorageReference prefix : listResult.getPrefixes()) {
                            try {
                                final File localFile = File.createTempFile(prefix.getName(), "png");
                                prefix.getFile(localFile).addOnSuccessListener(
                                        new OnSuccessListener<FileDownloadTask.TaskSnapshot>() {
                                            @Override
                                            public void onSuccess(FileDownloadTask.TaskSnapshot taskSnapshot) {
                                                res.add(new Pokemon(res.size(), prefix.getName(), localFile));
                                            }
                                        }).addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception exception) {
                                        Log.e("Pokemon ", "Erreur téléchargement des pokémons");
                                    }
                                });
                            } catch (IOException e) {
                                e.printStackTrace();
                            }

                        }

                        for (StorageReference item : listResult.getItems()) {
                            Log.i("Pokemon item ", item.getPath());
                        }
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        // Uh-oh, an error occurred!
                    }
                });

        // à retirer :
        task.getResult();
        return res;
    }
}
