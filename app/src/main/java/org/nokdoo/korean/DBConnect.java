package org.nokdoo.korean;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import java.util.HashMap;

/**
 * Created by nokdoo on 5/2/16.
 */
public class DBConnect {

    static final String DB_NAME = "korean.db";
    static final int DB_VERSION = 1;

    Context mContext = null;

    private static DBConnect mDbManager = null;
    private SQLiteDatabase mDatabase = null;

    public static DBConnect getInstance(Context context){
        if(mDbManager == null){
            mDbManager = new DBConnect(context);
        }
        return mDbManager;
    }

    private DBConnect(Context context){
        mContext = context;
        mDatabase = context.openOrCreateDatabase(DB_NAME, context.MODE_PRIVATE, null);
    }

    public Cursor QuestionList(ProblemInfo pi){
        String where = "where gubun between '"+pi.start+"' and '"+pi.end+"' " +
                "and "+pi.start_year+" <= year " +
                "and year < "+pi.end_year+" ";
        Cursor c = mDatabase.rawQuery("select gubun, num, solved from problem "+where+" order by num;", null);
        return c;
    }

    public Cursor Question(ProblemInfo pi){
        String add_where = "and num = "+pi.num+";";

        if(pi.random_flag.equals("Y")){
            add_where = "and num != "+pi.num+" and solved = 'N' order by random() limit 1";
        }

        Cursor c = mDatabase.rawQuery("select question, answer, keycode, num from problem " +
                "where gubun between '"+pi.start+"' and '"+pi.end+"' " +
                "and "+pi.start_year+" <= year " +
                "and year < "+pi.end_year+" " +
                add_where, null);
        return c;
    }

    public Cursor Example(int len, String answer){
        int length = 12 - len;
        Cursor c = mDatabase.rawQuery("SELECT substr(replace(group_concat(keyword),',', ''), 1, "+length+") " +
                "from (select substr(replace(replace(keyword, ',', ''), '.', ''), 1, 2) keyword from keycode where keyword != '"+answer+"' ORDER BY RANDOM() LIMIT 6);", null);
        return c;
    }

    public Cursor Description(String keycode){
        Cursor c = mDatabase.rawQuery("select desc from description where keycode = '"+keycode+"';", null);
        return c;
    }

    public Cursor Keyword(String keycode){
        int len = keycode.length();
        String where = "where ";
        String union = "";
        String len9 = "";
        String len7 = "";
        String len5 = "";
        String len3 = "";
        String len1 = "";

        if(len>=1){
            len1 = keycode.substring(0, 1);
            where = where.concat("keycode = '" + len1 + "' ");
            union = "union " +
                    "select case when exists (select * from keycode where keycode like '"+len1+"__') then (select '선택') else NULL end, " +
                    "       case when exists (select * from keycode where keycode like '"+len1+"__') then (select '"+len1+"00') else NULL end " +
                    "except select NULL, NULL";
            if(len>=3){
                len3 = keycode.substring(0,3);
                where = where.concat("or keycode = '" + len3 + "' ");
                union = "union " +
                        "select case when exists (select * from keycode where keycode like '"+len3+"__') then (select '선택') else NULL end, " +
                        "       case when exists (select * from keycode where keycode like '"+len3+"__') then (select '"+len3+"00') else NULL end " +
                        "except select NULL, NULL";
                if(len>=5){
                    len5 = keycode.substring(0,5);
                    where = where.concat("or keycode = '" + len5 + "' ");
                    union = "union " +
                            "select case when exists (select * from keycode where keycode like '"+len5+"__') then (select '선택') else NULL end, " +
                            "       case when exists (select * from keycode where keycode like '"+len5+"__') then (select '"+len5+"00') else NULL end " +
                            "except select NULL, NULL";
                    if(len>=7){
                        len7 = keycode.substring(0,7);
                        where = where.concat("or keycode = '" + len7 + "' ");
                        union = "union " +
                                "select case when exists (select * from keycode where keycode like '"+len7+"__') then (select '선택') else NULL end, " +
                                "       case when exists (select * from keycode where keycode like '"+len7+"__') then (select '"+len7+"00') else NULL end " +
                                "except select NULL, NULL";
                        if(len>=9){
                            len9 = keycode.substring(0, 9);
                            where = where.concat("or keycode = '" + len9 + "' ");
                        }
                    }
                }
            }
        }

        Cursor c = mDatabase.rawQuery("select keyword, keycode from keycode "+where+" "+union+" order by keycode;",null);
        return c;
    }

    public Cursor NewKeyword(String keycode){
        String parentOfKeycode = "";
        String root = "";
        String where = "";
        if(keycode.length()==1){
            root = "where length(keycode) = 1 ";
            where += root;
        }else{
            parentOfKeycode = keycode.substring(0, keycode.length() - 2);
            where += "where keycode like '"+parentOfKeycode+"__' ";
        }
        Cursor c = mDatabase.rawQuery("" +
                "select " +
                "   keyword, " +
                "   keycode, " +
                "   case when exists (select * from description where keycode = toBtn.keycode ) then 1 else 0 end hasDesc, " +
                "   case when exists (select * from keycode where keycode like toBtn.keycode||'__') then 1 else 0 end hasChild " +
                "from keycode as toBtn " +
                where +
                "order by case when keycode = '"+keycode+"' then 0 else 1 end, keycode;",null);
        return c;
    }

    public Cursor getUpdateTime(){
        Cursor c = mDatabase.rawQuery("select * from update_date", null);
        return c;
    }

    public void setUpdateTime(String updateTime){
        mDatabase.execSQL("update update_date set update_date = '"+updateTime+"';");
    }

    public void setSolved(ProblemInfo pi){
        mDatabase.execSQL("update problem set solved = 'Y' where gubun between '"+pi.start+"' and '"+pi.end+"' and num = "+pi.num+";");
        Log.d("","");
    }

    public Cursor getNextProblemInfo(ProblemInfo pi){
        String where_num = "and num = "+pi.num+";";

        Cursor c = mDatabase.rawQuery("select question, answer, keycode from problem " +
                "where gubun between '"+pi.start+"' and '"+pi.end+"' " +
                "and "+pi.start_year+" <= year " +
                "and year < "+pi.end_year+" " +
                where_num, null);
        return c;
    }

    public Cursor getSearchResult(String keyword){
        Cursor c = mDatabase.rawQuery(
                "with " +
                "b as (select represent from synonym where synonym = '"+keyword+"') "+
                "select "+
                    "case "+
                        "when length(a.keycode) = 1 then (select keyword from keycode where keycode = substr(a.keycode, 1, 1)) " +
                        "when length(a.keycode) = 3 then (select keyword from keycode where keycode = substr(a.keycode, 1, 1))||'-'||(select keyword from keycode where keycode = substr(a.keycode, 1, 3)) "+
                        "when length(a.keycode) = 5 then (select keyword from keycode where keycode = substr(a.keycode, 1, 1))||'-'||(select keyword from keycode where keycode = substr(a.keycode, 1, 3))||'-'||(select keyword from keycode where keycode = substr(a.keycode, 1, 5)) "+
                        "when length(a.keycode) = 7 then (select keyword from keycode where keycode = substr(a.keycode, 1, 1))||'-'||(select keyword from keycode where keycode = substr(a.keycode, 1, 3))||'-'||(select keyword from keycode where keycode = substr(a.keycode, 1, 5))||'-'||(select keyword from keycode where keycode = substr(a.keycode, 1, 7)) "+
                        "when length(a.keycode) = 9 then (select keyword from keycode where keycode = substr(a.keycode, 1, 1))||'-'||(select keyword from keycode where keycode = substr(a.keycode, 1, 3))||'-'||(select keyword from keycode where keycode = substr(a.keycode, 1, 5))||'-'||(select keyword from keycode where keycode = substr(a.keycode, 1, 7))||'-'||(select keyword from keycode where keycode = substr(a.keycode, 1, 9)) "+
                    "end|| case when exists (select * from b) then '("+keyword+")' else '' end "+
                "as keyword, keycode "+
                "from keycode a "+
                "where keyword like '%"+keyword+"%' "+
                "or keyword in (select * from b) order by keycode", null
        );
        return c;
    }
}
