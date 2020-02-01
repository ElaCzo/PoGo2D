package com.example.pogo2d;

import android.util.Log;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.ListResult;
import com.google.firebase.storage.StorageReference;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

public class Pokemon {
    public static ArrayList<Pokemon> pokemons = new ArrayList<Pokemon>();
    public static HashMap<String, Pokemon> pokemonFromName = new HashMap<String, Pokemon>();
    private String nom;
    private int num;
    private File fichier;

    protected Pokemon(String nom) {
        this.num = getPokemonFromName().get(nom).getNum();
        this.nom = nom;
        this.fichier = getPokemonFromName().get(nom).getFichier();
    }

    protected Pokemon(int num) {
        this.num = num;
        this.nom = getPokemons().get(num).getNom();
        this.fichier = getPokemons().get(num).getFichier();
    }

    Pokemon(int num, String nom, File fichier) {
        this.num = num;
        this.nom = nom;
        this.fichier = fichier;
    }

    public static ArrayList<Pokemon> getPokemons() {
        return pokemons;
    }

    public static HashMap<String, Pokemon> getPokemonFromName() {
        return pokemonFromName;
    }

    public static String shapeName(String nom) {
        if (nom.endsWith(".jpg")) {
            String tmp[] = nom.split(".jpg");
            nom = tmp[0];
        }
        if (nom.endsWith(".png")) {
            String tmp[] = nom.split(".png");
            nom = tmp[0];
        }
        return (nom.charAt(0) + "").toUpperCase() + nom.substring(1);
    }

    public static void init(ListResult lr, int i, MainActivity mainActivity) {
        if (i < lr.getItems().size()) {
            StorageReference item = lr.getItems().get(i);
            try {
                final File localFile = File.createTempFile(item.getName(), "png");
                item.getFile(localFile).addOnSuccessListener(
                        new OnSuccessListener<FileDownloadTask.TaskSnapshot>() {
                            @Override
                            public void onSuccess(FileDownloadTask.TaskSnapshot taskSnapshot) {
                                Log.i("Pokémon chargé", Pokemon.shapeName(item.getName()));

                                Pokemon pokemon = new Pokemon(
                                        Pokemon.getPokemons().size(),
                                        Pokemon.shapeName(item.getName()),
                                        localFile);

                                Pokemon.getPokemonFromName()
                                        .put(Pokemon.shapeName(item.getName()), pokemon);
                                Pokemon.getPokemons().add(pokemon);

                                //Log.i("Pokemon", Pokemon.shapeName(item));

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
        } else {
            Ash.init(mainActivity);
        }
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
