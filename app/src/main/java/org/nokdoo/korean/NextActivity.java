package org.nokdoo.korean;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Color;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AbsoluteLayout;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.Serializable;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class NextActivity extends AppCompatActivity implements View.OnTouchListener{

    private float x, y;

    private ProblemInfo pi;

    private DBConnect mDbConnect = null;

    private DescriptionAsyncTask descriptionAsyncTask;

    private class DescriptionAsyncTask extends AsyncTask<String, String, Boolean>{

        private Context mContext;

        public DescriptionAsyncTask(Context context){
            mContext = context;
        }

        @Override
        protected void onPreExecute() {
            LinearLayout descriptionLinear = (LinearLayout)findViewById(R.id.descLinear);
            descriptionLinear.removeAllViews();
        }

        @Override
        protected Boolean doInBackground(String... params) {
            publishProgress(params);
            return true;
        }

        protected void onProgressUpdate(String... keycode) {
            LinearLayout descLinear = (LinearLayout)findViewById(R.id.descLinear);
            mDbConnect = DBConnect.getInstance(getParent());
            Cursor c = mDbConnect.Description(keycode[0]);
            int i = 0;
            while(c.moveToNext()){

                TextView text = new TextView(mContext);
                text.setText(c.getString(0));
                if(i%2 == 0){
                    text.setBackgroundColor(Color.parseColor("#FFD9EC"));
                }else if(i%2==1){
                    text.setBackgroundColor(Color.parseColor("#FAE0D4"));
                }

                descLinear.addView(text);
                i++;
            }
        }

        @Override
        protected void onPostExecute(Boolean aBoolean) {

        }
    }

    private KeywordAsyncTask keywordAsyncTask;

    private class KeywordAsyncTask extends AsyncTask<String, String, Boolean>{

        private Context mContext;

        public KeywordAsyncTask(Context context){
            mContext = context;
        }

        @Override
        protected void onPreExecute() {

            LinearLayout keywordLinear = (LinearLayout)findViewById(R.id.keywordLinear);
            keywordLinear.removeAllViews();

        }

        @Override
        protected Boolean doInBackground(String... params) {
            publishProgress(params);
            return true;
        }

        protected void onProgressUpdate(final String... param_keycode) {
            final LinearLayout keywordLinear = (LinearLayout)findViewById(R.id.keywordLinear);
            Cursor c = mDbConnect.Keyword(param_keycode[0]);
            while(c.moveToNext()){
                final String keyword = c.getString(0);
                final String keycode = c.getString(1);
                ScrollView scroll = new ScrollView(mContext);
                final LinearLayout scrollLinear = new LinearLayout(mContext);
                scrollLinear.setOrientation(LinearLayout.VERTICAL);
                scrollLinear.setBackgroundColor(Color.parseColor("#d4f4fa"));
                scroll.setBackgroundColor(Color.parseColor("#bdbdbd"));
                Button button = new Button(mContext);
                button.setText(keyword);
                button.setTag(keycode);
                if(c.getString(1).equals(param_keycode[0])) {
                    button.setBackgroundColor(Color.parseColor("#faecc5"));
                }
                button.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        scrollLinear.removeAllViews();
                        final Cursor c2 = mDbConnect.NewKeyword(keycode);
                        while (c2.moveToNext()) {
                            String btnKeyword = c2.getString(0);
                            final Button btn = new Button(mContext);
                            btn.setTag(c2.getString(1));
                            if (c2.getInt(2) == 0) {
                                btn.setBackgroundColor(Color.parseColor("#eaeaea"));
                            }
                            btn.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    String onclick_keycode = (String) btn.getTag();
                                    new DescriptionAsyncTask(NextActivity.this).execute(onclick_keycode);
                                    new KeywordAsyncTask(NextActivity.this).execute(onclick_keycode);
                                }
                            });
                            btn.setText(btnKeyword);
                            if (btnKeyword.equals(keyword)) {
                                btn.setBackgroundColor(Color.parseColor("#ffd8d8"));
                            }
                            scrollLinear.addView(btn);
                        }
                    }
                });
                scrollLinear.addView(button);
                scroll.addView(scrollLinear);
                keywordLinear.addView(scroll);
            }
        }

        @Override
        protected void onPostExecute(Boolean aBoolean) {

        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.next_activity);

        LinearLayout ll = (LinearLayout)findViewById(R.id.activityLinear);
        ll.setOnTouchListener(this);

        Intent intent = getIntent();
        pi = (ProblemInfo)intent.getSerializableExtra("pi");
        new DescriptionAsyncTask(NextActivity.this).execute(pi.keycode);
        new KeywordAsyncTask(NextActivity.this).execute(pi.keycode);

        Toast toast = Toast.makeText(getApplicationContext(), "왼쪽으로 드래그하면 다음문제로 넘어갑니다.", Toast.LENGTH_SHORT);
        toast.show();
    }

    @Override
    public boolean onTouch(View v, MotionEvent event){

        switch (event.getAction()){
            case MotionEvent.ACTION_DOWN :
                x = event.getX();
                y = event.getY();
                Log.d("down", Float.toString(x));
                Log.d("down", Float.toString(y));
                break;
            case MotionEvent.ACTION_UP :
                float upX = event.getX(), upY = event.getY();

                if((x - upX) > 120 && Math.abs(y - upY) < 100){
                    Intent question = new Intent();
                    mDbConnect = DBConnect.getInstance(this);
                    if(pi.random_flag.equals("N")){
                        pi.num++;
                    }
                    question.putExtra("pi", (Serializable) pi);
                    setResult(RESULT_OK, question);
                    finish();
                }else if( (y - upY) > 120 && Math.abs(x - upX) < 100){
                    Intent search = new Intent(this, SearchActivity.class);
                    startActivityForResult(search, 1);
                    overridePendingTransition(R.anim.btm_in, 0);
                }
                return false;
        }
        return true;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent intent){
        if(resultCode == RESULT_OK){
            String keycode = intent.getStringExtra("keycode");

            new DescriptionAsyncTask(NextActivity.this).execute(keycode);
            new KeywordAsyncTask(NextActivity.this).execute(keycode);
        }
    }
}
