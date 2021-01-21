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
    public static final String TABLE_NAME = "StockMonitoring";
    public static final String ID = "id";
    public static final String TICKER = "Ticker";
    public static final String COMPANY_NAME = "CompanyName";
    public static final String MONITORING_OPTIONS = "MonitoringOptions";
    public static final String VIEW_STATE = "ViewState";
    public static final String SORTING_ORDER = "SortingOrder";

    // queries
    private static final String CREATE_TABLE = "CREATE TABLE "
            + TABLE_NAME + "("
            + ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
            + TICKER + " TEXT, "
            + COMPANY_NAME + " TEXT, "
            + MONITORING_OPTIONS + " TEXT, "
            + VIEW_STATE + " TEXT, "
            + SORTING_ORDER + " INTEGER DEFAULT 0);";

    public static final String SELECT_ALL = "SELECT * FROM " + TABLE_NAME;

    public static final String SELECT_MONITORING_BY_ID = "SELECT * FROM "
            + TABLE_NAME + " WHERE "
            + ID + " = ?";

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
        db.execSQL(CREATE_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
        // do nothing for now
    }
}
