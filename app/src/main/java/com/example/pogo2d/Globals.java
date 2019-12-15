package com.example.pogo2d;

import com.google.firebase.auth.FirebaseAuth;

public class Globals {
    private static Globals instance;

    // Global variable
    private FirebaseAuth mAuth;

    // Restrict the constructor from being instantiated
    private Globals(){}

    public void setMAuth(FirebaseAuth mAuth){
        this.mAuth=mAuth;
    }

    public FirebaseAuth getMAuth(){
        return this.mAuth;
    }

    public static synchronized Globals getInstance(){
        if(instance==null){
            instance=new Globals();
        }
        return instance;
    }
}
