package kr.co.itforone.forestmk_android;

import android.app.Activity;
import android.graphics.Bitmap;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Toast;

class ViewManager extends WebViewClient {

    Activity context;
    MainActivity mainActivity;

    public ViewManager(Activity context, MainActivity mainActivity) {
        this.context = context;
        this.mainActivity = mainActivity;
    }

    public ViewManager() {
    }

    @Override
    public boolean shouldOverrideUrlLoading(WebView view, String url) {
        // Toast.makeText(mainActivity.getApplicationContext(),mainActivity.webView.getUrl(),Toast.LENGTH_LONG).show();
        view.loadUrl(url);
        return true;
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

    }

}
