package com.example.palliativecareapp;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.webkit.WebView;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.VideoView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;


public class PostManagement extends AppCompatActivity {

    private DatabaseReference mDatabase;
    private List<Post> mPostsList;
    private PostsListAdapter mAdapter;
    private ProgressDialog mProgressDialog;
    private String mTopicId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post_management);

        mTopicId = getIntent().getStringExtra("TOPIC_ID");
        mDatabase = FirebaseDatabase.getInstance().getReference().child("topics").child(mTopicId).child("posts");

        mPostsList = new ArrayList<>();
        mAdapter = new PostsListAdapter(this, mPostsList);

        ListView postsListView = findViewById(R.id.posts_list_view);
        postsListView.setAdapter(mAdapter);
        postsListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Post post = mPostsList.get(i);
                Intent intent = new Intent(PostManagement.this, PostDetailActivity.class);
                intent.putExtra("POST_ID", post.getId());
                startActivity(intent);
            }
        });

        mProgressDialog = new ProgressDialog(this);
        mProgressDialog.setMessage("Loading posts...");
        mProgressDialog.show();

        mDatabase.addValueEventListener(new ValueEventListener() {
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
                Toast.makeText(PostManagement.this, "Error loading posts", Toast.LENGTH_SHORT).show();
                mProgressDialog.dismiss();
            }
        });

        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
               // showAddPostDialog();
            }
        });
    }
    private static final int REQUEST_IMAGE = 1;
    private static final int REQUEST_VIDEO = 2;
    private static final int REQUEST_PDF = 3;

    private EditText titleEditText;
    private EditText contentEditText;

    private void showAddPostDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Add Post");

        View view = LayoutInflater.from(this).inflate(R.layout.dialog_add_post, null);
        titleEditText = view.findViewById(R.id.title_edit_text);
        contentEditText = view.findViewById(R.id.content_edit_text);
        final Spinner mediaTypeSpinner = view.findViewById(R.id.media_type_spinner);

        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.media_types_array, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        mediaTypeSpinner.setAdapter(adapter);

        builder.setView(view);
        builder.setPositiveButton("Add", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                String title = titleEditText.getText().toString();
                String content = contentEditText.getText().toString();
                String mediaType = mediaTypeSpinner.getSelectedItem().toString();

                if (!TextUtils.isEmpty(title) && !TextUtils.isEmpty(content)) {
                    if (mediaType.equals("Text")) {
                        addPost(title, content, null, null, null);
                    } else if (mediaType.equals("Image")) {
                        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                        startActivityForResult(intent, REQUEST_IMAGE);
                    } else if (mediaType.equals("Video")) {
                        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Video.Media.EXTERNAL_CONTENT_URI);
                        startActivityForResult(intent, REQUEST_VIDEO);
                    } else if (mediaType.equals("PDF")) {
                        Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
                        intent.addCategory(Intent.CATEGORY_OPENABLE);
                        intent.setType("application/pdf");
                        startActivityForResult(intent, REQUEST_PDF);
                    }
                }
            }
        });
        builder.setNegativeButton("Cancel", null);

        builder.show();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK && data != null) {
            if (requestCode == REQUEST_IMAGE) {
                Uri imageUri = data.getData();
                String filePath = getFilePathFromUri(imageUri);
                addPost(titleEditText.getText().toString(), contentEditText.getText().toString(), "Image", filePath, null);
            } else if (requestCode == REQUEST_VIDEO) {
                Uri videoUri = data.getData();
                String filePath = getFilePathFromUri(videoUri);
                addPost(titleEditText.getText().toString(), contentEditText.getText().toString(), "Video", filePath, null);
            } else if (requestCode == REQUEST_PDF) {
                Uri pdfUri = data.getData();
                String filePath = getFilePathFromUri(pdfUri);
                addPost(titleEditText.getText().toString(), contentEditText.getText().toString(), "PDF", filePath, null);
            }
        }
    }

    private String getFilePathFromUri(Uri uri) {
        String[] projection = {MediaStore.Images.Media.DATA};
        Cursor cursor = getContentResolver().query(uri, projection, null, null, null);
        int columnIndex = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
        cursor.moveToFirst();
        String filePath = cursor.getString(columnIndex);
        cursor.close();
        return filePath;
    }

    private void addPost(String title, String content, String mediaType, String filePath, String author) {
        // create a new post object and add it to the database
        String postId = mDatabase.push().getKey();
        String topicId = getIntent().getStringExtra("topic_id");
        String date = DateFormat.getDateTimeInstance().format(new Date());
        Post post;
        if (mediaType.equals("Text")) {
            post = new Post(postId, topicId, title, content, mediaType, null, null, null, null, author, date);
        } else if (mediaType.equals("Image")) {
            post = new Post(postId, topicId, title, content, mediaType, filePath, null, null, null, author, date);
        } else if (mediaType.equals("Video")) {
            post = new Post(postId, topicId, title, content, mediaType, null, null, filePath, null, author, date);
        } else if (mediaType.equals("PDF")) {
            post = new Post(postId, topicId, title, content, mediaType, null, null, null, filePath, author, date);
        } else {
            throw new IllegalArgumentException("Invalid media type: " + mediaType);
        }
        mDatabase.child("posts").child(postId).setValue(post);
    }


}



