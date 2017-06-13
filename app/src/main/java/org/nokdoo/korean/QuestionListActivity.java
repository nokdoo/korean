package org.nokdoo.korean;

import android.app.*;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Color;
import android.os.*;

import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup.LayoutParams;

import android.widget.*;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Objects;


public class QuestionListActivity extends AppCompatActivity{

    ProblemInfo pi = new ProblemInfo();

    /** Called when the activity is first created. */

    private DBConnect mDbConnect = null;

    Context mContext = null;


    @Override

    public void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        //setContentView(R.layout.main);

        LinearLayout layout = new LinearLayout(this);

        layout.setOrientation(LinearLayout.VERTICAL);

        layout.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));

        TableLayout table = new TableLayout(this);
        table.setLayoutParams(new LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));

        TableRow row = new TableRow(this);
        row.setLayoutParams(new TableLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));

        Button setRandomBtn = new Button(this);
        setRandomBtn.setText("랜덤문제풀기");
        setRandomBtn.setBackgroundColor(Color.parseColor("#dad9ff"));
        setRandomBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v){
                Intent question = new Intent(QuestionListActivity.this, QuestionActivity.class);
                pi.random_flag = "Y";
                question.putExtra("pi", (Serializable)pi);
                startActivityForResult(question, 2);
            }
        });
        layout.addView(setRandomBtn);

        Intent intent = getIntent();
        pi = (ProblemInfo)intent.getSerializableExtra("pi");

        mDbConnect = DBConnect.getInstance(this);
        final Cursor c = mDbConnect.QuestionList(pi);

        int i = 0;
        while(c.moveToNext()) {

            i++;
            HashMap<String, String> param = new HashMap<String, String>();

            Button button = new Button(this);
            button.setLayoutParams(new TableRow.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT));
            button.setText(String.valueOf(i));

            param.put("gubun", c.getString(0));
            param.put("num", c.getString(1));
            if (c.getString(2).equals("Y")) {
                button.setBackgroundColor(Color.parseColor("#D4f4fa"));
            }
            button.setTag(param);
            button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent question = new Intent(QuestionListActivity.this, QuestionActivity.class);
                    HashMap<String, String> param = new HashMap<String, String>();
                    param = (HashMap)v.getTag();
                    pi.gubun = param.get("gubun");
                    pi.num = Integer.parseInt(param.get("num"));
                    question.putExtra("pi", (Serializable) pi);
                    startActivityForResult(question, 2);

                }
            });

            row.addView(button);

            if(i%4==0){
                table.addView(row);
                row = new TableRow(this);
                row.setLayoutParams(new TableLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));
            }
        }

        table.addView(row);
        layout.addView(table);
        setContentView(layout);
    }

    @Override
    protected void onResume(){
        super.onResume();
    }

    @Override
    protected void onPause(){
        super.onPause();
        pi.random_flag = "N";
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent intent){
        if(resultCode == RESULT_OK){
            Intent refresh = new Intent(this, QuestionListActivity.class);
            pi = (ProblemInfo)intent.getSerializableExtra("pi");
            refresh.putExtra("pi", pi);
            this.finish();
            startActivity(refresh);
        }
    }

}