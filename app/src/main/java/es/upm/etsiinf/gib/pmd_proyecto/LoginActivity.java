package es.upm.etsiinf.gib.pmd_proyecto;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import es.upm.etsiinf.gib.pmd_proyecto.grouplist.GroupListActivity;

public class LoginActivity extends AppCompatActivity {

    private static final String PREFS_NAME = "login_prefs";
    private static final String KEY_KEEP_LOGGED_IN = "keep_logged_in";
    private static final String KEY_USERNAME = "username";

    private EditText edtUsername;
    private EditText edtPassword;
    private CheckBox chkKeepLoggedIn;
    private Button btnLogin;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        edtUsername = findViewById(R.id.login_txt_name);
        edtPassword = findViewById(R.id.login_txt_psw);
        chkKeepLoggedIn = findViewById(R.id.chkKeepLoggedIn);

        SharedPreferences prefs = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
        boolean keepLoggedIn = prefs.getBoolean(KEY_KEEP_LOGGED_IN, false);
        String savedUser = prefs.getString(KEY_USERNAME, null);

        // If user chose "keep logged in" before, skip login screen
        if (keepLoggedIn && savedUser != null) {
            Intent intent = new Intent(LoginActivity.this, GroupListActivity.class);
            intent.putExtra("CURRENT_USER_NAME", savedUser);
            startActivity(intent);
            finish();
            return; // important: stop here
        }

        Button btnLogin = findViewById(R.id.login_btn_login);
        btnLogin.setOnClickListener(v -> {
            String username = edtUsername.getText().toString().trim();
            String password = edtPassword.getText().toString().trim();

            // Very simple validation
            if (username.isEmpty() || password.isEmpty()) {
                Toast.makeText(this, "Please enter username and password", Toast.LENGTH_SHORT).show();
                return;
            }

            SharedPreferences.Editor editor = prefs.edit();

            if (chkKeepLoggedIn.isChecked()) {
                editor.putBoolean(KEY_KEEP_LOGGED_IN, true);
                editor.putString(KEY_USERNAME, username);
            } else {
                // user did not tick "keep logged in"
                editor.putBoolean(KEY_KEEP_LOGGED_IN, false);
                editor.remove(KEY_USERNAME);
            }
            editor.apply();

            // Go to group list, passing the username
            Intent intent = new Intent(LoginActivity.this, GroupListActivity.class);
            intent.putExtra("CURRENT_USER_NAME", username);
            startActivity(intent);
            finish();
        });

    }
}