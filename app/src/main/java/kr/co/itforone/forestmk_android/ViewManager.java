package kr.co.itforone.forestmk_android;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Build;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Toast;

import androidx.annotation.RequiresApi;

import java.util.Arrays;


class ViewManager extends WebViewClient {

    Activity context;
    MainActivity mainActivity;
    String [] motion_page = {"category.php", ""};

    public ViewManager(Activity context, MainActivity mainActivity) {
        this.context = context;
        this.mainActivity = mainActivity;
    }

    public ViewManager() {
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    public boolean shouldOverrideUrlLoading(WebView view, String url) {
         //   Toast.makeText(mainActivity.getApplicationContext(),"test-"+url, Toast.LENGTH_LONG).show();

      if(url.contains("category.php") || url.contains("recent_list.php") || url.contains("mypage.php") ||  (url.contains("board.php")&&!url.contains("wr_id"))) {
          Intent intent = new Intent(mainActivity, SubWebveiwActivity.class);
          intent.putExtra("subview_url", url);
          mainActivity.startActivity(intent);
          mainActivity.overridePendingTransition(R.anim.fadein, R.anim.stay);
          return true;
      }
      //로그인, 글쓰기, 회원가입, 정보수정 뒤로가기 처리
      else {

          //Toast.makeText(mainActivity.getApplicationContext(),"view"+String.valueOf(mainActivity.flg_alert), Toast.LENGTH_LONG).show();
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
       // view.loadUrl("javascript:setToken('"+mainActivity.token+"')");
       if(url.contains("login_check.php") || url.contains("write_update.php") || url.contains("register_form_update.php")){
            mainActivity.webView.goBack();
        }
    }

    private void animate(final WebView view) {
        Animation anim = AnimationUtils.loadAnimation(mainActivity.getApplicationContext(),
                android.R.anim.fade_in);
        view.startAnimation(anim);
    }
}
