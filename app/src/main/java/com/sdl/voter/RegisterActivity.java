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
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;

public class RegisterActivity extends AppCompatActivity
{

    private Button CreateAccountButton;
    private EditText InputStudentId , InputName , InputPhoneNumber, InputPassword;
    private ProgressDialog  loadingBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        CreateAccountButton = (Button) findViewById(R.id.register_registerbutton);
        InputStudentId = (EditText) findViewById(R.id.register_studentid_input);
        InputName = (EditText) findViewById(R.id.register_name_input);
        InputPhoneNumber = (EditText) findViewById(R.id.register_phone_number_input);
        InputPassword = (EditText) findViewById(R.id.register_password_input);
        loadingBar=new ProgressDialog(this);

        CreateAccountButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CreateAccount();
            }
        });
    }

            private void CreateAccount()
            {

                String studentid=InputStudentId.getText().toString();
                String name=InputName.getText().toString();
                String phoneno=InputPhoneNumber.getText().toString();
                String password=InputPassword.getText().toString();

                if (TextUtils.isEmpty(studentid))
                {
                    Toast.makeText(this,"Please write your studentid...",Toast.LENGTH_SHORT).show();
                }
                else if (TextUtils.isEmpty(name))
                {
                    Toast.makeText(this,"Please write your name...",Toast.LENGTH_SHORT).show();
                }
                else if (TextUtils.isEmpty(phoneno))
                {
                    Toast.makeText(this,"Please write your phoneno...",Toast.LENGTH_SHORT).show();
                }
                else if (TextUtils.isEmpty(password))
                {
                    Toast.makeText(this,"Please write your password...",Toast.LENGTH_SHORT).show();
                }
                else
                {
                    loadingBar.setTitle("Create Account");
                    loadingBar.setMessage("please wait,while we are checking are credentials.");
                    loadingBar.setCanceledOnTouchOutside(false);
                    loadingBar.show();

                    ValidatephoneNumber(studentid,name,phoneno,password);
                }

                }

    private void ValidatephoneNumber(final String studentid, final String name, final String phoneno, final String password)
    {
        final DatabaseReference RootRef;
        RootRef= FirebaseDatabase.getInstance().getReference();

        RootRef.addListenerForSingleValueEvent(new ValueEventListener()
        {
            public void onDataChange(@NonNull DataSnapshot dataSnapshot)
            {
                if (!(dataSnapshot.child("Users").child(phoneno).exists()))
                {
                    HashMap<String, Object> userdataMap = new HashMap<>();
                    userdataMap.put("phoneno", phoneno);
                    userdataMap.put("password", password);
                    userdataMap.put("name", name);
                    userdataMap.put("studentid", studentid);

                    RootRef.child("Users").child(phoneno).updateChildren(userdataMap)
                            .addOnCompleteListener(new OnCompleteListener<Void>()
                            {
                                @Override
                                public void onComplete(@NonNull Task<Void> task)
                                {
                                    if(task.isSuccessful())
                                    {
                                        Toast.makeText(RegisterActivity.this,"Congratulation,your account has been created.",Toast.LENGTH_SHORT).show();
                                        loadingBar.dismiss();

                                        Intent intent = new Intent(RegisterActivity.this, LoginActivity.class);
                                        startActivity(intent);
                                    }
                                    else
                                    {
                                        loadingBar.dismiss();
                                        Toast.makeText(RegisterActivity.this,"Network Error:please try again some time...",Toast.LENGTH_SHORT).show();
                                    }
                                }
                            });
                }
                else
                {
                    Toast.makeText(RegisterActivity.this,"This"+phoneno+"already exists.",Toast.LENGTH_SHORT).show();
                    loadingBar.dismiss();
                    Toast.makeText(RegisterActivity.this,"Please try again using another phonr number.",Toast.LENGTH_SHORT).show();

                    Intent intent = new Intent(RegisterActivity.this, MainActivity.class);
                    startActivity(intent);
                }
            }

           @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

           }
       });
    }
}




