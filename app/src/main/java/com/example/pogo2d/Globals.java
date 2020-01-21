package com.example.pogo2d;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

public class Globals {
    private static Globals instance;

    private static FirebaseFirestore db;

    // Global variable
    private static FirebaseAuth mAuth;

    // Restrict the constructor from being instantiated
    private Globals(){
        mAuth=null;
        db=null;
    }

    public static void setMAuth(FirebaseAuth mAuth){
        Globals.mAuth=mAuth;
    }

    public static FirebaseAuth getMAuth(){
        return mAuth;
    }

    public static synchronized Globals getInstance(){
        if(instance==null){
            instance=new Globals();
        }
        return instance;
    }

    public static void setDB(FirebaseFirestore db){
        Globals.db=db;
    }

    public static FirebaseFirestore getDb(){
        if(db==null)
            db=FirebaseFirestore.getInstance();

        return db;
    }
}
