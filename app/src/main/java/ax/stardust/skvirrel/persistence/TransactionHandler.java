package ax.stardust.skvirrel.persistence;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

/**
 * Simple database transaction handler.
 */
public class TransactionHandler {

    /**
     * Ensures that a new database connection is opened before transaction is executed.
     * The opened connection will also be closed after execution.
     *
     * @param context     context for which a database connection is opened
     * @param transaction transaction to be executed
     */
    public static void runInTransaction(final Context context, final Transaction transaction) {
        SQLiteDatabase database = open(context);
        try {
            transaction.execute(database);
        } finally {
            database.close();
        }
    }

    private static SQLiteDatabase open(Context context) {
        return new DatabaseHelper(context).getWritableDatabase();
    }
}
