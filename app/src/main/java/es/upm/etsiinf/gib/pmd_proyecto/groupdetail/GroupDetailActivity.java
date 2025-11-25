package es.upm.etsiinf.gib.pmd_proyecto.groupdetail;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import java.util.ArrayList;

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

        // 1. Read the extras
        Intent intent = getIntent();
        int groupIndex = intent.getIntExtra("GROUP_INDEX", -1);
        String groupName = intent.getStringExtra("GROUP_NAME");
        String groupEmoji = intent.getStringExtra("GROUP_EMOJI");

        // 2. Set the header
        TextView txtTitle = findViewById(R.id.txtGroupTitle);
        TextView txtEmoji = findViewById(R.id.txtGroupEmoji);
        txtTitle.setText(groupName);
        txtEmoji.setText(groupEmoji);

        // 3. Build the list of expenses depending on the group
        ArrayList<Expense> expenseList = new ArrayList<>();

        switch (groupIndex) {
            case 0:
                // Group 0: e.g. "Madrid in French"
                expenseList.add(new Expense(
                        "💶", "Bar à jeux",
                        "Baptiste", 25.00, "€"));
                expenseList.add(new Expense(
                        "🍹", "Soft et sangria",
                        "Erell", 6.00, "€"));
                expenseList.add(new Expense(
                        "🍛", "Repas RL",
                        "Arthur", 19.95, "€"));
                break;

            case 1:
                // Group 1: e.g. "Club de billard"
                expenseList.add(new Expense(
                        "🎱", "Billard",
                        "Filip", 12.00, "€"));
                expenseList.add(new Expense(
                        "🍺", "Drinks",
                        "Antonio", 18.50, "€"));
                break;

            case 2:
                // Group 2: "Barca"
                expenseList.add(new Expense(
                        "🏖️", "Beach bar",
                        "Lisa", 30.00, "€"));
                break;

            case 3:
                // Group 3: "Malaga vacation"
                expenseList.add(new Expense(
                        "🏎️", "Car breakdown",
                        "Pedro", 430.00, "€"));
                break;

            default:
                // no expenses yet
                break;
        }

        // 4. Attach adapter
        ListView listView = findViewById(R.id.listViewExpenses);
        ExpenseAdapter adapter = new ExpenseAdapter(this, expenseList);
        listView.setAdapter(adapter);

        // TextViews in the header
        TextView txtMyExpensesValue = findViewById(R.id.txtMyExpensesValue);
        TextView txtTotalExpensesValue = findViewById(R.id.txtTotalExpensesValue);

        // TODO: adapt this to the logged-in user name
        String currentUserName = "Antonio";

        // Calculate totals
        double totalExpenses = 0.0;
        double myExpenses = 0.0;

        for (Expense e : expenseList) {
            totalExpenses += e.getAmount();

            if (e.getPayer().equalsIgnoreCase(currentUserName)) {
                myExpenses += e.getAmount();
            }
        }

        // Format and set the texts
        String currency = "€";   // or e.getCurrency() if always same

        txtTotalExpensesValue.setText(
                currency + " " + String.format("%.2f", totalExpenses)
        );

        txtMyExpensesValue.setText(
                currency + " " + String.format("%.2f", myExpenses)
        );

    }
}