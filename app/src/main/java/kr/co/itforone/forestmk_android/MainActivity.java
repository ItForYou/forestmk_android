package kr.co.itforone.forestmk_android;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.ActivityNotFoundException;
import android.content.ClipData;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Point;
import android.location.Location;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Message;
import android.provider.MediaStore;
import android.util.Log;
import android.view.Display;
import android.view.View;
import android.view.ViewTreeObserver;
import android.view.Window;
import android.webkit.JsResult;
import android.webkit.ValueCallback;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.Toast;
import android.widget.Toolbar;

import java.io.IOException;

import butterknife.BindView;
import butterknife.ButterKnife;


public class MainActivity extends AppCompatActivity {
    @BindView(R.id.webView)    WebView webView;
    @BindView(R.id.refreshlayout)    SwipeRefreshLayout refreshlayout;
    String token = "";
    public int flg_refresh = 1;
    ValueCallback<Uri[]> filePathCallbackLollipop;
    int flg_modal =0;
    private Location location;
    public Uri mImageCaptureUri,croppath;
    static final int FILECHOOSER_LOLLIPOP_REQ_CODE=1300;
    static final int PERMISSION_REQUEST_CODE = 1;
    static final int CROP_FROM_ALBUM =2;
    static final int GET_ADDRESS =3;
    private LocationManager locationManager;
    private EndDialog mEndDialog;
    int flg_alert = 0;
    long backPrssedTime =0;
    String[] PERMISSIONS = {
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.ACCESS_COARSE_LOCATION,
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.READ_EXTERNAL_STORAGE,
    };

    private final int MY_PERMISSIONS_REQUEST_CAMERA=1001;

    private boolean hasPermissions(String[] permissions) {
        // 퍼미션 확인
        int result = -1;
        for (int i = 0; i < permissions.length; i++) {
            result = ContextCompat.checkSelfPermission(getApplicationContext(), permissions[i]);
        }
        Log.d("per_result",String.valueOf(result));
        if (result == PackageManager.PERMISSION_GRANTED) {
            return true;

        }else {
            return false;
        }
    }

    @SuppressLint("MissingPermission")
    @Override
    public void onRequestPermissionsResult(int requestCode,String permissions[], int[] grantResults) {
        switch (requestCode) {
            case PERMISSION_REQUEST_CODE: {
                // If request is cancelled, the result arrays are empty.
                if (!hasPermissions(PERMISSIONS)){

                }else{
                    locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
                    location = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
                   /* LocationPosition.act=MainActivity.this;
                    LocationPosition.setPosition(this);
                    if(LocationPosition.lng==0.0){
                        LocationPosition.setPosition(this);
                    }
                    String place= LocationPosition.getAddress(LocationPosition.lat,LocationPosition.lng);
                    webView.loadUrl("javascript:getAddress('"+place+"')");*/
                }
                return;
            }
        }
    }

    @SuppressLint("MissingPermission")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        Intent splash = new Intent(MainActivity.this,SplashActivity.class);
        startActivity(splash);

        ActivityCompat.requestPermissions(this, PERMISSIONS, PERMISSION_REQUEST_CODE);

        if(hasPermissions(PERMISSIONS)) {
            locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
            location = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
        }

        webView.setWebChromeClient(new ChromeManager(this,this));
        webView.setWebViewClient(new ViewManager(this, this));
        webView.addJavascriptInterface(new WebviewJavainterface(this, this),"Android");
        WebSettings settings = webView.getSettings();

        settings.setJavaScriptEnabled(true);
        settings.setAllowFileAccess(true);//웹에서 파일 접근 여부
        settings.setAppCacheEnabled(true);//캐쉬 사용여부
        settings.setDatabaseEnabled(true);//HTML5에서 db 사용여부 -> indexDB
        settings.setDomStorageEnabled(true);//HTML5에서 DOM 사용여부
        settings.setCacheMode(WebSettings.LOAD_DEFAULT);//캐시 사용모드 LOAD_NO_CACHE는 캐시를 사용않는다는 뜻
        settings.setTextZoom(100);       // 폰트크기 고정
        ///settings.setUserAgentString(settings.getUserAgentString()+"//Brunei");
        webView.setWebContentsDebuggingEnabled(true);
        webView.setLongClickable(true);
        webView.loadUrl(getString(R.string.home));

        refreshlayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {

            @Override
            public void onRefresh() {
                webView.clearCache(true);
                webView.reload();
                refreshlayout.setRefreshing(false);
            }

        });

        refreshlayout.getViewTreeObserver().addOnScrollChangedListener(new ViewTreeObserver.OnScrollChangedListener() {
            @Override
            public void onScrollChanged() {

                if(webView.getScrollY() == 0 && flg_refresh ==1){
                    refreshlayout.setEnabled(true);
                }
                else{
                    refreshlayout.setEnabled(false);
                }
            }
        });

    }

    @Override
    public void onBackPressed() {



        if(webView.canGoBack()){
            webView.goBack();
        }
        else{
            //종료 다이얼로그 창
            mEndDialog = new EndDialog(this);
            mEndDialog.setCancelable(false);
            mEndDialog.show();
            Display display = getWindowManager().getDefaultDisplay();
            Point size = new Point();
            display.getSize(size);

            Window window = mEndDialog.getWindow();
            int x = (int)(size.x * 0.8f);
            int y = (int)(size.y* 0.45f);

            window.setLayout(x,y);

           /*     long tempTime = System.currentTimeMillis();
                long intervalTime = tempTime - backPrssedTime;
                if (0 <= intervalTime && 2000 >= intervalTime){
                finish();
            }
            else
            {
                backPrssedTime = tempTime;
                Toast.makeText(getApplicationContext(), "한번 더 뒤로가기 누를시 앱이 종료됩니다.", Toast.LENGTH_SHORT).show();
            }*/




        }
    }


    public void click_dialogN(View view){
      //  Toast.makeText(mContext.getApplicationContext(),"test",Toast.LENGTH_LONG).show();
        mEndDialog.dismiss();
    }

    public void click_dialogY(View view){
    //    Toast.makeText(mContext.getApplicationContext(),"test2",Toast.LENGTH_LONG).show();
          finish();
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

    public void set_filePathCallbackLollipop(ValueCallback<Uri[]> filePathCallbackLollipop){
        this.filePathCallbackLollipop = filePathCallbackLollipop;
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

    public void Norefresh(){
      //  refreshlayout.setEnabled(false);
    }
    public void Yesrefresh(){
      //  refreshlayout.setEnabled(true);
    }

}
