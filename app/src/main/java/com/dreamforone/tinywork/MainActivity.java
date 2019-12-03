package com.dreamforone.tinywork;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Message;
import android.os.StrictMode;
import android.support.annotation.RequiresApi;
import android.support.v4.app.ActivityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.webkit.CookieManager;
import android.webkit.CookieSyncManager;
import android.webkit.GeolocationPermissions;
import android.webkit.JavascriptInterface;
import android.webkit.JsResult;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.dreamforone.tinywork.R;

import java.lang.reflect.Method;

import util.BackPressCloseHandler;
import util.Common;
import util.ListViewAdapter;
import util.NetworkCheck;


public class MainActivity extends AppCompatActivity {
    private final String[] NAV_ITEMS = {"로그아웃","공지사항","자주하는 질문(QnA)","고객센터","개인정보 취급 방침","위치기반 이용약관","서비스 수수료 규정","서비스 취소 및 환급규정","서비스 이용약관","서비스 권장 금액표","회사 정보" };
    private final int MAP_REQUEST_CODE=1010;
    LinearLayout webLayout,layout_main_bottom1,layout_main_bottom2,layout_main_bottom3;
    DrawerLayout drawerLayout;
    RelativeLayout networkLayout;
    ListView lv_activity_main_nav_list;
    public static WebView webView;
    NetworkCheck netCheck;
    Button replayBtn;
    ProgressBar loadingProgress;
    public static boolean execBoolean = true;
    private BackPressCloseHandler backPressCloseHandler;
    boolean isIndex = true;
    private final int AUDIO_RECORED_REQ_CODE=1500,SNS_REQ_CODE=2000;
    String firstUrl = "";
    final int REQUEST_IMAGE_CODE = 1010;
    Context mContext;
    Activity mActivity;



    Button btn_main_left,btn_main_guin,btn_main_gujik,btn_main_gps;

    private Uri cameraImageUri;
    public static String no;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Intent startIntent = new Intent(MainActivity.this,SplashActivity.class);
        startActivity(startIntent);
        setContentView(R.layout.activity_main);
        mContext=this;
        mActivity=this;
        //스크린샷 했을 때 빈화면이 나오게
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_SECURE, WindowManager.LayoutParams.FLAG_SECURE);
        //화면을 계속 켜짐
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        if(Build.VERSION.SDK_INT>=24){
            try{
                Method m = StrictMode.class.getMethod("disableDeathOnFileUriExposure");
                m.invoke(null);
            }catch(Exception e){
                e.printStackTrace();
            }
        }
        firstUrl=getString(R.string.url);

        CookieSyncManager.createInstance(this);


        setLayout();
    }


    //레이아웃 설정
    public void setLayout() {







        networkLayout = (RelativeLayout) findViewById(R.id.networkLayout);//네트워크 연결이 끊겼을 때 레이아웃 가져오기
        layout_main_bottom1=(LinearLayout)findViewById(R.id.layout_main_bottom1);
        layout_main_bottom2=(LinearLayout)findViewById(R.id.layout_main_bottom2);
        layout_main_bottom3=(LinearLayout)findViewById(R.id.layout_main_bottom3);
        webLayout = (LinearLayout) findViewById(R.id.webLayout);//웹뷰 레이아웃 가져오기
        drawerLayout=(DrawerLayout)findViewById(R.id.drawerLayout);
        loadingProgress = (ProgressBar)findViewById(R.id.loadingProgress);
        lv_activity_main_nav_list=(ListView)findViewById(R.id.lv_activity_main_nav_list);
        ListViewAdapter adapter = new ListViewAdapter() ;
        lv_activity_main_nav_list.setAdapter(adapter);
        adapter.addItem(NAV_ITEMS);
        //리스트뷰 터치이벤트
        lv_activity_main_nav_list.setOnItemClickListener(mItemClickListener);



        webView = (WebView) findViewById(R.id.webView);//웹뷰 가져오기
        Log.d("url",firstUrl);
        webView.loadUrl(firstUrl);
        webViewSetting();

        btn_main_left=(Button)findViewById(R.id.btn_main_left);
        btn_main_guin=(Button)findViewById(R.id.btn_main_guin);
        btn_main_gujik=(Button)findViewById(R.id.btn_main_gujik);
        btn_main_gps=(Button)findViewById(R.id.btn_main_gps);

        btn_main_left.setOnClickListener(mOnClickListener);
        btn_main_guin.setOnClickListener(mOnClickListener);
        btn_main_gujik.setOnClickListener(mOnClickListener);
        btn_main_gps.setOnClickListener(mOnClickListener);

        layout_main_bottom1.setOnClickListener(mOnClickListener);
        layout_main_bottom2.setOnClickListener(mOnClickListener);
        layout_main_bottom3.setOnClickListener(mOnClickListener);

        ChangeLogin();

    }
    //갤러리 온클릭리스너 만들기

    @RequiresApi(api = Build.VERSION_CODES.ECLAIR_MR1)
    public void webViewSetting() {

        WebSettings setting = webView.getSettings();//웹뷰 세팅용

        setting.setAllowFileAccess(true);//웹에서 파일 접근 여부
        setting.setAppCacheEnabled(true);//캐쉬 사용여부
        setting.setGeolocationEnabled(true);//위치 정보 사용여부
        setting.setDatabaseEnabled(true);//HTML5에서 db 사용여부
        setting.setDomStorageEnabled(true);//HTML5에서 DOM 사용여부
        setting.setCacheMode(WebSettings.LOAD_DEFAULT);//캐시 사용모드 LOAD_NO_CACHE는 캐시를 사용않는다는 뜻
        setting.setJavaScriptEnabled(true);//자바스크립트 사용여부
        setting.setSupportMultipleWindows(false);//윈도우 창 여러개를 사용할 것인지의 여부 무조건 false로 하는 게 좋음
        setting.setUseWideViewPort(true);//웹에서 view port 사용여부
        webView.setWebChromeClient(chrome);//웹에서 경고창이나 또는 컴펌창을 띄우기 위한 메서드
        webView.setWebViewClient(client);//웹페이지 관련된 메서드 페이지 이동할 때 또는 페이지가 로딩이 끝날 때 주로 쓰임

        setting.setUserAgentString("factstaock");
        webView.addJavascriptInterface(new WebJavascriptEvent(), "Android");

        //네트워크 체킹을 할 때 쓰임
        netCheck = new NetworkCheck(this, this);
        netCheck.setNetworkLayout(networkLayout);
        netCheck.setWebLayout(webLayout);
        netCheck.networkCheck();
        //뒤로가기 버튼을 눌렀을 때 클래스로 제어함
        backPressCloseHandler = new BackPressCloseHandler(this);

        replayBtn=(Button)findViewById(R.id.replayBtn);
        replayBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                netCheck.networkCheck();
            }
        });
    }

    WebChromeClient chrome;
    {
        chrome = new WebChromeClient() {
            //새창 띄우기 여부
            @Override
            public boolean onCreateWindow(WebView view, boolean isDialog, boolean isUserGesture, Message resultMsg) {
                return false;
            }

            //경고창 띄우기
            @Override
            public boolean onJsAlert(WebView view, String url, String message, final JsResult result) {
                new AlertDialog.Builder(MainActivity.this)
                        .setMessage("\n" + message + "\n")
                        .setPositiveButton("확인",
                                new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog,
                                                        int which) {
                                        result.confirm();
                                    }
                                }).create().show();
                return true;
            }

            //컴펌 띄우기
            @Override
            public boolean onJsConfirm(WebView view, String url, String message,
                                       final JsResult result) {
                new AlertDialog.Builder(MainActivity.this)
                        .setMessage("\n" + message + "\n")
                        .setPositiveButton("확인",
                                new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog,
                                                        int which) {
                                        result.confirm();
                                    }
                                })
                        .setNegativeButton("취소",
                                new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog,
                                                        int which) {
                                        result.cancel();
                                    }
                                }).create().show();
                return true;
            }

            //현재 위치 정보 사용여부 묻기
            @Override
            public void onGeolocationPermissionsShowPrompt(String origin, GeolocationPermissions.Callback callback) {
                // Should implement this function.
                final String myOrigin = origin;
                final GeolocationPermissions.Callback myCallback = callback;
                AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                builder.setTitle("Request message");
                builder.setMessage("Allow current location?");
                builder.setPositiveButton("Allow", new DialogInterface.OnClickListener() {

                    public void onClick(DialogInterface dialog, int id) {
                        myCallback.invoke(myOrigin, true, false);
                    }

                });
                builder.setNegativeButton("Decline", new DialogInterface.OnClickListener() {

                    public void onClick(DialogInterface dialog, int id) {
                        myCallback.invoke(myOrigin, false, false);
                    }

                });
                AlertDialog alert = builder.create();
                alert.show();
            }


        };
    }

    WebViewClient client;
    {
        client = new WebViewClient() {
            //페이지 로딩중일 때 (마시멜로) 6.0 이후에는 쓰지 않음
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                loadingProgress.setVisibility(View.VISIBLE);
                Log.d("url",url);

                if (url.startsWith("tel")) {
                    Intent intent = new Intent(Intent.ACTION_DIAL);
                    intent.setData(Uri.parse(url));
                    loadingProgress.setVisibility(View.GONE);
                    try {
                        if (ActivityCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
                            // TODO: Consider calling
                            //    ActivityCompat#requestPermissions
                            // here to request the missing permissions, and then overriding
                            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                            //                                          int[] grantResults)
                            // to handle the case where the user grants the permission. See the documentation
                            // for ActivityCompat#requestPermissions for more details.


                        }
                        startActivity(intent);
                        return true;
                    } catch (Exception e) {
                        e.printStackTrace();
                    }



                }else if(url.startsWith("https://open")){
                    loadingProgress.setVisibility(View.GONE);
                    Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
                    intent.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY| Intent.FLAG_ACTIVITY_CLEAR_WHEN_TASK_RESET);
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK| Intent.FLAG_ACTIVITY_MULTIPLE_TASK);
                    startActivity(intent);
                    return true;
                }else if(url.startsWith("market://")){
                    try {
                        loadingProgress.setVisibility(View.GONE);
                        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=kr.foryou.ssum"));
                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_WHEN_TASK_RESET);
                        startActivity(intent);
                        return true;
                    }catch (Exception e){
                        e.printStackTrace();
                    }
                }else if(url.startsWith("http://pf.kakao.com/")){
                    loadingProgress.setVisibility(View.GONE);
                    Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
                    intent.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY| Intent.FLAG_ACTIVITY_CLEAR_WHEN_TASK_RESET);
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK| Intent.FLAG_ACTIVITY_MULTIPLE_TASK);
                    startActivity(intent);
                    return true;
                }else if (url.startsWith("intent:")) {
                    loadingProgress.setVisibility(View.GONE);
                    try {
                        Intent intent = Intent.parseUri(url, Intent.URI_INTENT_SCHEME);
                        Intent existPackage = getPackageManager().getLaunchIntentForPackage(intent.getPackage());
                        if (existPackage != null) {
                            getBaseContext().startActivity(intent);
                        } else {
                            Intent marketIntent = new Intent(Intent.ACTION_VIEW);
                            marketIntent.setData(Uri.parse("market://details?id=" + intent.getPackage()));
                            startActivity(marketIntent);
                        }
                        return true;
                    } catch (Exception e) {
                        Log.d("error1",e.toString());
                        e.printStackTrace();
                    }
                }


                return false;
            }
            //페이지 로딩이 다 끝났을 때
            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);
                //webLayout.setRefreshing(false);
                loadingProgress.setVisibility(View.GONE);
                Log.d("url",url);
                if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
                    CookieSyncManager.getInstance().sync();
                } else {
                    CookieManager.getInstance().flush();
                }
                Log.d("mb_id",Common.getPref(MainActivity.this,"ss_mb_id",""));
                if(Common.getPref(getApplicationContext(),"mb_id","").equals("")){
                    try{
                        String allCookie=CookieManager.getInstance().getCookie(url);
                        String array[]=allCookie.split(";");
                        for(String str:array){
                            String cookie[] = str.split("=");
                            if(cookie[0].trim().equals("mb_id")){
                                String mb_id=cookie[1].trim();
                                Common.savePref(getApplicationContext(),"mb_id",mb_id);
                            }
                            ChangeLogin();
                        }


                        Log.d("mb_id",Common.getPref(getApplicationContext(),"mb_id",""));
                    }catch (Exception e){

                    }


                }


                if (url.startsWith(getString(R.string.url)) || url.startsWith(getString(R.string.domain))||url.startsWith(getString(R.string.domain)+"/index.php")) {
                    isIndex=true;
                } else {
                    isIndex=false;
                }

            }
            //페이지 오류가 났을 때 6.0 이후에는 쓰이지 않음
            @Override
            public void onReceivedError(WebView view, int errorCode, String description, String failingUrl) {
                //super.onReceivedError(view, request, error);
                //view.loadUrl("");
                //페이지 오류가 났을 때 오류메세지 띄우기
                /*AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                builder.setPositiveButton("확인", new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        finish();
                    }
                });
                builder.setMessage("네트워크 상태가 원활하지 않습니다. 잠시 후 다시 시도해 주세요.");
                builder.show();*/
            }
        };
    }

    @Override
    protected void onStart() {
        super.onStart();

    }

    //쿠키 값 삭제
    public void deleteCookie(){
        CookieSyncManager cookieSyncManager = CookieSyncManager.createInstance(webView.getContext());
        CookieManager cookieManager = CookieManager.getInstance();
        cookieManager.setAcceptCookie(true);
        cookieManager.removeSessionCookie();
        cookieManager.removeAllCookie();
        cookieSyncManager.sync();
    }
    //다시 들어왔을 때
    @Override
    protected void onResume() {
        super.onResume();
        /*if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
            CookieSyncManager.getInstance().startSync();
        }*/


            CookieSyncManager.getInstance().startSync();


        execBoolean=true;
        Log.d("newtork","onResume");
        try{
            Intent intent=getIntent();
            Uri data=intent.getData();
            Log.d("data111",data.toString());
        }catch (Exception e){

        }

        //netCheck.networkCheck();
    }
    //홈버튼 눌러서 바탕화면 나갔을 때
    @Override
    protected void onPause() {
        super.onPause();

            CookieSyncManager.getInstance().stopSync();




        execBoolean=false;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        netCheck.stopReciver();
       // unregisterReceiver(receiver);


    }
    //뒤로가기를 눌렀을 때
    public void onBackPressed() {
        //super.onBackPressed();
        //웹뷰에서 히스토리가 남아있으면 뒤로가기 함
        Log.d("isIndex",isIndex+"");
        if(!isIndex) {
            if (webView.canGoBack()) {
                webView.goBack();
            } else if (webView.canGoBack() == false) {
                backPressCloseHandler.onBackPressed();
            }
        }else{
            backPressCloseHandler.onBackPressed();
        }
    }
    private void ChangeLogin(){
        TextView tv_login = (TextView) findViewById(R.id.tv_main_login);
        String id = Common.getPref(getApplicationContext(),"mb_id","");
        Log.i("TAG", "ChangeLogin id " + id);
        if(id.equals("")){
            tv_login.setText("회원가입/로그인");
        } else {
            tv_login.setText("프로필");
        }
    }
    //로그인 로그아웃
    class WebJavascriptEvent{


        @JavascriptInterface
        public void setLogin(String mb_id){
            Log.d("login","로그인");
            Common.savePref(getApplicationContext(),"ss_mb_id",mb_id);
        }
        @JavascriptInterface
        public void setLogout(){
            Log.d("logout","로그아웃");
            Common.savePref(getApplicationContext(),"ss_mb_id","");
        }
    }
    //온클릭 이벤트
    View.OnClickListener mOnClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            switch (v.getId()){
                case R.id.btn_main_left:
                    drawerLayout.openDrawer(lv_activity_main_nav_list);
                    break;
                case R.id.btn_main_guin:
                    webView.loadUrl(getString(R.string.url));
                    btn_main_guin.setBackgroundResource(R.drawable.logo1_over);
                    btn_main_gujik.setBackgroundResource(R.drawable.logo2);
                    break;
                case R.id.btn_main_gujik:
                    webView.loadUrl(getString(R.string.tab_url));
                    btn_main_guin.setBackgroundResource(R.drawable.logo1);
                    btn_main_gujik.setBackgroundResource(R.drawable.logo2_over);
                    break;
                case R.id.layout_main_bottom1:
                    Intent intent=new Intent(getApplicationContext(),AppInfoActivity.class);
                    startActivity(intent);
                    break;
                case R.id.layout_main_bottom2:
                    if(btn_main_guin.getBackground().getConstantState().equals(getResources().getDrawable(R.drawable.logo1_over).getConstantState())){
                        webView.loadUrl(getString(R.string.url)+"bbs/register_form_tasks.php");
                    }else{
                        webView.loadUrl(getString(R.string.url)+"bbs/register_form_tasks.php?mode=register_tab2");
                    }
                    break;
                case  R.id.layout_main_bottom3:
                    if(Common.getPref(getApplicationContext(),"mb_id","").equals("")){
                     webView.loadUrl(getString(R.string.url)+"bbs/login.php");
                    }else {
                        webView.loadUrl(getString(R.string.url) + "bbs/member_confirm.php?url=http://www.dreamforone.com/~tinywork/bbs/register_form.php");
                    }
                    break;
                case R.id.btn_main_gps:
                    Toast.makeText(mContext, "준비중입니다.", Toast.LENGTH_SHORT).show();
                   /* Intent mIntent = new Intent(MainActivity.this,MapActivity.class);
                    startActivityForResult(mIntent, MAP_REQUEST_CODE);*/
                    break;
            }

        }
    };

    //리스트뷰 터치 이벤트
    AdapterView.OnItemClickListener mItemClickListener=new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            String packName=MainActivity.this.getPackageName();
            String urlName="list_url"+position;
            if(position==0){
                Common.savePref(getApplicationContext(),"mb_id","");
                ChangeLogin();
            }

            int resId=getResources().getIdentifier(urlName,"string",packName);
            String url=getResources().getString(resId);
            webView.loadUrl(url);
            drawerLayout.closeDrawer(lv_activity_main_nav_list);
        }
    };
}