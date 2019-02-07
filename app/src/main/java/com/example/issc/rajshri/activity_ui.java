package com.example.issc.rajshri;

import android.content.Context;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.media.MediaPlayer;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class activity_ui extends AppCompatActivity {
    TextView book,auther,publication;
    SQLiteDatabase db;
    Button play,pause;
    MediaPlayer mp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ui);

        db = openOrCreateDatabase("dlvi11", Context.MODE_PRIVATE, null);

        book=(TextView)findViewById(R.id.bookname);
        auther=(TextView)findViewById(R.id.auther);
        publication=(TextView)findViewById(R.id.publication);
        play=(Button)findViewById(R.id.play);
        pause=(Button)findViewById(R.id.pause);

        Intent in = getIntent();
        String bookname1 = in.getStringExtra("bookname");
        book.setText(bookname1);
        auther.setText("L.Frank Baum");
        publication.setText("George M. Hill Company");

        mp = MediaPlayer.create(this, R.raw.music1);

        play.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mp.start();
            }
        });

        pause.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mp.pause();
            }
        });
    }
}