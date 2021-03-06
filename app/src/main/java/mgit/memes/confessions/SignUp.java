package mgit.memes.confessions;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.UserProfileChangeRequest;

public class SignUp extends AppCompatActivity implements View.OnClickListener {

    private static final int ACTIVITY_SIGN_UP_LAYOUT = R.layout.activity_sign_up;

    private static final int SIGNUP_BTN_REGISTER_ID = R.id.signup_btn_register;
    private static final int SIGNUP_BTN_LOGIN_ID = R.id.signup_btn_login;
    private static final int SIGNUP_BTN_FORGOT_PASS_ID = R.id.signup_btn_forgot_pass;
    private static final int SIGNUP_EMAIL_ID = R.id.signup_email;
    private static final int SIGNUP_PASSWORD_ID = R.id.signup_password;
    private static final int SIGNUP_NAME_ID = R.id.signup_name;
    private static final int ACTIVITY_SIGN_UP_ID = R.id.activity_sign_up;

    private Button btnSignup;
    private TextView btnLogin;
    private TextView btnForgotPass;
    private EditText input_email;
    private EditText input_pass;
    private EditText input_name;
    private RelativeLayout activity_sign_up;
    private Snackbar snackbar;
    private FirebaseAuth auth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(ACTIVITY_SIGN_UP_LAYOUT);

        //View
        btnSignup = findViewById(SIGNUP_BTN_REGISTER_ID);
        btnLogin = findViewById(SIGNUP_BTN_LOGIN_ID);
        btnForgotPass = findViewById(SIGNUP_BTN_FORGOT_PASS_ID);
        input_email = findViewById(SIGNUP_EMAIL_ID);
        input_pass = findViewById(SIGNUP_PASSWORD_ID);
        input_name = findViewById(SIGNUP_NAME_ID);
        activity_sign_up = findViewById(ACTIVITY_SIGN_UP_ID);

        btnSignup.setOnClickListener(this);
        btnLogin.setOnClickListener(this);
        btnForgotPass.setOnClickListener(this);

        //Init Firebase
        auth = FirebaseAuth.getInstance();

    }

    @Override
    public void onClick(View view) {
        int id = view.getId();
        if (id == SIGNUP_BTN_LOGIN_ID) {
            startActivity(new Intent(SignUp.this, login.class));
            finish();
        } else if (id == SIGNUP_BTN_FORGOT_PASS_ID) {
            startActivity(new Intent(SignUp.this, ForgotPassword.class));
            finish();
        } else if (id == SIGNUP_BTN_REGISTER_ID) {
            signUpUser(input_email.getText().toString(), input_pass.getText().toString());
        }
    }

    private void signUpUser(String email, String password) {
        auth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, task -> {
                    if (!task.isSuccessful()) {
                        snackbar = Snackbar.make(activity_sign_up, "Error: " + task.getException(), Snackbar.LENGTH_SHORT);
                        snackbar.show();
                    } else {

                        UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder()
                                .setDisplayName(input_name.getText().toString())
                                .build();
                        auth.getCurrentUser().updateProfile(profileUpdates);

                        snackbar = Snackbar.make(activity_sign_up, "Register success! ", Snackbar.LENGTH_SHORT);
                        snackbar.show();

                        startActivity(new Intent(SignUp.this, DashBoard.class));
                    }
                });
    }

}
