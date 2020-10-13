package kr.co.itforone.forestmk_android;

import android.content.Intent;
import android.content.SharedPreferences;
import android.location.LocationManager;
import android.net.Uri;
import android.util.Log;
import android.webkit.JavascriptInterface;
import android.widget.Toast;

import com.google.android.material.snackbar.Snackbar;

class SubWebviewJavainterface {

    SubWebveiwActivity activity;


    public SubWebviewJavainterface(SubWebveiwActivity activity){

        this.activity=activity;
    }

    @JavascriptInterface
    public void call(String number){

        Intent intent = new Intent(Intent.ACTION_DIAL);
        intent.setData(Uri.parse("tel:" + number));
        activity.startActivity(intent);

    }


    @JavascriptInterface
    public void set_address(String addr12, String addr11){
      //  Toast.makeText(activity.getApplicationContext(),addr11,Toast.LENGTH_LONG).show();
        Intent intent = new Intent();//startActivity()를 할것이 아니므로 그냥 빈 인텐트로 만듦
        intent.putExtra("address12",addr12);
        intent.putExtra("address11",addr11);
        activity.setResult(33,intent);
        activity.finish();

    }

    @JavascriptInterface
    public void get_Address() {
        //Toast.makeText(mainActivity.getApplicationContext(),"get_Address",Toast.LENGTH_LONG).show();
        Intent intent = new Intent(activity.getApplicationContext(), SubWebveiwActivity.class);
        intent.putExtra("subview_url", activity.getString(R.string.address));
        activity.startActivityForResult(intent, 3);
    }

    @JavascriptInterface
    public void getlocation() {
        boolean gps_enabled = false;
        try {
            gps_enabled = SubWebveiwActivity.locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
        } catch(Exception ex) {}
        if(gps_enabled) {
            double lat = activity.getlat() * 1000000;
            double lng = activity.getlng() * 1000000;
            lat = Math.ceil(lat) / 1000000;
            lng = Math.ceil(lng) / 1000000;
            double finalLat = lat;
            double finalLng = lng;
            activity.webView.post(new Runnable() {

                @Override
                public void run() {
                    if (activity.webView.getUrl().contains("register_form.php") || activity.webView.getUrl().contains("mymap.php"))
                        activity.webView.loadUrl("javascript:trans_addr('" + finalLat + "','" + finalLng + "');");
                    else
                        activity.webView.loadUrl("javascript:sort_distance('" + finalLat + "','" + finalLng + "');");
                }
            });

            Toast.makeText(activity.getApplicationContext(), "" + lat + " , " + lng, Toast.LENGTH_LONG).show();
        }
        else{
            activity.settingModal();
        }
    }

    @JavascriptInterface
    public void setLogininfo(String id,String password) {
        SharedPreferences pref = activity.getSharedPreferences("logininfo", activity.MODE_PRIVATE);
        SharedPreferences.Editor editor = pref.edit();
        editor.putString("id",id);
        editor.putString("pwd",password);
        editor.commit();
    }

    @JavascriptInterface
    public void setlogout() {
       // Toast.makeText(activity.getApplicationContext(),"logout",Toast.LENGTH_LONG).show();
        SharedPreferences pref = activity.getSharedPreferences("logininfo", activity.MODE_PRIVATE);
        SharedPreferences.Editor editor = pref.edit();
        editor.clear();
        editor.commit();
    }

    @JavascriptInterface
    public void back_pressed() {

        activity.webView.post(new Runnable() {
            public void run() {

                if(activity.webView.canGoBack()){
                    activity.webView.goBack();
                }
                else {
                    Log.d("backpress_interfaceback","now");
                    activity.onBackPressed();
                }

            }
        });
    }

    @JavascriptInterface
   public void show_snackbar(String text, int flg_value){

       // Toast.makeText(activity.getApplicationContext(),text, Toast.LENGTH_LONG).show();
        if(activity.flg_snackbar!=flg_value)
        Snackbar.make(activity.findViewById(R.id.sub_refreshlayout), text,Snackbar.LENGTH_LONG).show();
        activity.flg_snackbar=flg_value;

    }

    @JavascriptInterface
    public void setflgmodal2(int i) {
       // Toast.makeText(activity.getApplicationContext(),"setflgmodal!!", Toast.LENGTH_LONG).show();
        activity.flg_sortmodal=i;

    }
    @JavascriptInterface
    public void setflgmodal(int i) {
        activity.flg_modal=i;
    }

    @JavascriptInterface
    public void setflgmodal3(int i) {
        activity.flg_dclmodal=i;
    }

    @JavascriptInterface
    public void setflgmodal4(int i) {
        Log.d("backpress_","modal4");
        activity.flg_dclcommmodal=i;
    }

   /* @JavascriptInterface
    public void NoRefresh(){
        //Toast.makeText(mainActivity.getApplicationContext(),"Norefresh",Toast.LENGTH_LONG).show();
        activity.Norefresh();
        activity.flg_refresh=0;
    }

    @JavascriptInterface
    public void YesRefresh(){
        activity.Yesrefresh();
        activity.flg_refresh=1;
    }*/
}
