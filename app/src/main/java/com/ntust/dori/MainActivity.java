package com.ntust.dori;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.Bundle;
import android.speech.RecognizerIntent;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {
    private static final int SPEECH_REQUEST_CODE = 1, CAMERA_REQUEST_CODE = 2, SPEECHSHOW_REQUEST_CODE = 3;
    Button speak;
    ListView wordList;
    EditText editText;
    public static SQLiteDatabase db = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        speak = (Button)findViewById(R.id.button);
        wordList = (ListView)findViewById(R.id.listView);
        editText = (EditText) findViewById(R.id.editText);

        speak.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
                String lang = Locale.TRADITIONAL_CHINESE.toString();
                intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, lang);
                intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_PREFERENCE, lang);
                intent.putExtra(RecognizerIntent.EXTRA_ONLY_RETURN_LANGUAGE_PREFERENCE, lang);
                intent.putExtra(RecognizerIntent.EXTRA_PROMPT, "OK Dori");
                startActivityForResult(intent, SPEECH_REQUEST_CODE);
            }
        });

        PackageManager packageManager = getPackageManager();
        List<ResolveInfo> activities = packageManager.queryIntentActivities(new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH), 0);

        if (activities.size() == 0) {
            speak.setEnabled(false);
            speak.setText("不支援語音識別");
        }

        findViewById(R.id.test_button_1).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
                String lang = Locale.TRADITIONAL_CHINESE.toString();
                intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, lang);
                intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_PREFERENCE, lang);
                intent.putExtra(RecognizerIntent.EXTRA_ONLY_RETURN_LANGUAGE_PREFERENCE, lang);
                intent.putExtra(RecognizerIntent.EXTRA_PROMPT, "OK Dori");
                startActivityForResult(intent, SPEECHSHOW_REQUEST_CODE);
            }
        });

        findViewById(R.id.test_button_2).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

        findViewById(R.id.button_run).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ArrayList<String> matches = new ArrayList<>();
                matches.add(((EditText) findViewById(R.id.editText)).getText().toString());
                try {
                    exec(analyze(matches));
                } catch (Exception e) {
                    Toast.makeText(MainActivity.this, e.toString(), Toast.LENGTH_LONG).show();
                }
            }
        });

        db = openOrCreateDatabase("instruction", MODE_PRIVATE, null);
        db.execSQL("CREATE TABLE IF NOT EXISTS HotKey(_id INTEGER PRIMARY KEY, alias TEXT UNIQUE, instruction TEXT)");
        db.execSQL("CREATE TABLE IF NOT EXISTS OldKey(_id INTEGER PRIMARY KEY, instruction TEXT UNIQUE)");
        databaseInit();
    }

    void databaseInit() {
        Cursor cursor = db.rawQuery("SELECT * FROM OldKey", null);
        if (cursor != null) {
            if (cursor.getCount() == 0) {
                db.execSQL("INSERT INTO OldKey(instruction) VALUES('新增指令')");
                db.execSQL("INSERT INTO OldKey(instruction) VALUES('打開')");
                db.execSQL("INSERT INTO OldKey(instruction) VALUES('寄信')");
                db.execSQL("INSERT INTO OldKey(instruction) VALUES('撥給')");
                db.execSQL("INSERT INTO OldKey(instruction) VALUES('撥出')");
                db.execSQL("INSERT INTO OldKey(instruction) VALUES('連到')");
                db.execSQL("INSERT INTO OldKey(instruction) VALUES('執行')");
                db.execSQL("INSERT INTO OldKey(instruction) VALUES('選擇')");
                db.execSQL("INSERT INTO OldKey(instruction) VALUES('檔案')");
                db.execSQL("INSERT INTO OldKey(instruction) VALUES('搜尋')");
                db.execSQL("INSERT INTO OldKey(instruction) VALUES('位置')");
            }
            cursor.close();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == SPEECH_REQUEST_CODE && resultCode == RESULT_OK) {
            ArrayList<String> matches = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
            wordList.setAdapter(new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, matches));
            try {
                exec(analyze(matches));
            } catch (Exception e) {
                Toast.makeText(this, e.toString(), Toast.LENGTH_LONG).show();
            }
        }
        else if (requestCode == SPEECHSHOW_REQUEST_CODE && resultCode == RESULT_OK) {
            ArrayList<String> matches = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
            wordList.setAdapter(new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, matches));
            editText.setText(editText.getText() + matches.get(0));
        }
        else if (requestCode == CAMERA_REQUEST_CODE && resultCode == RESULT_OK) {
            Uri photoUri = data.getData();
            Toast.makeText(this, "The photo is put in:\n" + photoUri, Toast.LENGTH_LONG).show();
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    ArrayList<String> analyze(ArrayList<String> matches) throws Exception {
        ArrayList<String> instruction = new ArrayList<>();

        Cursor cursor = db.rawQuery("SELECT * FROM HotKey", null);
        if (cursor != null) {
            cursor.moveToFirst();
            for (int i=0; i<cursor.getCount(); ++i) {
                if (cursor.getString(1).equals(matches.get(0).substring(0, 2))) { //前2個
                    matches.set(0, cursor.getString(2) + matches.get(0).substring(2));
                    break;
                }
                cursor.moveToNext();
            }
        }

        cursor = db.rawQuery("SELECT * FROM OldKey", null);
        if (cursor != null) {
            cursor.moveToFirst();
            for (int i=0; i<cursor.getCount(); ++i) {
                if (cursor.getString(1).equals(matches.get(0).substring(0, 2))) { //前2個
                    instruction.add(cursor.getString(1)); //add ins
                    instruction.add(matches.get(0).substring(2));
                    cursor.close();
                    return instruction;
                }
                cursor.moveToNext();
            }

            cursor.moveToFirst();
            for (int i=0; i<cursor.getCount(); ++i) {
                if (cursor.getString(1).equals(matches.get(0).substring(0, 4))) { //前4個
                    instruction.add(cursor.getString(1)); //add ins
                    instruction.add(matches.get(0).substring(4));
                    cursor.close();
                    return instruction;
                }
                cursor.moveToNext();
            }
            cursor.close();
        }

        instruction.add("");
        instruction.add(matches.get(0));
        return instruction;
    }

    void exec(ArrayList<String> instruction) throws Exception {
        if (instruction.get(0).equals("新增指令")) {
            String hotKey = instruction.get(1).substring(0, 2), oldKey = instruction.get(1).substring(2);

            db.execSQL("INSERT INTO HotKey(alias,instruction) VALUES('" + hotKey + "','" + oldKey + "')");
        }
        else if (instruction.get(0).equals("打開")) {
            if (instruction.get(1).equals("相機")) {
                Intent intent = new Intent("android.media.action.IMAGE_CAPTURE");
                startActivity(intent);
            }
            else if (instruction.get(1).equals("聯絡人")) {
                Uri contacts = Uri.parse("content://contacts/people");
                Intent showContacts = new Intent(Intent.ACTION_VIEW, contacts);
                startActivity(showContacts);
            }
            else if (instruction.get(1).equals("YouTube")) {
                Intent launchApp = getPackageManager().getLaunchIntentForPackage("com.google.android.youtube");
                if (launchApp != null) {
                    launchApp.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(launchApp);
                } else {
                    Uri uri = Uri.parse("market://details?id=" + "com.google.android.youtube");
                    launchApp = new Intent(Intent.ACTION_VIEW, uri);
                    startActivity(launchApp);
                }
            }
        }
        else if (instruction.get(0).equals("寄信")) {
            if (instruction.get(1).contains("@")) {
                Uri mail = Uri.parse("mailto:" + instruction.get(1));
                Intent sendEmail = new Intent(Intent.ACTION_SENDTO, mail);
                startActivity(sendEmail);
            }
            else {
                Uri uri = Uri.parse("smsto:" + instruction.get(1));
                Intent intent = new Intent(Intent.ACTION_SENDTO, uri);
//            intent.putExtra("sms_body", "The SMS text");
                startActivity(intent);
            }
        }
        else if (instruction.get(0).equals("撥給")) {
            Uri uri = Uri.parse("tel:" + instruction.get(1));
            Intent intent = new Intent(Intent.ACTION_DIAL, uri);
            startActivity(intent);
        }
        else if (instruction.get(0).equals("撥出")) {
            Uri uri = Uri.parse("tel:" + instruction.get(1));
            Intent intent = new Intent(Intent.ACTION_CALL, uri);
            startActivity(intent);
        }
        else if (instruction.get(0).equals("連到")) {
            Uri uri = Uri.parse("http://" + instruction.get(1));
            Intent intent = new Intent(Intent.ACTION_VIEW, uri);
            startActivity(intent);
        }
        else if (instruction.get(0).equals("執行")) {
            Intent launchApp = getPackageManager().getLaunchIntentForPackage(instruction.get(1));
            if (launchApp != null) {
                launchApp.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(launchApp);
            } else {
                Uri uri = Uri.parse("market://details?id=" + instruction.get(1));
                launchApp = new Intent(Intent.ACTION_VIEW, uri);
                startActivity(launchApp);
            }
        }
        else if (instruction.get(0).equals("選擇")) {
            Intent selApp = new Intent(Intent.ACTION_MAIN);
            selApp.addCategory(Intent.CATEGORY_LAUNCHER);
            startActivity(Intent.createChooser(selApp, "Choose an App to launch"));
        }
        else if (instruction.get(0).equals("檔案")) {
            Intent pickFile = new Intent(Intent.ACTION_GET_CONTENT);
            pickFile.setType("*/*");
            pickFile.addCategory(Intent.CATEGORY_OPENABLE);
            startActivity(Intent.createChooser(pickFile, "selecta File from content provider"));
        }
        else if (instruction.get(0).equals("搜尋")) {
            Uri uri = Uri.parse("http://www.google.com/search?q=" + instruction.get(1));
            Intent intent = new Intent(Intent.ACTION_VIEW, uri);
            startActivity(intent);
        }
        else if (instruction.get(0).equals("位置")) {
            Uri uri = Uri.parse("geo:0,0?q=" + instruction.get(1));
            Intent it = new Intent(Intent.ACTION_VIEW, uri);
            startActivity(it);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.ins_list:
                Intent intent = new Intent();
                intent.setClass(this, InsListActivity.class);
                startActivity(intent);
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return super.onCreateOptionsMenu(menu);
    }
}