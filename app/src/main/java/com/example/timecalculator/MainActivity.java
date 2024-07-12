package com.example.timecalculator;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.SharedPreferences;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.inputmethod.EditorInfo;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStreamReader;
import java.time.Duration;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MainActivity extends AppCompatActivity {

    private ArrayList<String> letters;
    private ArrayAdapter<String> adapter;
    private TextView texttime;
    private long TotalTime;
    private boolean correctpattern;
    private TextInputEditText textinputE;
    private TextInputLayout textinputL;
    private Animation animation;

    @RequiresApi(api = Build.VERSION_CODES.S)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //------------------------------------------------------------------------------------------

        //Declaracion de variables
        textinputE = findViewById(R.id.TextInputE);
        textinputL = findViewById(R.id.TextInputL);
        Button buttonAdd = findViewById(R.id.buttonadd);
        Button buttonDelete = findViewById(R.id.buttondelete);
        Button buttonv1 = findViewById(R.id.button3);
        Button buttonv2 = findViewById(R.id.button4);
        Button buttonv3 = findViewById(R.id.button5);
        Button buttonv4 = findViewById(R.id.button6);
        Button buttonv5 = findViewById(R.id.button7);
        ListView listView = findViewById(R.id.listtimes);
        texttime = findViewById(R.id.texttime);
        animation = AnimationUtils.loadAnimation(this, R.anim.shake);

        letters = new ArrayList<>();
        adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, letters);
        TotalTime = 0;
        correctpattern = false;
        
        //------------------------------------------------------------------------------------------

        //List of Times
        listView.setAdapter(adapter);
        listView.setOnItemClickListener((parent, view, position, id) -> {
            RemoveHours(letters, position);
        });

        //------------------------------------------------------------------------------------------

        //Text Input
        textinputE.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }
            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if(Objects.requireNonNull(textinputE.getText()).length()==0){
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

        textinputE.setOnEditorActionListener((textView, i, keyEvent) -> {
            if (i == EditorInfo.IME_ACTION_DONE) {
                AddHours(String.valueOf(textinputE.getText()), letters);
                return true;
            }
            return false;
        });

        //------------------------------------------------------------------------------------------


        //Buttons
        buttonAdd.setOnClickListener(view -> {
            AddHours(String.valueOf(textinputE.getText()), letters);
        });

        buttonv1.setOnClickListener(view -> VelocityChanger(1F));
        buttonv2.setOnClickListener(view -> VelocityChanger(1.25F));
        buttonv3.setOnClickListener(view -> VelocityChanger(1.5F));
        buttonv4.setOnClickListener(view -> VelocityChanger(1.75F));
        buttonv5.setOnClickListener(view -> VelocityChanger(2F));

        buttonDelete.setOnClickListener(view -> {
            resetTimes();
        });

        //------------------------------------------------------------------------------------------

        //load times from file
        readFromFile();
    }

    @RequiresApi(api = Build.VERSION_CODES.S)
    public void readFromFile(){
        try {
            FileInputStream fileInputStream = openFileInput("times.txt");
            InputStreamReader inputStreamReader = new InputStreamReader(fileInputStream);
            BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
            String line;
            while ((line = bufferedReader.readLine()) != null) {
                if (match(line)){
                    String[] Hora = line.split(":");
                    if(Hora.length == 3) {
                        Duration t = Duration.ofHours(Integer.parseInt(Hora[0])).plusMinutes(Integer.parseInt(Hora[1])).plusSeconds(Integer.parseInt(Hora[2]));
                        TotalTime = TotalTime + t.getSeconds();
                        letters.add(line);
                    } else {
                        System.out.println("Error en el formato de tiempo guardado");
                        System.out.println(line);
                    }
                }
            }
            texttime.setText( String.format("%d:%02d:%02d", TotalTime / 3600, (TotalTime % 3600) / 60, (TotalTime % 60)));
            adapter.notifyDataSetChanged();
            fileInputStream.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void resetTimes(){
        adapter.clear();
        adapter.notifyDataSetChanged();
        TotalTime=0;
        texttime.setText("0:00:00");
        File file = new File(getFilesDir(), "times.txt");
        file.delete();
    }

    public boolean match(String input){
        Pattern pattern1 = Pattern.compile("^(((\\d\\d|\\d):(\\d|0\\d|[0-5]\\d):(\\d|0\\d|[0-5]\\d))|((\\d|0\\d|[0-5]\\d):(\\d|0\\d|[0-5]\\d)))"); // (1:1:20 or 1:0)
        Matcher matcher1 = pattern1.matcher(input);

        Pattern pattern2 = Pattern.compile("^((\\d+)[hms]( |))*"); // (1:1:20 or 1:0)
        Matcher matcher2 = pattern2.matcher(input);

        return matcher1.matches() || matcher2.matches();
    }


    @RequiresApi(api = Build.VERSION_CODES.S)
    public void AddHours(String input, ArrayList<String> letters){
        if(!correctpattern){
            textinputE.startAnimation(animation);
            textinputL.startAnimation(animation);
            return;
        }

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

            File file = new File(getFilesDir(), "times.txt");
            try {
                FileOutputStream fileOutputStream = new FileOutputStream(file, true);
                fileOutputStream.write((hora+":"+minuto+":"+segundos+"\n").getBytes());
                fileOutputStream.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        adapter.notifyDataSetChanged();
        textinputE.setText("");
    }

    @SuppressLint("DefaultLocale")
    @RequiresApi(api = Build.VERSION_CODES.O)
    public void RemoveHours(ArrayList<String> letters, int position){
        String r = letters.get(position); //obtengo el item a borrar
        String[] Hora = r.split(":"); //creo el objeto duracion para despues restarlo del total

        Duration t = Duration.ofHours(Integer.parseInt(Hora[0])).plusMinutes(Integer.parseInt(Hora[1])).plusSeconds(Integer.parseInt(Hora[2]));

        letters.remove(position); //borro el elemento

        TotalTime = TotalTime - t.getSeconds(); //lo resto del total

        texttime.setText( String.format("%d:%02d:%02d", TotalTime / 3600, (TotalTime % 3600) / 60, (TotalTime % 60)));
        File file = new File(getFilesDir(), "times.txt");
        try {
            FileOutputStream fileOutputStream = new FileOutputStream(file, false);
            for (String letter : letters) {
                fileOutputStream.write((letter + "\n").getBytes());
            }
            fileOutputStream.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        adapter.notifyDataSetChanged();
    }

    public void VelocityChanger(float i){
        long aux = (long) (TotalTime/i);
        texttime.setText(String.format("%d:%02d:%02d", aux / 3600, (aux % 3600) / 60, (aux % 60)));
    }
}