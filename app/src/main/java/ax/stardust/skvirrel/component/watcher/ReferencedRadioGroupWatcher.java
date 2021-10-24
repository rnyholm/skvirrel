package ax.stardust.skvirrel.component.watcher;

import android.widget.RadioButton;
import android.widget.RadioGroup;

import ax.stardust.skvirrel.fragment.StockFragment;
import ax.stardust.skvirrel.monitoring.AbstractMonitoring;
import ax.stardust.skvirrel.monitoring.Criteria;
import ax.stardust.skvirrel.persistence.DatabaseManager;
import lombok.RequiredArgsConstructor;

/**
 * Special implementation of radio group: on checked change listener which is specialised for
 * monitoring input. Also holds several references to dependencies in order for validation and so on to work.
 */
@RequiredArgsConstructor
public class ReferencedRadioGroupWatcher implements RadioGroup.OnCheckedChangeListener {

    private final StockFragment stockFragment;
    private final RadioButton belowRadioButton;
    private final RadioButton aboveRadioButton;
    private final AbstractMonitoring monitoring;

    private DatabaseManager databaseManager;

    @Override
    public void onCheckedChanged(RadioGroup radioGroup, int checkedId) {
        if (checkedId == belowRadioButton.getId()) {
            monitoring.setComparator(Criteria.Comparator.BELOW);
        } else if (checkedId == aboveRadioButton.getId()) {
            monitoring.setComparator(Criteria.Comparator.ABOVE);
        }

        getDatabaseManager().update(monitoring.getStockMonitoring());
    }

    private DatabaseManager getDatabaseManager() {
        if (databaseManager == null) {
            databaseManager = new DatabaseManager(stockFragment.getParent());
        }
        return databaseManager;
    }
}
