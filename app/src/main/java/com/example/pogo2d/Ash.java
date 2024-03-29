package com.example.pogo2d;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.File;
import java.io.IOException;

public class Ash {

    private static File fichier;

    public static void init(MainActivity mainActivity){
        try {
            final File sashaFile = File.createTempFile("sasha", "png");

            StorageReference sashaRef = FirebaseStorage.getInstance().getReference()
                    .child("sasha/sasha.png");

            sashaRef.getFile(sashaFile)
                    .addOnSuccessListener(new OnSuccessListener<FileDownloadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(FileDownloadTask.TaskSnapshot taskSnapshot) {
                            fichier = sashaFile;
                            mainActivity.switchToCollectionActivity();
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception exception) {
                    // Handle any errors
                }
            });
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static File getFichier() {
        return fichier;
    }
}
