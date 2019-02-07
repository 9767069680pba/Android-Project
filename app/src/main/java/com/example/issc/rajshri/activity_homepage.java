package com.example.issc.rajshri;

import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Build;
import android.speech.RecognizerIntent;
import android.speech.tts.TextToSpeech;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

@RequiresApi(api = Build.VERSION_CODES.DONUT)
public class activity_homepage extends AppCompatActivity implements
        TextToSpeech.OnInitListener ,AdapterView.OnItemSelectedListener {
    private TextToSpeech tts;
    private final int REQ_CODE_SPEECH_INPUT = 100;
    SQLiteDatabase db;

    private Button b_search,rated,readed,speak;
    private ImageView  setting,dashboard;
    private TextView search,uname;
    private Spinner spinner2;



    @Override
    public void onInit(int status) {

        if (status == TextToSpeech.SUCCESS) {

            int result = tts.setLanguage(Locale.ENGLISH);

            if (result == TextToSpeech.LANG_MISSING_DATA
                    || result == TextToSpeech.LANG_NOT_SUPPORTED) {
                Log.e("TTS", "This Language is not supported");
            } else {
                speakOut("");
            }

        } else {
            Log.e("TTS", "Initilization Failed!");
        }

    }

    @Override
    public void onDestroy() {
        if (tts != null)
        {
            tts.stop();
            tts.shutdown();
        }
        super.onDestroy();
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_homepage);

        tts = new TextToSpeech(this, this);
        search = (TextView) findViewById(R.id.search);
        speak = (Button) findViewById(R.id.speak);
        uname=(TextView)findViewById(R.id.uname);

        spinner2 = (Spinner) findViewById(R.id.spinner2);

        spinner2.setOnItemSelectedListener(this);

        setting=(ImageView)findViewById(R.id.setting);
        dashboard=(ImageView)findViewById(R.id.dashboard);

        Intent in = getIntent();
        String username = in.getStringExtra("username");
        uname.setText("Welcome "+username);


        db = openOrCreateDatabase("dlvi11", Context.MODE_PRIVATE, null);
        addItemsOnSpinner();

        speak.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                promptSpeechInput();
            }
        });

        setting.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent in=new Intent(activity_homepage.this,activity_setting.class);
                startActivity(in);
            }
        });
        dashboard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent in=new Intent(activity_homepage.this,activity_dashboard.class);
                startActivity(in);
            }
        });


    }
    public void addItemsOnSpinner() {

        spinner2 = (Spinner) findViewById(R.id.spinner2);
        List<String> list = new ArrayList<String>();
        String selectQuery = "SELECT  * FROM 'books'";
        Cursor cursor = db.rawQuery(selectQuery, null);//selectQuery,selectedArguments
        if (cursor.moveToFirst()) {
            do {
                list.add(cursor.getString(1));//adding 2nd column data
            } while (cursor.moveToNext());
        }
        ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_spinner_item, list);
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner2.setAdapter(dataAdapter);
    }

    /**
     * Showing google speech input dialog
     * */
    private void promptSpeechInput() {
        Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL,
                RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.ENGLISH);

        try {
            startActivityForResult(intent, REQ_CODE_SPEECH_INPUT);
        } catch (ActivityNotFoundException a) {

        }
    }

    private void speakOut(String text) {
        tts.speak(text, TextToSpeech.QUEUE_FLUSH, null);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        switch (requestCode) {
            case REQ_CODE_SPEECH_INPUT: {
                if (resultCode == RESULT_OK && null != data) {

                    ArrayList<String> result = data
                            .getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);

                    if((result.get(0)).equals("search the book"))
                    {
                        speakOut("speak the book name");
                    }
                    else
                    if((result.get(0)).equals("setting"))
                    {

                        Intent in=new Intent(activity_homepage.this,activity_setting.class);
                        startActivity(in);
                    }
                    else
                    if((result.get(0)).equals("dashboard"))
                    {

                        Intent in=new Intent(activity_homepage.this,activity_dashboard.class);
                        startActivity(in);
                    }
                    else
                        if((result.get(0)).equals("search"))
                        {
                            if(search.getText().toString().length()==0)
                            {
                                speakOut("please Speak The Boom Name");
                            }
                            else {
                                Intent in = new Intent(activity_homepage.this, activity_ui.class);
                                in.putExtra("bookname", search.getText().toString());
                                startActivity(in);
                            }
                        }
                    else
                        if((result.get(0)).equals("most rated") || (result.get(0)).equals("Most Rated") || (result.get(0)).equals("MOST RATED"))
                        {
                            spinner2 = (Spinner) findViewById(R.id.spinner2);
                            List<String> list = new ArrayList<String>();

                            String selectQuery = "SELECT  * FROM 'books'";

                            Cursor cursor = db.rawQuery(selectQuery, null);//selectQuery,selectedArguments

                            if (cursor.moveToFirst()) {
                                do {
                                    list.add(cursor.getString(1));//adding 2nd column data
                                } while (cursor.moveToNext());
                            }
                            ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(this,
                                    android.R.layout.simple_spinner_item, list);
                            dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                            spinner2.setAdapter(dataAdapter);
                        }
                    else {
                        search.setText(result.get(0));
                        addItemsOnSpinner();
                    }
                }
                break;
            }
        }
    }
    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        String item=parent.getSelectedItem().toString();

        if(position>=1) {
            Intent in = new Intent(activity_homepage.this, activity_ui.class);
            in.putExtra("bookname", item);
            startActivity(in);
        }
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }
}