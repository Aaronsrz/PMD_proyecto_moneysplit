package es.upm.etsiinf.gib.pmd_proyecto.groupdetail;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
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
import es.upm.etsiinf.gib.pmd_proyecto.groupdetail.AddExpense.AddExpenseActivity;
import es.upm.etsiinf.gib.pmd_proyecto.grouplist.GroupListActivity;

public class GroupDetailActivity extends AppCompatActivity {
    private static final int REQUEST_ADD_EXPENSE = 1;

    private ArrayList<Expense> expenseList;
    private ExpenseAdapter adapter;
    private String currentUserName;

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

        currentUserName = getIntent().getStringExtra("CURRENT_USER_NAME");
        if (currentUserName == null) currentUserName = "Unknown";

        // 3. Get the list of expenses for this group
        expenseList = ExpenseRepository.getExpensesForGroup(groupIndex);

        // 4. Attach adapter
        ListView listView = findViewById(R.id.listViewExpenses);
        adapter = new ExpenseAdapter(this, expenseList);
        listView.setAdapter(adapter);

        Button btnAddExpense = findViewById(R.id.btnAddExpense);
        btnAddExpense.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(GroupDetailActivity.this,
                        AddExpenseActivity.class);
                startActivityForResult(intent, REQUEST_ADD_EXPENSE);
            }
        });

        // Initial calculation of totals
        recalculateTotals();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == REQUEST_ADD_EXPENSE && resultCode == RESULT_OK && data != null) {
            String emoji = data.getStringExtra("EXTRA_EMOJI");
            String title = data.getStringExtra("EXTRA_TITLE");
            String payer = data.getStringExtra("EXTRA_PAYER");
            double amount = data.getDoubleExtra("EXTRA_AMOUNT", 0.0);

            Expense newExpense = new Expense(emoji, title, payer, amount);
            expenseList.add(newExpense);
            adapter.notifyDataSetChanged();

            // Recalculate totals after adding
            recalculateTotals();
        }
    }

    private void recalculateTotals() {
        TextView txtMyExpensesValue = findViewById(R.id.txtMyExpensesValue);
        TextView txtTotalExpensesValue = findViewById(R.id.txtTotalExpensesValue);

        double totalExpenses = 0.0;
        double myExpenses = 0.0;

        for (Expense e : expenseList) {
            totalExpenses += e.getAmount();
            if (e.getPayer().equalsIgnoreCase(currentUserName)) {
                myExpenses += e.getAmount();
            }
        }

        txtTotalExpensesValue.setText("€" + " " + String.format("%.2f", totalExpenses));
        txtMyExpensesValue.setText("€" + " " + String.format("%.2f", myExpenses));
    }


}