package com.example.palliativecareapp;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.List;

public class TopicsListAdapter extends BaseAdapter {

    private Context mContext;
    private List<Topic> mTopicsList;

    public TopicsListAdapter(Context context, List<Topic> topicsList) {
        mContext = context;
        mTopicsList = topicsList;
    }

    @Override
    public int getCount() {
        return mTopicsList.size();
    }

    @Override
    public Object getItem(int position) {
        return mTopicsList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder;
        if (convertView == null) {
            convertView = LayoutInflater.from(mContext).inflate(R.layout.topic_list_item, parent, false);
            viewHolder = new ViewHolder();
            viewHolder.titleTextView = convertView.findViewById(R.id.topic_title);
            viewHolder.descriptionTextView = convertView.findViewById(R.id.topic_description);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        Topic currentTopic = mTopicsList.get(position);

        viewHolder.titleTextView.setText(currentTopic.getTitle());
        viewHolder.descriptionTextView.setText(currentTopic.getDescription());

        return convertView;
    }

    private static class ViewHolder {
        TextView titleTextView;
        TextView descriptionTextView;
    }
}
