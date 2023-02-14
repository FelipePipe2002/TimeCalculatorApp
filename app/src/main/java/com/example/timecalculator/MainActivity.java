package com.example.timecalculator;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import android.content.Context;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.time.Duration;
import java.util.ArrayList;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MainActivity extends AppCompatActivity {

    private ListView listView;
    private ArrayList<String> letters;
    private ArrayAdapter<String> adapter;
    private TextView texttime;
    private ConstraintLayout confirmacion,setnombre;
    private long TotalTime;
    private boolean correctpattern;

    private ArrayList<Categories> categorias;

    @RequiresApi(api = Build.VERSION_CODES.S)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        categorias = new ArrayList<>();

        Bundle extras = getIntent().getExtras();
        if (extras != null){
            cargarhoras(extras.getString("key"));
            categorias =(ArrayList<Categories>) extras.get("categorias");
        } else {
            try {
                cargarcategorias();
            } catch (FileNotFoundException e) {
                throw new RuntimeException(e);
            }
        }

        TextInputEditText textinputE = findViewById(R.id.TextInputE);
        TextInputEditText textinputE2 = findViewById(R.id.TextInputE2);
        TextInputLayout textinputL = findViewById(R.id.TextInputL);
        Button buttonAdd = findViewById(R.id.buttonadd);
        Button buttonsave = findViewById(R.id.buttonsave);
        Button buttonhome = findViewById(R.id.buttonhome);
        Button buttontimes = findViewById(R.id.buttontimes);
        Button buttonsi = findViewById(R.id.buttonsi);
        Button buttonno = findViewById(R.id.buttonno);
        Button buttonname = findViewById(R.id.buttonname);
        Animation animation = AnimationUtils.loadAnimation(this, R.anim.shake);

        listView = findViewById(R.id.listtimes);
        texttime = findViewById(R.id.texttime);
        confirmacion = findViewById(R.id.constraintLayout4);
        setnombre = findViewById(R.id.constraintLayout5);
        letters = new ArrayList<>();
        adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, letters);
        TotalTime = 0;
        correctpattern = false;

        listView.setAdapter(adapter);

        listView.setOnItemClickListener((parent, view, position, id) -> {
            RemoveHours(letters,position);
            adapter.notifyDataSetChanged();
        });
        textinputE.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if(textinputE.getText().length()==0){
                    textinputE.setTextColor(Color.rgb(248,144,75));
                    textinputL.setBoxStrokeColor(Color.rgb(248,144,75));
                    textinputL.setHintTextColor(ColorStateList.valueOf(Color.rgb(248,144,75)));
                    correctpattern = false;
                } else {
                    if (match(String.valueOf(textinputE.getText()))) {
                        textinputE.setTextColor(Color.GREEN);
                        textinputL.setBoxStrokeColor(Color.GREEN);
                        textinputL.setHintTextColor(ColorStateList.valueOf(Color.GREEN));
                        correctpattern = true;
                    } else {
                        textinputE.setTextColor(Color.RED);
                        textinputL.setBoxStrokeColor(Color.RED);
                        textinputL.setHintTextColor(ColorStateList.valueOf(Color.RED));
                        correctpattern = false;
                    }
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
        buttonAdd.setOnClickListener(view -> {
            if (correctpattern) {
                AddHours(String.valueOf(textinputE.getText()),letters);
                adapter.notifyDataSetChanged();
                textinputE.setText("");
            } else{
                textinputE.startAnimation(animation);
                textinputL.startAnimation(animation);
            }

        });
        buttonsave.setOnClickListener(view -> {
            if (listView.getCount()>0){

                InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(buttonsave.getWindowToken(), 0);
                textinputE.setEnabled(false);
                buttonhome.setEnabled(false);
                buttontimes.setEnabled(false);
                buttonAdd.setEnabled(false);
                buttonsave.setEnabled(false);
                confirmacion.setVisibility(View.VISIBLE);
                confirmacion.bringToFront();

                buttonsi.setOnClickListener(view2 -> {
                    confirmacion.setVisibility(View.INVISIBLE);
                    setnombre.setVisibility(View.VISIBLE);
                    setnombre.bringToFront();

                    buttonname.setOnClickListener(view3 -> {
                        guardartiempo(String.valueOf(textinputE2.getText()));
                        textinputE.setEnabled(true);
                        buttonhome.setEnabled(true);
                        buttontimes.setEnabled(true);
                        buttonAdd.setEnabled(true);
                        buttonsave.setEnabled(true);
                        setnombre.setVisibility(View.INVISIBLE);
                    });
                });
                buttonno.setOnClickListener(view2 -> {
                    textinputE.setEnabled(true);
                    buttonhome.setEnabled(true);
                    buttontimes.setEnabled(true);
                    buttonAdd.setEnabled(true);
                    buttonsave.setEnabled(true);
                    confirmacion.setVisibility(View.INVISIBLE);
            });
            }
        });
        buttonhome.setOnClickListener(view -> {});
        buttontimes.setOnClickListener(view -> openTimes());
    }

    private void cargarcategorias() throws FileNotFoundException {
        FileInputStream fis = openFileInput("times.txt");
        Scanner scanner = new Scanner(fis);

        while (scanner.hasNextLine()){
            String aux = scanner.next();//agarro la categoria
            Categories categoria = new Categories(aux);  //creo el objeto categoria
            if (scanner.hasNextLine()) {
                aux = scanner.next();
                categoria.setTotaltime(aux); //agrego el tiempo total

                while (match(aux) && scanner.hasNextLine()) { //agrego los tiempos
                    categoria.addtime(aux);
                    if (scanner.hasNextLine())
                        aux = scanner.next();
                    else {
                        break;
                    }
                }
                categorias.add(categoria); //agrego la categoria a la lista de categorias
            }
        }
        Log.d("out", String.valueOf(categorias));
        scanner.close();
    }

    private void cargarhoras(String category) {
        category = category.substring(0,category.indexOf("   -"));
        categorias.get(categorias.indexOf(category)).cargarhoras(letters);
    }

    public boolean match(String input){
        Pattern pattern1 = Pattern.compile("^(((\\d\\d|\\d):(\\d|0\\d|[0-5]\\d):(\\d|0\\d|[0-5]\\d))|((\\d|0\\d|[0-5]\\d):(\\d|0\\d|[0-5]\\d)))"); // (1:1:20 or 1:0)
        Matcher matcher1 = pattern1.matcher(input);

        Pattern pattern2 = Pattern.compile("^((\\d+)[hms]( |))*"); // (1:1:20 or 1:0)
        Matcher matcher2 = pattern2.matcher(input);

        //agregar fix

        return matcher1.matches() || matcher2.matches();
    }

    @RequiresApi(api = Build.VERSION_CODES.S)
    public void AddHours(String input, ArrayList letters){
        Pattern pattern1 = Pattern.compile("^(((\\d\\d|\\d):(\\d|0\\d|[0-5]\\d):(\\d|0\\d|[0-5]\\d))|((\\d|0\\d|[0-5]\\d):(\\d|0\\d|[0-5]\\d)))"); // (1:1:20 or 1:0)
        Matcher matcher1 = pattern1.matcher(input);

        Pattern pattern2 = Pattern.compile("^((\\d+)[hms]( |))*"); // (1:1:20 or 1:0)
        Matcher matcher2 = pattern2.matcher(input);

        if (matcher1.matches() || matcher2.matches()) {

            Duration t = Duration.ZERO;
            if (matcher1.matches()){
                String[] formato = input.split(":");

                if (formato.length == 3){
                    t=Duration.ofHours(Integer.parseInt(formato[0])).plusMinutes(Integer.parseInt(formato[1])).plusSeconds(Integer.parseInt(formato[2]));
                } else{
                    t=Duration.ofHours(0).plusMinutes(Integer.parseInt(formato[0])).plusSeconds(Integer.parseInt(formato[1]));
                }
            } else if (matcher2.matches()){
                String[] formato = input.split(" ");
                for(String i: formato) {
                    if (i.matches("(.*)h(.*)")) {
                        i = i.substring(0,i.length()-1);
                        long hora = Long.parseLong(i);
                        t=t.plusHours(hora);
                    } else if (i.matches("(.*)m(.*)")) {

                        i = i.substring(0,i.length()-1);
                        long hora = (Long.parseLong(i) / 60);
                        long minuto = Long.parseLong(i) % 60;
                        t=t.plusHours(hora).plusMinutes(minuto);
                    } else {
                        i = i.substring(0,i.length()-1);
                        long hora = (Long.parseLong(i))/3600;
                        long minuto = ((Long.parseLong(i)) % 3600)/ 60;
                        long segundos = Long.parseLong(i) % 60;
                        t=t.plusHours(hora).plusMinutes(minuto).plusSeconds(segundos);
                    }
                }
            }
            long time = t.getSeconds();
            String hora = String.valueOf((Integer.parseInt(String.valueOf(time))/3600));
            String minuto= String.valueOf((Integer.parseInt(String.valueOf(time)) % 3600) / 60);
            String segundos= String.valueOf(Integer.parseInt(String.valueOf(time)) % 60);

            if (hora.length() == 1){hora= "0"+hora;}
            if (minuto.length() == 1){minuto= "0"+minuto;}
            if (segundos.length() == 1){segundos= "0"+segundos;}

            letters.add(hora+":"+minuto+":"+segundos);

            TotalTime = TotalTime + t.getSeconds();
            texttime.setText( String.format("%d:%02d:%02d", TotalTime / 3600, (TotalTime % 3600) / 60, (TotalTime % 60)));
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public void RemoveHours(ArrayList letters, int position){
        String r = (String) letters.get(position); //obtengo el item a borrar
        String[] Hora = r.split(":"); //creo el objeto duracion para despues restarlo del total

        Duration t = Duration.ofHours(Integer.parseInt(Hora[0])).plusMinutes(Integer.parseInt(Hora[1])).plusSeconds(Integer.parseInt(Hora[2]));

        letters.remove(position); //borro el elemento

        TotalTime = TotalTime - t.getSeconds(); //lo resto del total

        texttime.setText( String.format("%d:%02d:%02d", TotalTime / 3600, (TotalTime % 3600) / 60, (TotalTime % 60)));
    }

    public void guardartiempo(String categoria){
        if(!categorias.contains(categoria)) {
            Categories aux = new Categories(categoria);
            aux.setTotaltime(String.valueOf(TotalTime));
            for (int i = 0; i < listView.getCount(); i++) {
                aux.addtime(letters.get(i));
            }
        } else {
            //agregar la notificacion al usuario de que esa categoria ya esta
        }
    }

    public void openTimes(){
        Intent intent = new Intent(this, Times.class);
        intent.putExtra("categorias",categorias);
        startActivity(intent);
        overridePendingTransition(0, 0);
    }

}