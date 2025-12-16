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
import es.upm.etsiinf.gib.pmd_proyecto.groupdetail.Currency.CurrencyAdapter;
import es.upm.etsiinf.gib.pmd_proyecto.groupdetail.Currency.CurrencyItem;
import es.upm.etsiinf.gib.pmd_proyecto.grouplist.GroupListActivity;
import android.os.Handler;
import android.os.Looper;
import android.widget.Toast;

import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import java.text.NumberFormat;
import java.util.Locale;

public class   GroupDetailActivity extends AppCompatActivity {
    private static final String CHANNEL_ID = "expenses_channel";
    private ActivityResultLauncher<String> notifPermissionLauncher;
    private ArrayList<Expense> expenseList;
    private ExpenseAdapter adapter;
    private ActivityResultLauncher<Intent> addExpenseLauncher;
    private String currentUserName;
    private TextView txtApiResult;
    private ListView listViewRates;
    private ArrayList<CurrencyItem> rateItems;
    private CurrencyAdapter rateAdapter;
    private double lastTotalExpenses = 0.0;
    private TextView txtMyExpensesValue;
    private TextView txtTotalExpensesValue;
    private ListView listViewExpenses;
    private final NumberFormat moneyFormat = NumberFormat.getNumberInstance(Locale.US);

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

        moneyFormat.setMinimumFractionDigits(2);
        moneyFormat.setMaximumFractionDigits(2);

        txtMyExpensesValue = findViewById(R.id.txtMyExpensesValue);
        txtTotalExpensesValue = findViewById(R.id.txtTotalExpensesValue);

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
        listViewExpenses = findViewById(R.id.listViewExpenses);
        listViewExpenses.setItemsCanFocus(true);
        adapter = new ExpenseAdapter(this, expenseList, groupName);
        listViewExpenses.setAdapter(adapter);

        listViewExpenses.setOnItemClickListener((parent, view, position, id) -> {

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

        addExpenseLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == RESULT_OK && result.getData() != null) {
                        Intent data = result.getData();

                        String emoji = data.getStringExtra("EXTRA_EMOJI");
                        String title = data.getStringExtra("EXTRA_TITLE");
                        String payer = data.getStringExtra("EXTRA_PAYER");
                        double amount = data.getDoubleExtra("EXTRA_AMOUNT", 0.0);

                        // Safety check
                        if (emoji == null || title == null || payer == null) return;

                        expenseList.add(new Expense(emoji, title, payer, amount));
                        adapter.notifyDataSetChanged();
                        recalculateTotals();

                        listViewExpenses.post(() -> {
                            // Ensure ListView accepts programmatic scroll
                            listViewExpenses.requestFocusFromTouch();

                            // Hard refresh (helps when UI feels "laggy")
                            listViewExpenses.invalidateViews();

                            // Jump to last item (most reliable)
                            int last = adapter.getCount() - 1;
                            if (last >= 0) {
                                listViewExpenses.setSelection(last);
                            }
                        });
                    }
                }
        );


        Button btnAddExpense = findViewById(R.id.btnAddExpense);
        btnAddExpense.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(GroupDetailActivity.this,
                        AddExpenseActivity.class);
                addExpenseLauncher.launch(intent);
            }
        });

        txtApiResult = findViewById(R.id.txtApiResult);

        listViewRates = findViewById(R.id.listViewRates);
        rateItems = new ArrayList<>();
        rateAdapter = new CurrencyAdapter(this, rateItems);
        listViewRates.setAdapter(rateAdapter);

        listViewRates.setOnItemClickListener((parent, view, position, id) -> {
            CurrencyItem item = rateItems.get(position);

            double rate = item.getRate();
            String code = item.getCode();

            double converted = lastTotalExpenses * rate;

            new androidx.appcompat.app.AlertDialog.Builder(GroupDetailActivity.this)
                    .setTitle("Group total in " + code)
                    .setMessage(String.format(
                            "Current total: € %.2f\nApproximate in %s: %.2f %s",
                            lastTotalExpenses,
                            code, converted, code
                    ))
                    .setPositiveButton("OK", null)
                    .show();
        });

        fetchRatesInBackground();

        // Initial calculation of totals
        recalculateTotals();
    }

    private void recalculateTotals() {
        double totalExpenses = 0.0;
        double myExpenses = 0.0;

        for (Expense e : expenseList) {
            totalExpenses += e.getAmount();
            if (e.getPayer().equalsIgnoreCase(currentUserName)) {
                myExpenses += e.getAmount();
            }
        }

        lastTotalExpenses = totalExpenses;

        txtTotalExpensesValue.setText("€ " + moneyFormat.format(totalExpenses));
        txtMyExpensesValue.setText("€ " + moneyFormat.format(myExpenses));
    }


    private List<CurrencyItem> callFixerForList() {
        List<CurrencyItem> list = new ArrayList<>();
        HttpURLConnection conn = null;
        BufferedReader reader = null;

        try {
            //String apiKey = "adec94ce04be1c287e07f5216c30c80c";
            //Don't use too much during tests! (100 requests/month free plan)
            String apiKey = "YOUR_API_KEY_HERE";

            URL url = new URL(
                    "https://data.fixer.io/api/latest"
                            + "?access_key=" + apiKey
                            + "&symbols=USD,GBP,CHF"
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

            JSONObject json = new JSONObject(response);

            // If Fixer returns an error (success=false), stop here
            if (!json.optBoolean("success", true)) {
                JSONObject error = json.optJSONObject("error");
                String info = (error != null) ? error.optString("info", "API error") : "API error";
                txtApiResult.setText("Fixer error: " + info);
                return list;  // empty
            }

            JSONObject rates = json.getJSONObject("rates");

            // Add a row for each currency
            if (rates.has("USD")) {
                double usd = rates.getDouble("USD");
                list.add(new CurrencyItem("USD", usd, R.drawable.ic_flag_us));
            }

            if (rates.has("GBP")) {
                double gbp = rates.getDouble("GBP");
                list.add(new CurrencyItem("GBP", gbp, R.drawable.ic_flag_uk));
            }

            if (rates.has("CHF")) {
                double chf = rates.getDouble("CHF");
                list.add(new CurrencyItem("CHF", chf, R.drawable.ic_flag_ch));
            }

        } catch (Exception e) {
            e.printStackTrace();
            txtApiResult.setText("Exception: " + e.getClass().getSimpleName());
            // return empty list
        } finally {
            if (conn != null) conn.disconnect();
            if (reader != null) {
                try { reader.close(); } catch (IOException ignored) {}
            }
        }

        return list;
    }



    private void fetchRatesInBackground() {
        ExecutorService executor = Executors.newSingleThreadExecutor();
        Handler handler = new Handler(Looper.getMainLooper());

        executor.execute(() -> {
            List<CurrencyItem> result = callFixerForList();

            handler.post(() -> {
                rateItems.clear();
                rateItems.addAll(result);
                rateAdapter.notifyDataSetChanged();

                if (result.isEmpty()) {
                    txtApiResult.setText("No exchange rates available");
                } else {
                    // REMOVE the message once list is shown
                    txtApiResult.setVisibility(View.GONE);
                }
            });
        });
    }




}