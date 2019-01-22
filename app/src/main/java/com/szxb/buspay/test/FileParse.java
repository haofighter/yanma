package com.szxb.buspay.test;

import android.content.res.AssetManager;

import com.szxb.buspay.BusApp;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

/**
 * 作者：Tangren on 2018-09-18
 * 包名：com.szxb.buspay.test
 * 邮箱：996489865@qq.com
 * TODO:一句话描述
 */

public class FileParse {
    /**
     * 读取文件
     *
     * @param fileName .
     * @return .
     */
    public static List<String> readFileToListString(String fileName) {
        List<String> list = new ArrayList<>();
        try {
            AssetManager manager = BusApp.getInstance().getAssets();
            BufferedReader br = new BufferedReader(new InputStreamReader(manager.open(fileName)));
            String readline = "";
            while ((readline = br.readLine()) != null) {
                list.add(readline);
            }
            br.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }

}
