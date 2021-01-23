package ax.stardust.skvirrel.service;

import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;

import androidx.annotation.NonNull;
import androidx.core.app.JobIntentService;

import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.Objects;

import ax.stardust.skvirrel.exception.StockNotFoundException;
import ax.stardust.skvirrel.exception.StockServiceException;
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

    /**
     * Enqueues the work for this service for given context and intent
     *
     * @param context context of work
     * @param intent  intent of work
     */
    public static void enqueueWork(Context context, Intent intent) {
        enqueueWork(context, StockService.class, ServiceParams.STOCK_SERVICE_ID, intent);
    }

    @Override
    protected void onHandleWork(@NonNull Intent intent) {
        String operation = intent.getStringExtra(ServiceParams.STOCK_SERVICE);
        PendingIntent reply = intent.getParcelableExtra(ServiceParams.PENDING_RESULT);

        // mandatory to pass an operation with the intent
        if (operation == null) {
            StockServiceException stockServiceException = new StockServiceException("No stock service operation was passed in with intent");
            Timber.e(stockServiceException);
            throw stockServiceException;
        }

        // intent for result
        Intent result = new Intent();

        // set fragment tag if possible
        if (intent.hasExtra(ServiceParams.STOCK_FRAGMENT_TAG)) {
            result.putExtra(ServiceParams.STOCK_FRAGMENT_TAG, intent.getStringExtra(ServiceParams.STOCK_FRAGMENT_TAG));
        }

        // the meat
        try {
            Stock stock;
            switch (operation) {
                case GET_COMPANY_NAME:
                    stock = YahooFinance.get(Objects.requireNonNull(intent.getStringExtra(ServiceParams.RequestExtra.TICKER)));
                    validateStock(stock);

                    result.putExtra(ServiceParams.ResultExtra.COMPANY_NAME, stock.getName());
                    sendReply(reply, ServiceParams.ResultCode.SUCCESS, result);

                    Timber.d("onHandleWork: Successfully fetched stock: %s", stock.getName());

                    break;
                case GET_STOCK_INFO:
                    stock = YahooFinance.get(Objects.requireNonNull(intent.getStringExtra(ServiceParams.RequestExtra.TICKER)));
                    validateStock(stock);

                    result.putExtra(ServiceParams.ResultExtra.STOCK_INFO, ParcelableStock.from(stock));
                    sendReply(reply, ServiceParams.ResultCode.SUCCESS, result);

                    Timber.d("onHandleWork: Successfully fetched stock: %s", stock.getName());

                    break;
                case GET_STOCK_INFOS:
                    ArrayList<String> tickers = intent.getStringArrayListExtra(ServiceParams.RequestExtra.TICKERS);
                    ArrayList<ParcelableStock> parcelableStocks = new ArrayList<>();

                    if (tickers != null) {
                        tickers.forEach(ticker -> {
                            try {
                                Stock s = YahooFinance.get(Objects.requireNonNull(ticker));
                                validateStock(s);
                                parcelableStocks.add(ParcelableStock.from(s));
                            } catch (Exception e) {
                                Timber.e(e, "onHandleWork: Something went wrong while fetching stock info for ticker: %s", ticker);
                            }
                        });
                    }

                    // at last send broadcast and notify the job service that job has finished
                    sendBroadcast(parcelableStocks);
                    MonitoringJobService.Handler.getInstance().notifyJobFinished();

                    Timber.d("onHandleWork: Successfully fetched stocks: %s", StringUtils.joinWith(", ", tickers));

                    break;
                default:
                    String errorMessage = String.format("Unsupported operation: %s", intent.getStringExtra(ServiceParams.STOCK_SERVICE));
                    Timber.e("onHandleWork: %s", errorMessage);
                    result.putExtra(ServiceParams.ERROR_SITUATION, errorMessage);
                    sendReply(reply, ServiceParams.ResultCode.COMMON_ERROR, result);
            }
        } catch (Exception e) {
            // resolve both error message and result code
            String errorMessage = e instanceof StockNotFoundException ? "Stock was not found at Yahoo Finance"
                    : "Something went wrong invoking YahooFinanceAPI";
            int resultCode = e instanceof StockNotFoundException ? ServiceParams.ResultCode.STOCK_NOT_FOUND_ERROR
                    : ServiceParams.ResultCode.COMMON_ERROR;

            result.putExtra(ServiceParams.ERROR_SITUATION, errorMessage);
            sendReply(reply, resultCode, result);

            Timber.e(e, "onHandleWork: %s", errorMessage);
        }
    }

    private void sendReply(PendingIntent reply, int resultCode, Intent result) {
        // caller of service is responsible for getting a reply or not, pass in a pending intent for
        // reply with the intent if caller is expected to get a result back
        if (reply != null && result != null) {
            try {
                reply.send(this, resultCode, result);
            } catch (PendingIntent.CanceledException e) {
                Timber.e(e, "onHandleWork: Something unexpected happened while sending reply");
            }
        }
    }

    private void sendBroadcast(ArrayList<ParcelableStock> parcelableStocks) {
        // only send broadcast if there are anything to handle
        if (!parcelableStocks.isEmpty()) {
            final Intent receiverIntent = new Intent(this, MonitoringReceiver.class);
            receiverIntent.setAction(MonitoringReceiver.ACTION_STOCK_INFOS_FETCHED);
            receiverIntent.putParcelableArrayListExtra(ServiceParams.ResultExtra.STOCK_INFOS, parcelableStocks);

            sendBroadcast(receiverIntent);
        }
    }

    private void validateStock(Stock stock) throws StockNotFoundException {
        if (stock == null || StringUtils.isEmpty(stock.getName()) || StringUtils.isNumeric(stock.getName())) {
            throw new StockNotFoundException(stock);
        }
    }
}
