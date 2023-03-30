package com.example.palliativecareapp;

import android.content.ClipData;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.VideoView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.palliativecareapp.R;

import java.util.ArrayList;

public class PatientActivity extends AppCompatActivity {

    private TextView postTitle;
    private TextView postContent;
    private ImageView postPhoto;
    private VideoView postVideo;
    private ListView lvTopics;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_patient);
        lvTopics = findViewById(R.id.lv_topics);

                // Define the list of topics
                ArrayList<String> topics = new ArrayList<>();
                topics.add("Topic 1");
                topics.add("Topic 2");
                topics.add("Topic 3");
                // Add more topics as per your requirements

                // Create an adapter for the list of topics
                ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, topics);

                // Set the adapter for the list view
                lvTopics.setAdapter(adapter);

                // Set a click listener for the list items
                lvTopics.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        String selectedTopic = topics.get(position);

                        Intent intent = new Intent(PatientActivity.this, DisplayPostsActivity.class);
                        intent.putExtra("SELECTED_TOPIC", selectedTopic);
                        startActivity(intent);
                    }
                });
            }
        }




/* if (videoUri != null) {
                    mVideoView.setVisibility(View.VISIBLE);
                    mVideoView.setVideoURI(videoUri);
                    mThumbnailView.setVisibility(View.GONE);
                    mVideoView.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                        @Override
                        public void onPrepared(MediaPlayer mp) {
                            mPlayButton.setVisibility(View.VISIBLE);
                        }
                    });
                    mPlayButton.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            mVideoView.start();
                            mPlayButton.setVisibility(View.GONE);
                        }
                    });
                } else if (thumbnail != 0) {
                    mThumbnailView.setVisibility(View.VISIBLE);
                    mThumbnailView.setImageResource(thumbnail);
                    mVideoView.setVisibility(View.GONE);
                } else {
                    mThumbnailView.setVisibility(View.GONE);
                    mVideoView.setVisibility(View.GONE);
                }*/



