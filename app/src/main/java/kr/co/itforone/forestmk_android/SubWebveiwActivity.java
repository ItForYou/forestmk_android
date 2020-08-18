package kr.co.itforone.forestmk_android;

import android.content.ActivityNotFoundException;
import android.content.ClipData;
import android.content.Intent;
import android.graphics.Bitmap;
import android.location.Location;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.webkit.ValueCallback;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import java.io.IOException;

import butterknife.BindView;
import butterknife.ButterKnife;

public class SubWebveiwActivity extends AppCompatActivity {

    @BindView(R.id.subWebview)    public WebView webView;
    int flg_alert =0;
    ValueCallback<Uri[]> filePathCallbackLollipop;
    static final int FILECHOOSER_LOLLIPOP_REQ_CODE=1300;
    static final int PERMISSION_REQUEST_CODE = 1;
    static final int CROP_FROM_ALBUM =2;
    static final int GET_ADDRESS =3;
    private Location location;
    public Uri mImageCaptureUri,croppath;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_webview);
        ButterKnife.bind(this);

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

        Intent intent = getIntent();
        String url = intent.getExtras().getString("subview_url");

        if(url!=null && !url.isEmpty()){
            webView.loadUrl(url);
        }

        else{

        }
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

        if(webView.canGoBack()){
            webView.goBack();
        }
        else {
            super.onBackPressed();
            overridePendingTransition(R.anim.stay, R.anim.fadeout);
        }

    }
}
