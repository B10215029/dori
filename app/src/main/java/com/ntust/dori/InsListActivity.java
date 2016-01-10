package com.ntust.dori;

import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

public class InsListActivity extends AppCompatActivity {
    ListView oldList, newList;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ins_list);

        oldList = ((ListView) findViewById(R.id.listViewOld));
        newList = ((ListView) findViewById(R.id.listViewNew));

        ArrayAdapter<String> oldListAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, new String[]{"AAA", "BBB", "CCC", "BBB", "CCC", "BBB", "CCC", "BBB", "CCC"});
        oldList.setAdapter(oldListAdapter);

        ArrayAdapter<String> newListAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, new String[]{"aaa", "bbb", "ccc", "bbb", "ccc", "bbb", "ccc", "bbb", "ccc"});
        newList.setAdapter(newListAdapter);
        newList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                new AlertDialog.Builder(InsListActivity.this)
                        .setTitle("確認刪除")
                        .setMessage("要把這個指令刪掉嗎?")
                        .setPositiveButton("刪除", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {

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
