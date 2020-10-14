package kr.co.itforone.forestmk_android;


import android.content.Intent;
import android.graphics.Bitmap;
import android.util.Log;
import android.webkit.WebView;
import android.webkit.WebViewClient;

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

        boolean lastchk = context.now_refreshlayout;
        Log.d("backpress_should",url);

        if(url.contains("category.php") || url.contains("recent_list.php") || (url.contains("mypage.php") && !url.contains("compulsive"))
                ||  (url.contains("board.php")&&!url.contains("wr_id") && !url.contains("compulsive"))  || (url.contains("write.php") && url.contains("deal"))  ) {

            Log.d("backpress_newintent",url);

            Intent intent = new Intent(context, SubWebveiwActivity.class);
            intent.putExtra("subview_url", url);
            intent.putExtra("before_refresh", lastchk);
            context.startActivityForResult(intent,context.VIEW_REFRESH);
            context.overridePendingTransition(R.anim.fadein, R.anim.stay);
            return true;
        }

        else {
            //Toast.makeText(mainActivity.getApplicationContext(),"view"+String.valueOf(mainActivity.flg_alert), Toast.LENGTH_LONG).show();
            if(url.contains("register_form.php") || url.contains("password_lost.php") ||
                    (url.contains("board.php") && url.contains("wr_id=")) || url.contains("mypage.php") ||
                    url.contains("login.php") || url.contains("mymap.php") || url.contains("myhp.php")){
                context.Norefresh();
                context.flg_refresh=0;
            }
            else{
                context.Yesrefresh();
                context.flg_refresh=1;
            }
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

        /*if(url.contains("login_check.php") || url.contains("write_update.php") || url.contains("register_form_update.php")){
            WebBackForwardList list = null;
            String backurl ="";
            try{
                list = context.webView.copyBackForwardList();
                if(list.getSize()>2){
                    context.webView.goBack();
                    context.webView.goBack();
                }
                else{
                    context.finish();
                }
            } catch (NullPointerException e) {
                e.printStackTrace();
            }
        }*/
    }
}
