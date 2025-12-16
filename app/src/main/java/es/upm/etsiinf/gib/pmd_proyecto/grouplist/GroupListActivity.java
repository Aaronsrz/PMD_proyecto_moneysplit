package es.upm.etsiinf.gib.pmd_proyecto.grouplist;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import java.util.ArrayList;

import es.upm.etsiinf.gib.pmd_proyecto.R;
import es.upm.etsiinf.gib.pmd_proyecto.groupdetail.GroupDetailActivity;

public class GroupListActivity extends AppCompatActivity {
    private static final String PREFS_NAME = "login_prefs";
    private static final String KEY_KEEP_LOGGED_IN = "keep_logged_in";
    private static final String KEY_USERNAME = "username";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_group_list);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        String currentUserName = getIntent().getStringExtra("CURRENT_USER_NAME"); //useless for now

        ListView listView = findViewById(R.id.gpl_lw_main);

        ArrayList<Group> groups = new ArrayList<>();
        groups.add(new Group("🇪🇸", "Madrid trip"));
        groups.add(new Group("🎳", "Bowling"));
        groups.add(new Group("🌅", "Barca"));
        groups.add(new Group("🏝️", "Malaga vacation"));

        GroupAdapter adapter = new GroupAdapter(this, groups);
        listView.setAdapter(adapter);

        // Handle item click (open detail activity)
        listView.setOnItemClickListener((parent, view, position, id) -> {
            Group clicked = groups.get(position);
            Toast.makeText(this, "Clicked: " + clicked.getName(), Toast.LENGTH_SHORT).show();

            Intent intent = new Intent(this, GroupDetailActivity.class);
            intent.putExtra("GROUP_NAME", clicked.getName());
            startActivity(intent);
        });

        TextView txtLoggedInAs = findViewById(R.id.txtLoggedInAs);
        txtLoggedInAs.setText("Logged in as: " + currentUserName);

        TextView txtLogout = findViewById(R.id.txtLogout);
        txtLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SharedPreferences prefs = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
                SharedPreferences.Editor editor = prefs.edit();

                editor.putBoolean(KEY_KEEP_LOGGED_IN, false);
                editor.remove(KEY_USERNAME);
                editor.apply();

                Intent intent = new Intent(GroupListActivity.this, es.upm.etsiinf.gib.pmd_proyecto.LoginActivity.class);
                startActivity(intent);
                finish();  // close GroupListActivity, but ExpenseRepository stays in memory
            }
        });

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Group clicked = groups.get(position);

                Intent intent = new Intent(GroupListActivity.this, GroupDetailActivity.class);

                // send info about the group
                intent.putExtra("GROUP_INDEX", position);          // 0,1,2...
                intent.putExtra("GROUP_NAME", clicked.getName());
                intent.putExtra("GROUP_EMOJI", clicked.getEmoji());

                // forward current user name
                intent.putExtra("CURRENT_USER_NAME", currentUserName);


                startActivity(intent);
            }
        });

    }
}