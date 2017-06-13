package org.nokdoo.korean;

import android.os.Parcel;
import android.os.Parcelable;

import java.io.Serializable;

public class ProblemInfo implements Serializable{

    String start;
    String end;
    int start_year = -1000000;
    int end_year = 9999;
    int num = 0;
    String gubun;
    String keycode;
    String random_flag = "N";
}