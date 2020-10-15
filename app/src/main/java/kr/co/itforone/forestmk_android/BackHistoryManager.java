package kr.co.itforone.forestmk_android;

import android.app.Activity;

import java.util.ArrayList;

public class BackHistoryManager {
    private static BackHistoryManager backHistoryManager = null;
    private ArrayList<String> historylist = null;

    private BackHistoryManager() {

        historylist = new ArrayList<String>();

    }

    public static BackHistoryManager getInstance() {

        if( BackHistoryManager.backHistoryManager == null ) {
            backHistoryManager = new BackHistoryManager();
        }
        return backHistoryManager;

    }

    public ArrayList<String> getHistorylist(){

        return historylist;

    }

    public void addHitory(String Url){

        historylist.add(Url);

    }

    public boolean removeHistory(String Url){

        return historylist.remove(Url);

    }

    public void removeAllHistory() {

            historylist.clear();

    }
}
