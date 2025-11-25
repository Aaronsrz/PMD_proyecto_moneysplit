package es.upm.etsiinf.gib.pmd_proyecto;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import es.upm.etsiinf.gib.pmd_proyecto.grouplist.GroupListActivity;

public class LoginActivity extends AppCompatActivity {

    private EditText edtName;
    private EditText edtPassword;
    private Button btnLogin;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        // Link XML elements
        edtName = findViewById(R.id.login_txt_name);
        edtPassword = findViewById(R.id.login_txt_psw);
        btnLogin = findViewById(R.id.login_btn_login);

        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String name = edtName.getText().toString().trim();
                String password = edtPassword.getText().toString().trim();

                // Empty fields?
                if (name.isEmpty() || password.isEmpty()) {
                    Toast.makeText(LoginActivity.this,
                            "Please enter name and password",
                            Toast.LENGTH_SHORT).show();
                    return;
                }

                // Password must match name
                if (!name.equals(password)) {
                    Toast.makeText(LoginActivity.this,
                            "Password must be the SAME as the name",
                            Toast.LENGTH_SHORT).show();
                    return;
                }

                // SUCCESS → go to GroupListActivity
                Intent intent = new Intent(LoginActivity.this, GroupListActivity.class);
                intent.putExtra("CURRENT_USER_NAME", name); // send username
                startActivity(intent);
                finish();
            }
        });
    }
}