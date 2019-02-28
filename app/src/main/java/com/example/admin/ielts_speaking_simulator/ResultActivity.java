package com.example.admin.ielts_speaking_simulator;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;

public class ResultActivity extends AppCompatActivity {

    ListView lv;
    ImageView imgback, imginfo;
    ArrayList<String> ds = new ArrayList<String>();
    ArrayList<String> dsrecord = new ArrayList<String>();
    Context c;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_result);

        lv = findViewById(R.id.listview);
        imgback = findViewById(R.id.backimg);
        imginfo = findViewById(R.id.infoimg);

        ds = (ArrayList)getIntent().getExtras().get("question");
        dsrecord =(ArrayList)getIntent().getExtras().get("record");
        MyAdapter adapter = new MyAdapter(ResultActivity.this, ds, dsrecord);
        lv.setAdapter(adapter);

        imgback.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(ResultActivity.this, MainActivity.class);
                startActivity(i);
                finish();
            }
        });

        imginfo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                customDialogInfo();
            }
        });
    }

    public void customDialogInfo()
    {
        final AlertDialog alertDialog = new AlertDialog.Builder(
                ResultActivity.this).create();
        LayoutInflater inflater = this.getLayoutInflater();
        final View view = inflater.inflate(R.layout.dialog_them_sua_layout, null);
        TextView tv_title = view.findViewById(R.id.title);
        TextView txtinfo = view.findViewById(R.id.username);

        txtinfo.setText(" 1. There are 5 questions. You can't pause the program during its running. \n \n 2. After each question you will hear a sound, which means that you can answer the question. \n \n 3. You'll be given answers automatically after finishing the 5 questions asked. \n \n Hope you will have a great time.");
        alertDialog.setView(view);

        ImageView closeimg = (ImageView) view.findViewById(R.id.imgclose);
        closeimg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                    alertDialog.dismiss();
            }
        });
        alertDialog.show();
    }
}
