package mgit.memes.confessions;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.FacebookAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.GoogleAuthProvider;


public class login extends AppCompatActivity implements View.OnClickListener {

    private static final String TAG = "Login Activity";
    private static final int RC_SIGN_IN = 9001;

    private static final int ACTIVITY_LOGIN_LAYOUT = R.layout.activity_login;

    private static final int CIRCULAR_PROGRESS_ID = R.id.circular_progress;
    private static final int LOGIN_BTN_LOGIN_ID = R.id.login_btn_login;
    private static final int LOGIN_EMAIL_ID = R.id.login_email;
    private static final int LOGIN_PASSWORD_ID = R.id.login_password;
    private static final int LOGIN_BTN_SIGNUP_ID = R.id.login_btn_signup;
    private static final int LOGIN_BTN_FORGOT_PASSWORD_ID = R.id.login_btn_forgot_password;
    private static final int ACTIVITY_MAIN_ID = R.id.activity_main;
    private static final int GOOGLE_BUT_ID = R.id.googleBut;
    private static final int FB_LOGIN_BUTTON_ID = R.id.fb_login_button;

    private Button btnLogin;
    private EditText input_email;
    private EditText input_password;
    private TextView btnSignup;
    private TextView btnForgotPass;
    private SignInButton googleBut;
    private LoginButton fb_login;

    //Progress Bar
    private ProgressBar circularProgressBar;

    private RelativeLayout activity_main;

    // [START declare_auth]
    private FirebaseAuth auth;
    // [END declare_auth]

    private GoogleSignInClient mGoogleSignInClient;
    private CallbackManager mCallbackManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //Facebook sdk initilization
        FacebookSdk.sdkInitialize(getApplicationContext());

        setContentView(ACTIVITY_LOGIN_LAYOUT);

        // progress bar init;
        circularProgressBar = findViewById(CIRCULAR_PROGRESS_ID);

        //View
        btnLogin = findViewById(LOGIN_BTN_LOGIN_ID);
        input_email = findViewById(LOGIN_EMAIL_ID);
        input_password = findViewById(LOGIN_PASSWORD_ID);
        btnSignup = findViewById(LOGIN_BTN_SIGNUP_ID);
        btnForgotPass = findViewById(LOGIN_BTN_FORGOT_PASSWORD_ID);
        activity_main = findViewById(ACTIVITY_MAIN_ID);
        googleBut = findViewById(GOOGLE_BUT_ID);
        fb_login = findViewById(FB_LOGIN_BUTTON_ID);

        btnSignup.setOnClickListener(this);
        btnForgotPass.setOnClickListener(this);
        btnLogin.setOnClickListener(this);
        googleBut.setOnClickListener(this);

        //After logout, logout facebook account
        if (getIntent().hasExtra("logout")) {
            LoginManager.getInstance().logOut();
        }

        //Initilize Facebook Login button
        mCallbackManager = CallbackManager.Factory.create();
        fb_login.setReadPermissions("email", "public_profile");
        fb_login.registerCallback(mCallbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                Log.d(TAG, "facebook:onSuccess:" + loginResult);
                handleFacebookAccessToken(loginResult.getAccessToken());
            }

            @Override
            public void onCancel() {
                Log.d(TAG, "facebook:onCancel");
            }

            @Override
            public void onError(FacebookException error) {
                Log.d(TAG, "facebook:onError", error);
            }
        });
        // [END initialize_fblogin]

        //Init Firebase Auth
        auth = FirebaseAuth.getInstance();

        // Configure Google Sign In
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();

        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);

    }

    // [START on_start_check_user]
    @Override
    protected void onStart() {
        super.onStart();

        //Check already session , if ok-> DashBoard
        if (auth.getCurrentUser() != null)
            startActivity(new Intent(login.this, DashBoard.class));

    }
    // [END on_start_check_user]

    private void signIn() {
        Intent signInIntent = mGoogleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);
        if (requestCode == RC_SIGN_IN) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            try {
                // Google Sign In was successful, authenticate with Firebase
                GoogleSignInAccount account = task.getResult(ApiException.class);
                firebaseAuthWithGoogle(account);
            } catch (ApiException e) {
                // Google Sign In failed, update UI appropriately
                Log.w(TAG, "Google sign in failed", e);
                // ...
            }
        }


        //Pass the activity result back to the Facebook SDK
        mCallbackManager.onActivityResult(requestCode, resultCode, data);
    }

    private void firebaseAuthWithGoogle(GoogleSignInAccount acct) {
        Log.d(TAG, "firebaseAuthWithGoogle:" + acct.getId());

        AuthCredential credential = GoogleAuthProvider.getCredential(acct.getIdToken(), null);
        auth.signInWithCredential(credential)
                .addOnCompleteListener(this, task -> {
                    if (task.isSuccessful()) {
                        circularProgressBar.setVisibility(View.INVISIBLE);
                        // Sign in success, update UI with the signed-in user's information
                        Log.d(TAG, "signInWithCredential:success");
                        startActivity(new Intent(login.this, DashBoard.class));
                    } else {
                        circularProgressBar.setVisibility(View.INVISIBLE);
                        // If sign in fails, display a message to the user.
                        Log.w(TAG, "signInWithCredential:failure", task.getException());
                        Toast.makeText(login.this, "Authentication failed.",
                                Toast.LENGTH_SHORT).show();
                    }

                    // ...
                });
    }


    @Override
    public void onClick(View view) {
        int id = view.getId();
        if (id == LOGIN_BTN_FORGOT_PASSWORD_ID) {
            startActivity(new Intent(login.this, ForgotPassword.class));
            finish();
        } else if (id == LOGIN_BTN_SIGNUP_ID) {
            startActivity(new Intent(login.this, SignUp.class));
            finish();
        } else if (id == LOGIN_BTN_LOGIN_ID) {
            circularProgressBar.setVisibility(View.VISIBLE);
            loginUser(input_email.getText().toString(), input_password.getText().toString());
        } else if (id == GOOGLE_BUT_ID) {
            circularProgressBar.setVisibility(View.VISIBLE);
            signIn();
        }
    }

    private void loginUser(String email, final String password) {
        auth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, task -> {
                    if (!task.isSuccessful()) {
                        if (password.length() < 6) {
                            circularProgressBar.setVisibility(View.INVISIBLE);
                            Snackbar snackBar = Snackbar.make(activity_main, "Password length must be over 6", Snackbar.LENGTH_SHORT);
                            snackBar.show();
                        }
                    } else {
                        circularProgressBar.setVisibility(View.INVISIBLE);
                        startActivity(new Intent(login.this, DashBoard.class));
                    }
                });
    }

    // [START auth_with_facebook]
    private void handleFacebookAccessToken(AccessToken token) {
        Log.d(TAG, "handleFacebookAccessToken:" + token);

        AuthCredential credential = FacebookAuthProvider.getCredential(token.getToken());
        auth.signInWithCredential(credential)
                .addOnCompleteListener(this, task -> {
                    if (task.isSuccessful()) {
                        // Sign in success, update UI with the signed-in user's information
                        Log.d(TAG, "signInWithCredential:success");
                    } else {
                        // If sign in fails, display a message to the user.
                        Log.w(TAG, "signInWithCredential:failure", task.getException());
                        Toast.makeText(login.this, "Authentication failed.",
                                Toast.LENGTH_SHORT).show();
                    }

                });
    }
// [END auth_with_facebook]

}


