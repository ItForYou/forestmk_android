package kr.co.itforone.forestmk_android;


import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.webkit.JavascriptInterface;
import android.widget.Toast;

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
    public void sharelink() {
        Intent intent = new Intent(android.content.Intent.ACTION_SEND);
        intent.setType("text/plain");
        intent.putExtra(Intent.EXTRA_TEXT, "https://play.google.com/store/apps/details?id=kr.co.itforone.forestmk");
        Intent chooser = Intent.createChooser(intent, "공유하기");
        mainActivity.startActivity(chooser);
    }

    @JavascriptInterface
    public void get_Address() {
        Intent intent = new Intent(mainActivity, SubWebveiwActivity.class);
        intent.putExtra("subview_url", "");
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
    public void getlocation() {

      /*  double lat = mainActivity.getlat() * 1000000;
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
        // Toast.makeText(mainActivity.getApplicationContext(),""+lat+" , "+lng, Toast.LENGTH_LONG).show();

       */
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
