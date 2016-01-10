package com.ntust.dori;

import android.content.DialogInterface;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.util.ArrayList;

public class InsListActivity extends AppCompatActivity {
    ListView oldList, newList;
    ArrayList<String> oldStr = new ArrayList<>(), newStr = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ins_list);

        oldList = ((ListView) findViewById(R.id.listViewOld));
        newList = ((ListView) findViewById(R.id.listViewNew));

        Cursor cursor = MainActivity.db.rawQuery("SELECT * FROM OldKey", null);
        if (cursor != null) {
            cursor.moveToFirst();
            for (int i=0; i<cursor.getCount(); ++i) {
                oldStr.add(cursor.getString(1));
                cursor.moveToNext();
            }
        }

        cursor = MainActivity.db.rawQuery("SELECT * FROM HotKey", null);
        if (cursor != null) {
            cursor.moveToFirst();
            for (int i=0; i<cursor.getCount(); ++i) {
                newStr.add(cursor.getString(1) + "=" + cursor.getString(2));
                cursor.moveToNext();
            }
            cursor.close();
        }

        ArrayAdapter<String> oldListAdapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, oldStr);
        oldList.setAdapter(oldListAdapter);

        final ArrayAdapter<String> newListAdapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, newStr);
        newList.setAdapter(newListAdapter);
        newList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, final View view, final int position, long id) {
                new AlertDialog.Builder(InsListActivity.this)
                        .setTitle("確認刪除")
                        .setMessage("要把這個指令刪掉嗎?")
                        .setPositiveButton("刪除", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                MainActivity.db.execSQL("DELETE FROM HotKey WHERE alias='" + newList.getItemAtPosition(position).toString().split("=")[0] + "'");
                                //newStr.remove(position);
                                newListAdapter.remove(newList.getItemAtPosition(position).toString());
                            }
                        })
                        .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {

                            }
                        })
                        .show();
            }
        });
    }
}
