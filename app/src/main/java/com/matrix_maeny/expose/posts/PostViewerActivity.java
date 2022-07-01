package com.matrix_maeny.expose.posts;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.text.Html;
import android.view.View;

import com.google.firebase.FirebaseApp;
import com.google.firebase.appcheck.FirebaseAppCheck;
import com.google.firebase.appcheck.safetynet.SafetyNetAppCheckProviderFactory;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.matrix_maeny.expose.R;
import com.matrix_maeny.expose.UserModel;
import com.matrix_maeny.expose.comments.CommentViewerActivity;
import com.matrix_maeny.expose.databinding.ActivityPostViewerBinding;
import com.matrix_maeny.expose.fragments.profile.ProfileActivity;
import com.squareup.picasso.Picasso;

import java.util.Objects;

public class PostViewerActivity extends AppCompatActivity {

    private ActivityPostViewerBinding binding;


    public static PostModel model = null;
    private FirebaseDatabase database;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityPostViewerBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        setSupportActionBar(binding.pvToolbar);
        Objects.requireNonNull(getSupportActionBar()).setTitle(getString(R.string.app_name));

        FirebaseApp.initializeApp(PostViewerActivity.this);
        FirebaseAppCheck firebaseAppCheck = FirebaseAppCheck.getInstance();
        firebaseAppCheck.installAppCheckProviderFactory(
                SafetyNetAppCheckProviderFactory.getInstance());


        database = FirebaseDatabase.getInstance();

        if (model != null) {
            setPostContent();
        }

        binding.pvUserIv.setOnClickListener(v -> {
            ProfileActivity.profileUserId = model.getUserUid();
            startActivity(new Intent(PostViewerActivity.this, ProfileActivity.class));
        });
    }


    private void setPostContent() {
        setUserDetails();

        setPostDetails();

        setRecipeContent();


    }

    private void setRecipeContent() {


        binding.pvProcedureTv.setText(model.getContent());

        String headings = "<u><b>" + getString(R.string.any_other_references) + "</b></u>";
        binding.pvAdInsHeadingTv.setText(Html.fromHtml(headings));

        if (model.getReferences().equals("")) {
            binding.addInsLayout2.setVisibility(View.GONE);
        } else {
            binding.pvAddInsTv.setText(model.getReferences());
        }


    }

    @SuppressLint("SetTextI18n")
    private void setPostDetails() {
        Picasso.get().load(model.getImageUrl()).into(binding.pvPostIv);
        binding.pvPostTitleTv.setText(model.getTitle());
        binding.pvPostTagTv.setText(model.getTagLine());
        binding.pvDateTv.setText(Html.fromHtml("shared on <b>"+model.getLocalDate()+"</b>"));

    }

    private void setUserDetails() {
        setProfilePic();

        binding.pvUsernameTv.setText(model.getUsername());

    }


    private void setProfilePic() {
        database.getReference().child("Users").child(Objects.requireNonNull(model.getUserUid()))
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        UserModel model = snapshot.getValue(UserModel.class);
                        if (model != null) {
                            try {

                                Picasso.get().load(model.getProfilePicUrl()).placeholder(R.drawable.profile_pic).into(binding.pvUserIv);

                            } catch (Exception e) {
                                e.printStackTrace();
                                binding.pvUserIv.setImageResource(R.drawable.profile_pic);
                            }
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
    }

    @Override
    protected void onStart() {
        super.onStart();

        FirebaseApp.initializeApp(PostViewerActivity.this);
        FirebaseAppCheck firebaseAppCheck = FirebaseAppCheck.getInstance();
        firebaseAppCheck.installAppCheckProviderFactory(
                SafetyNetAppCheckProviderFactory.getInstance());
    }
}