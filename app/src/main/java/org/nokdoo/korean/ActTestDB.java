/*
package com.sohon.testDB;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

import android.app.Activity;
import android.content.Context;
import android.content.res.AssetManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.CursorAdapter;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.AdapterView.OnItemClickListener;

public class ActTestDB extends Activity {
    private ListView m_listIndex;

    */
/** Called when the activity is first created. *//*

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        initialize(this);

        m_listIndex = (ListView) this.findViewById(R.id.IndexList);
        m_listIndex.setOnItemClickListener(new OnItemClickListener() {
            public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
                                    long arg3) {
            }
        });

        setAdaptor();
    }

    public static final String ROOT_DIR = "/data/data/com.sohon.testDB/";
    private static final String DATABASE_NAME = "test.db";
    public static final String TABLE_NAME = "lecture";
    private static final String COLUMN_KEY_ID = "_id";
    private static final String COLUMN_NAME = "name";

    public static void initialize(Context ctx) {
        // check
        File folder = new File(ROOT_DIR + "databases");
        folder.mkdirs();
        File outfile = new File(ROOT_DIR + "databases/" + DATABASE_NAME);
        if (outfile.length() <= 0) {
            AssetManager assetManager = ctx.getResources().getAssets();
            try {
                InputStream is = assetManager.open(DATABASE_NAME, AssetManager.ACCESS_BUFFER);
                long filesize = is.available();
                byte [] tempdata = new byte[(int)filesize];
                is.read(tempdata);
                is.close();

                outfile.createNewFile();
                FileOutputStream fo = new FileOutputStream(outfile);
                fo.write(tempdata);
                fo.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private SQLiteDatabase mDatabase;
    private void setAdaptor() {
        if (mDatabase == null) {
            mDatabase = openOrCreateDatabase(DATABASE_NAME, Context.MODE_PRIVATE, null);
        }
        Cursor cursor = null;
        final CursorAdapter adaptor;
        String[] columns = new String[] {COLUMN_KEY_ID, COLUMN_NAME};

        cursor = mDatabase.query(TABLE_NAME, columns, null, null, null, null, null);
        adaptor = new SimpleCursorAdapter(this,
                android.R.layout.simple_list_item_1,
                cursor,
                new String[] {COLUMN_NAME},
                new int[] {android.R.id.text1}
        );

        m_listIndex.setAdapter(adaptor);
    }
}
*/
