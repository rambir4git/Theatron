package com.example.theatrondemo;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import de.hdodenhof.circleimageview.CircleImageView;

public class SocialPostHolder extends RecyclerView.ViewHolder {
    View currentView;
    ImageView imageView;
    TextView viewsCount;
    CircleImageView likeBtn, commentBtn, shareBtn;

    public SocialPostHolder(@NonNull View itemView) {
        super(itemView);
        this.currentView = itemView;
        this.imageView = itemView.findViewById(R.id.postImage);
        this.likeBtn = itemView.findViewById(R.id.likePost);
        this.commentBtn = itemView.findViewById(R.id.commentPost);
        this.shareBtn = itemView.findViewById(R.id.sharePost);
        this.viewsCount = itemView.findViewById(R.id.viewscount);
    }

}
