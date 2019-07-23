package nitc.online.nitconlinevoting.User;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

import nitc.online.nitconlinevoting.MainActivity;
import nitc.online.nitconlinevoting.R;

public class Voting_Page extends AppCompatActivity {

    Spinner spinner1,spinner2;
    String pos,RollNumber,posname;
    Button vote;
    RequestQueue requestQueue;
    SharedPreferences pref;
    SharedPreferences.Editor editor;
    long backButtonCount;
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

        getMenuInflater().inflate(R.menu.commonmenu,menu);
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
        setContentView(R.layout.activity_voting__page);

        pref=getSharedPreferences("User Email",MODE_PRIVATE);
        editor=pref.edit();
        RollNumber=pref.getString("Rollnumber",null);



        requestQueue= Volley.newRequestQueue(Voting_Page.this);
        spinner1=(Spinner)findViewById(R.id.setposition);
        spinner2=(Spinner)findViewById(R.id.setcandidate);
        vote=(Button)findViewById(R.id.vote);
        Position();

        VotingArea();


        spinner1.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                selectProductName(parent.getItemAtPosition(position).toString());



            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        spinner2.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                posname=parent.getItemAtPosition(position).toString();


            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

    }

    private void VotingArea()
    {

        vote.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String localrollnumber=RollNumber;
                String localposname=pos;
                String localcanname=posname;


                FinalVotingArea finalvotingarea=new FinalVotingArea(localrollnumber, localposname, localcanname, new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {

                        Log.i("Response",response);


                        try
                        {
                            if(new JSONObject(response).get("status").equals("register"))
                            {


                                Toast.makeText(getApplicationContext(),"Thanks for Voting",Toast.LENGTH_LONG).show();
                                //startActivity(new Intent(getApplicationContext(),User_Email_verification.class));


                            }

                            else if(new JSONObject(response).get("status").equals("duplicate"))
                            {

                                Toast.makeText(getApplicationContext(),"You Already Vote",Toast.LENGTH_LONG).show();

                            }




                        }
                        catch (JSONException e)
                        {
                            e.printStackTrace();
                        }
                    }
                });

               requestQueue.add(finalvotingarea);


            }
        });


    }


    public void Position()
    {

        getJSONProductCategory("https://nitconlinevoting.000webhostapp.com/Online%20Voting/User/PositionList.php");

    }



    private void getJSONProductCategory(final String urlWebService) {

        class GetJSON extends AsyncTask<Void, Void, String> {

            @Override
            protected void onPreExecute() {
                super.onPreExecute();
            }

            @Override
            protected String doInBackground(Void... voids)
            {
                try {
                    URL url = new URL(urlWebService);
                    HttpURLConnection con = (HttpURLConnection) url.openConnection();
                    StringBuilder sb = new StringBuilder();
                    BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(con.getInputStream()));
                    String json;
                    while ((json = bufferedReader.readLine()) != null) {
                        sb.append(json + "\n");
                    }
                    return sb.toString().trim();
                } catch (Exception e) {
                    return null;
                }
            }

            @Override
            protected void onPostExecute(String s) {
                super.onPostExecute(s);
                try {
                    loadIntoListView(s);
                } catch (JSONException e)
                {
                    e.printStackTrace();
                }
            }


        }
        GetJSON getJSON = new GetJSON();
        getJSON.execute();
    }

    private void loadIntoListView(String json) throws JSONException {
        JSONArray jsonArray = new JSONArray(json);
        String[] heroes = new String[jsonArray.length()];
        for (int i = 0; i < jsonArray.length(); i++) {
            JSONObject obj = jsonArray.getJSONObject(i);
            heroes[i] = obj.getString("CategoryName");
        }
        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, heroes);
        spinner1.setAdapter(arrayAdapter);
    }


    public void selectProductName(String product)
    {

         pos=product;
        ProductList productList=new ProductList(product,new Response.Listener<String>(){
            @Override

            public void onResponse(String response) {
                Log.i("Response", response);
                try
                {
                    JSONArray jsonArray=new JSONArray(response);
                    JSONObject jsonObject=null;
                    String[] heroes = new String[jsonArray.length()];
                    for(int i=0;i<jsonArray.length();i++)
                    {
                        jsonObject=jsonArray.getJSONObject(i);
                        heroes[i] = jsonObject.getString("Candidate_Name");

                    }
                    ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(Voting_Page.this, android.R.layout.simple_list_item_1, heroes);
                    spinner2.setAdapter(arrayAdapter);
                }
                catch (Exception e)
                {
                    e.printStackTrace();
                }

            }


        });

        requestQueue.add(productList);





    }


    public class ProductList extends StringRequest {

        private static final String REGISTER_URL = "https://nitconlinevoting.000webhostapp.com/Online%20Voting/User/getCandidate.php";
        private Map<String, String> parameters;

        public ProductList(String Id,Response.Listener<String> listener) {
            super(Method.POST, REGISTER_URL, listener, null);
            parameters = new HashMap<>();
            parameters.put("pos",Id);
        }

        @Override
        protected Map<String, String> getParams() throws AuthFailureError
        {
            return parameters;
        }
    }



    class FinalVotingArea extends StringRequest {
         private final static String URL="https://nitconlinevoting.000webhostapp.com/Online%20Voting/User/VotingPage.php";
         Map<String,String>par;


        public FinalVotingArea(String Roll,String CandaditePos,String Candidatename, Response.Listener<String> listener) {
            super(Method.POST, URL, listener,null);
            par=new HashMap<>();
            par.put("roll",Roll);
            par.put("pos",CandaditePos);
            par.put("posname",Candidatename);


        }

        @Override
        protected Map<String, String> getParams() throws AuthFailureError
        {
            return par;
        }
    }



}
