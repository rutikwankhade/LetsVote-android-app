package com.sdl.voter;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.sdl.voter.Model.Users;

public class LoginActivity extends AppCompatActivity
{

    private EditText InputPhoneno,InputPassword;
    private Button LoginButton;
    private ProgressDialog loadingBar;
    private TextView AdminLink, NotAdminLink;

    private String ParentDbName="Users";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        LoginButton = (Button) findViewById(R.id.login_btn);
        InputPassword = (EditText) findViewById(R.id.login_password_input);
        InputPhoneno = (EditText) findViewById(R.id.login_phone_number_input);
        AdminLink=(TextView) findViewById(R.id.admin_panel_link);
        NotAdminLink=(TextView) findViewById(R.id.not_admin_panel_link);
        loadingBar=new ProgressDialog(this);


        LoginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                LoginUser();
            }
        });

        AdminLink.setOnClickListener(new View.OnClickListener() {
                                   @Override
            public void onClick(View view)
                                   {
                                       LoginButton.setText("Login Admin");
                                       AdminLink.setVisibility(View.INVISIBLE);
                                       NotAdminLink.setVisibility(View.VISIBLE);
                                       ParentDbName="Admins";

                                   }
                                     });
        NotAdminLink.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view)
            {
                LoginButton.setText("Login");
                AdminLink.setVisibility(View.VISIBLE);
                NotAdminLink.setVisibility(View.INVISIBLE);
                ParentDbName="Users";
            }
        });

    }

    private void LoginUser()
    {
        String phoneno=InputPhoneno.getText().toString();
        String password=InputPassword.getText().toString();

         if (TextUtils.isEmpty(phoneno))
            {
                Toast.makeText(this,"Please write your phoneno...",Toast.LENGTH_SHORT).show();
            }
            else if (TextUtils.isEmpty(password))
            {
                Toast.makeText(this,"Please write your password...",Toast.LENGTH_SHORT).show();
            }
            else{
             loadingBar.setTitle("Login Account");
             loadingBar.setMessage("please wait,while we are checking are credentials.");
             loadingBar.setCanceledOnTouchOutside(false);
             loadingBar.show();


             AllowAccessToAccount(phoneno, password);
         }

            }

    private void AllowAccessToAccount(final String phoneno, final String password)
    {
        final DatabaseReference RootRef;
        RootRef= FirebaseDatabase.getInstance().getReference();

        RootRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot)
            {
                if (dataSnapshot.child(ParentDbName).child(phoneno).exists())
                {
                    Users usersData = dataSnapshot.child(ParentDbName).child(phoneno).getValue(Users.class);
                    if(usersData.getPhoneno().equals(phoneno))
                    {
 
                        if(usersData.getPassword().equals(password))
                        {
                            if(ParentDbName.equals("Admins"))
                            {
                                Toast.makeText(LoginActivity.this," Welcome Admin you are Logged in Successfully...",Toast.LENGTH_SHORT).show();
                                loadingBar.dismiss();

                                Intent intent = new Intent(LoginActivity.this, AdminAddNewActivity.class);
                                startActivity(intent);
                            }
                            else if(ParentDbName.equals("Users"))
                            {
                                Toast.makeText(LoginActivity.this,"Logged in Successfully...",Toast.LENGTH_SHORT).show();
                                loadingBar.dismiss();

                                Intent intent = new Intent(LoginActivity.this, HomeActivity.class);
                                startActivity(intent);
                            }
                        }
                        else
                        {
                            loadingBar.dismiss();
                            Toast.makeText(LoginActivity.this,"Password is incorrect..",Toast.LENGTH_SHORT).show();
                        }

                }
                }
                else
                {
                    Toast.makeText(LoginActivity.this, "Account with this" + phoneno + "number do not exist", Toast.LENGTH_SHORT).show();
                            loadingBar.dismiss();

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

}
