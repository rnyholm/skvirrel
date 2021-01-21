package ax.stardust.skvirrel.component.watcher;

import android.widget.RadioButton;
import android.widget.RadioGroup;

import ax.stardust.skvirrel.fragment.StockFragment;
import ax.stardust.skvirrel.monitoring.AbstractMonitoring;
import ax.stardust.skvirrel.monitoring.Criteria;
import ax.stardust.skvirrel.persistence.DatabaseManager;

/**
 * Special implementation of radio group: on checked change listener which is specialised for
 * monitoring input. Also holds several references to dependencies in order for validation and so on to work.
 */
public class ReferencedRadioGroupWatcher implements RadioGroup.OnCheckedChangeListener {

    private StockFragment stockFragment;
    private RadioButton belowRadioButton;
    private RadioButton aboveRadioButton;
    private AbstractMonitoring monitoring;

    private DatabaseManager databaseManager;

    /**
     * Creates a new instance of referenced radio group watcher with given data
     *
     * @param stockFragment    stock fragment for the watcher
     * @param belowRadioButton radio button for below comparator action
     * @param aboveRadioButton radio button for above comparator action
     * @param monitoring       monitoring for this watcher
     */
    public ReferencedRadioGroupWatcher(StockFragment stockFragment, RadioButton belowRadioButton,
                                       RadioButton aboveRadioButton, AbstractMonitoring monitoring) {
        this.stockFragment = stockFragment;
        this.belowRadioButton = belowRadioButton;
        this.aboveRadioButton = aboveRadioButton;
        this.monitoring = monitoring;
    }

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
