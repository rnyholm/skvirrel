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

/**
 * Service for communicating with Yahoo Finance API.
 */
public class StockService extends IntentService {
    private static final String LOG_TAG = StockService.class.getSimpleName();

    public StockService() {
        super(LOG_TAG);
    }

    @Override
    protected void onHandleIntent(@Nullable Intent intent) {
        if (intent != null) {
            PendingIntent reply = intent.getParcelableExtra(ServiceParams.PENDING_RESULT);
            if (reply != null) {
                Intent result = new Intent();
                try {
                    try {
                        if (ServiceParams.Operation.GET_COMPANY_NAME.get().equals(intent.getStringExtra(ServiceParams.STOCK_SERVICE))) {
                            Stock stock = YahooFinance.get(Objects.requireNonNull(intent.getStringExtra(ServiceParams.RequestExtra.SYMBOL.get())));
                            Log.d(LOG_TAG, "onHandleIntent(...) ->  Successfully fetched stock -> " + stock.getName());

                            result.putExtra(ServiceParams.ResultExtra.COMPANY_NAME.get(), stock.getName());
                            reply.send(this, ServiceParams.ResultCode.SUCCESS.getCode(), result);
                        } else {
                            Log.e(LOG_TAG, "onHandleIntent(...) -> Unsupported operation for request");
                            result.putExtra(ServiceParams.ERROR_SITUATION, "Unsupported operation -> " + intent.getStringExtra(ServiceParams.STOCK_SERVICE));
                            reply.send(this, ServiceParams.ResultCode.ERROR.getCode(), result);
                        }
                    } catch (IOException e) {
                        Log.e(LOG_TAG, "onHandleIntent(...) -> Something went wrong invoking YahooFinanceAPI", e);
                        result.putExtra(ServiceParams.ERROR_SITUATION, "Something went wrong invoking YahooFinanceAPI");
                        reply.send(this, ServiceParams.ResultCode.ERROR.getCode(), result);
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
