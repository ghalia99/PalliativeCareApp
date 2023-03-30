package com.example.palliativecareapp;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.palliativecareapp.Post;
import com.example.palliativecareapp.R;

import java.util.List;

public class PostsListAdapter extends ArrayAdapter<Post> {
    private Context mContext;
    private List<Post> mPostsList;

    public PostsListAdapter(Context context, List<Post> postsList) {
        super(context, R.layout.item_post, postsList);
        mContext = context;
        mPostsList = postsList;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View rowView = inflater.inflate(R.layout.item_post, parent, false);

        TextView titleTextView = rowView.findViewById(R.id.title_text_view);
        TextView authorTextView = rowView.findViewById(R.id.author_text_view);
        TextView dateTextView = rowView.findViewById(R.id.date_text_view);
        ImageView imageView = rowView.findViewById(R.id.image_view);

        Post post = mPostsList.get(position);

        titleTextView.setText(post.getTitle());
        authorTextView.setText(post.getAuthor());
        dateTextView.setText(post.getDate());

        if (post.getFilePath() != null && !post.getFilePath().isEmpty()) {
            Glide.with(mContext)
                    .load(post.getFilePath())
                    .centerCrop()
                    .into(imageView);
        } else {
            imageView.setVisibility(View.GONE);
        }

        return rowView;
    }
}
