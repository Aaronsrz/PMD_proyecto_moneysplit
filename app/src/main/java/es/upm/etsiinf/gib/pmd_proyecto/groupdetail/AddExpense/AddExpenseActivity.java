package es.upm.etsiinf.gib.pmd_proyecto.groupdetail.AddExpense;

import android.Manifest;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import es.upm.etsiinf.gib.pmd_proyecto.R;

public class AddExpenseActivity extends AppCompatActivity {

    private Uri billPhotoUri;

    private ActivityResultLauncher<String> requestCameraPermissionLauncher;
    private ActivityResultLauncher<Uri> takePictureLauncher;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_expense);

        Spinner spinnerEmoji = findViewById(R.id.spinnerEmoji);
        EditText edtTitle = findViewById(R.id.addExpenseTitle);
        EditText edtAmount = findViewById(R.id.addExpenseAmount);
        EditText edtPayer = findViewById(R.id.addExpensePayer);

        Button btnConfirm = findViewById(R.id.btnConfirmAddExpense);
        Button btnAddBill = findViewById(R.id.btnAddBill);

        ImageButton btnBack = findViewById(R.id.btnBackAddExpense);

        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        // Emoji options
        String[] emojis = {"💶", "🍔", "🚕", "🍹", "🍛", "🎱", "🍺", "🏖️", "🏎️"};

        ArrayAdapter<String> emojiAdapter = new ArrayAdapter<>(
                this, R.layout.item_emoji, emojis);
        emojiAdapter.setDropDownViewResource(R.layout.item_emoji);
        spinnerEmoji.setAdapter(emojiAdapter);

        // 1) Launcher that actually takes the picture and saves it to billPhotoUri
        takePictureLauncher = registerForActivityResult(
                new ActivityResultContracts.TakePicture(),
                success -> {
                    if (success) {
                        Toast.makeText(this, "Bill photo captured ✅", Toast.LENGTH_SHORT).show();
                    } else {
                        billPhotoUri = null;
                        Toast.makeText(this, "Cancelled", Toast.LENGTH_SHORT).show();
                    }
                }
        );

        // 2) Launcher that asks for CAMERA permission
        requestCameraPermissionLauncher = registerForActivityResult(
                new ActivityResultContracts.RequestPermission(),
                granted -> {
                    if (granted) {
                        openCameraAndCapture();
                    } else {
                        Toast.makeText(this, "Camera permission denied", Toast.LENGTH_SHORT).show();
                    }
                }
        );

        // Add bill button
        btnAddBill.setOnClickListener(v -> {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA)
                    == PackageManager.PERMISSION_GRANTED) {
                openCameraAndCapture();
            } else {
                requestCameraPermissionLauncher.launch(Manifest.permission.CAMERA);
            }
        });

        // Confirm expense button (existing behavior + include bill uri if present)
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

                Intent result = new Intent();
                result.putExtra("EXTRA_EMOJI", emoji);
                result.putExtra("EXTRA_TITLE", title);
                result.putExtra("EXTRA_PAYER", payer);
                result.putExtra("EXTRA_AMOUNT", amount);

                if (billPhotoUri != null) {
                    result.putExtra("EXTRA_BILL_URI", billPhotoUri.toString());
                }

                setResult(RESULT_OK, result);
                finish();
            }
        });
    }

    private void openCameraAndCapture() {
        try {
            File imageFile = createBillImageFile();
            billPhotoUri = FileProvider.getUriForFile(
                    this,
                    getPackageName() + ".fileprovider",
                    imageFile
            );
            takePictureLauncher.launch(billPhotoUri);
        } catch (IOException e) {
            billPhotoUri = null;
            Toast.makeText(this, "Could not create image file", Toast.LENGTH_SHORT).show();
        }
    }

    private File createBillImageFile() throws IOException {
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.US).format(new Date());
        String imageFileName = "BILL_" + timeStamp + "_";

        File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        if (storageDir == null) storageDir = getCacheDir();

        return File.createTempFile(imageFileName, ".jpg", storageDir);
    }


}
