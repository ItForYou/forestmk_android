package kr.co.itforone.forestmk_android;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Toast;

import com.google.android.material.snackbar.Snackbar;

class SubViewManager extends WebViewClient {
    SubWebveiwActivity context;
    MainActivity mainActivity;

    public SubViewManager(SubWebveiwActivity context, MainActivity mainActivity) {
        this.context = context;
        this.mainActivity = mainActivity;
    }
    public SubViewManager(SubWebveiwActivity context) {
        this.context = context;
    }

    public SubViewManager() {
    }

    @Override
    public boolean shouldOverrideUrlLoading(WebView view, String url) {
      //  Toast.makeText(context.getApplicationContext(),"sub - " +url,Toast.LENGTH_LONG).show();


        if(url.contains("register_form.php") || url.contains("password_lost.php") ||
                (url.contains("board.php") && url.contains("wr_id=")) || url.contains("mypage.php") ||
                url.contains("login.php") || url.contains("mymap.php")){
            context.Norefresh();
            context.flg_refresh=0;
        }

        else{
            context.Yesrefresh();
            context.flg_refresh=1;
        }

        if(url.contains("category.php") || url.contains("recent_list.php") || url.contains("mypage.php") ||  (url.contains("board.php")&&!url.contains("wr_id")) || url.contains("write.php")) {
            Intent intent = new Intent(context, SubWebveiwActivity.class);
            intent.putExtra("subview_url", url);
            context.startActivity(intent);
            context.overridePendingTransition(R.anim.fadein, R.anim.stay);
            return true;
        }

        else {
            //Toast.makeText(mainActivity.getApplicationContext(),"view"+String.valueOf(mainActivity.flg_alert), Toast.LENGTH_LONG).show();
            if(context.flg_alert!=1)
                view.loadUrl(url);
            context.flg_alert=0;
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
        if(url.contains("login_check.php") || url.contains("write_update.php") || url.contains("register_form_update.php") || url.contains("write_comment_update.php")){
            context.webView.goBack();
        }


    }
}
