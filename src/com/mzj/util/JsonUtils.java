package com.mzj.util;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by hasee on 2017/12/2.
 */

public class JsonUtils {

    public static List<String>  getAdIdList(String taskDetail){
        List<String> adIdList = new ArrayList<>();

        try {
            JSONArray array = new JSONArray(taskDetail);
            for(int i=0;i<array.length();i++){
                JSONObject obj = array.getJSONObject(i);
                String id = obj.getString("id");
                adIdList.add(id);
            }
        } catch (JSONException e) {
            e.printStackTrace();
            MzjLog.i("getAdIdList","getAdIdList = "+e.getMessage());
        }
        return adIdList;
    }
}
