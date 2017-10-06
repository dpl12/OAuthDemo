package com.example.dpl.oauthdemo;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.baidu.api.AsyncBaiduRunner;
import com.baidu.api.Baidu;
import com.baidu.api.BaiduDialog;
import com.baidu.api.BaiduDialogError;
import com.baidu.api.BaiduException;
import com.google.gson.Gson;

import java.io.IOException;

public class MainActivity extends AppCompatActivity {
    private TextView AccessToken=null;
    private Baidu baidu=null;
    private TextView tvResult=null;
    private TextView tvResultUser=null;
    private Gson gson=null;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        baidu=new Baidu("6wTQf2VVu9QhznNUSCt7Esqk",this);
        setContentView(R.layout.activity_main);
        gson=new Gson();
        AccessToken= (TextView) findViewById(R.id.access_taken);
        tvResult=(TextView)findViewById(R.id.tv_result);
        tvResultUser= (TextView) findViewById(R.id.tv_resultUser);
    }
    public void clickOAuth(View v){
        baidu.authorize(this, true, true, new BaiduDialog.BaiduDialogListener() {
            @Override
            public void onComplete(Bundle bundle) {//成功
                refreshUI(baidu.getAccessToken());
            }

            @Override
            public void onBaiduException(BaiduException e) {
                refreshUI("exception");
            }

            @Override
            public void onError(BaiduDialogError baiduDialogError) {
               refreshUI("error");
            }

            @Override
            public void onCancel() {
                refreshUI("cancel");
            }
        });
    }
    private void refreshUI(final String msg){
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                AccessToken.setText(msg);
            }
        });
    }
    private void refreshResultUI(final String msg){
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                tvResult.setText(msg);
            }
        });
    }
    public void clickGetUserInfo(View view){
        String token=baidu.getAccessToken();//获取令牌信息
        if(TextUtils.isEmpty(token)){//判断是否有令牌
            Toast.makeText(this,"令牌为空",Toast.LENGTH_SHORT).show();
        }else{
            //异步请求
            AsyncBaiduRunner runner=new AsyncBaiduRunner(baidu);
            String url="https://openapi.baidu.com/rest/2.0/passport/users/getInfo";
            runner.request(url, null, "GET", new AsyncBaiduRunner.RequestListener() {
                @Override
                public void onComplete(String s) {
                    refreshResultUI(s);
                }

                @Override
                public void onIOException(IOException e) {
                    refreshResultUI("IOException");
                }

                @Override
                public void onBaiduException(BaiduException e) {
                    refreshResultUI("BaiduException");
                }
            });
//            new Thread(){//线程请求同步
//                @Override
//                public void run() {
//                    super.run();
//                    String url="https://openapi.baidu.com/rest/2.0/passport/users/getInfo";
//                    try {
//                        final String jsonText=baidu.request(url,null,"GET");
//                        final UserEntity user=gson.fromJson(jsonText,UserEntity.class);
//                        runOnUiThread(new Runnable() {
//                            @Override
//                            public void run() {
//                                tvResult.setText(jsonText);
//                                tvResultUser.setText(user.getUserName());
//                            }
//                        });
//                    } catch (IOException e) {
//                        e.printStackTrace();
//                    } catch (BaiduException e) {
//                        e.printStackTrace();
//                    }
//                }
//            }.start();
        }
    }
//    class UserEntity{
//        private String userId;
//        private String blood;
//
//        private String userName;
//
//        public String getUserId() {
//            return userId;
//        }
//
//        public void setUserId(String userId) {
//            this.userId = userId;
//        }
//
//        public String getBlood() {
//            return blood;
//        }
//
//        public void setBlood(String blood) {
//            this.blood = blood;
//        }
//
//        public String getUserName() {
//            return userName;
//        }
//
//        public void setUserName(String userName) {
//            this.userName = userName;
//        }
//    }
}
