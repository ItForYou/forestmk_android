package kr.co.itforone.forestmk_android;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.webkit.WebView;
import android.webkit.WebViewClient;

class SubViewManager extends WebViewClient {
    Activity context;
    MainActivity mainActivity;

    public SubViewManager(Activity context, MainActivity mainActivity) {
        this.context = context;
        this.mainActivity = mainActivity;
    }
    public SubViewManager(Activity context) {
        this.context = context;
    }

    public SubViewManager() {
    }

    @Override
    public boolean shouldOverrideUrlLoading(WebView view, String url) {
        // Toast.makeText(mainActivity.getApplicationContext(),mainActivity.webView.getUrl(),Toast.LENGTH_LONG).show();
        if(url.contains("category.php") || url.contains("recent_list.php") || url.contains("mypage.php") ||  (url.contains("board.php")&&!url.contains("wr_id"))) {
            Intent intent = new Intent(context, SubWebveiwActivity.class);
            intent.putExtra("subview_url", url);
            context.startActivity(intent);
            context.overridePendingTransition(R.anim.fadein, R.anim.stay);
            return true;
        }
        else {
            view.loadUrl(url);
            // Toast.makeText(mainActivity.getApplicationContext(),mainActivity.webView.getUrl(),Toast.LENGTH_LONG).show();
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

    }
}
