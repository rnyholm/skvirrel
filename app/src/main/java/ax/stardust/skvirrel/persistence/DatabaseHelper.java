package ax.stardust.skvirrel.persistence;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Helper class for the database handling.
 */
public class DatabaseHelper extends SQLiteOpenHelper {

    // basic database information
    private static final String DB_NAME = "Skvirrel.db";
    private static final int DB_VERSION = 1;

    // table specific data
    public static final String STOCK_MONITORING_TABLE_NAME = "StockMonitoring";
    public static final String INDICATOR_CACHE_TABLE_NAME = "IndicatorCache";
    public static final String ID_COLUMN = "id";
    public static final String TICKER_COLUMN = "Ticker";
    public static final String COMPANY_NAME_COLUMN = "CompanyName";
    public static final String MONITORING_OPTIONS_COLUMN = "MonitoringOptions";
    public static final String VIEW_STATE_COLUMN = "ViewState";
    public static final String SORTING_ORDER_COLUMN = "SortingOrder";
    public static final String SMA_COLUMN = "Sma";
    public static final String EMA_COLUMN = "Ema";
    public static final String RSI_COLUMN = "Rsi";
    public static final String EXPIRES_COLUMN = "Expires";

    // queries
    private static final String CREATE_STOCK_MONITORING_TABLE = "CREATE TABLE "
            + STOCK_MONITORING_TABLE_NAME + "("
            + ID_COLUMN + " INTEGER PRIMARY KEY AUTOINCREMENT, "
            + TICKER_COLUMN + " TEXT, "
            + COMPANY_NAME_COLUMN + " TEXT, "
            + MONITORING_OPTIONS_COLUMN + " TEXT, "
            + VIEW_STATE_COLUMN + " TEXT, "
            + SORTING_ORDER_COLUMN + " INTEGER DEFAULT 0);";

    private static final String CREATE_INDICATOR_CACHE_TABLE = "CREATE TABLE "
            + INDICATOR_CACHE_TABLE_NAME + "("
            + ID_COLUMN + " INTEGER PRIMARY KEY AUTOINCREMENT, "
            + TICKER_COLUMN + " TEXT UNIQUE NOT NULL, "
            + SMA_COLUMN + " TEXT, "
            + EMA_COLUMN + " TEXT, "
            + RSI_COLUMN + " TEXT, "
            + EXPIRES_COLUMN + " INTEGER);";

    public static final String SELECT_ALL_FROM_STOCK_MONITORING_TABLE = "SELECT * FROM " + STOCK_MONITORING_TABLE_NAME;
    public static final String SELECT_ALL_FROM_INDICATOR_CACHE_TABLE = "SELECT * FROM " + INDICATOR_CACHE_TABLE_NAME;
    public static final String SELECT_ALL_FOR_TICKER_FROM_INDICATOR_CACHE_TABLE =
            "SELECT * FROM " + INDICATOR_CACHE_TABLE_NAME + " WHERE " + TICKER_COLUMN + " = ?";

    /**
     * Creates a new database helper with given context
     *
     * @param context context for database helper
     */
    public DatabaseHelper(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_STOCK_MONITORING_TABLE);
        db.execSQL(CREATE_INDICATOR_CACHE_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
        // do nothing for now
    }
}
