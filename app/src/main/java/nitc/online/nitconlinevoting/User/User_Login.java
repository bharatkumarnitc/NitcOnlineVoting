package nitc.online.nitconlinevoting.User;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
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

import nitc.online.nitconlinevoting.R;

public class User_Login extends AppCompatActivity {

    EditText User_Roll;
    Button User_Sign;
    ProgressDialog progressDialog;
    RequestQueue requestQueue;
    private SharedPreferences loginPreferences;
    private SharedPreferences.Editor loginPrefsEditor;

    private long back;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user__login);

        User_Roll = (EditText) findViewById(R.id.user_rollnumber);
        User_Sign = (Button) findViewById(R.id.user_submit);

        requestQueue = Volley.newRequestQueue(User_Login.this);
        loginPreferences=getSharedPreferences("User Email",MODE_PRIVATE);
        loginPrefsEditor=loginPreferences.edit();


        progressDialog=new ProgressDialog(this);
        User_Sign.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String RollNumber = User_Roll.getText().toString();

                if (TextUtils.isEmpty(RollNumber))
                    User_Roll.setError("Please Enter Nitc Roll Number");

                else {

                        if(RollNumber.length()<8)
                        {

                            Toast.makeText(getApplicationContext(),"Roll Number is Short",Toast.LENGTH_LONG).show();
                        }


                        else

                        {


                            progressDialog.setTitle("Processing...");
                            progressDialog.setMessage("Please wait...");
                            progressDialog.setCancelable(false);
                            progressDialog.setIndeterminate(true);
                            progressDialog.show();

                            UserLogin userLogin=new UserLogin(RollNumber, new Response.Listener<String>() {
                                @Override
                                public void onResponse(String response) {

                                    Log.i("Response",response);


                                    try
                                    {
                                        if(new JSONObject(response).get("success").equals("true"))
                                        {
                                            loginPrefsEditor.putString("Email",new JSONObject(response).getString("Email"));
                                            loginPrefsEditor.putString("Rollnumber",new JSONObject(response).getString("Roll"));
                                            loginPrefsEditor.commit();
                                            progressDialog.dismiss();
                                            startActivity(new Intent(getApplicationContext(),User_Email_verification.class));


                                        }

                                        else if(new JSONObject(response).get("success").equals("false"))
                                        {
                                            progressDialog.dismiss();
                                            Toast.makeText(getApplicationContext(),"Roll Number is Invalid",Toast.LENGTH_LONG).show();

                                        }
                                        else
                                        {

                                            Toast.makeText(getApplicationContext(),"Network Problem",Toast.LENGTH_LONG).show();
                                        }




                                    }
                                    catch (JSONException e)
                                    {
                                        e.printStackTrace();
                                    }


                                }
                            });

                            requestQueue.add(userLogin);
                        }


                }


            }
        });


    }

    class UserLogin extends StringRequest {

        final static String URL = "https://nitconlinevoting.000webhostapp.com/Online%20Voting/User/Login.php";
        private Map<String, String> par;

        public UserLogin(String Roll, Response.Listener<String> listener) {
            super(Method.POST, URL, listener, null);
            par = new HashMap<>();

            par.put("roll", Roll);
        }

        @Override
        protected Map<String, String> getParams() throws AuthFailureError {
            return par;


        }
    }
}
