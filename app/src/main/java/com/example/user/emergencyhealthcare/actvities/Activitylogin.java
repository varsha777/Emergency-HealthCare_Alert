package com.example.user.emergencyhealthcare.actvities;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.provider.Settings;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.user.emergencyhealthcare.R;
import com.example.user.emergencyhealthcare.database.DBHelper;

public class Activitylogin extends AppCompatActivity implements View.OnClickListener {

    Button mLogin;
    Button mRegister;

    EditText muname;
    EditText mpassword;

    DBHelper DB = null;
    TextView forgot_password;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);


    }

    @Override
    protected void onResume() {
        super.onResume();
        mRegister = (Button)findViewById(R.id.register);
        mLogin = (Button)findViewById(R.id.login);
        forgot_password = (TextView)findViewById(R.id.forgot_password);

        mRegister.setOnClickListener(this);
        mLogin.setOnClickListener(this);
        forgot_password.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch(v.getId())
        {

            case R.id.register:
                Intent i = new Intent(getBaseContext(), ActivityRegistration.class);
                startActivity(i);
                break;

            case R.id.forgot_password:
                LayoutInflater layoutInflater = LayoutInflater.from(v.getContext());
                View promptsView = layoutInflater.inflate(R.layout.forgot_password_view, null);
                AlertDialog.Builder builder = new AlertDialog.Builder(v.getContext());
                builder.setView(promptsView);
                final EditText fusername = (EditText) promptsView.findViewById(R.id.forgot_password_username);
                final EditText newPassword = (EditText) promptsView.findViewById(R.id.forgot_password_newpassword);
                final Button reset = (Button) promptsView.findViewById(R.id.reset_password);
                final Button cancel = (Button) promptsView.findViewById(R.id.cancel);

                builder.setCancelable(true);
                builder.setTitle("Forgot Password");
                final AlertDialog alertDialog = builder.create();
                alertDialog.show();
                reset.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (fusername.getText().length() > 0) {
                            if(newPassword.getText().length()>0)
                            {
                                DB = new DBHelper(getBaseContext());
                                SQLiteDatabase db = DB.getReadableDatabase();

                                Cursor mCursor = db.rawQuery("select * from register where username = '"+fusername.getText().toString()+"'", null);
                                int numberOfRows = mCursor.getCount();
                                if (numberOfRows > 0)
                                {
                                    mCursor.moveToFirst();
                                    db = DB.getWritableDatabase();
                                    ContentValues values = new ContentValues();
                                    values.put("password", newPassword.getText().toString());
                                    db.update(DBHelper.DATABASE_TABLE_NAME, values, "username= '"+fusername.getText().toString()+"'", null);
                                    Toast.makeText(Activitylogin.this, "Password reset succesfully", Toast.LENGTH_SHORT).show();
                                }
                                else {
                                    Toast.makeText(getApplicationContext(), "Invalid UserName", Toast.LENGTH_SHORT).show();
                                }



                            }
                            alertDialog.dismiss();
                        }
                    }
                });
                cancel.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        alertDialog.dismiss();
                    }
                });
                break;

            case R.id.login:

                muname = (EditText)findViewById(R.id.Ledituname);
                mpassword = (EditText)findViewById(R.id.Leditpw);

                String username = muname.getText().toString();
                String password = mpassword.getText().toString();



                if(username.equals("") || username == null)
                {
                    Toast.makeText(getApplicationContext(), "Please enter User Name", Toast.LENGTH_SHORT).show();
                }
                else if(password.equals("") || password == null)
                {
                    Toast.makeText(getApplicationContext(), "Please enter your Password", Toast.LENGTH_SHORT).show();
                }
                else
                {
                    boolean validLogin = validateLogin(username, password, getBaseContext());
                    if(validLogin)
                    {
                        //System.out.println("In Valid");
                        Intent in = new Intent(getBaseContext(), MainActivity.class);
                        in.putExtra("USERNAME", muname.getText().toString());
                        startActivity(in);
                        //finish();
                    }
                    else
                    {
                        Toast.makeText(Activitylogin.this, "Incorrect username or password", Toast.LENGTH_SHORT).show();
                    }
                   /* Intent intent = new Intent(this, MainActivity.class);
                    startActivity(intent);*/
                }
                break;

        }
    }

    private boolean validateLogin(String username, String password, Context baseContext)
    {
        DB = new DBHelper(getBaseContext());
        SQLiteDatabase db = DB.getReadableDatabase();

        String[] columns = {"_id"};

        String selection = "username=? AND password=?";
        String[] selectionArgs = {username,password};

        Cursor cursor = null;
        try{

            cursor = db.query(DBHelper.DATABASE_TABLE_NAME, columns, selection, selectionArgs, null, null, null);
            startManagingCursor(cursor);
        }
        catch(Exception e)

        {
            e.printStackTrace();
        }
        int numberOfRows = cursor.getCount();

        if(numberOfRows <= 0)
        {

            Toast.makeText(getApplicationContext(), "User Name and Password miss match..\nPlease Try Again", Toast.LENGTH_LONG).show();
            Intent intent = new Intent(getBaseContext(), Activitylogin.class);
            startActivity(intent);
            finish();
            return false;
        }


        return true;

    }

    public void onDestroy()
    {
        super.onDestroy();
        DB.close();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        System.exit(0);
    }
}
