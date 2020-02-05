package com.example.theatrondemo;


import android.app.AlertDialog;
import android.content.DialogInterface;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.paging.PagedList;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.firebase.ui.firestore.paging.FirestorePagingAdapter;
import com.firebase.ui.firestore.paging.FirestorePagingOptions;
import com.firebase.ui.firestore.paging.LoadingState;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

import dmax.dialog.SpotsDialog;

public class HomeFragment extends Fragment {
    final String TAG = "HomeFragment";
    EditText input;
    String message;
    AlertDialog alertDialog;
    androidx.appcompat.app.AlertDialog inputAlert;
    Uri image = null;
    StorageReference storage;
    FirebaseFirestore db;
    FirebaseAuth auth;
    FirestorePagingAdapter adapter;

    public HomeFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);

        RecyclerView recyclerView = view.findViewById(R.id.recycler_view);
        auth = FirebaseAuth.getInstance();
        storage = FirebaseStorage.getInstance().getReference("media");
        db = FirebaseFirestore.getInstance();
        alertDialog = new SpotsDialog.Builder()
                .setCancelable(false)
                .setContext(getActivity())
                .setMessage("Making post")
                .build();
        /*
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(Intent.createChooser(intent,"Select Picture"),PICK_IMAGE);
            }
        });
         */
        Query query = FirebaseFirestore.getInstance()
                .collection("ALL POSTS")
                .orderBy("timestamp", Query.Direction.DESCENDING);

        PagedList.Config config = new PagedList.Config.Builder()
                .setEnablePlaceholders(false)
                .setPrefetchDistance(3)
                .setPageSize(2)
                .build();

        FirestorePagingOptions<SocialPost> options = new FirestorePagingOptions.Builder<SocialPost>()
                .setQuery(query, config, SocialPost.class)
                .setLifecycleOwner(this)
                .build();

        adapter = new FirestorePagingAdapter<SocialPost, SocialPostHolder>(options) {
            @NonNull
            @Override
            public SocialPostHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                Log.d(TAG, "onCreateViewHolder: card layour inflated");
                return new SocialPostHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.card_social_post, parent, false));
            }

            @Override
            protected void onBindViewHolder(@NonNull final SocialPostHolder holder, int position, @NonNull final SocialPost model) {
                Glide.with(getActivity()).load(model.getMedia()).into(holder.imageView);
                if (!model.getViews().contains(auth.getUid())) {
                    db.collection("ALL POSTS")
                            .document(model.getPostID())
                            .update("views", FieldValue.arrayUnion(auth.getUid()))
                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {
                                    Log.d(TAG, "onSuccess: views updated.");
                                }
                            });
                }
                String views = "Views: " + model.getViews().size();
                holder.viewsCount.setText(views);
                if (model.getLikes().contains(auth.getUid()))
                    holder.likeBtn.setCircleBackgroundColor(getResources().getColor(R.color.liked));
                holder.likeBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        likePost(model);
                        holder.likeBtn.setCircleBackgroundColor(getResources().getColor(R.color.liked));
                    }
                });
                holder.commentBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        commentInput(model);
                    }
                });
                if (model.getShares().contains(auth.getUid()))
                    holder.shareBtn.setCircleBackgroundColor(getResources().getColor(R.color.shared));
                holder.shareBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        sharePost(model);
                        holder.shareBtn.setCircleBackgroundColor(getResources().getColor(R.color.shared));
                    }
                });
                holder.imageView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Toast.makeText(getActivity(), "Make frame to display full image", Toast.LENGTH_SHORT).show();
                    }
                });
            }

            @Override
            protected void onLoadingStateChanged(@NonNull LoadingState state) {
                super.onLoadingStateChanged(state);
                adapter.notifyDataSetChanged();
            }
        };
        recyclerView.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false);
        recyclerView.setLayoutManager(linearLayoutManager);
        recyclerView.setAdapter(adapter);

        try {
            image = getArguments().getParcelable("Uri");
            postInput();
        } catch (NullPointerException e) {
            Log.d(TAG, "onCreateView: normal start");
        }
        return view;
    }

    private void likePost(SocialPost socialPost) {
        db.collection("ALL POSTS")
                .document(socialPost.getPostID())
                .update("likes", FieldValue.arrayUnion(auth.getUid()))
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.d(TAG, "onSuccess: Likes on the post updated.");
                    }
                });

        Map<String, Object> userLike = new HashMap<>();
        userLike.put("postID", socialPost.getPostID());
        userLike.put("timestamp", new Timestamp(Calendar.getInstance().getTime()));
        db.collection("ALL USERS")
                .document(auth.getUid())
                .collection("LIKED")
                .document()
                .set(userLike)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.d(TAG, "onSuccess: User likes updated.");
                    }
                });
    }

    private void commentInput(final SocialPost socialPost) {
        input = new EditText(getActivity());
        input.setHint("Enter message for your comment");
        inputAlert = new androidx.appcompat.app.AlertDialog.Builder(getActivity())
                .setTitle("Enter Message")
                .setView(input)
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        message = input.getText().toString();
                        inputAlert.hide();
                        commentPost(socialPost);
                    }
                }).create();
        inputAlert.show();
    }

    private void postInput() {
        input = new EditText(getActivity());
        input.setHint("Enter title for your post");
        inputAlert = new androidx.appcompat.app.AlertDialog.Builder(getActivity())
                .setTitle("Enter Message")
                .setView(input)
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        message = input.getText().toString();
                        inputAlert.hide();
                        makePost();
                    }
                }).create();
        inputAlert.show();
    }

    private void commentPost(SocialPost socialPost) {
        SocialPost.Comment comment = new SocialPost.Comment(message, auth.getUid());
        db.collection("ALL POSTS")
                .document(socialPost.getPostID())
                .collection("Comments")
                .document()
                .set(comment)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.d(TAG, "onSuccess: new comments added.");
                    }
                });

        Map<String, Object> userComment = new HashMap<>();
        userComment.put("postID", socialPost.getPostID());
        userComment.put("timestamp", new Timestamp(Calendar.getInstance().getTime()));
        db.collection("ALL USERS")
                .document(auth.getUid())
                .collection("COMMENTED")
                .document()
                .set(userComment);
    }

    private void sharePost(SocialPost socialPost) {

        db.collection("ALL POSTS")
                .document(socialPost.getPostID())
                .update("shares", FieldValue.arrayUnion(auth.getUid()))
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.d(TAG, "onSuccess: Shares on the post updated.");
                    }
                });

        Map<String, Object> userShare = new HashMap<>();
        userShare.put("postID", socialPost.getPostID());
        userShare.put("timestamp", new Timestamp(Calendar.getInstance().getTime()));
        db.collection("ALL USERS")
                .document(auth.getUid())
                .collection("SHARED")
                .document()
                .set(userShare)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.d(TAG, "onSuccess: User shares updated.");
                    }
                });
    }

    private void makePost() {
        alertDialog.show();
        final StorageReference path = storage.child(image.getLastPathSegment());
        path.putFile(image).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                path.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {
                        final DocumentReference documentReference = db.collection("ALL POSTS").document();
                        SocialPost newPost = new SocialPost(message, uri.toString(), auth.getUid(), documentReference.getId(), new Timestamp(Calendar.getInstance().getTime()));
                        documentReference
                                .set(newPost).addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                Log.d(TAG, "onSuccess: Document uploaded :)");
                                Map<String, Object> userPost = new HashMap<>();
                                userPost.put("postID", documentReference.getId());
                                userPost.put("timestamp", new Timestamp(Calendar.getInstance().getTime()));
                                db.collection("ALL USERS")
                                        .document(auth.getUid())
                                        .collection("POSTED")
                                        .document()
                                        .set(userPost)
                                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                                            @Override
                                            public void onSuccess(Void aVoid) {
                                                Log.d(TAG, "onSuccess: User updated.");
                                            }
                                        }).addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        Log.d(TAG, "onComplete: completed");
                                        alertDialog.dismiss();
                                    }
                                });
                            }
                        });
                    }
                });
            }
        });
    }
}
