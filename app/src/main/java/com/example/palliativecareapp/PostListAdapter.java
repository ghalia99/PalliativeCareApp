package com.example.palliativecareapp;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.bumptech.glide.Glide;
import com.example.palliativecareapp.Post;

import java.util.List;

public class PostListAdapter extends ArrayAdapter<Post> {

    private Context mContext;
    private List<Post> mPostsList;

    public PostListAdapter(Context context, List<Post> postsList) {
        super(context, 0, postsList);
        mContext = context;
        mPostsList = postsList;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        View listItem = convertView;
        if (listItem == null) {
            listItem = LayoutInflater.from(mContext).inflate(R.layout.post_list_item, parent, false);
        }

        Post currentPost = mPostsList.get(position);

        TextView titleTextView = listItem.findViewById(R.id.post_title);
        TextView authorTextView = listItem.findViewById(R.id.post_author);
        ImageView mediaImageView = listItem.findViewById(R.id.post_media);

        titleTextView.setText(currentPost.getTitle());
        authorTextView.setText(currentPost.getAuthor());

        // Load the post media image into the ImageView using Glide
        if (currentPost.getMediaType().equals("image")) {
            mediaImageView.setVisibility(View.VISIBLE);
            Glide.with(mContext).load(currentPost.getFilePath()).into(mediaImageView);
        } else {
            mediaImageView.setVisibility(View.GONE);
        }

        return listItem;
    }

}
