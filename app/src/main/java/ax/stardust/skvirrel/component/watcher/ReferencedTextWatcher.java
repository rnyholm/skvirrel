package ax.stardust.skvirrel.component.watcher;

import android.text.Editable;
import android.text.TextWatcher;

import org.apache.commons.lang3.StringUtils;

import ax.stardust.skvirrel.R;
import ax.stardust.skvirrel.component.keyboard.SkvirrelKeyboard;
import ax.stardust.skvirrel.component.widget.KeyboardlessEditText;
import ax.stardust.skvirrel.fragment.StockFragment;
import ax.stardust.skvirrel.monitoring.AbstractMonitoring;
import ax.stardust.skvirrel.persistence.DatabaseManager;
import timber.log.Timber;

/**
 * Special implementation of text watcher which is specialised for monitoring input. Also
 * holds several references to dependencies in order for validation and so on to work.
 */
public class ReferencedTextWatcher implements TextWatcher {

    private final StockFragment stockFragment;
    private final KeyboardlessEditText editText;
    private final SkvirrelKeyboard skvirrelKeyboard;
    private final AbstractMonitoring monitoring;

    private DatabaseManager databaseManager;

    /**
     * Creates a new instance of referenced text watcher with given data
     *
     * @param stockFragment    stock fragment for the watcher
     * @param editText         input field from which input should be taken
     * @param skvirrelKeyboard keyboard for this input field
     * @param monitoring       monitoring for this watcher
     */
    public ReferencedTextWatcher(StockFragment stockFragment, KeyboardlessEditText editText,
                                 SkvirrelKeyboard skvirrelKeyboard, AbstractMonitoring monitoring) {
        this.stockFragment = stockFragment;
        this.editText = editText;
        this.skvirrelKeyboard = skvirrelKeyboard;
        this.monitoring = monitoring;
    }

    @Override
    public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
        // do nothing..
    }

    @Override
    public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
        String input = charSequence.toString();
        if (StringUtils.isNotEmpty(input)) {
            try {
                monitoring.setValue(input);
                this.editText.setBackgroundResource(R.drawable.input_default);
            } catch (Exception e) {
                // given value was faulty, reset monitoring to better reflect whats happening
                monitoring.resetValue();
                this.editText.setBackgroundResource(R.drawable.input_error);
                Timber.d(e, "Value of monitoring could not be set");
            }
        } else { // empty input is okay but to be sure, reset monitoring
            monitoring.resetValue();
            this.editText.setBackgroundResource(R.drawable.input_default);
        }

        // always update monitoring as it will in some form be set previously
        getDatabaseManager().update(monitoring.getStockMonitoring());

        // enable/disable delete button
        skvirrelKeyboard.enableDeleteButton(StringUtils.isNotEmpty(input));

        // update monitoring status
        stockFragment.updateMonitoringStatusWidget();
    }

    @Override
    public void afterTextChanged(Editable editable) {
        // do nothing..
    }

    private DatabaseManager getDatabaseManager() {
        if (databaseManager == null) {
            databaseManager = new DatabaseManager(stockFragment.getParent());
        }
        return databaseManager;
    }
}
