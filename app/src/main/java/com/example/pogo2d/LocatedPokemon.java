package com.example.pogo2d;

public class LocatedPokemon extends Pokemon {
    protected double latitude;
    protected double longitude;

    public LocatedPokemon(int num, double latitude, double longitude) {
        super();
        this.num = num;
        this.latitude = latitude;
        this.longitude = longitude;
    }
}
