package ax.stardust.skvirrel.service;

import android.app.IntentService;
import android.app.PendingIntent;
import android.content.Intent;
import android.util.Log;

import androidx.annotation.Nullable;

import java.io.IOException;
import java.util.Objects;

import ax.stardust.skvirrel.parcelable.ParcelableStock;
import ax.stardust.skvirrel.service.ServiceParams.Operation;
import yahoofinance.Stock;
import yahoofinance.YahooFinance;

/**
 * Service for communicating with Yahoo Finance API.
 */
public class StockService extends IntentService {
    private static final String TAG = StockService.class.getSimpleName();

    public StockService() {
        super(TAG);
    }

    @Override
    protected void onHandleIntent(@Nullable Intent intent) {
        if (intent != null) {
            PendingIntent reply = intent.getParcelableExtra(ServiceParams.PENDING_RESULT);
            if (reply != null) {
                Intent result = new Intent();
                try {
                    try {
                        Operation operation = Operation.from(intent.getStringExtra(ServiceParams.STOCK_SERVICE));
                        Stock stock;
                        switch (operation) {
                            case GET_COMPANY_NAME:
                                stock = YahooFinance.get(Objects.requireNonNull(intent.getStringExtra(ServiceParams.RequestExtra.SYMBOL.get())));
                                Log.d(TAG, "onHandleIntent(...) ->  Successfully fetched stock -> " + stock.getName());

                                result.putExtra(ServiceParams.ResultExtra.COMPANY_NAME.get(), stock.getName());
                                reply.send(this, ServiceParams.ResultCode.SUCCESS.getCode(), result);

                                break;
                            case GET_STOCK_INFO:
                                stock = YahooFinance.get(Objects.requireNonNull(intent.getStringExtra(ServiceParams.RequestExtra.SYMBOL.get())));
                                Log.d(TAG, "onHandleIntent(...) ->  Successfully fetched stock -> " + stock.getName());

                                result.putExtra(ServiceParams.ResultExtra.STOCK_INFO.get(), ParcelableStock.from(stock));
                                reply.send(this, ServiceParams.ResultCode.SUCCESS.getCode(), result);

                                break;
                            default:
                                Log.e(TAG, "onHandleIntent(...) -> Unsupported operation for request");
                                result.putExtra(ServiceParams.ERROR_SITUATION, "Unsupported operation -> " + intent.getStringExtra(ServiceParams.STOCK_SERVICE));
                                reply.send(this, ServiceParams.ResultCode.ERROR.getCode(), result);
                        }
                    } catch (IOException e) {
                        Log.e(TAG, "onHandleIntent(...) -> Something went wrong invoking YahooFinanceAPI", e);
                        result.putExtra(ServiceParams.ERROR_SITUATION, "Something went wrong invoking YahooFinanceAPI");
                        reply.send(this, ServiceParams.ResultCode.ERROR.getCode(), result);
                    }
                } catch (PendingIntent.CanceledException e) {
                    Log.i(TAG, "Reply cancelled", e);
                }
            } else {
                Log.e(TAG, "onHandleIntent(...) -> No PendingIntent was passed in with intent");
            }
        } else {
            Log.e(TAG, "onHandleIntent(...) -> invoked with null intent");
        }
    }
}
