package com.example.palliativecareapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.DownloadManager;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
import java.util.List;


public class DisplayPostsActivity extends AppCompatActivity {

    private ListView mPostsListView;
    private DatabaseReference mDatabase;
    private List<Post> mPostsList;
    private PostListAdapter mAdapter;
    private StorageReference mStorage;
    private ProgressDialog mProgressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_display_posts);

        // Get the selected topic from the previous activity
        Intent intent = getIntent();
        String selectedTopic = intent.getStringExtra("SELECTED_TOPIC");

        // Set the activity title to the selected topic
        setTitle(selectedTopic);

        mPostsListView = findViewById(R.id.posts_list_view);
        mDatabase = FirebaseDatabase.getInstance().getReference().child("posts");
        mStorage = FirebaseStorage.getInstance().getReference();

        mPostsList = new ArrayList<>();
        mAdapter = new PostListAdapter(this, mPostsList);
        mPostsListView.setAdapter(mAdapter);

        mProgressDialog = new ProgressDialog(this);
        mProgressDialog.setMessage("Loading posts...");
        mProgressDialog.show();

        // Query the database to get posts related to the selected topic
        Query query = mDatabase.orderByChild("topic").equalTo(selectedTopic);
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                mPostsList.clear();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    Post post = snapshot.getValue(Post.class);
                    mPostsList.add(post);
                }
                mAdapter.notifyDataSetChanged();
                mProgressDialog.dismiss();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(DisplayPostsActivity.this, "Error loading posts", Toast.LENGTH_SHORT).show();
                mProgressDialog.dismiss();
            }
        });

        // Set a long click listener on the posts list view to download the post media
        mPostsListView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> adapterView, View view, int i, long l) {
                Post post = mPostsList.get(i);
                downloadPostMedia(post);
                return true;
            }
        });
    }

    private void downloadPostMedia(Post post) {
        // Get the post media type and file path from the post object
        String mediaType = post.getMediaType();
        String filePath = post.getFilePath();

        // Show a progress dialog while the media is being downloaded
        mProgressDialog.setMessage("Downloading " + mediaType + "...");
        mProgressDialog.show();

        // Download the media file from Firebase Storage
        mStorage.child(filePath).getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {
                // Media file downloaded successfully, create a new file in the Downloads folder
                DownloadManager downloadManager = (DownloadManager) getSystemService(Context.DOWNLOAD_SERVICE);
                Uri downloadUri = Uri.parse(uri.toString());
                DownloadManager.Request request = new DownloadManager.Request(downloadUri);
                request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
                request.setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, mediaType);
                downloadManager.enqueue(request);
                mProgressDialog.dismiss();
                Toast.makeText(DisplayPostsActivity.this, "Download started", Toast.LENGTH_SHORT).show();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                // Failed to download the media file
                mProgressDialog.dismiss();
                Toast.makeText(DisplayPostsActivity.this, "Failed to download " + mediaType, Toast.LENGTH_SHORT).show();
            }
        });
    }
}

