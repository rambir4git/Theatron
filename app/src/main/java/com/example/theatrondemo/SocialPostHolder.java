package com.example.theatrondemo;

import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.button.MaterialButton;

import de.hdodenhof.circleimageview.CircleImageView;

public class SocialPostHolder extends RecyclerView.ViewHolder {
    View currentView;
    ImageButton imageBtn;
    CircleImageView profileIcon;
    TextView opName, timestamp, description, postSource;
    MaterialButton likeBtn, commentBtn, shareBtn, viewsBtn;

    public SocialPostHolder(@NonNull View itemView) {
        super(itemView);
        this.currentView = itemView;
        this.profileIcon = itemView.findViewById(R.id.profile_icon);
        this.opName = itemView.findViewById(R.id.profile_name);
        this.timestamp = itemView.findViewById(R.id.post_timestamp);
        this.description = itemView.findViewById(R.id.post_description);
        this.postSource = itemView.findViewById(R.id.why_this_post_is_visible);
        this.imageBtn = itemView.findViewById(R.id.postImage);
        this.likeBtn = itemView.findViewById(R.id.likePost);
        this.commentBtn = itemView.findViewById(R.id.commentPost);
        this.shareBtn = itemView.findViewById(R.id.sharePost);
        this.viewsBtn = itemView.findViewById(R.id.views_count);
    }

}
