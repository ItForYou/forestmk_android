package kr.co.itforone.forestmk_android;


import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.webkit.JavascriptInterface;
import android.widget.Toast;

import com.google.android.material.snackbar.Snackbar;

class WebviewJavainterface {
    Activity activity;
    MainActivity mainActivity;

    public WebviewJavainterface(Activity activity, MainActivity mainActivity){

        this.mainActivity = mainActivity;
        this.activity=activity;

    }

    public WebviewJavainterface(Activity activity){

        this.activity=activity;

    }

    @JavascriptInterface
    public void call(String number){

        Intent intent = new Intent(Intent.ACTION_DIAL);
        intent.setData(Uri.parse("tel:" + number));
        mainActivity.startActivity(intent);

    }

    @JavascriptInterface
    public void show_snackbar(String message){

       //     Toast.makeText(mainActivity.getApplicationContext(),message, Toast.LENGTH_LONG).show();
        Snackbar.make(mainActivity.getCurrentFocus(), message,Snackbar.LENGTH_LONG).show();

    }

    @JavascriptInterface
    public void sharelink() {
        Intent intent = new Intent(android.content.Intent.ACTION_SEND);
        intent.setType("text/plain");
        intent.putExtra(Intent.EXTRA_TEXT, "https://play.google.com/store/apps/details?id=kr.co.itforone.forestmk");
        Intent chooser = Intent.createChooser(intent, "공유하기");
        mainActivity.startActivity(chooser);
    }

    @JavascriptInterface
    public void get_Address() {

        //Toast.makeText(mainActivity.getApplicationContext(),"get_Address",Toast.LENGTH_LONG).show();
        Intent intent = new Intent(mainActivity.getApplicationContext(), SubWebveiwActivity.class);
        intent.putExtra("subview_url", mainActivity.getString(R.string.address));
        mainActivity.startActivityForResult(intent, 3);

    }

    @JavascriptInterface
    public void setLogininfo(String id,String password) {
        SharedPreferences pref = mainActivity.getSharedPreferences("logininfo", mainActivity.MODE_PRIVATE);
        SharedPreferences.Editor editor = pref.edit();
        editor.putString("id",id);
        editor.putString("pwd",password);
        editor.commit();
    }

    @JavascriptInterface
    public void setlogout() {
     //   Toast.makeText(mainActivity.getApplicationContext(),"logout",Toast.LENGTH_LONG).show();
        SharedPreferences pref = mainActivity.getSharedPreferences("logininfo", mainActivity.MODE_PRIVATE);
        SharedPreferences.Editor editor = pref.edit();
        editor.clear();
        editor.commit();
    }

    @JavascriptInterface
    public void setflgmodal(int i) {
        mainActivity.flg_modal=i;
    }

    @JavascriptInterface
    public void setflgmodal2(int i) {
        mainActivity.flg_sortmodal=i;
    }

    @JavascriptInterface
    public void getlocation() {

        double lat = mainActivity.getlat() * 1000000;
        double lng = mainActivity.getlng() * 1000000;
        lat = Math.ceil(lat) / 1000000;
        lng = Math.ceil(lng) / 1000000;
        double finalLat = lat;
        double finalLng = lng;
        mainActivity.webView.post(new Runnable() {
            @Override
            public void run() {
                if(mainActivity.webView.getUrl().contains("register_form.php") || mainActivity.webView.getUrl().contains("mymap.php"))
                    mainActivity.webView.loadUrl("javascript:trans_addr('" + finalLat + "','" + finalLng + "');");
                else
                    mainActivity.webView.loadUrl("javascript:sort_distance('" + finalLat + "','" + finalLng + "');");
            }
        });
         Toast.makeText(mainActivity.getApplicationContext(),""+lat+" , "+lng, Toast.LENGTH_LONG).show();


    }

    @JavascriptInterface
    public void NoRefresh(){
        //Toast.makeText(mainActivity.getApplicationContext(),"Norefresh",Toast.LENGTH_LONG).show();
        mainActivity.Norefresh();
        mainActivity.flg_refresh=0;
    }

    @JavascriptInterface
    public void YesRefresh(){
        mainActivity.Yesrefresh();
        mainActivity.flg_refresh=1;
    }

}
