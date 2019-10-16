package com.sdl.voter;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.HashMap;

public class AdminAddNewActivity extends AppCompatActivity {
    private String  Cname , description, div;
    private String downloadImageUrl;
    private ImageView InputCandidatePhoto;
    private Button AddNewCandidateButton;
    private EditText InputCandidateName, InputCandidateDiv, InputCandidateDescription;
private static final int GalleryPick=1;
private Uri ImageUri;
private Uri getDownloadUrl;
private StorageReference candidateImagesRef;
private DatabaseReference CandidateRef;
    private ProgressDialog loadingBar;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_add_new);

        candidateImagesRef = (StorageReference) FirebaseStorage.getInstance().getReference().child("candidate images");
        candidateImagesRef = (StorageReference) FirebaseStorage.getInstance().getReference().child("candidate images");
        CandidateRef= FirebaseDatabase.getInstance().getReference().child("candidates");

        AddNewCandidateButton= (Button) findViewById(R.id.add_candidate);
        InputCandidatePhoto=(ImageView) findViewById(R.id.candidate_photo);
        InputCandidateName=(EditText) findViewById(R.id.candidate_name);
        InputCandidateDiv=(EditText) findViewById(R.id.candidate_div);
        InputCandidateDescription=(EditText) findViewById(R.id.candidate_description);
        loadingBar=new ProgressDialog(this);


       // Toast.makeText(this, "Welcome admin", Toast.LENGTH_SHORT).show();

        InputCandidatePhoto.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                OpenGallery();
            }
        });

        AddNewCandidateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                ValdateCandidateData();
            }
        });


    }
    private void OpenGallery()
    {
        Intent galleryIntent =new Intent();
        galleryIntent.setAction(Intent.ACTION_GET_CONTENT);
        galleryIntent.setType("image/*");
        startActivityForResult(galleryIntent, GalleryPick);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode==GalleryPick  && resultCode==RESULT_OK  && data!=null)
        {
            ImageUri= data.getData();
            InputCandidatePhoto.setImageURI(ImageUri);

        }
    }

    private void  ValdateCandidateData()
    {

        Cname=InputCandidateName.getText().toString();
        div=InputCandidateDiv.getText().toString();
        description=InputCandidateDescription.getText().toString();

        if(ImageUri==null) {
            Toast.makeText(this, "Product image is necessary", Toast.LENGTH_SHORT).show();
        }
        else if(TextUtils.isEmpty(Cname))
        {
            Toast.makeText(this, "Please write candidate name", Toast.LENGTH_SHORT).show();
        }
        else if(TextUtils.isEmpty(div))
        {
            Toast.makeText(this, "Please write candidate division", Toast.LENGTH_SHORT).show();
        }
        else if(TextUtils.isEmpty(description))
        {
            Toast.makeText(this, "Please write candidate description", Toast.LENGTH_SHORT).show();
        }
        else
        {
            StoreCandidateInformation();
        }

    }

    private void StoreCandidateInformation()
    {
        loadingBar.setTitle("Adding new candidate");
        loadingBar.setMessage("please wait,while we are adding new candidate.");
        loadingBar.setCanceledOnTouchOutside(false);
        loadingBar.show();





        final StorageReference filePath = candidateImagesRef.child( ImageUri.getLastPathSegment() + ".jpg");
       final UploadTask  uploadTask=  filePath.putFile(ImageUri);

       uploadTask.addOnFailureListener(new OnFailureListener() {
           @Override
           public void onFailure(@NonNull Exception e)
           {
               String message=e.toString();
               Toast.makeText(AdminAddNewActivity.this, "Error: " + message, Toast.LENGTH_SHORT);
               loadingBar.dismiss();
           }
       }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
           @Override
           public void onSuccess(UploadTask.TaskSnapshot taskSnapshot)
           {
               Toast.makeText(AdminAddNewActivity.this, "Image uploaded Successfully", Toast.LENGTH_SHORT);


               Task <Uri>  urlTask = uploadTask.  continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>()
               {
                   @Override
                   public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception
                   {
                       if (!task.isSuccessful())
                       {
                           throw task.getException();
                       }

                       downloadImageUrl=filePath.getDownloadUrl().toString();
                       return filePath.getDownloadUrl();
                   }

           }).addOnCompleteListener(new OnCompleteListener<Uri>()
           {
                   @Override
                   public void onComplete(@NonNull Task<Uri> task)
           {
                       if (task.isSuccessful())
                       {
                           Toast.makeText(AdminAddNewActivity.this, "getting candidate Image url Successfully", Toast.LENGTH_SHORT).show();

                           SaveCandidateInfoToDatabase();
                       }
                   }
           });
       }
    });
}

    private void SaveCandidateInfoToDatabase()
     {
         HashMap<String, Object> CandidateMap= new HashMap<>();
         CandidateMap.put("image", downloadImageUrl);
         CandidateMap.put("name", Cname);
         CandidateMap.put("division", div);
         CandidateMap.put("description", description);

         CandidateRef.updateChildren(CandidateMap)
                 .addOnCompleteListener(new OnCompleteListener<Void>()
                 {

                     @Override
                     public void onComplete(@NonNull Task<Void> task)
                     {
                         if(task.isSuccessful())
                         {
                             Intent intent = new Intent(AdminAddNewActivity.this, AdminAddNewActivity.class);
                             startActivity(intent);

                             loadingBar.dismiss();
                             Toast.makeText(AdminAddNewActivity.this, "candidate is added successfully", Toast.LENGTH_SHORT);
                         }
                         else
                         {  loadingBar.dismiss();
                             String message=task.getException().toString();
                             Toast.makeText(AdminAddNewActivity.this, "Error:"+ message, Toast.LENGTH_SHORT);
                         }
                     }

                 });
     }








}
