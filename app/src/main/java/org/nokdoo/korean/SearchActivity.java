package org.nokdoo.korean;

import android.app.Activity;
import android.content.Context;
import android.database.Cursor;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.ListView;

import java.util.ArrayList;

public class SearchActivity extends AppCompatActivity {

    ListView mListView = null;

    BaseAdapter mAdapter = null;

    ArrayList<SearchResult> mList = null;

    DBConnect mDbConnect = null;

    Context mContext = null;

    Activity mActivity = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.search_activity);
        mContext = getApplicationContext();
        mActivity = SearchActivity.this;

        EditText search = (EditText)findViewById(R.id.search);
        search.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                String search = s.toString();
                if(!search.equals("")) {
                    mDbConnect = DBConnect.getInstance(getApplicationContext());
                    Cursor c = mDbConnect.getSearchResult(s.toString());
                    mList = new ArrayList<SearchResult>();
                    while (c.moveToNext()) {
                        SearchResult searchResult = new SearchResult();
                        searchResult.keyword = c.getString(0);
                        searchResult.keycode = c.getString(1);
                        mList.add(searchResult);
                    }
                    if (mList.size() > 0) {
                        mAdapter = new BaseAdapterEx(mContext, mList, mActivity);
                        mListView = (ListView) findViewById(R.id.list_view);
                        mListView.setAdapter(mAdapter);
                    }
                }
            }
        });
    }
}
