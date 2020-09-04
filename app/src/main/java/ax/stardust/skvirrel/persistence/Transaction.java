package ax.stardust.skvirrel.persistence;

import android.database.sqlite.SQLiteDatabase;

/**
 * Interface for a transaction.
 */
public interface Transaction {

    /**
     * Executes with given database.
     *
     * @param database database for execution
     */
    void execute(SQLiteDatabase database);
}
