package ax.stardust.skvirrel.activity;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import java.io.IOException;

import ax.stardust.skvirrel.R;
import ax.stardust.skvirrel.component.keyboard.KeyboardHandler;
import ax.stardust.skvirrel.component.keyboard.SkvirrelKeyboard;
import ax.stardust.skvirrel.component.widget.KeyboardlessEditText;
import yahoofinance.Stock;
import yahoofinance.YahooFinance;

public class Skvirrel extends AppCompatActivity {
    private static final String LOG_TAG = Skvirrel.class.getSimpleName();

    private SkvirrelKeyboard skvirrelKeyboard;

    private TextView companyTextView;
    private TextView debugStockInfoTextView;
    private TextView versionNameTextView;

    private Button pollStockButton;

    private KeyboardlessEditText tickerEditText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_skvirrel);
        findViews();

        tickerEditText.setText("AMD");

        pollStockButton.setOnClickListener(view -> {
            StringBuilder sb = new StringBuilder();

            // dangerous as fuck and discouraged in android, do this in other service later
            new Thread(() -> {
                // Do network action in this function
                try {
                    Stock stock = YahooFinance.get(tickerEditText.getText().toString());
                    Log.d(LOG_TAG, stock.toString());

                    sb.append("Name: " + stock.getName() + "\n");
                    sb.append("Symbol: " + stock.getSymbol() + "\n");
                    sb.append("Stock exchange: " + stock.getStockExchange() + "\n");
                    sb.append("Currency: " + stock.getCurrency() + "\n");
                    sb.append("Price: " + stock.getQuote().getPrice() + "\n");
                    sb.append("Bid: " + stock.getQuote().getBid() + "\n");
                    sb.append("Ask: " + stock.getQuote().getAsk() + "\n");
                    sb.append("Prev close: " + stock.getQuote().getPreviousClose() + "\n");

                    companyTextView.setText(stock.getName());
                } catch (IOException e) {
                    e.printStackTrace();
                    Log.e(LOG_TAG, "Something wen't wrong fetching stock info", e);
                }
            }).start();
            debugStockInfoTextView.setText(sb.toString());
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

        tickerEditText = findViewById(R.id.ticker_et);
        tickerEditText.setOnFocusChangeListener(new KeyboardHandler(skvirrelKeyboard));
        tickerEditText.setOnTouchListener(new KeyboardHandler(skvirrelKeyboard));
    }
}