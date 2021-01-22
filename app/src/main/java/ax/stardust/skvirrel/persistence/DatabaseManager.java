package ax.stardust.skvirrel.persistence;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import ax.stardust.skvirrel.monitoring.AbstractMonitoring;
import ax.stardust.skvirrel.monitoring.StockMonitoring;
import ax.stardust.skvirrel.persistence.gson.AbstractMonitoringJsonAdapter;
import timber.log.Timber;

/**
 * Manager responsible for database handling.
 */
public class DatabaseManager {

    private final Context context;
    private final Gson gson;

    /**
     * Creates a new database manager with given context
     *
     * @param context context for database handling
     */
    public DatabaseManager(Context context) {
        this.context = context;
        gson = new GsonBuilder()
                .registerTypeAdapter(AbstractMonitoring.class, new AbstractMonitoringJsonAdapter())
                .create();
    }

    /**
     * Inserts a new stock monitoring into database
     *
     * @param stockMonitoring stock monitoring to be inserted
     * @return inserted stock monitoring with the newly created id after insertion
     */
    public StockMonitoring insert(StockMonitoring stockMonitoring) {
        TransactionHandler.runInTransaction(context, database -> {
            long id = database.insert(DatabaseHelper.TABLE_NAME, null, getContentValues(stockMonitoring));
            stockMonitoring.setId(id);

            Timber.d("Stock monitoring with id: %s and ticker: %s inserted",
                    stockMonitoring.getId(),
                    stockMonitoring.getTicker());
        });

        return stockMonitoring;
    }

    /**
     * Updates stock monitoring
     *
     * @param stockMonitoring stock monitoring to be updated
     * @return updated stock monitoring
     */
    public StockMonitoring update(StockMonitoring stockMonitoring) {
        TransactionHandler.runInTransaction(context, database -> {
            // ignore result on update, it's always 0
            database.update(DatabaseHelper.TABLE_NAME, getContentValues(stockMonitoring),
                    DatabaseHelper.ID + " = ?", new String[]{String.valueOf(stockMonitoring.getId())});

            Timber.d("Stock monitoring with id: %s and ticker: %s updated",
                    stockMonitoring.getId(),
                    stockMonitoring.getTicker());
        });

        return stockMonitoring;
    }

    /**
     * Fetch all stock monitorings that exists in database
     *
     * @return list of all stock monitorings existing in database
     */
    @SuppressLint("StringFormatInTimber")
    public List<StockMonitoring> fetchAll() {
        List<StockMonitoring> stockMonitorings = new ArrayList<>();

        TransactionHandler.runInTransaction(context, database -> {
            Cursor cursor = database.rawQuery(DatabaseHelper.SELECT_ALL, null);
            if (cursor != null) {
                if (cursor.moveToFirst()) {
                    do {
                        stockMonitorings.add(getStockMonitoring(cursor));
                    } while (cursor.moveToNext());
                }

                cursor.close();
                stockMonitorings.sort(Comparator.comparing(StockMonitoring::getSortingOrder));
            }

            Timber.d("Stock monitorings fetched with id's and tickers: %s",
                    stockMonitorings.stream()
                            .map(sm -> String.format("%s(%s)", sm.getId(), sm.getTicker()))
                            .collect(Collectors.joining(", ")));
        });

        return stockMonitorings;
    }

    /**
     * Fetch all stock monitorings that should be monitored
     *
     * @return list of stock monitorings that should be monitored
     */
    public List<StockMonitoring> fetchAllForMonitoring() {
        return fetchAll().stream()
                .filter(StockMonitoring::hasValidDataForMonitoring)
                .filter(StockMonitoring::shouldBeMonitored)
                .collect(Collectors.toList());
    }

    /**
     * Fetch all tickers for stock monitorings that should be monitored
     *
     * @return array list of tickers that should be monitored
     */
    public ArrayList<String> fetchAllTickersForMonitoring() {
        return fetchAllForMonitoring().stream()
                .map(StockMonitoring::getTicker)
                .distinct()
                .collect(Collectors.toCollection(ArrayList::new));
    }

    /**
     * Deletes stock monitoring with given id
     *
     * @param id id of stock monitoring to be deleted
     */
    public void delete(long id) {
        TransactionHandler.runInTransaction(context, database -> {
            // returns rows affected by operation, for now we ignore it
            database.delete(DatabaseHelper.TABLE_NAME, DatabaseHelper.ID + " = ?", new String[]{String.valueOf(id)});

            Timber.d("Stock monitoring with id: %s deleted", id);
        });
    }

    private ContentValues getContentValues(StockMonitoring stockMonitoring) {
        ContentValues contentValues = new ContentValues();
        contentValues.put(DatabaseHelper.TICKER, stockMonitoring.getTicker());
        contentValues.put(DatabaseHelper.COMPANY_NAME, stockMonitoring.getCompanyName());
        contentValues.put(DatabaseHelper.MONITORING_OPTIONS, gson.toJson(stockMonitoring.getMonitoringOptions()));
        contentValues.put(DatabaseHelper.VIEW_STATE, stockMonitoring.getViewState().name());
        contentValues.put(DatabaseHelper.SORTING_ORDER, stockMonitoring.getSortingOrder());

        return contentValues;
    }

    private StockMonitoring getStockMonitoring(Cursor cursor) {
        StockMonitoring stockMonitoring = new StockMonitoring(cursor.getInt(cursor.getColumnIndex(DatabaseHelper.ID)));
        stockMonitoring.setTicker(cursor.getString(cursor.getColumnIndex(DatabaseHelper.TICKER)));
        stockMonitoring.setCompanyName(cursor.getString(cursor.getColumnIndex(DatabaseHelper.COMPANY_NAME)));
        stockMonitoring.setViewState(StockMonitoring.ViewState.valueOf(cursor.getString(cursor.getColumnIndex(DatabaseHelper.VIEW_STATE))));
        stockMonitoring.setSortingOrder(cursor.getInt(cursor.getColumnIndex(DatabaseHelper.SORTING_ORDER)));

        // special handling for monitoring options as the specific monitorings within it should
        // hold a reference to the stock monitoring that owns it, and this reference is not
        // serializable which means we must set it manual
        StockMonitoring.MonitoringOptions monitoringOptions = gson.fromJson(
                cursor.getString(cursor.getColumnIndex(DatabaseHelper.MONITORING_OPTIONS)),
                StockMonitoring.MonitoringOptions.class);
        monitoringOptions.setStockMonitoring(stockMonitoring);

        // at last set monitoring options to stock monitoring
        stockMonitoring.setMonitoringOptions(monitoringOptions);

        return stockMonitoring;
    }
}
