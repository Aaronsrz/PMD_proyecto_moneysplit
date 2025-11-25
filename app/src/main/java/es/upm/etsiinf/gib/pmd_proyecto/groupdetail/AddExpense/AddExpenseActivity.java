package es.upm.etsiinf.gib.pmd_proyecto.groupdetail.AddExpense;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import es.upm.etsiinf.gib.pmd_proyecto.R;

public class AddExpenseActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_expense);

        Spinner spinnerEmoji = findViewById(R.id.spinnerEmoji);
        EditText edtTitle = findViewById(R.id.addExpenseTitle);
        EditText edtAmount = findViewById(R.id.addExpenseAmount);
        EditText edtPayer = findViewById(R.id.addExpensePayer);
        Button btnConfirm = findViewById(R.id.btnConfirmAddExpense);

        ImageButton btnBack = findViewById(R.id.btnBackAddExpense);

        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Go back to the GroupListActivity
                finish();
            }
        });

        // Emoji options
        String[] emojis = {"💶", "🍔", "🚕", "🍹", "🍛", "🎱", "🍺", "🏖️", "🏎️"};

        // Adapters
        ArrayAdapter<String> emojiAdapter = new ArrayAdapter<>(
                this, R.layout.item_emoji, emojis);
        emojiAdapter.setDropDownViewResource(R.layout.item_emoji);
        spinnerEmoji.setAdapter(emojiAdapter);

        btnConfirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String emoji = spinnerEmoji.getSelectedItem().toString();
                String title = edtTitle.getText().toString().trim();
                String payer = edtPayer.getText().toString().trim();
                String amountStr = edtAmount.getText().toString().trim();

                if (title.isEmpty() || payer.isEmpty() || amountStr.isEmpty()) {
                    Toast.makeText(AddExpenseActivity.this,
                            "Please fill all fields", Toast.LENGTH_SHORT).show();
                    return;
                }

                amountStr = amountStr.replace(',', '.');

                double amount;
                try {
                    amount = Double.parseDouble(amountStr);
                } catch (NumberFormatException e) {
                    Toast.makeText(AddExpenseActivity.this,
                            "Invalid amount", Toast.LENGTH_SHORT).show();
                    return;
                }

                // Put data into result Intent
                Intent result = new Intent();
                result.putExtra("EXTRA_EMOJI", emoji);
                result.putExtra("EXTRA_TITLE", title);
                result.putExtra("EXTRA_PAYER", payer);
                result.putExtra("EXTRA_AMOUNT", amount);

                setResult(RESULT_OK, result);
                finish();   // close AddExpenseActivity and return to GroupDetailActivity
            }
        });
    }
}