package org.nokdoo.korean;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;

public class BaseAdapterEx extends BaseAdapter {

    Context mContext = null;
    ArrayList<SearchResult> mList = null;
    LayoutInflater mLayoutInflater = null;
    Activity mActivity = null;

    public BaseAdapterEx(Context context, ArrayList<SearchResult> list, Activity activity){
        mContext = context;
        mList = list;
        mLayoutInflater = LayoutInflater.from(mContext);
        mActivity = activity;
    }

    @Override
    public int getCount(){
        return mList.size();
    }

    @Override
    public long getItemId(int position){
        return position;
    }

    @Override
    public SearchResult getItem(int position){
        return mList.get(position);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent){
        View itemLayout = mLayoutInflater.inflate(R.layout.list_view_item_layout, null);

        TextView resultText = (TextView)itemLayout.findViewById(R.id.resultText);
        resultText.setText(mList.get(position).keyword);
        resultText.setTag(mList.get(position).keycode);
        resultText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent next = new Intent();
                next.putExtra("keycode", (String) v.getTag());
                mActivity.setResult(Activity.RESULT_OK, next);
                mActivity.finish();
            }
        });

        return itemLayout;
    }




}
