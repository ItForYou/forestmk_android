package kr.co.itforone.forestmk_android;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.ActivityNotFoundException;
import android.content.ClipData;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Point;
import android.location.Location;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.Display;
import android.view.View;
import android.view.ViewTreeObserver;
import android.view.Window;
import android.webkit.ValueCallback;
import android.webkit.WebBackForwardList;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import java.io.IOException;

import butterknife.BindView;
import butterknife.ButterKnife;

public class SubWebveiwActivity extends AppCompatActivity {
    @BindView(R.id.sub_refreshlayout)   SwipeRefreshLayout subrefreshlayout;
    @BindView(R.id.subWebview)    public WebView webView;
    int flg_alert =0,flg_confirm=0,flg_modal =0,flg_sortmodal=0;
    public int flg_refresh = 1;
    private ActivityManager am = ActivityManager.getInstance();
    Dialog current_dialog;
    ValueCallback<Uri[]> filePathCallbackLollipop;
    static final int FILECHOOSER_LOLLIPOP_REQ_CODE=1300;
    static final int PERMISSION_REQUEST_CODE = 1;
    static final int CROP_FROM_ALBUM =2;
    static final int GET_ADDRESS =3;
    private Location location;
    public Uri mImageCaptureUri,croppath;
    private EndDialog mEndDialog;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_webview);
        ButterKnife.bind(this);
        am.addActivity(this);

        webView.setWebChromeClient(new SubChromeManager(this));
        webView.setWebViewClient(new SubViewManager(this));
        webView.addJavascriptInterface(new SubWebviewJavainterface(this),"Android");
        WebSettings settings = webView.getSettings();
        settings.setJavaScriptEnabled(true);
        settings.setJavaScriptCanOpenWindowsAutomatically(true);
        settings.setAllowFileAccess(true);//웹에서 파일 접근 여부
        settings.setAppCacheEnabled(true);//캐쉬 사용여부
        settings.setDatabaseEnabled(true);//HTML5에서 db 사용여부 -> indexDB
        settings.setDomStorageEnabled(true);//HTML5에서 DOM 사용여부
        settings.setCacheMode(WebSettings.LOAD_DEFAULT);//캐시 사용모드 LOAD_NO_CACHE는 캐시를 사용않는다는 뜻
        settings.setTextZoom(100);       // 폰트크기 고정
        ///settings.setUserAgentString(settings.getUserAgentString()+"//Brunei");

        webView.setLongClickable(true);
        String url="";
        Intent intent = getIntent();

        if(intent!=null) {
            url = intent.getExtras().getString("subview_url");
        }

        if(url!=null && !url.isEmpty()){
            if(url.contains("write.php")){
                Norefresh();
            }
            else{
                Yesrefresh();
            }
            webView.loadUrl(url);
        }
        else{

        }

        subrefreshlayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {

            @Override
            public void onRefresh() {
                webView.clearCache(true);
                webView.reload();
                subrefreshlayout.setRefreshing(false);
            }

        });

        subrefreshlayout.getViewTreeObserver().addOnScrollChangedListener(new ViewTreeObserver.OnScrollChangedListener() {
            @Override
            public void onScrollChanged() {

                if(webView.getScrollY() == 0  && flg_refresh ==1){
                    subrefreshlayout.setEnabled(true);
                }
                else{
                    subrefreshlayout.setEnabled(false);
                }
            }
        });


    }


    public void set_filePathCallbackLollipop(ValueCallback<Uri[]> filePathCallbackLollipop){
        this.filePathCallbackLollipop = filePathCallbackLollipop;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        switch (requestCode) {
            //광고 글쓰기 주소 검색
            case GET_ADDRESS:
                if(resultCode==33) {
                    String data_address12 = data.getStringExtra("address12");
                    String data_address11 = data.getStringExtra("address11");
                    webView.loadUrl("javascript:set_wr12('" + data_address12 + "','"+data_address11+"')");
                }
                //Toast.makeText(getApplicationContext(),"get_addr", Toast.LENGTH_LONG).show();
                break;
            case ChromeManager.FILECHOOSER_LOLLIPOP_REQ_CODE:
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    if (resultCode == RESULT_OK && webView.getUrl().contains("register_form.php")) {
                        if (data != null) {
                            //String dataString = data.getDataString();
                            //  ClipData clipData = data.getClipData();
                            mImageCaptureUri = data.getData();

                            try {
                                Bitmap bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), mImageCaptureUri);
                                String path = MediaStore.Images.Media.insertImage(this.getContentResolver(), bitmap, "Title", null);
                                croppath = Uri.parse(path);
                            } catch (IOException e) {
                                e.printStackTrace();
                            }

                            try {
                                Intent intent = new Intent("com.android.camera.action.CROP");
                                intent.setDataAndType(mImageCaptureUri, "image/*");
                                intent.putExtra("outputX", 500);
                                intent.putExtra("outputY", 500);
                                intent.putExtra("aspectX", 1);
                                intent.putExtra("aspectY", 1);
                                intent.putExtra("scale", true);
                                intent.putExtra("return-data", true);
                                intent.putExtra(MediaStore.EXTRA_OUTPUT, croppath);
                                startActivityForResult(intent, CROP_FROM_ALBUM);
                            } catch (ActivityNotFoundException e) {
                                String errorMessage = "your device doesn't support the crop action!";
                                Toast toast = Toast.makeText(this, errorMessage, Toast.LENGTH_SHORT);
                                toast.show();
                            }
//                        if (clipData != null) {
//                            result = new Uri[clipData.getItemCount()];
//                            for (int i = 0; i < clipData.getItemCount(); i++) {
//                                ClipData.Item item = clipData.getItemAt(i);
//                                result[i] = item.getUri();
//                            }
//                        }
//                        else {
//                            result = ChromeManager.FileChooserParams.parseResult(resultCode, data);
//                            //result = (data == null) ? new Uri[]{mCapturedImageURI} : WebChromeClient.FileChooserParams.parseResult(resultCode, data);
//                        }
                        } else {
                            filePathCallbackLollipop.onReceiveValue(null);
                            filePathCallbackLollipop = null;
                        }
                    } else if (resultCode == RESULT_OK && !webView.getUrl().contains("register_form.php")) {
                        Uri[] result = null;
                        if (data != null) {
                            //String dataString = data.getDataString();
                            ClipData clipData = data.getClipData();
                            if (clipData != null) {
                                result = new Uri[clipData.getItemCount()];
                                for (int i = 0; i < clipData.getItemCount(); i++) {
                                    ClipData.Item item = clipData.getItemAt(i);
                                    result[i] = item.getUri();
                                }
                            } else {
                                result = ChromeManager.FileChooserParams.parseResult(resultCode, data);
                                //result = (data == null) ? new Uri[]{mCapturedImageURI} : WebChromeClient.FileChooserParams.parseResult(resultCode, data);
                            }
                            filePathCallbackLollipop.onReceiveValue(result);
                        } else {
                            filePathCallbackLollipop.onReceiveValue(null);
                            filePathCallbackLollipop = null;
                        }
                    } else {
                        try {
                            if (filePathCallbackLollipop != null) {
                                filePathCallbackLollipop.onReceiveValue(null);
                                filePathCallbackLollipop = null;

                            }
                        } catch (Exception e) {

                        }
                    }
                }
                break;
            case CROP_FROM_ALBUM:
                if (resultCode == RESULT_OK) {
                    Log.d("imgresult", String.valueOf(resultCode));
                    Uri[] result = null;
                    result = new Uri[1];
                    // Bitmap photo = data.getExtras().getParcelable("data");
                    /*ByteArrayOutputStream bytes = new ByteArrayOutputStream();
                    photo.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
                    String path = MediaStore.Images.Media.insertImage(this.getContentResolver(), photo, "Title", null);*/
                    //result[0] = Uri.parse(path);
                    result[0] = croppath;

                    if (result[0] != null) {
                        //  Toast.makeText(getApplicationContext(),"step1",Toast.LENGTH_LONG).show();
                        filePathCallbackLollipop.onReceiveValue(result);
                    } else {
                        //  Toast.makeText(getApplicationContext(),"step2",Toast.LENGTH_LONG).show();
                    }
                    break;

                } else {
                    try {
                        if (filePathCallbackLollipop != null) {
                            filePathCallbackLollipop.onReceiveValue(null);
                            filePathCallbackLollipop = null;
                        }
                    } catch (Exception e) {

                    }
                }
        }
    }

    public double getlat(){
        //Toast.makeText(getApplicationContext(),""+location.getLatitude() + "//" +location.getLongitude(),Toast.LENGTH_LONG).show();
        if(location!=null) {
            return location.getLatitude();
        }
        else return 0;
    }

    public double getlng(){
        //Toast.makeText(getApplicationContext(),""+location.getLatitude() + "//" +location.getLongitude(),Toast.LENGTH_LONG).show();
        if(location!=null) {
            return location.getLongitude();
        }
        else return 0;

    }



    @Override
    public void onBackPressed() {
        WebBackForwardList list = null;
        String backurl ="";

   /*     try{
            list = webView.copyBackForwardList();
            if(list.getSize() >1 ){
                backurl = list.getItemAtIndex(list.getCurrentIndex() - 1).getUrl();
                Toast.makeText(getApplicationContext(),backurl,Toast.LENGTH_LONG).show();
                return;
            }
        } catch (NullPointerException e) {
            e.printStackTrace();
        }
*/
        if (flg_modal==1 && (webView.getUrl().equals(getString(R.string.home))|| webView.getUrl().equals(getString(R.string.home2)))){
            webView.loadUrl("javascript:close_writemd()");
        }
       else if (flg_sortmodal!=0 && (webView.getUrl().contains("bo_table=deal") || webView.getUrl().equals(getString(R.string.home2)))){
            webView.loadUrl("javascript:close_sortmd("+flg_sortmodal+")");
        }
        //if(webView.getUrl().equals(getString(R.string.home)))
        //Toast.makeText(getApplicationContext(),webView.getUrl(),Toast.LENGTH_LONG).show();
        else if(webView.getUrl().equals(getString(R.string.home)) || webView.getUrl().equals(getString(R.string.home2))){
            mEndDialog = new EndDialog(SubWebveiwActivity.this);
            mEndDialog.setCancelable(true);
            mEndDialog.show();
            Display display = getWindowManager().getDefaultDisplay();
            Point size = new Point();
            display.getSize(size);

            Window window = mEndDialog.getWindow();
            int x = (int)(size.x * 0.8f);
            int y = (int)(size.y* 0.45f);

            window.setLayout(x,y);
        }
       else  if(webView.getUrl().contains("write.php")){
            AlertDialog.Builder builder = new AlertDialog.Builder(SubWebveiwActivity.this);
            // Set a title for alert dialog
            builder.setTitle("");
            String message;
            if(webView.getUrl().contains("w=u")){
                message="수정을 취소하시겠습니까?";
            }
            else{
                message = "글쓰기를 종료하시겠습니까?";
            }

            // Show a message on alert dialog
            builder.setMessage(message);

            // Set the positive button
            builder.setPositiveButton("확인",   new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int which) {
                    if(webView.canGoBack()){
                        webView.goBack();
                    }
                    else{
                        finish();
                        overridePendingTransition(R.anim.stay, R.anim.fadeout);
                    }
                }
            });
            // Set the negative button
            builder.setNegativeButton("취소",  new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {

                }
            });
            builder.setCancelable(true);
            // Create the alert dialog
            AlertDialog dialog = builder.create();
            // Finally, display the alert dialog
          //  current_dialog = dialog;
            dialog.show();
            // Get the alert dialog buttons reference
            Button positiveButton = dialog.getButton(AlertDialog.BUTTON_POSITIVE);
            Button negativeButton = dialog.getButton(AlertDialog.BUTTON_NEGATIVE);

            // Change the alert dialog buttons text and background color
            positiveButton.setTextColor(Color.parseColor("#9dc543"));
            negativeButton.setTextColor(Color.parseColor("#ff0000"));

        }
        else if(webView.canGoBack()){
            webView.getUrl();
            webView.goBack();
        }
        else {
            super.onBackPressed();
            overridePendingTransition(R.anim.stay, R.anim.fadeout);
        }

    }

    public void Norefresh(){
        subrefreshlayout.setEnabled(false);
    }
    public void Yesrefresh(){
        subrefreshlayout.setEnabled(true);
    }

    public void click_dialogN(View view){
        //  Toast.makeText(mContext.getApplicationContext(),"test",Toast.LENGTH_LONG).show();
        mEndDialog.dismiss();
    }

    public void click_dialogY(View view){
        //    Toast.makeText(mContext.getApplicationContext(),"test2",Toast.LENGTH_LONG).show();
        am.finishAllActivity();
    }

}
