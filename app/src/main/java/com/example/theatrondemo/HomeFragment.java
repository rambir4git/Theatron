package com.example.theatrondemo;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.res.ColorStateList;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.paging.PagedList;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.firebase.ui.firestore.paging.FirestorePagingAdapter;
import com.firebase.ui.firestore.paging.FirestorePagingOptions;
import com.firebase.ui.firestore.paging.LoadingState;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.button.MaterialButton;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.Calendar;
import java.util.List;

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
    AppUser currentAppUser;
    FirestorePagingAdapter adapter;
    HomeFragment homfragment;
    FragmentManager fragmentManager;

    public HomeFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             final Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);
        homfragment = this;
        fragmentManager = getFragmentManager();
        RecyclerView recyclerView = view.findViewById(R.id.recycler_view);
        auth = FirebaseAuth.getInstance();
        storage = FirebaseStorage.getInstance().getReference("media");
        db = FirebaseFirestore.getInstance();
        db.collection("ALL USERS")
                .document(auth.getUid())
                .get()
                .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        currentAppUser = documentSnapshot.toObject(AppUser.class);
                    }
                });
        alertDialog = new SpotsDialog.Builder()
                .setCancelable(false)
                .setContext(getActivity())
                .setMessage("Making post")
                .build();

        Query query = FirebaseFirestore.getInstance()
                .collection("ALL POSTS")
                .orderBy("timestamp", Query.Direction.DESCENDING);

        PagedList.Config config = new PagedList.Config.Builder()
                .setEnablePlaceholders(false)
                .setPrefetchDistance(1)
                .setPageSize(1)
                .build();

        FirestorePagingOptions<SocialPost> options = new FirestorePagingOptions.Builder<SocialPost>()
                .setQuery(query, config, SocialPost.class)
                .setLifecycleOwner(this)
                .build();

        adapter = new FirestorePagingAdapter<SocialPost, SocialPostHolder>(options) {
            @NonNull
            @Override
            public SocialPostHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                return new SocialPostHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.card_social_post, parent, false));
            }

            @Override
            protected void onBindViewHolder(@NonNull final SocialPostHolder holder, int position, @NonNull final SocialPost model) {
                setCardDetails(holder, model);
            }

            @Override
            protected void onLoadingStateChanged(@NonNull LoadingState state) {
                super.onLoadingStateChanged(state);
                adapter.notifyDataSetChanged();
            }
        };
        recyclerView.setHasFixedSize(true);
        recyclerView.setAdapter(adapter);

        try {
            image = getArguments().getParcelable("Uri");
            postInput();
        } catch (NullPointerException e) {
            Log.d(TAG, "onCreateView: normal start");
        }
        return view;
    }

    private void setCardDetails(final SocialPostHolder holder, final SocialPost model) {
        Glide.with(getActivity()).load(model.getMedia()).into(holder.imageBtn);
        Glide.with(getActivity()).load(auth.getCurrentUser().getPhotoUrl()).into(holder.profileIcon);
        holder.description.setText(model.getTitle());
        holder.opName.setText(auth.getCurrentUser().getDisplayName());
        holder.timestamp.setText(model.getTimestamp().toDate().toString().substring(0, 16));
        updateViews(holder.viewsBtn, model.getViews());
        updateLikes(holder.likeBtn, model, false);
        updateComments(holder.commentBtn, model);
        updateShares(holder.shareBtn, model, false);
        holder.commentBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Bundle args = new Bundle();
                args.putCharSequence("postID", model.getPostID());
                CommentFragment fragment = new CommentFragment();
                fragment.setArguments(args);
                getFragmentManager().beginTransaction().replace(R.id.frames, fragment).addToBackStack(null).commit();
            }
        });
        holder.imageBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getActivity(), "Make frame to display full image", Toast.LENGTH_SHORT).show();
            }
        });
        db.collection("ALL POSTS")
                .document(model.getPostID())
                .set(model);
    }

    private void updateComments(MaterialButton commentBtn, SocialPost model) {
        if (model.getCommentators().contains(auth.getUid())) {
            commentBtn.setIcon(getResources().getDrawable(R.drawable.ic_insert_comment_filled_24px, null));
            commentBtn.setIconTint(ColorStateList.valueOf(getResources().getColor(R.color.commented, null)));
        }
    }

    private void updateShares(final MaterialButton shareBtn, final SocialPost model, boolean recursiveCall) {
        FirebaseFirestore.getInstance()
                .collection("ALL POSTS")
                .document(model.getPostID())
                .set(model);
        if (model.getShares().contains(auth.getUid())) {
            shareBtn.setIcon(getResources().getDrawable(R.drawable.ic_people_outline_filled_24px, null));
            shareBtn.setIconTint(ColorStateList.valueOf(getResources().getColor(R.color.shared, null)));
            shareBtn.setText(String.valueOf(model.getShares().size()));
            shareBtn.setTextColor(ColorStateList.valueOf(getResources().getColor(R.color.shared, null)));
        } else if (recursiveCall) {
            shareBtn.setIcon(getResources().getDrawable(R.drawable.ic_people_outline_24px, null));
            shareBtn.setIconTint(ColorStateList.valueOf(getResources().getColor(R.color.colorPrimary, null)));
            shareBtn.setText(String.valueOf(model.getShares().size()));
            shareBtn.setTextColor(ColorStateList.valueOf(getResources().getColor(R.color.colorPrimary, null)));
            return;
        }
        shareBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!model.getShares().contains(auth.getUid()))
                    model.getShares().add(auth.getUid());
                else model.getShares().remove(auth.getUid());
                updateShares(shareBtn, model, true);
            }
        });
    }

    private void updateLikes(final MaterialButton likeBtn, final SocialPost model, boolean recursiveCall) {
        FirebaseFirestore.getInstance()
                .collection("ALL POSTS")
                .document(model.getPostID())
                .set(model);
        if (model.getLikes().contains(auth.getUid())) {
            likeBtn.setIcon(getResources().getDrawable(R.drawable.ic_whatshot_filled_24px, null));
            likeBtn.setIconTint(ColorStateList.valueOf(getResources().getColor(R.color.liked, null)));
            likeBtn.setText(String.valueOf(model.getLikes().size()));
            likeBtn.setTextColor(ColorStateList.valueOf(getResources().getColor(R.color.liked, null)));
        } else if (recursiveCall) {
            likeBtn.setIcon(getResources().getDrawable(R.drawable.ic_whatshot_24px, null));
            likeBtn.setIconTint(ColorStateList.valueOf(getResources().getColor(R.color.colorPrimary, null)));
            likeBtn.setText(String.valueOf(model.getLikes().size()));
            likeBtn.setTextColor(ColorStateList.valueOf(getResources().getColor(R.color.colorPrimary, null)));
            return;
        }
        likeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!model.getLikes().contains(auth.getUid())) {
                    model.getLikes().add(auth.getUid());

                } else model.getLikes().remove(auth.getUid());
                updateLikes(likeBtn, model, true);
            }
        });
    }

    private void updateViews(final Button viewsBtn, final List<String> views) {
        if (!views.contains(auth.getUid())) {
            views.add(auth.getUid());
        }
        viewsBtn.setText(String.valueOf(views.size()));
    }


    private void postInput() {
        input = new EditText(getActivity());
        input.setHint("Enter title for your post");
        inputAlert = new androidx.appcompat.app.AlertDialog.Builder(getActivity())
                .setTitle("Enter Title")
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
                        documentReference.set(newPost).addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                alertDialog.dismiss();
                                fragmentManager.beginTransaction().replace(R.id.frames, new HomeFragment()).commit();
                            }
                        });
                    }
                });
            }
        });
    }
}
