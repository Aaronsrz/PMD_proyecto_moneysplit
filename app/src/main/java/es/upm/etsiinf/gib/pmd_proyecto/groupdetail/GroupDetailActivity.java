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
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.pm.PackageManager;
import android.os.Build;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.core.content.ContextCompat;
import java.util.ArrayList;

import es.upm.etsiinf.gib.pmd_proyecto.R;
import es.upm.etsiinf.gib.pmd_proyecto.groupdetail.AddExpense.AddExpenseActivity;
import es.upm.etsiinf.gib.pmd_proyecto.grouplist.GroupListActivity;
import android.os.Handler;
import android.os.Looper;

import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class   GroupDetailActivity extends AppCompatActivity {
    private static final int REQUEST_ADD_EXPENSE = 1;
    private static final String CHANNEL_ID = "expenses_channel";
    private ActivityResultLauncher<String> notifPermissionLauncher;
    private ArrayList<Expense> expenseList;
    private ExpenseAdapter adapter;
    private String currentUserName;
    private TextView txtApiResult;
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
        listView.setItemsCanFocus(true); // <-- IMPORTANT for ImageButton clicks inside ListView rows
        adapter = new ExpenseAdapter(this, expenseList, groupName); // include groupName (optional)
        listView.setAdapter(adapter);

        listView.setOnItemClickListener((parent, view, position, id) -> {

            Expense selectedExpense = expenseList.get(position);

            new androidx.appcompat.app.AlertDialog.Builder(GroupDetailActivity.this)
                    .setTitle("Delete expense")
                    .setMessage("Are you sure you want to delete:\n\n" +
                            selectedExpense.getEmoji() + "  " +
                            selectedExpense.getTitle() + " (" +
                            selectedExpense.getPayer() + ", €" +
                            String.format("%.2f", selectedExpense.getAmount()) + ")?")
                    .setPositiveButton("Delete", (dialog, which) -> {

                        // Remove the item
                        expenseList.remove(position);

                        // Refresh list
                        adapter.notifyDataSetChanged();

                        // Update totals
                        recalculateTotals();
                    })
                    .setNegativeButton("Cancel", null)
                    .show();
        });


        Button btnAddExpense = findViewById(R.id.btnAddExpense);
        btnAddExpense.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(GroupDetailActivity.this,
                        AddExpenseActivity.class);
                startActivityForResult(intent, REQUEST_ADD_EXPENSE);
            }
        });

        txtApiResult = findViewById(R.id.txtApiResult);
        fetchRateInBackground();   // starts the background task


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

    private void fetchRateInBackground() {
        ExecutorService executor = Executors.newSingleThreadExecutor();
        Handler handler = new Handler(Looper.getMainLooper());

        txtApiResult.setText("Fetching rate from API...");

        executor.execute(() -> {
            String result = callFixerApi();
            handler.post(() -> txtApiResult.setText(result));
        });
    }

    private String callFixerApi() {
        HttpURLConnection conn = null;
        BufferedReader reader = null;

        try {
            //String apiKey = "adec94ce04be1c287e07f5216c30c80c";
            //Don't use too much during tests! (100 requests/month free plan)

            String apiKey = "YOUR_API_KEY_HERE";   // replace with real key for testing

            if (apiKey.equals("YOUR_API_KEY_HERE")) {
                return "API key missing (demo mode)";
            }


            // URL exactly as in the documentation, but with HTTPS
            URL url = new URL(
                    "https://data.fixer.io/api/latest"
                            + "?access_key=" + apiKey
                            + "&symbols=USD"
            );

            conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");
            conn.connect();

            InputStream in = new BufferedInputStream(conn.getInputStream());
            reader = new BufferedReader(new InputStreamReader(in));
            StringBuilder sb = new StringBuilder();
            String line;

            while ((line = reader.readLine()) != null) {
                sb.append(line);
            }

            String response = sb.toString();

            // 👉 TEMP: show raw JSON to be sure it works
            // return response;

            // Normal case: parse JSON
            JSONObject json = new JSONObject(response);

            // If the API says success = false, show the error message
            if (!json.optBoolean("success", true)) {
                JSONObject error = json.optJSONObject("error");
                String info = (error != null)
                        ? error.optString("info", "Unknown API error")
                        : "Unknown API error";
                return "API error: " + info;
            }

            JSONObject rates = json.getJSONObject("rates");
            double usd = rates.getDouble("USD");

            return "1 EUR = " + usd + " USD (via Fixer)";

        } catch (Exception e) {
            e.printStackTrace();
            return "Exception: " + e.getClass().getSimpleName()
                    + " – " + e.getMessage();
        } finally {
            if (conn != null) conn.disconnect();
            if (reader != null) {
                try { reader.close(); } catch (IOException ignored) {}
            }
        }
    }




}