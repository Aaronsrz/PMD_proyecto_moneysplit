package es.upm.etsiinf.gib.pmd_proyecto.grouplist;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.ListView;
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
        ListView listView = findViewById(R.id.gpl_lw_main);

        ArrayList<Group> groups = new ArrayList<>();
        groups.add(new Group("🇪🇸", "Madrid in French"));
        groups.add(new Group("🚽", "Club de billard"));
        groups.add(new Group("🌅", "Barca"));
        groups.add(new Group("🇪🇸", "Vacances malaga"));

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
    }
}