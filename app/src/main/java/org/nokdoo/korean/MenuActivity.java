package org.nokdoo.korean;


import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.res.AssetManager;
import android.database.Cursor;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;
import java.net.URL;

public class MenuActivity extends AppCompatActivity {

    private DBConnect mDbConnect = null;

    private class CheckVersion extends AsyncTask<Void, Boolean, Void>{

        private ProgressDialog mPrgsDlg;
        private Context mContext;
        String in_update_time = "";
        String out_update_time = "";

        public CheckVersion(Context context){
            mContext = context;
        }

        @Override
        protected void onPreExecute() {
            mPrgsDlg = new ProgressDialog(mContext);
            mPrgsDlg.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            mPrgsDlg.setMessage("데이터베이스 최신화 중...");
            mPrgsDlg.show();

        }

        @Override
        protected Void doInBackground(Void... voida) {
            Document doc = null;
            try {
                doc = Jsoup.connect("https://github.com/nokdoo/korean").get();
            } catch (Exception e) {
                Log.i("exception_menuactivity", e.getMessage());
            }
            Elements rows = doc.select("tr.js-navigation-item");
            Elements tr = rows.select("tr:contains(korean.db)");
            if (tr.isEmpty()) {
                Log.i("exception_menuactivity", "db가 존재하지 않음");
            }
            Elements tag_hasTime = tr.select("time-ago");
            out_update_time = tag_hasTime.attr("datetime");

            mDbConnect = DBConnect.getInstance(mContext);
            Cursor c = mDbConnect.getUpdateTime();
            if(c.moveToNext()){
                in_update_time = c.getString(0);
            }

            if((in_update_time.compareTo(out_update_time) != 0)){
                DownloadDB();
                mDbConnect.setUpdateTime(out_update_time);
            }

            return null;
        }

        @Override
        protected void onPostExecute(Void asd) {
            mPrgsDlg.dismiss();
        }
    }

    public static final String ROOT_DIR = "/data/data/org.nokdoo.korean/";
    private static final String DATABASE_NAME = "korean.db";

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.menu_activity);
/*

        //File folder = new File(ROOT_DIR + "databases");
        //folder.mkdirs();
        //File outfile = new File(ROOT_DIR + "databases/" + DATABASE_NAME);
        //if (outfile.length() <= 0) {
            AssetManager assetManager = this.getResources().getAssets();
            try {
                InputStream is = assetManager.open(DATABASE_NAME, AssetManager.ACCESS_BUFFER);
                long filesize = is.available();
                byte [] tempdata = new byte[(int)filesize];
                is.read(tempdata);
                is.close();

                //outfile.createNewFile();
                FileOutputStream fo = new FileOutputStream(ROOT_DIR + "databases/korean.db");
                fo.write(tempdata);
                fo.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        //}
*/

        try {
            ConnectivityManager manager = (ConnectivityManager) this.getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo mobile = manager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);
            NetworkInfo wifi = manager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);

            if(wifi.isConnected() || mobile.isConnected()) {
                new CheckVersion(MenuActivity.this).execute();
            }else{

                Toast toast = Toast.makeText(getApplicationContext(), "온라인 상태이면 데이터베이스를 업데이트 할 수 있습니다.", Toast.LENGTH_SHORT);
                ViewGroup group = (ViewGroup) toast.getView();
                TextView messageTextView = (TextView)group.getChildAt(0);
                messageTextView.setTextSize(12);
                toast.show();
            }
        }catch (Exception e){
            Log.i("error!!!", e.getMessage());
        }

        findViewById(R.id.AtoM).setOnClickListener(onclickListener);
        findViewById(R.id.NtoR).setOnClickListener(onclickListener);
        findViewById(R.id.StoS1598).setOnClickListener(onclickListener);
        findViewById(R.id.S1598toS1876).setOnClickListener(onclickListener);
        findViewById(R.id.S1876toU).setOnClickListener(onclickListener);
        findViewById(R.id.VtoW).setOnClickListener(onclickListener);

    }

    LinearLayout.OnClickListener onclickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            Intent questionList = new Intent(MenuActivity.this, QuestionListActivity.class);
            ProblemInfo pi = new ProblemInfo();
            switch(v.getId()){
                case R.id.AtoM :
                    pi.start = "A";
                    pi.end = "M";
                    questionList.putExtra("pi", (Serializable)pi);
                    startActivity(questionList);
                    break;
                case R.id.NtoR :
                    pi.start = "N";
                    pi.end = "R";
                    questionList.putExtra("pi", (Serializable)pi);
                    startActivity(questionList);
                    break;
                case R.id.StoS1598 :
                    pi.start = "S";
                    pi.end = "S";
                    pi.end_year = 1598;
                    questionList.putExtra("pi", (Serializable)pi);
                    startActivity(questionList);
                    break;
                case R.id.S1598toS1876 :
                    pi.start = "S";
                    pi.end = "S";
                    pi.start_year = 1598;
                    pi.end_year = 1875;
                    questionList.putExtra("pi", (Serializable)pi);
                    startActivity(questionList);
                    break;
                case R.id.S1876toU :
                    pi.start = "S";
                    pi.end = "U";
                    pi.start_year = 1876;
                    questionList.putExtra("pi", (Serializable)pi);
                    startActivity(questionList);
                    break;
                case R.id.VtoW:
                    pi.start = "V";
                    pi.end = "W";
                    questionList.putExtra("pi", (Serializable)pi);
                    startActivity(questionList);
                    break;
            }
        }
    };

    private void DownloadDB(){
        try{
            URL downloadURL = new URL("https://github.com/nokdoo/korean/raw/master/korean.db");
            BufferedInputStream bis = new BufferedInputStream(downloadURL.openStream());
            FileOutputStream fos = new FileOutputStream("/data/data/org.nokdoo.korean/databases/korean.db");
            byte[] buffer = new byte[1024];
            int count = 0;
            while((count = bis.read(buffer, 0, 1024)) != -1){
                fos.write(buffer, 0, count);
            }
            fos.close();
            bis.close();
        }catch (Exception e){
            Log.d("exception_URL", e.getMessage());
        }
    }
}
