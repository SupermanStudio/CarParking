package com.network.jiufen.carparking.carparking.activity;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.network.jiufen.carparking.carparking.R;
import com.network.jiufen.carparking.carparking.util.DictionaryUtil;
import com.network.jiufen.carparking.carparking.util.MySingleton;
import com.network.jiufen.carparking.carparking.widget.CustomDatePicker;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class BookingActivity extends AppCompatActivity  implements View.OnClickListener {

    private String url = "https://carparkingservice.herokuapp.com/booking/save";

    /**
     * Called when the activity is first created.
     */
    private TextView startDateTime;
    private TextView endDateTime;
    private Button confirmBooking;
    private EditText numberPicker;
    private EditText carPlate;

    private CustomDatePicker startTimeDatePicker;
    private CustomDatePicker endTimeDatePicker;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.booking_content);

        startDateTime = (TextView) findViewById(R.id.startDialog);
        endDateTime = (TextView) findViewById(R.id.endDialog);
        confirmBooking = (Button) findViewById(R.id.confirmBooking);
        numberPicker = (EditText) findViewById(R.id.numberPicker);
        carPlate = (EditText) findViewById(R.id.carPlate);
        startDateTime.setOnClickListener(this);
        endDateTime.setOnClickListener(this);
        confirmBooking.setOnClickListener(this);
        initDatePicker();
    }

    private void initDatePicker() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.CHINA);
        String now = sdf.format(new Date());
        startDateTime.setText(now);
        endDateTime.setText(now);
        startTimeDatePicker = new CustomDatePicker(this, new CustomDatePicker.ResultHandler() {
            @Override
            public void handle(String time) { // 回调接口，获得选中的时间
                startDateTime.setText(time);
            }
        }, now, "2020-01-01 00:00"); // 初始化日期格式请用：yyyy-MM-dd HH:mm，否则不能正常运行
        startTimeDatePicker.showSpecificTime(true); // 显示时和分
        startTimeDatePicker.setIsLoop(true); // 允许循环滚动

        endTimeDatePicker = new CustomDatePicker(this, new CustomDatePicker.ResultHandler() {
            @Override
            public void handle(String time) { // 回调接口，获得选中的时间
                endDateTime.setText(time);
            }
        }, now, "2020-01-01 00:00"); // 初始化日期格式请用：yyyy-MM-dd HH:mm，否则不能正常运行
        endTimeDatePicker.showSpecificTime(true); // 显示时和分
        endTimeDatePicker.setIsLoop(true); // 允许循环滚动
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.startDialog:
                // 日期格式为yyyy-MM-dd
                startTimeDatePicker.show(startDateTime.getText().toString());
                break;

            case R.id.endDialog:
                // 日期格式为yyyy-MM-dd HH:mm
                endTimeDatePicker.show(endDateTime.getText().toString());
                break;

            case R.id.confirmBooking:
                String startTime = startDateTime.getText().toString().trim();
                String endTime = endDateTime.getText().toString().trim();
                Integer number = Integer.valueOf(numberPicker.getText().toString().trim());
                String plate = carPlate.getText().toString().trim();
                Map map = new HashMap<>();
                map.put("startTime",startTime);
                map.put("endTime",endTime);
                map.put("plateNumber",plate);
                map.put("carCounts",number);
                JSONObject params = new JSONObject(map);
                saveBookingInfo(params);
                break;
        }
    }




    public void saveBookingInfo(JSONObject jsonObject)
    {
        JsonObjectRequest objRequest = new JsonObjectRequest(Request.Method.POST, url, jsonObject,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            String status = response.getString("status");
                            if(status.equals(DictionaryUtil.SUCCESS))
                            {
                                Toast.makeText(BookingActivity.this, "注册成功", Toast.LENGTH_LONG).show();
                            }else
                            {
                                Toast.makeText(BookingActivity.this, "账号已存在", Toast.LENGTH_LONG).show();
                            }
                        } catch (JSONException e) {
                            Toast.makeText(BookingActivity.this, "无法注册", Toast.LENGTH_LONG).show();
                            return;
                        }

                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(BookingActivity.this, "无法注册", Toast.LENGTH_LONG).show();
            }
        });
        MySingleton.getInstance(BookingActivity.this).addToRequestQueue(objRequest);
    }


}
