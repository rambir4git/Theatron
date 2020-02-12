package com.example.theatrondemo;

import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.paging.PagedList;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.firebase.ui.firestore.paging.FirestorePagingAdapter;
import com.firebase.ui.firestore.paging.FirestorePagingOptions;
import com.firebase.ui.firestore.paging.LoadingState;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.Calendar;

public class CommentFragment extends Fragment {
    Uri commentMedia = null;
    ImageButton attachPhoto;
    FirebaseStorage storage;
    FirebaseFirestore firestore;
    FirestorePagingAdapter adapter;

    public CommentFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_comment, container, false);
        final RecyclerView recyclerView = view.findViewById(R.id.recycler_view_comments);
        final EditText editText = view.findViewById(R.id.comment_text);
        ImageButton sendComment = view.findViewById(R.id.comment_send);
        attachPhoto = view.findViewById(R.id.comment_camera);
        final String postID = getArguments().getCharSequence("postID").toString();
        Query query = FirebaseFirestore.getInstance()
                .collection("ALL POSTS")
                .document(postID)
                .collection("Comments")
                .orderBy("timestamp", Query.Direction.DESCENDING);

        PagedList.Config config = new PagedList.Config.Builder()
                .setEnablePlaceholders(false)
                .setPrefetchDistance(1)
                .setPageSize(1)
                .build();

        FirestorePagingOptions<SocialPost.Comment> options = new FirestorePagingOptions.Builder<SocialPost.Comment>()
                .setQuery(query, config, SocialPost.Comment.class)
                .setLifecycleOwner(this)
                .build();

        adapter = new FirestorePagingAdapter<SocialPost.Comment, SocialCommentHolder>(options) {
            @NonNull
            @Override
            public SocialCommentHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                return new SocialCommentHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.card_social_comment, parent, false));
            }

            @Override
            protected void onBindViewHolder(@NonNull final SocialCommentHolder holder, int position, @NonNull final SocialPost.Comment model) {
                FirebaseFirestore.getInstance()
                        .collection("ALL USERS")
                        .document(model.getUserId())
                        .get()
                        .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                            @Override
                            public void onSuccess(DocumentSnapshot documentSnapshot) {
                                AppUser commentator = documentSnapshot.toObject(AppUser.class);
                                holder.commentatorName.setText(commentator.getDisplay());
                                Glide.with(CommentFragment.this).load(commentator.getProfilePic()).into(holder.commentatorPic);
                            }
                        });
                Glide.with(CommentFragment.this).load(model.commentMedia).into(holder.commentMedia);
                holder.timeDifference.setText("sometime ago");
                holder.commentBody.setText(model.message);
            }

            @Override
            protected void onLoadingStateChanged(@NonNull LoadingState state) {
                super.onLoadingStateChanged(state);
                adapter.notifyDataSetChanged();
            }
        };
        recyclerView.setAdapter(adapter);
        recyclerView.setHasFixedSize(true);
        sendComment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final SocialPost.Comment comment = new SocialPost.Comment(editText.getText().toString(), FirebaseAuth.getInstance().getUid(), null, new Timestamp(Calendar.getInstance().getTime()));
                if (commentMedia != null) {
                    final StorageReference path = FirebaseStorage.getInstance().getReference("comment media").child(commentMedia.getLastPathSegment());
                    path.putFile(commentMedia).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            path.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                @Override
                                public void onSuccess(Uri uri) {
                                    comment.setCommentMedia(uri.toString());
                                    commentMedia = null;
                                }
                            });
                        }
                    });
                }
                FirebaseFirestore.getInstance()
                        .collection("ALL POSTS")
                        .document(postID)
                        .collection("Comments")
                        .document()
                        .set(comment);
                FirebaseFirestore.getInstance()
                        .collection("ALL POSTS")
                        .document(postID)
                        .update("commentators", FieldValue.arrayUnion(FirebaseAuth.getInstance().getUid()));
                editText.setText("");
                getFragmentManager().beginTransaction().detach(CommentFragment.this).attach(CommentFragment.this).commit();
            }
        });
        return view;
    }

}
