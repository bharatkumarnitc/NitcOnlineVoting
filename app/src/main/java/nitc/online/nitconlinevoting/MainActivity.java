package nitc.online.nitconlinevoting;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import nitc.online.nitconlinevoting.User.User_Login;

public class MainActivity extends AppCompatActivity {


    Button Admin;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Admin=(Button)findViewById(R.id.student);

        Admin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                startActivity(new Intent(MainActivity.this, User_Login.class));
            }
        });
    }
}
