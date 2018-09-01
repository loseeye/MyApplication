package com.example.king.myapplication;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import org.apache.http.*;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;


public class MainActivity extends AppCompatActivity {

    public static final String TAG="MainActivity";
    private Button Send;
    private Button Receive;
    private TextView textView;
    private String response;
    private EditText inputUserName;
    private EditText inputPassWord;
    private EditText inputName;
    private EditText inputAddress;
    private EditText inputState;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initViews();
        Receive.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                receive();
                textView.setText(response);

            }
        });
        Send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDialog();
            }
        });
    }

    public void initViews(){
        Send =(Button) findViewById(R.id.Send);
        Receive= (Button) findViewById(R.id.Receive);
        textView=(TextView) findViewById(R.id.textView);

    }



    /*从MySQL里获取数据*/
    private void receive() {
        new Thread(
                new Runnable() {
                    @Override
                    public void run() {
                        response=executeHttpGet();
                    }
                }

        ).start();
    }
    private String executeHttpGet() {

        HttpURLConnection con=null;
        InputStream in=null;
        String      path="http://192.168.3.200/dsf/allusers.php";
        try {
            con= (HttpURLConnection) new URL(path).openConnection();
            con.setConnectTimeout(5000);
            con.setReadTimeout(5000);
            con.setDoInput(true);
            con.setRequestMethod("GET");
            if(con.getResponseCode()==200){

                in=con.getInputStream();
                return parseInfo(in);
            }

        } catch (IOException e) {
            e.printStackTrace();
        }

        return null;
    }
  
    private String parseInfo(InputStream in) throws IOException {
        BufferedReader  br=new BufferedReader(new InputStreamReader(in));
        StringBuilder sb=new StringBuilder();
        String line=null;
        while ((line=br.readLine())!=null){
            sb.append(line+"\n");
        }
        Log.i(TAG, "parseInfo: sb:"+sb.toString());
        return sb.toString();
    }

    /*发送数据给MySQL数据库*/

    private void showDialog(){
        AlertDialog.Builder builder=new AlertDialog.Builder(MainActivity.this);
        builder.setTitle("添加个人信息");
        View view= View.inflate(MainActivity.this,R.layout.dialog_custom,null);
        builder.setView(view);


        builder.setPositiveButton("确定", new OnClickListener(){

            @Override
            public void onClick(DialogInterface dialog, int which) {
                String username=inputUserName.getText().toString();
                String password=inputPassWord.getText().toString();
                String name=inputName.getText().toString();
                String address=inputAddress.getText().toString();
                String state=inputState.getText().toString();
                try {
                    jsonObject.put("username",username);
                    jsonObject.put("password",password);
                    jsonObject.put("name",name);
                    jsonObject.put("address",address);
                    jsonObject.put("state",state);
                } catch (JSONException e) {
                    e.printStackTrace();
                };
                send();
            }
        });
        builder.setNegativeButton("取消",new OnClickListener(){

            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        });

        AlertDialog ad=builder.create();
        ad.show();

        inputUserName= (EditText)ad.findViewById(R.id.et_Id_P);
        inputPassWord= (EditText)ad.findViewById(R.id.et_LastName);
        inputName= (EditText)ad.findViewById(R.id.et_FirstName);
        inputAddress= (EditText)ad.findViewById(R.id.et_Address);
        inputState= (EditText)ad.findViewById(R.id.et_City);

    }
    private void send() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                executeHttpPost();
            }
        }).start();

    }
    JSONObject jsonObject=new JSONObject();
    private void executeHttpPost() {
        HttpURLConnection con=null;
        String path="http://192.168.3.200/dsf/adduser.php";
        try {
            URL url=new URL(path);
            con= (HttpURLConnection) url.openConnection();
            con.setDoInput(true);
            con.setDoOutput(true);
            con.setUseCaches(false);
            con.setRequestMethod("POST");
            con.setRequestProperty("Connection", "keep-alive");

            con.connect();
            DataOutputStream out=new DataOutputStream(con.getOutputStream());
            out.flush();
            if (con.getResponseCode()==HttpURLConnection.HTTP_OK){
                InputStream in=con.getInputStream();
                byte [] buf=new byte[in.available()];
                in.read(buf);
                String str=new String(buf);
            }
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
