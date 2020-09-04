package ax.stardust.skvirrel.service;

import android.app.IntentService;
import android.app.PendingIntent;
import android.content.Intent;
import android.util.Log;

import androidx.annotation.Nullable;

import org.apache.commons.lang3.StringUtils;

import java.io.IOException;
import java.util.Objects;

import ax.stardust.skvirrel.exception.StockNotFoundException;
import ax.stardust.skvirrel.parcelable.ParcelableStock;
import yahoofinance.Stock;
import yahoofinance.YahooFinance;

import static ax.stardust.skvirrel.service.ServiceParams.Operation.GET_COMPANY_NAME;
import static ax.stardust.skvirrel.service.ServiceParams.Operation.GET_STOCK_INFO;

/**
 * Service for communicating with Yahoo Finance API.
 */
public class StockService extends IntentService {
    private static final String TAG = StockService.class.getSimpleName();

    public StockService() {
        super(TAG);
    }

    private void validateStock(Stock stock) throws StockNotFoundException {
        if (stock == null || !stock.isValid() || StringUtils.isNumeric(stock.getName())) {
            throw new StockNotFoundException();
        }
    }

    @Override
    protected void onHandleIntent(@Nullable Intent intent) {
        if (intent != null) {
            PendingIntent reply = intent.getParcelableExtra(ServiceParams.PENDING_RESULT);
            if (reply != null) {
                Intent result = new Intent();
                result.putExtra(ServiceParams.STOCK_FRAGMENT_TAG, intent.getStringExtra(ServiceParams.STOCK_FRAGMENT_TAG));
                try {
                    try {
                        String operation = intent.getStringExtra(ServiceParams.STOCK_SERVICE);
                        Stock stock;
                        switch (operation) {
                            case GET_COMPANY_NAME:
                                stock = YahooFinance.get(Objects.requireNonNull(intent.getStringExtra(ServiceParams.RequestExtra.SYMBOL)));
                                validateStock(stock);

                                Log.d(TAG, "onHandleIntent(...) ->  Successfully fetched stock -> " + stock.getName());

                                result.putExtra(ServiceParams.ResultExtra.COMPANY_NAME, stock.getName());
                                reply.send(this, ServiceParams.ResultCode.SUCCESS, result);

                                break;
                            case GET_STOCK_INFO:
                                stock = YahooFinance.get(Objects.requireNonNull(intent.getStringExtra(ServiceParams.RequestExtra.SYMBOL)));
                                validateStock(stock);

                                Log.d(TAG, "onHandleIntent(...) ->  Successfully fetched stock -> " + stock.getName());

                                result.putExtra(ServiceParams.ResultExtra.STOCK_INFO, ParcelableStock.from(stock));
                                reply.send(this, ServiceParams.ResultCode.SUCCESS, result);

                                break;
                            default:
                                Log.e(TAG, "onHandleIntent(...) -> Unsupported operation for request");
                                result.putExtra(ServiceParams.ERROR_SITUATION, "Unsupported operation -> " + intent.getStringExtra(ServiceParams.STOCK_SERVICE));
                                reply.send(this, ServiceParams.ResultCode.COMMON_ERROR, result);
                        }
                    } catch (StockNotFoundException e) {
                        Log.e(TAG, "onHandleIntent(...) -> Stock was not found at Yahoo Finance", e);
                        result.putExtra(ServiceParams.ERROR_SITUATION, "Stock was not found at Yahoo Finance");
                        reply.send(this, ServiceParams.ResultCode.STOCK_NOT_FOUND_ERROR, result);
                    } catch (IOException e) {
                        Log.e(TAG, "onHandleIntent(...) -> Something went wrong invoking YahooFinanceAPI", e);
                        result.putExtra(ServiceParams.ERROR_SITUATION, "Something went wrong invoking YahooFinanceAPI");
                        reply.send(this, ServiceParams.ResultCode.COMMON_ERROR, result);
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
