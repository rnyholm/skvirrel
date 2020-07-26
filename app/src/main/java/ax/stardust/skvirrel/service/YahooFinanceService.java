package ax.stardust.skvirrel.service;

import android.app.IntentService;
import android.app.PendingIntent;
import android.content.Intent;
import android.util.Log;

import androidx.annotation.Nullable;

import java.io.IOException;
import java.util.Objects;

import yahoofinance.Stock;
import yahoofinance.YahooFinance;

public class YahooFinanceService extends IntentService {
    private static final String LOG_TAG = YahooFinanceService.class.getSimpleName();

    public static final String PENDING_RESULT = "pending_result";
    public static final String YAHOO_FINANCE_API_OPERATION = "yahoo_finance_api_operation";
    public static final String STOCK_SYMBOL = "stock_symbol";
    public static final String GET = "get";
    public static final String STOCK_NAME = "stock_name";
    public static final String STOCK_INFO = "stock_info";
    public static final String ERROR_SITUATION = "error_situation";

    public static final int REQUEST_SUCESS_CODE = 0;
    public static final int REQUEST_ERROR_CODE = -1;

    public YahooFinanceService() {
        super(LOG_TAG);
    }

    @Override
    protected void onHandleIntent(@Nullable Intent intent) {
        if (intent != null) {
            PendingIntent reply = intent.getParcelableExtra(PENDING_RESULT);
            if (reply != null) {
                Intent result = new Intent();
                try {
                    try {
                        if (GET.equals(intent.getStringExtra(YAHOO_FINANCE_API_OPERATION))) {
                            Stock stock = YahooFinance.get(Objects.requireNonNull(intent.getStringExtra(STOCK_SYMBOL)));
                            String info = "Name: " + stock.getName() + "\n" +
                                    "Symbol: " + stock.getSymbol() + "\n" +
                                    "Stock exchange: " + stock.getStockExchange() + "\n" +
                                    "Currency: " + stock.getCurrency() + "\n" +
                                    "Price: " + stock.getQuote().getPrice() + "\n" +
                                    "Bid: " + stock.getQuote().getBid() + "\n" +
                                    "Ask: " + stock.getQuote().getAsk() + "\n" +
                                    "Prev close: " + stock.getQuote().getPreviousClose() + "\n";
                            result.putExtra(STOCK_INFO, info);
                            result.putExtra(STOCK_NAME, stock.getName());
                            reply.send(this, REQUEST_SUCESS_CODE, result);
                        } else {
                            Log.e(LOG_TAG, "onHandleIntent(...) -> Unsupported operation for request");
                            result.putExtra(ERROR_SITUATION, "Unsupported operation");
                            reply.send(this, REQUEST_ERROR_CODE, result);
                        }
                    } catch (IOException e) {
                        Log.e(LOG_TAG, "onHandleIntent(...) -> Something went wrong invoking YahooFinanceAPI", e);
                        result.putExtra(ERROR_SITUATION, "Something went wrong invoking YahooFinanceAPI");
                        reply.send(this, REQUEST_ERROR_CODE, result);
                    }
                } catch (PendingIntent.CanceledException e) {
                    Log.i(LOG_TAG, "Reply cancelled", e);
                }
            } else {
                Log.e(LOG_TAG, "onHandleIntent(...) -> No PendingIntent was passed in with intent");
            }
        } else {
            Log.e(LOG_TAG, "onHandleIntent(...) -> invoked with null intent");
        }
    }
}
