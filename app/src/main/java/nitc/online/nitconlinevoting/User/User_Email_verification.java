package nitc.online.nitconlinevoting.User;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

import nitc.online.nitconlinevoting.MainActivity;
import nitc.online.nitconlinevoting.R;

public class User_Email_verification extends AppCompatActivity {

    EditText email,otp;
    String UserEmail;
    Button submit,verify;
    SharedPreferences pref;
    SharedPreferences.Editor editor;
    RequestQueue requestQueue;
    ProgressDialog progressDialog;
    private static long backButtonCount;

    //boolean doubleBackToExitPressedOnce = false;
    @Override
    public void onBackPressed()
    {
        if(backButtonCount >= 1)
        {
            Intent it = new Intent(getApplicationContext(), MainActivity.class);
            it.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(it);
            finish();
        }
        else
        {
            Toast.makeText(this, "Press the back button once again to close the application.", Toast.LENGTH_SHORT).show();
            backButtonCount++;
        }
    }




    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        MenuInflater menuInflater=getMenuInflater();
        menuInflater.inflate(R.menu.commonmenu,menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id=item.getItemId();
        if(id==R.id.logout)
        {

            Intent it = new Intent(getApplicationContext(), MainActivity.class);
            it.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(it);
            finish();
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user__email_verification);



        pref=getSharedPreferences("User Email",MODE_PRIVATE);
        editor=pref.edit();
        UserEmail=pref.getString("Email",null);

        email=(EditText)findViewById(R.id.email);
        submit=(Button)findViewById(R.id.submit);
        verify=(Button)findViewById(R.id.verify);
        otp=(EditText)findViewById(R.id.otp);

        email.setText(UserEmail);
        email.setEnabled(false);

        requestQueue= Volley.newRequestQueue(User_Email_verification.this);
        progressDialog=new ProgressDialog(this);
        progressDialog.setTitle("Processing...");
        progressDialog.setMessage("Please wait...");
        progressDialog.setCancelable(false);
        progressDialog.setIndeterminate(true);


        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                progressDialog.show();
               SendOtp sendotp=new SendOtp(UserEmail, new Response.Listener<String>() {
                  @Override
                  public void onResponse(String response) {

                      Log.i("Response",response);

                      try
                      {

                          if(new JSONObject(response).getString("success").equals("true"))
                          {
                              progressDialog.dismiss();
                              Toast.makeText(User_Email_verification.this,"Otp Send your Email Id",Toast.LENGTH_LONG).show();

                          }
                          else
                          {

                               Toast.makeText(User_Email_verification.this,"Network Problem",Toast.LENGTH_LONG).show();



                          }


                      }

                      catch (JSONException  e)
                      {
                          e.printStackTrace();
                      }
                  }
              });

                requestQueue.add(sendotp);


            }
        });



        verify.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {




                String OTP=otp.getText().toString();

                if(TextUtils.isEmpty(OTP))
                {

                    otp.setError("Please Enter the OTP");

                }
                else
                {

                    progressDialog.show();
                    StudentVerify studentverify=new StudentVerify(UserEmail, OTP, new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response)
                        {


                            Log.i("Response",response);

                            try
                            {

                                if(new JSONObject(response).getString("success").equals("true"))
                                {
                                    progressDialog.dismiss();
                                    Toast.makeText(User_Email_verification.this,"Otp Verified",Toast.LENGTH_LONG).show();

                                    startActivity(new Intent(User_Email_verification.this,Voting_Page.class));

                                }
                                else if(new JSONObject(response).getString("success").equals("false"))
                                {
                                    progressDialog.dismiss();

                                    Toast.makeText(getApplicationContext(),"Invalid OTP",Toast.LENGTH_LONG).show();


                                }
                                else
                                {
                                    Toast.makeText(User_Email_verification.this,"Network Problem",Toast.LENGTH_LONG).show();

                                }

                            }

                            catch (JSONException  e)
                            {
                                e.printStackTrace();
                            }

                        }
                    });


                    requestQueue.add(studentverify);




                }
            }
        });

    }

    class  SendOtp extends StringRequest {

       private final static String URL="https://nitconlinevoting.000webhostapp.com/Online%20Voting/Mail/mail_test.php";
       Map<String,String>par;
        public SendOtp(String Email, Response.Listener<String> listener) {
            super(Method.POST, URL, listener, null);
            par=new HashMap<>();
            par.put("email",Email);
        }

        @Override
        protected Map<String, String> getParams() throws AuthFailureError
        {
            return par;
        }
    }



    class StudentVerify extends StringRequest
    {
        private final static  String URL="https://nitconlinevoting.000webhostapp.com/Online%20Voting/User/OtpVerify.php";
        Map<String,String>par;
        public StudentVerify(String Email,String Otp,  Response.Listener<String> listener) {
            super(Method.POST, URL, listener,null);

            par=new HashMap<>();
            par.put("email",Email);
            par.put("otp",Otp);
        }


        @Override
        protected Map<String, String> getParams() throws AuthFailureError {
            return par;
        }
    }


    private void finishscreen() {
        this.finish();
    }


}
