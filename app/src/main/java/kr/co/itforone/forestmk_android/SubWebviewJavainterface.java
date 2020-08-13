package kr.co.itforone.forestmk_android;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.webkit.JavascriptInterface;

class SubWebviewJavainterface {
    Activity activity;
    MainActivity mainActivity;

    public SubWebviewJavainterface(Activity activity, MainActivity mainActivity){
        this.mainActivity = mainActivity;
        this.activity=activity;
    }
    public SubWebviewJavainterface(Activity activity){

        this.activity=activity;
    }


    @JavascriptInterface
    public void set_address(String addr12, String addr11){

        Intent intent = new Intent();//startActivity()를 할것이 아니므로 그냥 빈 인텐트로 만듦
        intent.putExtra("address12",addr12);
        intent.putExtra("address11",addr11);
        activity.setResult(33,intent);
        activity.finish();

    }
}
