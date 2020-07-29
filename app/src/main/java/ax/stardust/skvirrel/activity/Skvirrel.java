package ax.stardust.skvirrel.activity;

import android.app.PendingIntent;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import ax.stardust.skvirrel.R;
import ax.stardust.skvirrel.component.keyboard.KeyboardHandler;
import ax.stardust.skvirrel.component.keyboard.SkvirrelKeyboard;
import ax.stardust.skvirrel.component.widget.KeyboardlessEditText;
import ax.stardust.skvirrel.service.YahooFinanceService;

public class Skvirrel extends AppCompatActivity {
    private static final String LOG_TAG = Skvirrel.class.getSimpleName();

    private static final int GET_STOCK_QUOTE_REQUEST_CODE = 0;

    private SkvirrelKeyboard skvirrelKeyboard;

    private TextView companyTextView;
    private TextView debugStockInfoTextView;
    private TextView versionNameTextView;

    private Button pollStockButton;

    private KeyboardlessEditText symbolEditText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_skvirrel);
        findViews();

        symbolEditText.setText("AMD");

        pollStockButton.setOnClickListener(view -> {
            PendingIntent pendingResult = createPendingResult(GET_STOCK_QUOTE_REQUEST_CODE, new Intent(), 0);
            Intent intent = new Intent(getApplicationContext(), YahooFinanceService.class);
            intent.putExtra(YahooFinanceService.YAHOO_FINANCE_API_OPERATION, YahooFinanceService.GET);
            intent.putExtra(YahooFinanceService.STOCK_SYMBOL, symbolEditText.getText().toString());
            intent.putExtra(YahooFinanceService.PENDING_RESULT, pendingResult);
            startService(intent);
        });
    }

    @Override
    public void onBackPressed() {
        if (skvirrelKeyboard.getVisibility() == View.VISIBLE) {
            // hacky way to release focus from any edit text, by releasing it also the keyboard will be closed
            versionNameTextView.requestFocus();
        } else {
            super.onBackPressed();
        }
    }

    private void findViews() {
        skvirrelKeyboard = findViewById(R.id.soft_keyboard);

        companyTextView = findViewById(R.id.company_tv);
        debugStockInfoTextView = findViewById(R.id.debug_stock_info_tv);
        versionNameTextView = findViewById(R.id.version_name_tv);

        pollStockButton = findViewById(R.id.poll_stock_btn);

        symbolEditText = findViewById(R.id.symbol_et);
        symbolEditText.setOnFocusChangeListener(new KeyboardHandler(skvirrelKeyboard));
        symbolEditText.setOnTouchListener(new KeyboardHandler(skvirrelKeyboard));
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if (requestCode == GET_STOCK_QUOTE_REQUEST_CODE) {
            if (resultCode == YahooFinanceService.REQUEST_SUCESS_CODE) {
                debugStockInfoTextView.setText(data.getStringExtra(YahooFinanceService.STOCK_INFO));
                companyTextView.setText(data.getStringExtra(YahooFinanceService.STOCK_NAME));
            } else {
                Toast.makeText(getApplicationContext(), data.getStringExtra(YahooFinanceService.ERROR_SITUATION), Toast.LENGTH_LONG).show();
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }
}