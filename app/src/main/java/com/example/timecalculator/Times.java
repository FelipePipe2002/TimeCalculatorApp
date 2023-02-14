package com.example.timecalculator;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;


import java.util.ArrayList;

public class Times extends AppCompatActivity {
    private ArrayList<String> letters;
    private ArrayList<Categories> categorias;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_times);

        Bundle extras = getIntent().getExtras();
        if (extras != null){
            categorias = (ArrayList<Categories>) extras.get("categorias");
        }
        CargarLista();

        Button buttonhome = findViewById(R.id.buttonhome);
        Button buttontimes= findViewById(R.id.buttontimes);
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, R.layout.textview, letters);
        ListView listtimes = findViewById(R.id.listtimes);

        letters = new ArrayList<>();

        listtimes.setAdapter(adapter);
        listtimes.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                openCategory(letters.get(position));
            }
        });
        listtimes.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                RemoveCategory(letters,position);
                adapter.notifyDataSetChanged();
                return true;
            }
        });

        buttontimes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
            }

        });
        buttonhome.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {openHome();}
        });
    }

    private void CargarLista(){
        for (Categories i: categorias) {
            String nombre = i.getName();
            String time = i.getTotaltime();

            String hora = String.valueOf((Integer.parseInt(String.valueOf(time))/3600));
            String minuto= String.valueOf((Integer.parseInt(String.valueOf(time)) % 3600) / 60);
            String segundos= String.valueOf(Integer.parseInt(String.valueOf(time)) % 60);

            if (hora.length() == 1){hora= "0"+hora;}
            if (minuto.length() == 1){minuto= "0"+minuto;}
            if (segundos.length() == 1){segundos= "0"+segundos;}

            nombre = nombre + "   -   " + hora + ":" + minuto + ":" + segundos;
            letters.add(nombre);
        }
    }

    public void RemoveCategory(ArrayList letters, int position){
        String category = String.valueOf(letters.get(position));
        category = category.substring(0,category.indexOf("   -"));
        categorias.remove(category);
        letters.remove(position);
    }

    public void openHome(){
        Intent intent = new Intent(this, MainActivity.class);
        intent.putExtra("categorias",categorias);
        startActivity(intent);
        overridePendingTransition(0, 0);
    }

    public void openCategory(String categoria){
        Intent intent = new Intent(this, MainActivity.class);
        intent.putExtra("key",categoria);
        intent.putExtra("categorias",categorias);
        startActivity(intent);
        overridePendingTransition(0, 0);
    }


}