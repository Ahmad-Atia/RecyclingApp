package com.example.recyclingapp.models;

import java.util.Calendar;

public class AbholTermin {
    private final String tonnenTyp;
    private final Calendar datum;

    public AbholTermin(String tonnenTyp, Calendar datum) {
        this.tonnenTyp = tonnenTyp;
        this.datum = datum;
    }

    public String getTonnenTyp() { return tonnenTyp; }
    public Calendar getDatum() { return datum; }
}
