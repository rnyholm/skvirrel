package ax.stardust.skvirrel.service;

import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import androidx.annotation.Nullable;
import androidx.core.app.JobIntentService;

import org.apache.commons.lang3.StringUtils;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Objects;

import ax.stardust.skvirrel.exception.StockNotFoundException;
import ax.stardust.skvirrel.parcelable.ParcelableStock;
import ax.stardust.skvirrel.receiver.MonitoringReceiver;
import yahoofinance.Stock;
import yahoofinance.YahooFinance;

import static ax.stardust.skvirrel.service.ServiceParams.Operation.GET_COMPANY_NAME;
import static ax.stardust.skvirrel.service.ServiceParams.Operation.GET_STOCK_INFOS;
import static ax.stardust.skvirrel.service.ServiceParams.Operation.GET_STOCK_INFO;

/**
 * Service for communicating with Yahoo Finance API.
 */
public class StockService extends JobIntentService {
    private static final String TAG = StockService.class.getSimpleName();

    public static void enqueueWork(Context context, Intent intent) {
        enqueueWork(context, StockService.class, ServiceParams.STOCK_SERVICE_ID, intent);
    }

    @Override
    protected void onHandleWork(@Nullable Intent intent) {
        if (intent != null) {
            PendingIntent reply = intent.getParcelableExtra(ServiceParams.PENDING_RESULT);
            String operation = intent.getStringExtra(ServiceParams.STOCK_SERVICE);

            if (operation != null) {
                // for all operations except get several stock info's we need to have a reply
                if (reply != null || GET_STOCK_INFOS.equals(operation)) {
                    Intent result = new Intent();

                    // set fragment tag if possible
                    if (intent.hasExtra(ServiceParams.STOCK_FRAGMENT_TAG)) {
                        result.putExtra(ServiceParams.STOCK_FRAGMENT_TAG, intent.getStringExtra(ServiceParams.STOCK_FRAGMENT_TAG));
                    }

                    try {
                        try {
                            Stock stock;
                            switch (operation) {
                                case GET_COMPANY_NAME:
                                    stock = YahooFinance.get(Objects.requireNonNull(intent.getStringExtra(ServiceParams.RequestExtra.SYMBOL)));
                                    validateStock(stock);

                                    Log.d(TAG, "onHandleWork: Successfully fetched stock -> " + stock.getName());

                                    result.putExtra(ServiceParams.ResultExtra.COMPANY_NAME, stock.getName());
                                    reply.send(this, ServiceParams.ResultCode.SUCCESS, result);

                                    break;
                                case GET_STOCK_INFO:
                                    stock = YahooFinance.get(Objects.requireNonNull(intent.getStringExtra(ServiceParams.RequestExtra.SYMBOL)));
                                    validateStock(stock);

                                    Log.d(TAG, "onHandleWork: Successfully fetched stock -> " + stock.getName());

                                    result.putExtra(ServiceParams.ResultExtra.STOCK_INFO, ParcelableStock.from(stock));
                                    reply.send(this, ServiceParams.ResultCode.SUCCESS, result);

                                    break;
                                case GET_STOCK_INFOS:
                                    ArrayList<String> symbols = intent.getStringArrayListExtra(ServiceParams.RequestExtra.SYMBOLS);
                                    ArrayList<ParcelableStock> parcelableStocks = new ArrayList<>();

                                    if (symbols != null) {
                                        symbols.forEach(symbol -> {
                                            try {
                                                Stock s = YahooFinance.get(Objects.requireNonNull(symbol));
                                                validateStock(s);
                                                parcelableStocks.add(ParcelableStock.from(s));
                                            } catch (Exception e) {
                                                Log.e(TAG, "onHandleWork: Something went wrong while fetching stock info for symbol -> " + symbol, e);
                                            }
                                        });
                                    } else {
                                        Log.d(TAG, "onHandleWork: Unable to fetch several stock info's as no symbols was provided");
                                    }

                                    // send broadcast
                                    final Intent receiverIntent = new Intent(this, MonitoringReceiver.class);
                                    receiverIntent.setAction(MonitoringReceiver.STOCK_INFOS_FETCHED);
                                    receiverIntent.putParcelableArrayListExtra(ServiceParams.ResultExtra.STOCK_INFOS, parcelableStocks);
                                    sendBroadcast(receiverIntent);

                                    // at last notify the job service that job has finished
                                    MonitoringJobService.Handler.getInstance().notifyJobFinished();

                                    break;
                                default:
                                    Log.e(TAG, "onHandleWork: Unsupported operation for request");
                                    result.putExtra(ServiceParams.ERROR_SITUATION, "Unsupported operation -> " + intent.getStringExtra(ServiceParams.STOCK_SERVICE));
                                    reply.send(this, ServiceParams.ResultCode.COMMON_ERROR, result);
                            }
                        } catch (StockNotFoundException e) {
                            Log.e(TAG, "onHandleWork: Stock was not found at Yahoo Finance", e);
                            result.putExtra(ServiceParams.ERROR_SITUATION, "Stock was not found at Yahoo Finance");
                            reply.send(this, ServiceParams.ResultCode.STOCK_NOT_FOUND_ERROR, result);
                        } catch (IOException e) {
                            Log.e(TAG, "onHandleWork: Something went wrong invoking YahooFinanceAPI", e);
                            result.putExtra(ServiceParams.ERROR_SITUATION, "Something went wrong invoking YahooFinanceAPI");
                            reply.send(this, ServiceParams.ResultCode.COMMON_ERROR, result);
                        }
                    } catch (PendingIntent.CanceledException e) {
                        Log.i(TAG, "onHandleWork: Reply cancelled", e);
                    }
                } else {
                    Log.e(TAG, "onHandleWork: No PendingResult was passed in with intent");
                }
            } else {
                Log.e(TAG, "onHandleWork: No stock service operation was passed in with intent");
            }
        } else {
            Log.e(TAG, "onHandleWork: invoked with null intent");
        }
    }

    private void validateStock(Stock stock) throws StockNotFoundException {
        if (stock == null || !stock.isValid() || StringUtils.isNumeric(stock.getName())) {
            throw new StockNotFoundException();
        }
    }
}
