package es.upm.etsiinf.gib.pmd_proyecto.groupdetail;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import es.upm.etsiinf.gib.pmd_proyecto.R;
import es.upm.etsiinf.gib.pmd_proyecto.grouplist.GroupListActivity;

public class GroupDetailActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_group_detail);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        ImageButton btnBack = findViewById(R.id.btnBack);

        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                // Go back to the GroupListActivity
                Intent intent = new Intent(GroupDetailActivity.this, GroupListActivity.class);
                startActivity(intent);

                // Optional: close this activity so it doesn't stay in history
                finish();
            }
        });
    }
}