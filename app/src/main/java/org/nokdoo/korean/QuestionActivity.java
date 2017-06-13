package org.nokdoo.korean;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import android.os.Handler;

public class QuestionActivity extends AppCompatActivity implements View.OnTouchListener {

    class BindObj{
        String exampleText;
        Button exampleBtn;
    };

    private float x, y;

    private ProblemInfo pi = new ProblemInfo();

    private DBConnect mDbConnect = null;

    private Context mContext = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.question_activity);
        Intent intent = getIntent();
        pi = (ProblemInfo)intent.getSerializableExtra("pi");
        mContext = getApplicationContext();
        mDbConnect = DBConnect.getInstance(this);
        final Cursor c = mDbConnect.Question(pi);
        FrameLayout frame = (FrameLayout)findViewById(R.id.frame);
        frame.setOnTouchListener(this);


        if(c.moveToFirst()){
            TextView questionView = (TextView)findViewById(R.id.questionView);
            questionView.setText(c.getString(0));
            final String answer = c.getString(1);
            pi.keycode = c.getString(2);
            pi.num = c.getInt(3);
            

            int i = 0;
            final int len = answer.length();
            //final TableRow answerRow = (TableRow)findViewById(R.id.answerRow);
            final LinearLayout answerRow = (LinearLayout)findViewById(R.id.answerRow);
            for(;i<len;i++){
                final Button answerBtn = new Button(this);
                LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, TableRow.LayoutParams.WRAP_CONTENT);

                params.gravity = Gravity.CENTER;
                params.weight = 1;
                answerBtn.setLayoutParams(params);
                //answerBtn.setWidth(10);


                answerBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        BindObj bo = (BindObj) answerBtn.getTag();
                        if(bo != null) {
                            bo.exampleBtn.setText(bo.exampleText);
                        }
                        answerBtn.setText("");
                    }
                });
                answerRow.addView(answerBtn);
            }

            Cursor cursor = mDbConnect.Example(len, answer);
            String example = "";
            cursor.moveToFirst();
            example = cursor.getString(0);
            example = example.concat(answer);
            example = shuffle(example);
            TableLayout exampleTable = (TableLayout)findViewById(R.id.exampleTable);

            TableRow exampleRow = new TableRow(this);
            exampleRow.setLayoutParams(new TableLayout.LayoutParams(TableLayout.LayoutParams.MATCH_PARENT, TableLayout.LayoutParams.MATCH_PARENT));


            for(i=0; i<12; i++){
                final Button exampleButton = new Button(this);
                exampleButton.setLayoutParams(new TableRow.LayoutParams(TableRow.LayoutParams.MATCH_PARENT, TableRow.LayoutParams.MATCH_PARENT, 1));
                exampleButton.setText(String.valueOf(example.charAt(i)));
                exampleButton.setWidth(50);
                exampleButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        SetAnswer(answerRow, len, exampleButton, answer);
                    }
                });
                exampleRow.addView(exampleButton);
                if((i+1)%6 == 0){
                    exampleTable.addView(exampleRow);
                    exampleRow = null;
                    exampleRow = new TableRow(this);
                    exampleRow.setLayoutParams(new TableLayout.LayoutParams(TableLayout.LayoutParams.MATCH_PARENT, TableLayout.LayoutParams.MATCH_PARENT));
                }
            }
        }else{
            Toast toast = Toast.makeText(getApplicationContext(), "마지막 문제입니다.\n시대선택화면으로 돌아갑니다.", Toast.LENGTH_SHORT);
            toast.show();
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    Intent menu = new Intent(mContext, MenuActivity.class);
                    startActivity(menu);
                }
            }, 1000);


        }
    }

    private void SetAnswer(LinearLayout answerRow, int len, Button exampleButton, String answer){

        String tempAnswer = "";
        String text = String.valueOf(exampleButton.getText());
        for(int i = 0;i<len; i++){
            Button childAnswer = (Button)answerRow.getChildAt(i);

            if(childAnswer.getText() == null || childAnswer.getText() == ""){
                childAnswer.setText(text);
                BindObj bo = new BindObj();
                bo.exampleText = text;
                bo.exampleBtn = exampleButton;
                childAnswer.setTag(bo);
                exampleButton.setText("");
                break;
            }
        }

        for(int i = 0; i<len; i++){
            Button childAnswer = (Button)answerRow.getChildAt(i);
            tempAnswer = tempAnswer.concat(String.valueOf(childAnswer.getText()));
        }

        if(tempAnswer.length() == len) {

            if (answer.equals(tempAnswer)) {
                Toast toast = Toast.makeText(getApplicationContext(), "정답!!", Toast.LENGTH_SHORT);
                toast.show();
                mDbConnect = DBConnect.getInstance(this);
                mDbConnect.setSolved(pi);
                Intent next = new Intent(this, NextActivity.class);
                next.putExtra("pi", (Serializable)pi);
                startActivityForResult(next, 0);
            }else{
                Toast toast = Toast.makeText(getApplicationContext(), "오답!!", Toast.LENGTH_SHORT);
                toast.show();
            }
        }
    }

    private String shuffle(String input){
        List<Character> characters = new ArrayList<Character>();
        for(char c:input.toCharArray()){
            characters.add(c);
        }
        StringBuilder output = new StringBuilder(input.length());
        while(characters.size()!=0){
            int randPicker = (int)(Math.random()*characters.size());
            output.append(characters.remove(randPicker));
        }
        return output.toString();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent intent){
        if(resultCode == RESULT_OK){
            pi = (ProblemInfo)intent.getSerializableExtra("pi");
            Intent question = new Intent(this, QuestionActivity.class);
            question.putExtra("pi", (Serializable)pi);
            this.finish();
            startActivity(question);
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event){
        if((keyCode == KeyEvent.KEYCODE_BACK)){
            Intent questionList = new Intent();
            questionList.putExtra("pi", (Serializable)pi);
            setResult(RESULT_OK, questionList);
            finish();
        }
        return false;
    }


    @Override
    public boolean onTouch(View v, MotionEvent event){

        switch (event.getAction()){
            case MotionEvent.ACTION_DOWN :
                x = event.getX();
                y = event.getY();
                break;
            case MotionEvent.ACTION_UP :
                float upX = event.getX(), upY = event.getY();

                if((x - upX) > 120 && Math.abs(y - upY) < 100){
                    Intent question = new Intent(this, QuestionActivity.class);
                    if(pi.random_flag.equals("N")){
                        pi.num++;
                    }

                    question.putExtra("pi", (Serializable) pi);
                    finish();
                    startActivityForResult(question, 2);

                }
                return false;
        }
        return true;
    }
}
