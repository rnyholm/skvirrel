package ax.stardust.skvirrel.service;

import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;

import androidx.annotation.Nullable;
import androidx.core.app.JobIntentService;

import org.apache.commons.lang3.StringUtils;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Objects;

import ax.stardust.skvirrel.exception.StockNotFoundException;
import ax.stardust.skvirrel.parcelable.ParcelableStock;
import ax.stardust.skvirrel.receiver.MonitoringReceiver;
import timber.log.Timber;
import yahoofinance.Stock;
import yahoofinance.YahooFinance;

import static ax.stardust.skvirrel.service.ServiceParams.Operation.GET_COMPANY_NAME;
import static ax.stardust.skvirrel.service.ServiceParams.Operation.GET_STOCK_INFO;
import static ax.stardust.skvirrel.service.ServiceParams.Operation.GET_STOCK_INFOS;

/**
 * Service for communicating with Yahoo Finance API.
 */
public class StockService extends JobIntentService {

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

                                    Timber.d("onHandleWork: Successfully fetched stock: %s", stock.getName());

                                    result.putExtra(ServiceParams.ResultExtra.COMPANY_NAME, stock.getName());
                                    reply.send(this, ServiceParams.ResultCode.SUCCESS, result);

                                    break;
                                case GET_STOCK_INFO:
                                    stock = YahooFinance.get(Objects.requireNonNull(intent.getStringExtra(ServiceParams.RequestExtra.SYMBOL)));
                                    validateStock(stock);

                                    Timber.d("onHandleWork: Successfully fetched stock: %s", stock.getName());

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
                                                Timber.e(e, "onHandleWork: Something went wrong while fetching stock info for symbol: %s", symbol);
                                            }
                                        });
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
                                    String errorMessage = String.format("Unsupported operation: %s", intent.getStringExtra(ServiceParams.STOCK_SERVICE));
                                    Timber.e("onHandleWork: %s", errorMessage);
                                    result.putExtra(ServiceParams.ERROR_SITUATION, errorMessage);
                                    reply.send(this, ServiceParams.ResultCode.COMMON_ERROR, result);
                            }
                        } catch (StockNotFoundException e) {
                            String errorMessage = "Stock was not found at Yahoo Finance";
                            Timber.e(e, "onHandleWork: %s", errorMessage);
                            result.putExtra(ServiceParams.ERROR_SITUATION, errorMessage);
                            reply.send(this, ServiceParams.ResultCode.STOCK_NOT_FOUND_ERROR, result);
                        } catch (IOException e) {
                            String errorMessage = "Something went wrong invoking YahooFinanceAPI";
                            Timber.e(e, "onHandleWork: %s", errorMessage);
                            result.putExtra(ServiceParams.ERROR_SITUATION, errorMessage);
                            reply.send(this, ServiceParams.ResultCode.COMMON_ERROR, result);
                        }
                    } catch (PendingIntent.CanceledException e) {
                        Timber.w(e, "onHandleWork: Reply cancelled");
                    }
                } else {
                    Timber.e("onHandleWork: No PendingResult was passed in with intent");
                }
            } else {
                Timber.e("onHandleWork: No stock service operation was passed in with intent");
            }
        } else {
            Timber.e("onHandleWork: Invoked with null intent");
        }
    }

    private void validateStock(Stock stock) throws StockNotFoundException {
        if (stock == null || StringUtils.isBlank(stock.getName()) || StringUtils.isNumeric(stock.getName())) {
            throw new StockNotFoundException(stock);
        }
    }
}
