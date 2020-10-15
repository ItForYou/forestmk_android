package kr.co.itforone.forestmk_android;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Build;
import android.util.Log;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import androidx.annotation.RequiresApi;

class ViewManager extends WebViewClient {

    Activity context;
    MainActivity mainActivity;

    public ViewManager(Activity context, MainActivity mainActivity) {
        this.context = context;
        this.mainActivity = mainActivity;
    }

    public ViewManager() {
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    public boolean shouldOverrideUrlLoading(WebView view, String url){
           // Toast.makeText(mainActivity.getApplicationContext(),"test-"+url, Toast.LENGTH_LONG).show();
        Log.d("backpress_nowrefre",String.valueOf(mainActivity.now_refreshlayout));
        boolean lastchk = mainActivity.now_refreshlayout;

      if(url.contains("category.php") || url.contains("recent_list.php") || url.contains("mypage.php") ||  (url.contains("board.php")&&!url.contains("wr_id")) || url.contains("write.php")) {
          Log.d("backpress_newintent",url);
          Intent intent = new Intent(mainActivity, SubWebveiwActivity.class);
          intent.putExtra("subview_url", url);
          intent.putExtra("before_refresh", lastchk);
      //    Toast.makeText(mainActivity.getApplicationContext(),String.valueOf(mainActivity.now_refreshlayout), Toast.LENGTH_LONG).show();

          mainActivity.startActivityForResult(intent,mainActivity.VIEW_REFRESH);
          mainActivity.overridePendingTransition(R.anim.fadein, R.anim.stay);
          return true;

      }

      else {

          //Toast.makeText(mainActivity.getApplicationContext(),"view"+String.valueOf(mainActivity.flg_alert), Toast.LENGTH_LONG).show();
          if(url.contains("register_form.php") || url.contains("password_lost.php") ||
                  (url.contains("board.php") && url.contains("wr_id=")) || url.contains("mypage.php") ||
                  url.contains("login.php") || url.contains("mymap.php")){
              mainActivity.Norefresh();
              mainActivity.flg_refresh=0;
          }
          else{
              mainActivity.Yesrefresh();
              mainActivity.flg_refresh=1;
          }
          if(mainActivity.flg_alert!=1)
              view.loadUrl(url);
          mainActivity.flg_alert=0;
          return false;

      }
    }

    @Override
    public void onPageStarted(WebView view, String url, Bitmap favicon) {
        super.onPageStarted(view, url, favicon);
        // mainActivity.dialogloading.show();
    }

    @Override
    public void onPageFinished(WebView view, String url) {
        super.onPageFinished(view, url);
        view.loadUrl("javascript:setToken('"+mainActivity.token+"')");
   /*    if(url.contains("login_check.php") || url.contains("write_update.php") || url.contains("register_form_update.php") || url.contains("write_comment_update.php")){
            mainActivity.webView.goBack();
        }*/
    }

    private void animate(final WebView view) {
        Animation anim = AnimationUtils.loadAnimation(mainActivity.getApplicationContext(),
                android.R.anim.fade_in);
        view.startAnimation(anim);
    }
}
