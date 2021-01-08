package ax.stardust.skvirrel.persistence;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;

import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import ax.stardust.skvirrel.entity.StockMonitoring;
import ax.stardust.skvirrel.exception.StockMonitoringNotFoundException;

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
        gson = new Gson();
    }

    /**
     * Inserts a new stock monitoring into database
     *
     * @param stockMonitoring stock monitoring to be inserted
     * @return inserted stock monitoring with the newly created id after insertion
     */
    public StockMonitoring insert(StockMonitoring stockMonitoring) {
        WrappedDatabaseResult result = new WrappedDatabaseResult();

        TransactionHandler.runInTransaction(context, database -> {
            long id = database.insert(DatabaseHelper.TABLE_NAME, null, getContentValues(stockMonitoring));
            result.setResult(id);
        });

        stockMonitoring.setId(result.getLong());
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
        });

        return stockMonitoring;
    }

    /**
     * Fetching stock monitoring on id
     *
     * @param id id for stock monitoring to fetched
     * @return found stock monitoring
     * @throws StockMonitoringNotFoundException is thrown if stock monitoring isn't found on given id
     */
    public StockMonitoring fetch(long id) throws StockMonitoringNotFoundException {
        WrappedDatabaseResult result = new WrappedDatabaseResult();

        TransactionHandler.runInTransaction(context, database -> {
            Cursor cursor = database.rawQuery(DatabaseHelper.SELECT_MONITORING_BY_ID, new String[]{String.valueOf(id)});
            if (cursor != null) {
                cursor.moveToFirst();
                result.setResult(getStockMonitoring(cursor));
                cursor.close();
            }
        });

        if (result.getStockMonitoring() == null) {
            throw new StockMonitoringNotFoundException(id);
        }

        return result.getStockMonitoring();
    }

    /**
     * Fetch all stock monitorings that exists in database
     *
     * @return list of all stock monitorings existing in database
     */
    public List<StockMonitoring> fetchAll() {
        List<StockMonitoring> stockMonitorings = new ArrayList<>();

        TransactionHandler.runInTransaction(context, database -> {
            Cursor cursor = database.rawQuery(DatabaseHelper.SELECT_ALL, null);
            if (cursor != null) {
                cursor.moveToFirst();

                while (cursor.moveToNext()) {
                    stockMonitorings.add(getStockMonitoring(cursor));
                }

                cursor.close();
                stockMonitorings.sort(Comparator.comparing(StockMonitoring::getSortingOrder));
            }
        });

        return stockMonitorings;
    }

    /**
     * Fetch all symbols of stock monitoring that exists in database
     *
     * @return array list of symbols
     */
    public ArrayList<String> fetchAllSymbols() {
        return fetchAll().stream()
                .map(StockMonitoring::getSymbol)
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
        });
    }

    private ContentValues getContentValues(StockMonitoring stockMonitoring) {
        ContentValues contentValues = new ContentValues();
        contentValues.put(DatabaseHelper.SYMBOL, stockMonitoring.getSymbol());
        contentValues.put(DatabaseHelper.COMPANY_NAME, stockMonitoring.getCompanyName());
        contentValues.put(DatabaseHelper.MONITORING_OPTIONS, gson.toJson(stockMonitoring.getMonitoringOptions()));
        contentValues.put(DatabaseHelper.NOTIFIED, boolToInt(stockMonitoring.isNotified()));
        contentValues.put(DatabaseHelper.SORTING_ORDER, stockMonitoring.getSortingOrder());

        return contentValues;
    }

    private StockMonitoring getStockMonitoring(Cursor cursor) {
        StockMonitoring stockMonitoring = new StockMonitoring(cursor.getInt(cursor.getColumnIndex(DatabaseHelper.ID)));
        stockMonitoring.setSymbol(cursor.getString(cursor.getColumnIndex(DatabaseHelper.SYMBOL)));
        stockMonitoring.setCompanyName(cursor.getString(cursor.getColumnIndex(DatabaseHelper.COMPANY_NAME)));
        stockMonitoring.setMonitoringOptions(gson.fromJson(cursor.getString(cursor.getColumnIndex(DatabaseHelper.MONITORING_OPTIONS)), StockMonitoring.MonitoringOptions.class));
        stockMonitoring.setNotified(intToBoolean(cursor.getInt(cursor.getColumnIndex(DatabaseHelper.NOTIFIED))));
        stockMonitoring.setSortingOrder(cursor.getInt(cursor.getColumnIndex(DatabaseHelper.SORTING_ORDER)));

        return stockMonitoring;
    }

    private int boolToInt(boolean b) {
        return b ? 1 : 0;
    }

    private boolean intToBoolean(int i) {
        return i == 1;
    }

    /**
     * Simple wrapper for database results.
     */
    private static class WrappedDatabaseResult {
        private long l = -1;
        private int i = -1;
        private StockMonitoring stockMonitoring = null;

        /**
         * Creates a new wrapped database result
         */
        public WrappedDatabaseResult() {
        }

        public void setResult(long l) {
            checkIfUnsetOrThrow();
            this.l = l;
        }

        public void setResult(int i) {
            checkIfUnsetOrThrow();
            this.i = i;
        }

        public void setResult(StockMonitoring stockMonitoring) {
            checkIfUnsetOrThrow();
            this.stockMonitoring = stockMonitoring;
        }

        public long getLong() {
            if (l == -1) {
                throw new IllegalStateException(Long.class.getSimpleName() + " for " + WrappedDatabaseResult.class.getSimpleName() + " is not set");
            }
            return l;
        }

        public int getInt() {
            if (i == -1) {
                throw new IllegalStateException(Integer.class.getSimpleName() + " for " + WrappedDatabaseResult.class.getSimpleName() + " is not set");
            }
            return i;
        }

        public StockMonitoring getStockMonitoring() {
            if (stockMonitoring == null) {
                throw new IllegalStateException(StockMonitoring.class.getSimpleName() + " for " + WrappedDatabaseResult.class.getSimpleName() + " is not set");
            }
            return stockMonitoring;
        }

        private void checkIfUnsetOrThrow() {
            // check if some field is already set
            if (l != -1 || i != -1 || stockMonitoring != null) {
                throw new IllegalStateException(WrappedDatabaseResult.class.getSimpleName() + " is already set");
            }
        }
    }
}
