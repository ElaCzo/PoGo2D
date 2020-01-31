package com.example.pogo2d;

import android.content.Intent;
import android.util.Log;
import android.widget.Toast;

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

import static android.widget.Toast.LENGTH_LONG;
import static android.widget.Toast.LENGTH_SHORT;

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

    Pokemon(int num, String nom, File fichier) {
        this.num = num;
        this.nom = nom;
        this.fichier = fichier;
    }

    public static ArrayList<Pokemon> getPokemons() {
        return pokemons;
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

    public static void init(ListResult lr, int i, MainActivity mainActivity){
        if(i<lr.getItems().size()) {
            StorageReference item = lr.getItems().get(i);
            try {
                final File localFile = File.createTempFile(item.getName(), "png");
                item.getFile(localFile).addOnSuccessListener(
                        new OnSuccessListener<FileDownloadTask.TaskSnapshot>() {
                            @Override
                            public void onSuccess(FileDownloadTask.TaskSnapshot taskSnapshot) {
                                Log.i("Pokémon chargé", Pokemon.shapeNameFromStorage(item));
                                Pokemon.getPokemons().add(new Pokemon(
                                        Pokemon.getPokemons().size(),
                                        Pokemon.shapeNameFromStorage(item),
                                        localFile));

                                init(lr, i + 1, mainActivity);
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
        else{
            Ash.init(mainActivity);
        }
    }
}
