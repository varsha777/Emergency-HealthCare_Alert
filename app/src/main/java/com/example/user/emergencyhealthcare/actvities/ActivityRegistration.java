package com.example.user.emergencyhealthcare.actvities;

import android.app.DatePickerDialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.example.user.emergencyhealthcare.R;
import com.example.user.emergencyhealthcare.database.DBHelper;
import com.example.user.emergencyhealthcare.model.State;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ActivityRegistration extends AppCompatActivity implements View.OnClickListener {

    private Button mSubmit;
    private Button mCancel;

    private EditText mname;
    private TextView dob, tdob;
    private EditText mUsername;
    private EditText mPassword;
    private EditText mEmail;
    private EditText mphone;
    private EditText maddress;
    private EditText mbloodgroup;
    private EditText memergencycontactno1;
    private EditText memergencycontactno2;
    private DatePickerDialog fromDatePicker;
    private SimpleDateFormat simpleDateFormat;
    private TextInputLayout inputLayoutName, inputLayoutUserName,inputLayoutEmail, inputLayoutPhoneNumber, inputLayoutPassword,
    inputLayoutAddress, inputLayoutEmerCont1, inputLayoutEmercont2, inputLayoutBloodGr, inputLayoutDOB;

//    private Spinner mGender;
//    private String Gen;

    protected DBHelper DB = new DBHelper(ActivityRegistration.this);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        simpleDateFormat = new SimpleDateFormat("dd-MM-yyyy", Locale.ENGLISH);

    }

    @Override
    protected void onResume() {
        super.onResume();

        mSubmit = (Button)findViewById(R.id.submit);
        mSubmit.setOnClickListener(this);

        mCancel = (Button)findViewById(R.id.cancel);
        mCancel.setOnClickListener(this);

        mname = (EditText)findViewById(R.id.efname);
        dob = (TextView) findViewById(R.id.dob);
        tdob = (TextView) findViewById(R.id.vdob);

        mUsername = (EditText)findViewById(R.id.reuname);
        mPassword = (EditText)findViewById(R.id.repass);
        mEmail = (EditText)findViewById(R.id.eemail);
        mphone=(EditText)findViewById(R.id.pphoneno);
        maddress=(EditText)findViewById(R.id.paddress);
        mbloodgroup=(EditText)findViewById(R.id.pbldgrp);
        memergencycontactno1=(EditText)findViewById(R.id.pemc1);
        memergencycontactno2=(EditText)findViewById(R.id.pemc2);
        dob.setOnClickListener(this);

        inputLayoutName = (TextInputLayout) findViewById(R.id.tl1);
        inputLayoutUserName = (TextInputLayout) findViewById(R.id.tl2);
        inputLayoutEmail = (TextInputLayout) findViewById(R.id.tl4);
        inputLayoutPhoneNumber = (TextInputLayout) findViewById(R.id.tl5);
        inputLayoutPassword = (TextInputLayout) findViewById(R.id.tl3);
        inputLayoutAddress = (TextInputLayout) findViewById(R.id.tl6);
        inputLayoutBloodGr = (TextInputLayout) findViewById(R.id.tl7);
        inputLayoutEmerCont1 = (TextInputLayout) findViewById(R.id.tl8);
        inputLayoutEmercont2 = (TextInputLayout) findViewById(R.id.tl9);
        inputLayoutDOB = (TextInputLayout) findViewById(R.id.tl10);

        mname.addTextChangedListener(new MyTextWatcher(mname));
        mUsername.addTextChangedListener(new MyTextWatcher(mUsername));
        mEmail.addTextChangedListener(new MyTextWatcher(mEmail));
        mphone.addTextChangedListener(new MyTextWatcher(mphone));
        mPassword.addTextChangedListener(new MyTextWatcher(mPassword));
        maddress.addTextChangedListener(new MyTextWatcher(maddress));
        mbloodgroup.addTextChangedListener(new MyTextWatcher(mbloodgroup));
        memergencycontactno1.addTextChangedListener(new MyTextWatcher(memergencycontactno1));
        memergencycontactno2.addTextChangedListener(new MyTextWatcher(memergencycontactno2));
        dob.addTextChangedListener(new MyTextWatcher(dob));


        Calendar newCalender = Calendar.getInstance();
        fromDatePicker = new DatePickerDialog(this, new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                Calendar newDate = Calendar.getInstance();
                newDate.set(year,monthOfYear,dayOfMonth);
                //startDates = simpleDateFormat.parse(newDate.getStart_time());
                tdob.setText(simpleDateFormat.format(newDate.getTime()));
                tdob.setVisibility(View.VISIBLE);
            }
        },newCalender.get(Calendar.YEAR),newCalender.get(Calendar.MONTH),newCalender.get(Calendar.DAY_OF_MONTH));
    }

    @Override
    public void onClick(View v) {

        switch (v.getId()) {

            case R.id.cancel:
                Intent i = new Intent(getBaseContext(), Activitylogin.class);
                startActivity(i);
                //finish();
                break;

            case R.id.dob:
                Toast.makeText(ActivityRegistration.this, "DATE OF BIRTH", Toast.LENGTH_SHORT).show();
                fromDatePicker.show();
                break;

            case R.id.submit:

                if(!validateName())
                {
                    return;
                }

                if(!validateDOB())
                {
                    return;
                }
                if(!validateUserName())
                {
                    return;
                }
                if(!validatePassword())
                {
                    return;
                }
                if(!validateEmail())
                {
                    return;
                }
                if(!validatePhoneNumber())
                {
                    return;
                }
                if(!validateAddress())
                {
                    return;
                }
                if(!validateBloodGr())
                {
                    return;
                }
                if(!validateContact1())
                {
                    return;
                }
                if(!validateContact2())
                {
                    return;
                }

                hideKeyboard();
                addEntry(mname.getText().toString(), dob.getText().toString(), mUsername.getText().toString(), mPassword.getText().toString(),
                        mEmail.getText().toString(), mphone.getText().toString(),maddress.getText().toString(),mbloodgroup.getText().toString(),
                        memergencycontactno1.getText().toString(),memergencycontactno2.getText().toString());
                Intent i_register = new Intent(ActivityRegistration.this, Activitylogin.class);
                startActivity(i_register);
                finish();

        }

    }

    private void hideKeyboard() {
        View view = this.getCurrentFocus();
        if (view != null) {
            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }
    private class MyTextWatcher implements TextWatcher {

        private View view;

        private MyTextWatcher(View view) {
            this.view = view;
        }

        public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
        }

        public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
        }

        public void afterTextChanged(Editable editable) {
            switch (view.getId()) {
                case R.id.efname:
                    validateName();
                    break;
                case R.id.eemail:
                    validateEmail();
                    break;
                case R.id.reuname:
                    validateUserName();
                    break;
                case R.id.repass:
                    validatePassword();
                    break;
                case R.id.pphoneno:
                    validatePhoneNumber();
                    break;
                case R.id.pemc1:
                    validateContact1();
                    break;
                case R.id.pemc2:
                    validateContact2();
                    break;
                case R.id.paddress:
                    validateAddress();
                    break;
                case R.id.pbldgrp:
                    validateBloodGr();
                    break;
                case R.id.dob:
                    validateDOB();
                    break;
            }
        }
    }

    private boolean validateName() {
        if (mname.getText().toString().trim().isEmpty()) {
            try {
                inputLayoutName.setError("Enter Your Name");
            }
            catch (Exception e)
            {}
            requestFocus(mname);
            return false;
        } else {
            try {
                inputLayoutName.setError("");
                inputLayoutName.setErrorEnabled(false);
            }catch (Exception e){
            }
        }

        return true;
    }

    private boolean validateDOB() {
        if (tdob.getText().toString().trim().isEmpty()) {
            try {
                inputLayoutDOB.setError("Enter date of birth");
            }
            catch (Exception e)
            {}
            requestFocus(dob);
            return false;
        } else {
            try {
                inputLayoutDOB.setError("");
                inputLayoutUserName.setErrorEnabled(false);
            }catch (Exception e){
            }
        }

        return true;
    }
    private boolean validateUserName() {
        if (mUsername.getText().toString().trim().isEmpty()) {
            try {
                inputLayoutUserName.setError("Enter User Name");
            }
            catch (Exception e)
            {}
            requestFocus(mUsername);
            return false;
        } else {
            try {
                inputLayoutUserName.setError("");
                inputLayoutUserName.setErrorEnabled(false);
            }catch (Exception e){
            }
        }

        return true;
    }

    private boolean validateAddress() {
        if (maddress.getText().toString().trim().isEmpty()) {
            try {
                inputLayoutAddress.setError("Enter Address");
            }
            catch (Exception e)
            {}
            requestFocus(maddress);
            return false;
        } else {
            try {
                inputLayoutAddress.setError("");
                inputLayoutAddress.setErrorEnabled(false);
            }catch (Exception e){
            }
        }

        return true;
    }

    private boolean validateContact1() {
        String phoneno = memergencycontactno1.getText().toString();
        if (phoneno.isEmpty() || !isValidPhone(phoneno)) {
            try {
                inputLayoutEmerCont1.setError("Enter valid phone number");
            }
            catch (Exception e)
            {}
            requestFocus(memergencycontactno1);
            return false;
        } else {
            try {
                inputLayoutEmerCont1.setError("");
                inputLayoutEmerCont1.setErrorEnabled(false);
            }catch (Exception e){
            }
        }

        return true;
    }

    private boolean validateContact2() {
        String phoneno = memergencycontactno2.getText().toString();
        if (phoneno.isEmpty() || !isValidPhone(phoneno)) {
            try {
                inputLayoutEmercont2.setError("Enter valid phone number");
            }
            catch (Exception e)
            {}
            requestFocus(memergencycontactno2);
            return false;
        } else {
            try {
                inputLayoutEmercont2.setError("");
                inputLayoutEmercont2.setErrorEnabled(false);
            }catch (Exception e){
            }
        }

        return true;
    }

    private boolean validateBloodGr() {
        String bgroup = mbloodgroup.getText().toString();
        if (bgroup.isEmpty() || !isValidBloodGr(bgroup)) {
            try {
                inputLayoutBloodGr.setError("Enter valid blood group");
            }
            catch (Exception e)
            {}
            requestFocus(mbloodgroup);
            return false;
        } else {
            try {
                inputLayoutBloodGr.setError("");
                inputLayoutBloodGr.setErrorEnabled(false);
            }catch (Exception e){
            }
        }

        return true;
    }

    private boolean validatePhoneNumber() {
        String phoneno = mphone.getText().toString();
        if (phoneno.isEmpty() || !isValidPhone(phoneno)) {
            try {
                inputLayoutPhoneNumber.setError("Enter valid phone number");
            }
            catch (Exception e)
            {}
            requestFocus(mphone);
            return false;
        } else {
            try {
                inputLayoutPhoneNumber.setError("");
                inputLayoutPhoneNumber.setErrorEnabled(false);
            }catch (Exception e){
            }
        }

        return true;
    }

    private boolean validatePassword() {
        String password = mPassword.getText().toString();
        if(password.isEmpty() || !isValidPassword(password)){
            try {
                inputLayoutPassword.setError("Password length should be minimun 6 charactes");
            }
            catch (Exception e)
            {}
            requestFocus(mPassword);
            return false;
        } else {
            try {
                inputLayoutPassword.setError("");
                inputLayoutPassword.setErrorEnabled(false);
            }catch (Exception e){
            }
        }

        return true;
    }

    private boolean isValidPassword(String password) {

        if(password.length() >= 6)
            return true;
        else
            return false;
    }

    private boolean isValidPhone(String phoneno) {

        if(phoneno.length() != 10)
        {
            return false;
        }
        else
            return android.util.Patterns.PHONE.matcher(phoneno).matches();
    }

    private boolean isValidBloodGr(String bgroup) {

        if(bgroup.equals("A+") || bgroup.equals("A+") || bgroup.equals("B+") || bgroup.equals("B+ve") || bgroup.equals("O+")
                || bgroup.equals("O+ve") || bgroup.equals("AB+") || bgroup.equals("AB+ve") || bgroup.equals("O-") || bgroup.equals("O-ve")
                || bgroup.equals("A-") || bgroup.equals("A-ve") || bgroup.equals("B-") || bgroup.equals("B-ve") || bgroup.equals("AB-ve")
                || bgroup.equals("AB-"))
        {
            return true;
        }
        else {
            return false;
        }
    }


    private boolean validateEmail() {
        String email = mEmail.getText().toString().trim();
        if (email.isEmpty() || !isValidEmail(email)) {
            try {
                inputLayoutEmail.setError("Enter valid email address");
            }
            catch (Exception e)
            {}
            requestFocus(inputLayoutEmail);
            return false;
        } else {
            try {
                inputLayoutEmail.setError("");
                inputLayoutEmail.setErrorEnabled(false);
            }catch (Exception e){
            }
        }

        return true;
    }

    private static boolean isValidEmail(String email) {
    //    return !TextUtils.isEmpty(email) && android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches();
        String EMAIL_PATTERN = "^[_A-Za-z0-9-\\+]+(\\.[_A-Za-z0-9-]+)*@"
                + "[A-Za-z0-9-]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$";

        Pattern pattern = Pattern.compile(EMAIL_PATTERN);
        Matcher matcher = pattern.matcher(email);
        return matcher.matches();
    }

    private void requestFocus(View view) {
        if (view.requestFocus()) {
            getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
        }
    }


    public void onDestroy()
    {
        super.onDestroy();
        DB.close();
    }



    private void addEntry(String name, String dob, String uname, String pass, String email, String phoneno, String addr,
                          String bloodgr, String contact1, String contact2)
    {

        SQLiteDatabase db = DB.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put("name", name);
        values.put("dob", dob);
        values.put("username", uname);
        values.put("password", pass);
        values.put("email", email);
        values.put("phoneno", phoneno);
        values.put("address", addr);
        values.put("bloodgroup", bloodgr);
        values.put("econtact1", contact1);
        values.put("econtact2", contact2);

        try
        {
            db.insert(DBHelper.DATABASE_TABLE_NAME, null, values);

            Toast.makeText(getApplicationContext(), "your details submitted Successfully...", Toast.LENGTH_SHORT).show();
        }
        catch(Exception e)
        {
            e.printStackTrace();
        }
    }
}
