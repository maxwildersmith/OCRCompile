package com.example.max.aicomplier;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.RetryPolicy;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.max.aicomplier.MainActivity;
import com.example.max.aicomplier.R;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;



public class Compile extends AppCompatActivity {
    /**
     * ATTENTION: This was auto-generated to implement the App Indexing API.
     * See https://g.co/AppIndexing/AndroidStudio for more information.
     */

    Button submit;
    EditText text;
    TextView output;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setContentView(R.layout.activity_compile);
        super.onCreate(savedInstanceState);

        submit = (Button) (findViewById(R.id.button));
        text = (EditText) (findViewById(R.id.input));
        output = (TextView) (findViewById(R.id.output));


        Log.e("asdf",submit.toString());
        text.setText(getIntent().getStringExtra("code"));

        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                RequestQueue MyRequestQueue = Volley.newRequestQueue(Compile.this);
                String RunUrl = "http://api.hackerearth.com/code/run/";
                String CompileUrl = "http://api.hackerearth.com/code/compile/";
                StringRequest MyStringRequest = new StringRequest(Request.Method.POST, RunUrl, new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        //This code is executed if the server responds, whether or not the response contains data.
                        //The String 'response' contains the server's response.*-
                        Log.e("asdf", response);
                        try {
                            JSONObject obj = new JSONObject(response);
                            Log.e("asdf5555", obj.toString() + " " + obj.length());
                            output.setText(obj.optString("compile_status")+": "+obj.optJSONObject("run_status").optString("output"));

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() { //Create an error listener to handle errors appropriately.
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        //This code is executed if there is an error.
                        output.setText(error.getMessage());
                        Log.wtf("asdf", error);
                    }
                }) {
                    protected Map<String, String> getParams() {
                        Map<String, String> MyData = new HashMap<>();
                        MyData.put("client_secret", "9009a132de4a9f699acaf12120adb5510a9eb602");
                        MyData.put("async", "0");
                        MyData.put("source", "public class Main{public static void main(String[] args){"+text.getText().toString()+"}}");
                        MyData.put("lang", "JAVA");
                        MyData.put("time_limit", "5");
                        MyData.put("memory_limit", "262144");//Add the data you'd like to send to the server.
                        return MyData;
                    }
                };
                MyStringRequest.setRetryPolicy(new RetryPolicy() {
                    @Override
                    public int getCurrentTimeout() {
                        return 50000;
                    }

                    @Override
                    public int getCurrentRetryCount() {
                        return 50000;
                    }

                    @Override
                    public void retry(VolleyError error) throws VolleyError {

                    }
                });
                MyRequestQueue.add(MyStringRequest);
            }


        });
    }
}
