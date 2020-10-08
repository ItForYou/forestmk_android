package kr.co.itforone.forestmk_android;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.ClipData;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.Point;
import android.location.Location;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
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
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.InstanceIdResult;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;
import butterknife.BindView;
import butterknife.ButterKnife;


public class MainActivity extends AppCompatActivity {
    @BindView(R.id.webView)    WebView webView;
    @BindView(R.id.refreshlayout)    SwipeRefreshLayout refreshlayout;
    String token = "";
    public int flg_refresh = 1;
    ValueCallback<Uri[]> filePathCallbackLollipop;
    Dialog current_dialog;
    private Location location;
    private ActivityManager am = ActivityManager.getInstance();
    public Uri mImageCaptureUri,croppath;
    static final int FILECHOOSER_LOLLIPOP_REQ_CODE=1300;
    static final int PERMISSION_REQUEST_CODE = 1;
    static final int CROP_FROM_ALBUM =2;
    static final int GET_ADDRESS =3;
    static final int VIEW_REFRESH =4;
    private LocationManager locationManager;
    private EndDialog mEndDialog;
    boolean gps_enabled = false, now_refreshlayout=true;
    int flg_alert = 0, flg_confirm=0, flg_modal=0,flg_sortmodal=0, flg_dclmodal=0, flg_dclcommmodal=0;
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
                    location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
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
        am.addActivity(this);

        ActivityCompat.requestPermissions(this, PERMISSIONS, PERMISSION_REQUEST_CODE);

        if(hasPermissions(PERMISSIONS)) {

            locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
            location = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);

        }

        FirebaseInstanceId.getInstance().getInstanceId()
                .addOnCompleteListener(new OnCompleteListener<InstanceIdResult>() {
                    @Override
                    public void onComplete(@NonNull Task<InstanceIdResult> task) {
                        if (!task.isSuccessful()) {
                            Log.w("D", "getInstanceId failed", task.getException());
                            return;
                        }
                        // Get new Instance ID token
                        token = task.getResult().getToken();
                    }
                });

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
        webView.setWebContentsDebuggingEnabled(true);
       // webView.setLongClickable(true);

        SharedPreferences pref = getSharedPreferences("logininfo", MODE_PRIVATE);
        String id = pref.getString("id", "");
        String pwd = pref.getString("pwd", "");

       // Toast.makeText(getApplicationContext(),id+"-"+pwd,Toast.LENGTH_LONG).show();

        if(!id.isEmpty() && !pwd.isEmpty()){
            webView.loadUrl(getString(R.string.login)+"mb_id="+id+"&mb_password="+pwd);
         //   webView.clearCache(true);
         //   webView.clearHistory();
        }
        else {
            webView.loadUrl(getString(R.string.home));
        }

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
                   Log.d("nowrefre",String.valueOf(now_refreshlayout));
                    now_refreshlayout = true;
                    refreshlayout.setEnabled(true);
                }
                else{
                    Log.d("nowrefre",String.valueOf(now_refreshlayout));
                  //  refreshlayout.setEnabled(false);
                }
            }
        });

        Log.d("lastchk",String.valueOf(now_refreshlayout));
    }

    @Override
    public void onBackPressed(){

        WebBackForwardList list = null;
        String backurl ="";

      try{
            list = webView.copyBackForwardList();
            if(list.getSize() >1 ){

                backurl = list.getItemAtIndex(list.getCurrentIndex() - 1).getUrl();
                Log.d("back_url", backurl);
              // Toast.makeText(getApplicationContext(),backurl,Toast.LENGTH_LONG).show();

            }
        }catch (NullPointerException e) {
            e.printStackTrace();
        }
        Log.d("now_url", webView.getUrl());
       // Toast.makeText(getApplicationContext(),backurl,Toast.LENGTH_LONG).show();

        if(backurl.contains("register_form.php") || backurl.contains("password_lost.php") ||
                (backurl.contains("board.php") && backurl.contains("wr_id=")) || backurl.contains("mypage.php") ||
                backurl.contains("login.php") || backurl.contains("mymap.php")) {
                    Log.d("NoRefresh!!", webView.getUrl());
                    Norefresh();
        }
        else{
            Log.d("YesRefresh!!", webView.getUrl());
            Yesrefresh();
        }


        if (flg_modal==1 && (webView.getUrl().equals(getString(R.string.home))|| webView.getUrl().equals(getString(R.string.home2)))){
            //Toast.makeText(getApplicationContext(),"close_excute!!",Toast.LENGTH_LONG).show();
            webView.loadUrl("javascript:close_writemd()");
        }
        else if (flg_sortmodal!=0 && ((webView.getUrl().contains("bo_table=deal")&&!webView.getUrl().contains("wr_id=")) || webView.getUrl().equals(getString(R.string.home2)))){
            webView.loadUrl("javascript:close_sortmd("+flg_sortmodal+")");
        }
        else if(flg_dclmodal!=0 && (webView.getUrl().contains("bo_table=deal")&&webView.getUrl().contains("wr_id="))){
            webView.loadUrl("javascript:close_dclmd()");
        }
       else if(webView.getUrl().equals(getString(R.string.home)) || webView.getUrl().equals(getString(R.string.home2))){
            mEndDialog = new EndDialog(MainActivity.this);
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
        else if(webView.getUrl().contains("refer_c=1")){
            webView.loadUrl(getString(R.string.home));
            webView.clearCache(true);
        }
        else if(webView.getUrl().contains("http://14.48.175.177/bbs/register_form.php?w=u")){
            Confirm_alert("수정을 취소하시겠습니까?");
        }
        else if(webView.getUrl().contains("write.php")){

            String message;

            if(webView.getUrl().contains("w=u")){
                message="수정을 취소하시겠습니까?";
            }
            else{
                message = "글쓰기를 종료하시겠습니까?";
            }
            Confirm_alert(message);

        }

       else if(webView.canGoBack()){
            webView.goBack();
        }

        else{
            //종료 다이얼로그 창
            mEndDialog = new EndDialog(this);
            mEndDialog.setCancelable(true);
            //current_dialog = mEndDialog;
            mEndDialog.show();
            Display display = getWindowManager().getDefaultDisplay();
            Point size = new Point();
            display.getSize(size);

            Window window = mEndDialog.getWindow();
            int x = (int)(size.x * 0.8f);
            int y = (int)(size.y* 0.45f);

            window.setLayout(x,y);

//                long tempTime = System.currentTimeMillis();
//                long intervalTime = tempTime - backPrssedTime;
//                if (0 <= intervalTime && 2000 >= intervalTime){
//                finish();
//            }
//            else
//            {
//                backPrssedTime = tempTime;
//                Toast.makeText(getApplicationContext(), "한번 더 뒤로가기 누를시 앱이 종료됩니다.", Toast.LENGTH_SHORT).show();
//            }
        }
    }

    public void Confirm_alert(String Message){

        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
        // Set a title for alert dialog
        builder.setTitle("");
        // Show a message on alert dialog
        builder.setMessage(Message);
        // Set the positive button
        builder.setPositiveButton("확인",   new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                if(webView.canGoBack()){
                    webView.goBack();
                }
                else{
                    mEndDialog = new EndDialog(MainActivity.this);
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
            }

        });
        // Set the negative button
        builder.setNegativeButton("취소",  new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        });

        builder.setCancelable(false);
        // Create the alert dialog
        AlertDialog dialog = builder.create();
        // Finally, display the alert dialog
        //   current_dialog = dialog;
        dialog.show();
        // Get the alert dialog buttons reference
        Button positiveButton = dialog.getButton(AlertDialog.BUTTON_POSITIVE);
        Button negativeButton = dialog.getButton(AlertDialog.BUTTON_NEGATIVE);

        // Change the alert dialog buttons text and background color
        positiveButton.setTextColor(Color.parseColor("#9dc543"));
        negativeButton.setTextColor(Color.parseColor("#ff0000"));

    }

    public void click_dialogN(View view){
      //  Toast.makeText(mContext.getApplicationContext(),"test",Toast.LENGTH_LONG).show();
        mEndDialog.dismiss();
    }

    public void click_dialogY(View view){
    //    Toast.makeText(mContext.getApplicationContext(),"test2",Toast.LENGTH_LONG).show();
          am.finishAllActivity();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data){
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
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP){
                    if (resultCode == RESULT_OK && webView.getUrl().contains("register_form.php")) {
                        if (data != null) {
                            //String dataString = data.getDataString();
                            //  ClipData clipData = data.getClipData();
                            Uri result = (data == null || resultCode != RESULT_OK) ? null : data.getData();
                            CropImage.activity(result)
                                    .setAspectRatio(1,1)//가로 세로 1:1로 자르기 기능 * 1:1 4:3 16:9로 정해져 있어요
                                    .setCropShape(CropImageView.CropShape.OVAL)
                                    .start(this);
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
            case CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE:

                        CropImage.ActivityResult result = CropImage.getActivityResult(data);
                        if(result!=null) {
                                Uri resultUri = result.getUri();
                                Uri[] arr_Uri = new Uri[1];
                                arr_Uri[0] = resultUri;
                                filePathCallbackLollipop.onReceiveValue(arr_Uri);
                                filePathCallbackLollipop = null;
                            }
                        else {
                            try {
                                if (filePathCallbackLollipop != null) {
                                    filePathCallbackLollipop.onReceiveValue(null);
                                    filePathCallbackLollipop = null;
                                }
                            } catch (Exception e) {
                            }
                        }
                            break;
            case VIEW_REFRESH:

                            if(data!=null){

                                boolean backflg_refresh = data.getExtras().getBoolean("refresh");
                                Log.d("backpress_flg",String.valueOf(backflg_refresh));
                                if(backflg_refresh==true) {
                                    webView.reload();
                                }
                                break;

                            }
                            else{
                                break;
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
        now_refreshlayout = false;
        refreshlayout.setEnabled(false);
        Log.d("nowrefre",String.valueOf(now_refreshlayout));
        //Log.d("nowrefre","false");
    }
    public void Yesrefresh(){
        now_refreshlayout = true;
        refreshlayout.setEnabled(true);
        Log.d("nowrefre",String.valueOf(now_refreshlayout));
        //Log.d("nowrefre","false");
    }

}
