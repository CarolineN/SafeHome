package com.example.caroline.safehome;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;

public class SignUp extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_sign_up, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public void ShowDetails(View view) {
        Intent intent = new Intent(this, firstPage.class);
        final EditText nameTextField = (EditText) findViewById(R.id.nameTextField);
        final EditText passwordTextField = (EditText) findViewById(R.id.PasswordTextField);
        final EditText emailTextField = (EditText) findViewById(R.id.emailTextView);
        final EditText homeLocationTextField = (EditText) findViewById(R.id.HomeTextField);
        final EditText phoneTextField = (EditText) findViewById(R.id.phoneTextField);
        String homeLocation = homeLocationTextField.getText().toString();
        String userName = nameTextField.getText().toString();
        String password = passwordTextField.getText().toString();
        String email = emailTextField.getText().toString();
        String phone = phoneTextField.getText().toString();

        intent.putExtra("UsersName", userName);
        intent.putExtra("password",password);
        intent.putExtra("email", email);
        intent.putExtra("homeLocation",homeLocation);
        intent.putExtra("phone",phone);
        startActivity(intent);
    }
}

