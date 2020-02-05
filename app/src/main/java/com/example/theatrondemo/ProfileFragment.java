package com.example.theatrondemo;


import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.fragment.app.Fragment;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import de.hdodenhof.circleimageview.CircleImageView;

public class ProfileFragment extends Fragment {

    private FirebaseUser user;
    private CircleImageView circleImageView;
    private TextView name, accID, email;

    public ProfileFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_profile, container, false);

        accID = view.findViewById(R.id.acc_id);
        circleImageView = view.findViewById(R.id.dp);
        name = view.findViewById(R.id.show_name);
        email = view.findViewById(R.id.show_email);

        user = FirebaseAuth.getInstance().getCurrentUser();
        accID.setText(user.getUid());
        name.setText(user.getDisplayName());
        email.setText(user.getEmail());

        Glide.with(this).load(user.getPhotoUrl()).into(circleImageView);

        return view;
    }

}
