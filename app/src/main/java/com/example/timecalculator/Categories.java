package com.example.timecalculator;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.ArrayList;

public class Categories {
    private String name;
    private String totaltime;
    private ArrayList<String> time;

    public Categories(String name) {
        this.name = name;
        this.time = new ArrayList<>();
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getTotaltime() {
        return totaltime;
    }

    public void setTotaltime(String totaltime) {
        this.totaltime = totaltime;
    }

    public void addtime(String time) {
        this.time.add(time);
    }

    public void cargarhoras(ArrayList<String> letters){
        letters.addAll(time);
    }

    @Override
    public boolean equals(@Nullable Object obj) {
        // self check
        if (this == obj)
            return true;
        // null check
        if (obj == null)
            return false;
        // type check and cast
        if (getClass() != obj.getClass())
            return false;
        Categories categorie = (Categories) obj;
        // field comparison
        return categorie.getName().equals(this.name);
    }

    @NonNull
    @Override
    public String toString() {
        String contenido = this.name + "\n" + this.totaltime + "\n";
        for (String i: time) {
            contenido = contenido + i + "\n";
        }

        return contenido;
    }
}
