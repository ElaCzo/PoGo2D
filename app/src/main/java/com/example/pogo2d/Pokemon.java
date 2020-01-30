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
    public static ArrayList<Pokemon> pokemons = new ArrayList<>();
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
        initPokemons();
    }

    private static void initPokemons() {

        final Task<ListResult> task = FirebaseStorage.getInstance().getReference().child("pokemons/")
                .listAll()
                .addOnSuccessListener(new OnSuccessListener<ListResult>() {
                    @Override
                    public void onSuccess(ListResult listResult) {

                        for (final StorageReference item : listResult.getItems()) {
                            try {
                                final File localFile = File.createTempFile(item.getName(), "png");
                                item.getFile(localFile).addOnSuccessListener(
                                        new OnSuccessListener<FileDownloadTask.TaskSnapshot>() {
                                            @Override
                                            public void onSuccess(FileDownloadTask.TaskSnapshot taskSnapshot) {
                                                Log.i("Pokémon chargé", Pokemon.shapeNameFromStorage(item));
                                                pokemons.add(new Pokemon(
                                                        pokemons.size(),
                                                        shapeNameFromStorage(item),
                                                        localFile));
                                            }
                                        }).addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception exception) {
                                        Log.e("Pokemon", "Erreur de téléchargement des pokémons");
                                    }
                                });
                            } catch (IOException e) {
                                Log.e("Pokemon", "Erreur.");
                                e.printStackTrace();
                            }
                        }
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.e("Pokemon", "Erreur accès Storage");
                    }
                });
    }

    public static String shapeNameFromStorage(StorageReference item) {
        String nom = item.getName();
        return (nom.charAt(0) + "").toUpperCase() + nom.substring(1, item.getName().length() - 4);
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
