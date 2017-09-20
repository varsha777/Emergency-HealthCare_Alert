package com.example.user.emergencyhealthcare.actvities;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;

import com.example.user.emergencyhealthcare.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;

public class output_bloodgroup extends AppCompatActivity {

    TextView t1,a1,a2,a3;
    String dist_name,address1,address2,address3;
    String name;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_output_bloodgroup);

        t1=(TextView)findViewById(R.id.distname);
        a1=(TextView)findViewById(R.id.address1);
        a2=(TextView)findViewById(R.id.address2);
        a3=(TextView)findViewById(R.id.address3);

        Bundle Extras=getIntent().getExtras();
        if (Extras!=null)
        {
            name=Extras.getString("name");
        }
        try {
            JSONObject obj = new JSONObject(loadJSONFromAsset());
            JSONArray m_jArry = obj.getJSONArray(name);
            ArrayList<HashMap<String, String>> formList = new ArrayList<HashMap<String, String>>();
            HashMap<String, String> m_li;

            for (int i = 0; i < m_jArry.length(); i++) {
                JSONObject jo_inside = m_jArry.getJSONObject(i);
                dist_name = jo_inside.getString("Name");
                address1 = jo_inside.getString("Address-1");
                address2 = jo_inside.getString("Address-2");
                address3 = jo_inside.getString("Address-3");
            }
            t1.setText(dist_name);
            a1.setText(address1);
            a2.setText(address2);
            a3.setText(address3);
        } catch (JSONException e) {
            e.printStackTrace();
        }

    }


    public String loadJSONFromAsset() {
        String json = null;
        try {
            InputStream is =getAssets().open("sujithaJson.json");
            int size = is.available();
            byte[] buffer = new byte[size];
            is.read(buffer);
            is.close();
            json = new String(buffer, "UTF-8");
        } catch (IOException ex) {
            ex.printStackTrace();
            return null;
        }
        return json;
    }
}
