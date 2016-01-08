package com.ntust.dori;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.net.Uri;
import android.os.Bundle;
import android.speech.RecognizerIntent;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {
    private static final int REQUEST_CODE = 1, CAMERA_REQUEST_CODE = 2;
    Button speak;
    ListView wordList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        speak = (Button)findViewById(R.id.button);
        wordList = (ListView)findViewById(R.id.listView);

        speak.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
                String lang = Locale.TRADITIONAL_CHINESE.toString();
                intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, lang);
                intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_PREFERENCE, lang);
                intent.putExtra(RecognizerIntent.EXTRA_ONLY_RETURN_LANGUAGE_PREFERENCE, lang);
                intent.putExtra(RecognizerIntent.EXTRA_PROMPT, "OK Dori");
                startActivityForResult(intent, REQUEST_CODE);
            }
        });

        PackageManager pm = getPackageManager();
        List<ResolveInfo> activities = pm.queryIntentActivities(new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH), 0);

        if (activities.size() == 0) {
            speak.setEnabled(false);
            speak.setText("no intent");
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_CODE && resultCode == RESULT_OK) {
            ArrayList<String> matches = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
            extract(matches);
            wordList.setAdapter(new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, matches));


        }
        else if (requestCode == REQUEST_CODE && resultCode == CAMERA_REQUEST_CODE) {
            Uri photo

        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    void extract(ArrayList<String> matches) {
        if (matches.get(0).equals("打開相機")) {
            startActivityForResult(new Intent("android.media.action.IMAGE_CAPTURE"), CAMERA_REQUEST_CODE);
        }
    }
}
