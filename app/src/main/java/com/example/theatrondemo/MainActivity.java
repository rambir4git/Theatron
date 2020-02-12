package com.example.theatrondemo;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import dmax.dialog.SpotsDialog;

public class MainActivity extends AppCompatActivity {

    private int RC_SIGN_IN = 123;
    private EditText email, pass;
    private Button login;
    private SignInButton googleSignIn;
    private TextView register;
    private FirebaseAuth mainAuth;
    private FirebaseFirestore firestore;
    private AlertDialog alertDialog;
    private FirebaseAuth.AuthStateListener authStateListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        init();
        final GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();
        final GoogleSignInClient mGoogleSignInClient = GoogleSignIn.getClient(this, gso);
        alertDialog = new SpotsDialog.Builder()
                .setContext(MainActivity.this)
                .setMessage("Logging in...")
                .setCancelable(false)
                .build();
        authStateListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull final FirebaseAuth firebaseAuth) {
                if (firebaseAuth.getCurrentUser() != null) {
                    alertDialog.show();
                    firestore = FirebaseFirestore.getInstance();
                    firestore.collection("ALL USERS")
                            .whereEqualTo("id", firebaseAuth.getCurrentUser().getUid())
                            .get()
                            .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                                @Override
                                public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                                    if (queryDocumentSnapshots.size() == 1) {
                                        startActivity(new Intent(MainActivity.this, HomeActivity.class));
                                        finish();
                                    } else {
                                        AppUser appUser = new AppUser();
                                        appUser.setDisplay(firebaseAuth.getCurrentUser().getDisplayName());
                                        appUser.setProfilePic(firebaseAuth.getCurrentUser().getPhotoUrl().toString());
                                        appUser.setId(firebaseAuth.getCurrentUser().getUid());
                                        firestore.collection("ALL USERS")
                                                .document(appUser.getId())
                                                .set(appUser);
                                        startActivity(new Intent(MainActivity.this, HomeActivity.class));
                                        finish();
                                    }
                                    alertDialog.dismiss();
                                }
                            });
                }
            }
        };
        mainAuth = FirebaseAuth.getInstance();
        mainAuth.addAuthStateListener(authStateListener);

        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                alertDialog.show();
                String e = email.getText().toString();
                String p = pass.getText().toString();
                signIn(e, p);
            }
        });

        googleSignIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alertDialog.show();
                mGoogleSignInClient.signOut();
                Intent signInIntent = mGoogleSignInClient.getSignInIntent();
                startActivityForResult(signInIntent, RC_SIGN_IN);
            }
        });

        register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(MainActivity.this, RegisterActivity.class));
                finish();
            }
        });

        Bundle args = getIntent().getExtras();
        if (args != null) {
            if (args.getBoolean("LOGOUT")) {
                mGoogleSignInClient.revokeAccess();
                mainAuth.signOut();
            }
        }
    }

    private void signIn(String e, String p) {

        // Validations
        if (e.isEmpty()) {
            email.setError("Email is required");
            email.requestFocus();
            return;
        }
        if (!Patterns.EMAIL_ADDRESS.matcher(e).matches()) {
            email.setError("Invalid Email");
            email.requestFocus();
            return;
        }
        if (p.isEmpty()) {
            pass.setError("Password is required");
            pass.requestFocus();
            return;
        }
        if (p.length() < 8) {
            pass.setError("Minimum length should be 8");
            pass.requestFocus();
            return;
        }
        mainAuth.signInWithEmailAndPassword(e, p).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (!task.isSuccessful()) {
                    print(task.getException().getMessage());
                }
                alertDialog.dismiss();
            }
        });
    }

    private void print(String message) {

        Toast.makeText(MainActivity.this, message, Toast.LENGTH_SHORT).show();
    }

    private void init() {
        email = findViewById(R.id.emailID);
        pass = findViewById(R.id.pass);
        login = findViewById(R.id.login);
        googleSignIn = findViewById(R.id.google_signIn);
        register = findViewById(R.id.register);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == RC_SIGN_IN) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            try {
                // Google Sign In was successful, authenticate with Firebase
                GoogleSignInAccount account = task.getResult(ApiException.class);
                AuthCredential credential = GoogleAuthProvider.getCredential(account.getIdToken(), null);
                mainAuth.signInWithCredential(credential);

            } catch (ApiException e) {
                Toast.makeText(this, "Something went wrong !!", Toast.LENGTH_SHORT).show();
            }
            alertDialog.dismiss();
        }
    }


}
