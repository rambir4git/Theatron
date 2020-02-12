package com.example.theatrondemo;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import de.hdodenhof.circleimageview.CircleImageView;

public class SocialCommentHolder extends RecyclerView.ViewHolder {
    CircleImageView commentatorPic;
    TextView commentatorName, commentBody, timeDifference, like, reply;
    ImageView commentMedia;

    public SocialCommentHolder(@NonNull View itemView) {
        super(itemView);
        this.commentatorPic = itemView.findViewById(R.id.commentator_profile_pic);
        this.commentatorName = itemView.findViewById(R.id.commentator_name);
        this.commentBody = itemView.findViewById(R.id.comment_body);
        this.timeDifference = itemView.findViewById(R.id.comment_timestamp);
        this.like = itemView.findViewById(R.id.comment_like);
        this.reply = itemView.findViewById(R.id.comment_comment);
        this.commentMedia = itemView.findViewById(R.id.comment_media);
    }
}
