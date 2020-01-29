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
    public static ArrayList<Pokemon> pokemons;
    private String nom;
    private int num;
    private File fichier;

    protected Pokemon(int num) {
        this.num = num;
        this.nom = getPokemons().get(num).nom;
        this.fichier = getPokemons().get(num).fichier;
    }

    private Pokemon(int num, String nom, File fichier) {
        this.num = num;
        this.nom = nom;
        this.fichier = fichier;
    }

    public static ArrayList<Pokemon> getPokemons() {
        return pokemons;
    }

    public static void init() {
        pokemons = initPokemons();
    }

    private static ArrayList<Pokemon> initPokemons() {
        final ArrayList<Pokemon> res = new ArrayList<>();

        Log.e("ICI ", "ça marche?");
        final Task<ListResult> task = FirebaseStorage.getInstance().getReference().child("pokemons")
                .listAll()
                .addOnSuccessListener(new OnSuccessListener<ListResult>() {
                    @Override
                    public void onSuccess(ListResult listResult) {
                        Log.e("ici 2 ", listResult.getPrefixes().size() + "");
                        Log.e("ici 3 ", listResult.getItems().size() + "");
                        for (final StorageReference prefix : listResult.getPrefixes()) {
                        }

                        for (final StorageReference item : listResult.getItems()) {
                            try {
                                final File localFile = File.createTempFile(item.getName(), "png");
                                item.getFile(localFile).addOnSuccessListener(
                                        new OnSuccessListener<FileDownloadTask.TaskSnapshot>() {
                                            @Override
                                            public void onSuccess(FileDownloadTask.TaskSnapshot taskSnapshot) {
                                                Log.e("Pok ", item.getName().substring(0, item.getName().length()-4));
                                                res.add(new Pokemon(res.size(), item.getName().substring(0, item.getName().length()-4), localFile));
                                            }
                                        }).addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception exception) {
                                        Log.e("Pokemon ", "Erreur téléchargement des pokémons");
                                    }
                                });
                            } catch (IOException e) {
                                e.printStackTrace();
                                Log.e("ici 4 ", "pq fail :(");
                            }
                            Log.i("Pokemon item ", item.getPath());
                        }
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.e("Pokemon ", "Erreur :(");
                    }
                });

        return res;
    }

    public String getNom() {
        return nom;
    }

    public int getNum() {
        return num;
    }

    public File getFichier() {
        return fichier;
    }
}
