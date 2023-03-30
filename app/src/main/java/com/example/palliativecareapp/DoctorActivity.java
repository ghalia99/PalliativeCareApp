package com.example.palliativecareapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;


public class DoctorActivity extends AppCompatActivity {

            private DatabaseReference mDatabase;
            private List<Topic> mTopicsList;
            private TopicsListAdapter mAdapter;
            private ProgressDialog mProgressDialog;
            private EditText mTitleEditText, mDescriptionEditText;
            private View mLongPressedView;
            @Override
            protected void onCreate(Bundle savedInstanceState) {
                super.onCreate(savedInstanceState);
                setContentView(R.layout.activity_doctor);

                mDatabase = FirebaseDatabase.getInstance().getReference().child("topics");
                mTopicsList = new ArrayList<>();
                mAdapter = new TopicsListAdapter(this, mTopicsList);

                ListView topicsListView = findViewById(R.id.topics_list_view);
                topicsListView.setAdapter(mAdapter);
                // Get a reference to the "topics" node in your Firebase Realtime Database
                DatabaseReference topicsRef = FirebaseDatabase.getInstance().getReference("topics");

// Add a listener to retrieve the topics data from the database
                topicsRef.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        // Clear the current topics list
                        mTopicsList.clear();

                        // Loop through the snapshot of the "topics" node
                        for (DataSnapshot topicSnapshot : dataSnapshot.getChildren()) {
                            // Get the topic data as a Topic object
                            Topic topic = topicSnapshot.getValue(Topic.class);

                            // Add the topic to the topics list
                            mTopicsList.add(topic);

                        }

                        // Notify the adapter that the data has changed
                        mAdapter.notifyDataSetChanged();
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        // Handle any errors
                    }
                });

                topicsListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                        Topic topic = mTopicsList.get(i);
                        Intent intent = new Intent(DoctorActivity.this, PostManagement.class);
                        intent.putExtra("TOPIC_ID", topic.getId());
                        startActivity(intent);
                    }
                });


                mProgressDialog = new ProgressDialog(this);
                mProgressDialog.setMessage("Loading topics...");
                mProgressDialog.show();

                mDatabase.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        mTopicsList.clear();
                        for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                            Topic topic = snapshot.getValue(Topic.class);
                            mTopicsList.add(topic);

                        }
                        mAdapter.notifyDataSetChanged();
                        mProgressDialog.dismiss();
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                        Toast.makeText(DoctorActivity.this, "Error loading topics", Toast.LENGTH_SHORT).show();
                        mProgressDialog.dismiss();
                    }
                });

                FloatingActionButton fab = findViewById(R.id.fab);
                fab.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        showAddTopicDialog();
                    }
                });


                topicsListView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
                    @Override
                    public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {

                        mLongPressedView = view; // Store a reference to the view that was long-pressed
                        registerForContextMenu(view);
                        openContextMenu(view);
                        return true;
                    }
                });
            }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.topic_context_menu, menu);
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo)item.getMenuInfo();
        switch(item.getItemId()) {
            case R.id.menu_update_topic:
// Get the topic from the database using the topicId
                DatabaseReference topicRef = FirebaseDatabase.getInstance().getReference().child("topics").child(String.valueOf(info.id));
                topicRef.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        Topic topic = snapshot.getValue(Topic.class);

                        // Create a dialog for editing the topic
                        AlertDialog.Builder builder = new AlertDialog.Builder(DoctorActivity.this);
                        builder.setTitle("Edit Topic");

                        // Add EditText views for the topic name and description
                        LinearLayout layout = new LinearLayout(DoctorActivity.this);
                        layout.setOrientation(LinearLayout.VERTICAL);
                        EditText TitleEditText = new EditText(DoctorActivity.this);
                        TitleEditText.setText(topic.getTitle());
                        layout.addView(TitleEditText);
                        EditText descriptionEditText = new EditText(DoctorActivity.this);
                        descriptionEditText.setText(topic.getDescription());
                        layout.addView(descriptionEditText);
                        builder.setView(layout);

                        // Add a "Save" button to the dialog
                        builder.setPositiveButton("Save", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                // Save the updated topic to the database
                                String newName = TitleEditText.getText().toString();
                                String newDescription = descriptionEditText.getText().toString();
                                updateTopic(String.valueOf(info.id), newName, newDescription);
                            }
                        });

                        // Add a "Cancel" button to the dialog
                        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                // Cancel the dialog
                                dialog.cancel();
                            }
                        });

                        // Show the dialog
                        AlertDialog dialog = builder.create();
                        dialog.show();
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        // Handle the error
                    }
                });
                return true;
            case R.id.menu_hide_topic:
                // Hide the topic from the doctor's view in the database
               hideTopic(String.valueOf(info.id));
                return true;
            case R.id.menu_delete_topic:
                // Delete the topic from the database
               deleteTopic(String.valueOf(info.id));
                return true;
            default:
                return super.onContextItemSelected(item);
        }
            }

            private void showAddTopicDialog() {
                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setTitle("Add Topic");

                View view = LayoutInflater.from(this).inflate(R.layout.dialog_add_topic, null);
                mTitleEditText = view.findViewById(R.id.title_edit_text);
                mDescriptionEditText = view.findViewById(R.id.description_edit_text);

                builder.setView(view);
                builder.setPositiveButton("Add", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        String title = mTitleEditText.getText().toString();
                        String description = mDescriptionEditText.getText().toString();
                        if (!TextUtils.isEmpty(title) && !TextUtils.isEmpty(description)) {
                            addTopic(title, description);
                        }
                    }
                });
                builder.setNegativeButton("Cancel", null);

                builder.show();
            }

            private void addTopic(String title, String description) {
                String id = mDatabase.push().getKey();
                Topic topic = new Topic(id, title, description);
                mDatabase.child(id).setValue(topic);
                Toast.makeText(this, "Topic added", Toast.LENGTH_SHORT).show();
            }

            private void updateTopic(String topicId, String name, String description) {
        // Get a reference to the topic in the database
                    DatabaseReference topicRef = FirebaseDatabase.getInstance().getReference().child("topics").child(String.valueOf(topicId));

        // Update the topic fields
                    topicRef.child("name").setValue(name);
                    topicRef.child("description").setValue(description);

        // Update the topic row in the list view
                    int position = mTopicsList.indexOf(getTopicById(topicId));
                    Topic updatedTopic = new Topic(String.valueOf(topicId), name, description);
                    mTopicsList.set(position, updatedTopic);
                    mAdapter.notifyDataSetChanged();
    }


    private void hideTopic(String topicId) {
        DatabaseReference topicRef = FirebaseDatabase.getInstance().getReference("topics").child(topicId);
        topicRef.child("hidden").setValue(true);
           }

           private void deleteTopic(String topicId) {
        DatabaseReference topicRef = FirebaseDatabase.getInstance().getReference("topics").child(topicId);
        topicRef.removeValue();
    }
    private Topic getTopicById(String topicId) {
        for (Topic topic : mTopicsList) {
            if (topic.getId().equals(topicId)) {
                return topic;
            }
        }
        return null;
    }
}


