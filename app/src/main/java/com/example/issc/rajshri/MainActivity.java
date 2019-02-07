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
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

@RequiresApi(api = Build.VERSION_CODES.DONUT)
public class MainActivity extends AppCompatActivity implements
        TextToSpeech.OnInitListener {
    private TextToSpeech tts;
    private final int REQ_CODE_SPEECH_INPUT = 100;
    SQLiteDatabase db;

    private Button login,registration,b_search,rated,readed,liked,speak;
    private ImageView  setting,dashboard;
    private TextView search;
    private Spinner spinner2;


    @Override
    public void onInit(int status) {

        if (status == TextToSpeech.SUCCESS) {

            int result = tts.setLanguage(Locale.ENGLISH);

            if (result == TextToSpeech.LANG_MISSING_DATA
                    || result == TextToSpeech.LANG_NOT_SUPPORTED) {
                Log.e("TTS", "This Language is not supported");
            } else {
                //btnSpeak.setEnabled(true);
                speakOut("");
            }

        } else {
            Log.e("TTS", "Initilization Failed!");
        }

    }

    @Override
    public void onDestroy() {
        // Don't forget to shutdown tts!
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
        setContentView(R.layout.activity_main);

        tts = new TextToSpeech(this, this);
        search = (TextView) findViewById(R.id.search);
        speak = (Button) findViewById(R.id.speak);
        login=(Button) findViewById(R.id.login);
        registration=(Button) findViewById(R.id.register);
        setting=(ImageView)findViewById(R.id.setting);
        dashboard=(ImageView)findViewById(R.id.dashboard);



        db = openOrCreateDatabase("dlvi1111", Context.MODE_PRIVATE, null);

        db.execSQL("CREATE TABLE IF NOT EXISTS user(U_ID INTEGER , name VARCHAR, DOB DATE, password NUMERIC, contact INTEGER , interest VARCHAR,course VARCHAR);");
        db.execSQL("CREATE TABLE IF NOT EXISTS download(U_ID INTEGER , book_id INTEGER, download_date DATE, download_book VARCHAR);");
        db.execSQL("CREATE TABLE IF NOT EXISTS wishlist(U_ID INTEGER , book_id INTEGER, wishlist_date DATE, wishlist_book VARCHAR);");
        db.execSQL("CREATE TABLE IF NOT EXISTS bookmark(U_ID INTEGER , book_id INTEGER, bookmark_date DATE, bookmark_name VARCHAR);");
        db.execSQL("CREATE TABLE IF NOT EXISTS userSetting(U_ID INTEGER , speed INTEGER, accent VARCHAR, language VARCHAR,delete_profile VARCHAR);");
        db.execSQL("CREATE TABLE IF NOT EXISTS books( book_id INTEGER, book_name VARCHAR,author_name VARCHAR,publication_name VARCHAR ,isbn VARCHAR,category VARCHAR,feedback VARCHAR ,rating NUMERIC);");
        db.execSQL("CREATE TABLE IF NOT EXISTS rating(U_ID INTEGER , book_id INTEGER, rating NUMERIC);");
        db.execSQL("CREATE TABLE IF NOT EXISTS feedback(U_ID INTEGER , book_id INTEGER, feedback_date DATE, feedback VARCHAR);");



        db.execSQL("INSERT INTO Books VALUES(1,'Doorthy and the wizard in oz','L.Frank Baum','George M. Hill Company','NULL','Fiction','NULL','NULL');");
        db.execSQL("INSERT INTO Books VALUES(2,'War and Peace','Leo Tolstoy','The Russian Messenger','NULL','Fiction','NULL','NULL');");
        db.execSQL("INSERT INTO Books VALUES(3,'The Peasant story of Nepoleon','B','C','12345678913245','History','NULL','NULL');");
        db.execSQL("INSERT INTO Books VALUES(4,'The Art of War','Sun Tzu','Jesuit Jean Joseph MArie Amiot','NULL','History','NULL','NULL');");
        db.execSQL("INSERT INTO Books VALUES(5,'As a man Thinketh','James Allen','TarcherPerigee','NULL','Literature','NULL','NULL');");
        db.execSQL("INSERT INTO Books VALUES(6,'As U Like It','Shekespeare','publication of shekespeare','NULL','Literature','NULL','NULL');");
        db.execSQL("INSERT INTO Books VALUES(7,'Beyong Good And Evil','Friedrich Nietzsche','NULL','NULL','Philosophy','NULL','NULL');");
        db.execSQL("INSERT INTO Books VALUES(8,'Bhagwatgita','Sir Edwin Arnold','NULL','12345678913245','Philosophy','NULL','NULL');");
        db.execSQL("INSERT INTO Books VALUES(9,'The Memories of Sherlock Holmes','A.Conan Doyle','George Newnes','NULL','Mistery','NULL','NULL');");



        addItemsOnSpinner();

        speak.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                promptSpeechInput();
            }
        });

        login.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                Intent in = new Intent(MainActivity.this, activity_login.class);
                startActivity(in);
            }
        });

        registration.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                Intent in = new Intent(MainActivity.this, activity_registration.class);
                startActivity(in);
            }
        });

        setting.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent in=new Intent(MainActivity.this,activity_setting.class);
                startActivity(in);
            }
        });
        dashboard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent in=new Intent(MainActivity.this,activity_dashboard.class);
                startActivity(in);
            }
        });

    }
    public void addItemsOnSpinner() {

        spinner2 = (Spinner) findViewById(R.id.spinner2);
        List<String> list = new ArrayList<String>();

        // Select All Query
        String selectQuery = "SELECT  * FROM 'books'";

        Cursor cursor = db.rawQuery(selectQuery, null);//selectQuery,selectedArguments

        // looping through all rows and adding to list
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
//        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault());
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.ENGLISH);

        try {
            startActivityForResult(intent, REQ_CODE_SPEECH_INPUT);
        } catch (ActivityNotFoundException a) {

        }
    }

    private void speakOut(String text) {
        tts.speak(text, TextToSpeech.QUEUE_FLUSH, null);
    }

    /**
     * Receiving speech input
     * */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        switch (requestCode) {
            case REQ_CODE_SPEECH_INPUT: {
                if (resultCode == RESULT_OK && null != data) {

                    ArrayList<String> result = data
                            .getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
                    if((result.get(0)).equals("login"))
                    {
                        Intent in = new Intent(MainActivity.this, activity_login.class);
                        startActivity(in);
                    }else
                    if((result.get(0)).equals("registration"))
                    {
                        Intent in = new Intent(MainActivity.this, MainActivity.class);
                        startActivity(in);
                    }
                    else
                    if((result.get(0)).equals("search the book"))
                    {
                        speakOut("speak the book name");
                    }
                    else
                    if((result.get(0)).equals("setting"))
                    {

                        Intent in=new Intent(MainActivity.this,activity_setting.class);
                        startActivity(in);
                    }
                    else
                    if((result.get(0)).equals("dashboard"))
                    {

                        Intent in=new Intent(MainActivity.this,activity_dashboard.class);
                        startActivity(in);
                    }
                    else {
                        search.setText(result.get(0));
                       // addItemsOnSpinner();
                    }
                }
                break;
            }
        }
    }
}