package ax.stardust.skvirrel.fragment;

import android.app.PendingIntent;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import org.apache.commons.lang3.StringUtils;

import java.util.SplittableRandom;

import ax.stardust.skvirrel.R;
import ax.stardust.skvirrel.activity.Skvirrel;
import ax.stardust.skvirrel.component.dialog.ConfirmDialog;
import ax.stardust.skvirrel.component.dialog.DialogInteractionListener;
import ax.stardust.skvirrel.component.dialog.StockDetailsDialog;
import ax.stardust.skvirrel.component.keyboard.AlphanumericKeyboard;
import ax.stardust.skvirrel.component.keyboard.KeyboardHandler;
import ax.stardust.skvirrel.component.keyboard.NumericKeyboard;
import ax.stardust.skvirrel.component.watcher.ReferencedRadioGroupWatcher;
import ax.stardust.skvirrel.component.watcher.ReferencedTextWatcher;
import ax.stardust.skvirrel.component.widget.KeyboardlessEditText;
import ax.stardust.skvirrel.monitoring.AbstractMonitoring;
import ax.stardust.skvirrel.monitoring.Criteria;
import ax.stardust.skvirrel.monitoring.PeMonitoring;
import ax.stardust.skvirrel.monitoring.PriceMonitoring;
import ax.stardust.skvirrel.monitoring.RsiMonitoring;
import ax.stardust.skvirrel.monitoring.StockMonitoring;
import ax.stardust.skvirrel.persistence.DatabaseManager;
import ax.stardust.skvirrel.service.ServiceParams;
import ax.stardust.skvirrel.service.StockService;
import ax.stardust.skvirrel.stock.parcelable.ParcelableStock;
import lombok.NoArgsConstructor;
import timber.log.Timber;

/**
 * Fragment holding ui and data behind it for a stock monitoring.
 */
@SuppressWarnings("deprecation")
@NoArgsConstructor
public class StockFragment extends Fragment implements DialogInteractionListener {

    // parent of fragment
    private Skvirrel activity;
    private AlphanumericKeyboard alphanumericKeyboard;
    private NumericKeyboard numericKeyboard;

    private StockMonitoring stockMonitoring;
    private DatabaseManager databaseManager;

    // a handle to dialogs are needed in order to handle events from them
    private ConfirmDialog removeDialog;
    private StockDetailsDialog stockDetailsDialog;

    private View fragmentView;

    private TextView companyTextView;
    private TextView monitoringStatusTextView;

    private KeyboardlessEditText tickerEditText;
    private KeyboardlessEditText priceEditText;
    private KeyboardlessEditText peEditText;
    private KeyboardlessEditText rsiEditText;

    private Button viewStockInfoButton;
    private Button resetNotificationButton;
    private Button removeStockMonitoringButton;

    private RadioGroup priceRadioGroup;
    private RadioGroup peRadioGroup;
    private RadioGroup rsiRadioGroup;

    private RadioButton priceBelowRadioButton;
    private RadioButton priceAboveRadioButton;
    private RadioButton peBelowRadioButton;
    private RadioButton peAboveRadioButton;
    private RadioButton rsiBelowRadioButton;
    private RadioButton rsiAboveRadioButton;

    /**
     * Initializes this {@link StockFragment} with necessary data
     *
     * @param activity             parent of this fragment
     * @param stockMonitoring      stock monitoring belonging to this fragment
     * @param alphanumericKeyboard alpha numeric keyboard of the application
     * @param numericKeyboard      numeric keyboard of the application
     */
    public void initFragment(Skvirrel activity, StockMonitoring stockMonitoring,
                                              AlphanumericKeyboard alphanumericKeyboard, NumericKeyboard numericKeyboard) {
        if (activity == null || stockMonitoring == null
                || alphanumericKeyboard == null || numericKeyboard == null) {
            String errorMessage = "Cannot initialize fragment with null activity, stockMonitoring, alphanumeric or numeric keyboard";
            IllegalArgumentException exception = new IllegalArgumentException(errorMessage);
            Timber.e(exception, "Unable to instantiate StockFragment");
            throw exception;
        }

        this.activity = activity;
        this.stockMonitoring = stockMonitoring;
        this.alphanumericKeyboard = alphanumericKeyboard;
        this.numericKeyboard = numericKeyboard;
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        fragmentView = inflater.inflate(R.layout.stock_fragment, container, false);
        findViews(fragmentView);
        setInputTypes();
        setDefaultValues();
        updateWidgets();
        setListeners();
        return fragmentView;
    }

    private void findViews(View view) {
        companyTextView = view.findViewById(R.id.company_tv);
        monitoringStatusTextView = view.findViewById(R.id.monitoring_status_tv);

        tickerEditText = view.findViewById(R.id.ticker_et);
        priceEditText = view.findViewById(R.id.price_et);
        peEditText = view.findViewById(R.id.pe_et);
        rsiEditText = view.findViewById(R.id.rsi_et);

        viewStockInfoButton = view.findViewById(R.id.view_stock_info_btn);
        resetNotificationButton = view.findViewById(R.id.reset_notification_btn);
        removeStockMonitoringButton = view.findViewById(R.id.remove_stock_monitoring_btn);

        priceRadioGroup = view.findViewById(R.id.price_rg);
        peRadioGroup = view.findViewById(R.id.pe_rg);
        rsiRadioGroup = view.findViewById(R.id.rsi_rg);

        priceBelowRadioButton = view.findViewById(R.id.price_below_rb);
        priceAboveRadioButton = view.findViewById(R.id.price_above_rb);
        peBelowRadioButton = view.findViewById(R.id.pe_below_rb);
        peAboveRadioButton = view.findViewById(R.id.pe_above_rb);
        rsiAboveRadioButton = view.findViewById(R.id.rsi_above_rb);
        rsiBelowRadioButton = view.findViewById(R.id.rsi_below_rb);
    }

    private void setInputTypes() {
        tickerEditText.setInput(KeyboardlessEditText.Input.TEXT);
        priceEditText.setInput(KeyboardlessEditText.Input.NUMERIC_DECIMAL);
        peEditText.setInput(KeyboardlessEditText.Input.NUMERIC_INTEGER);
        rsiEditText.setInput(KeyboardlessEditText.Input.NUMERIC_INTEGER);
    }

    private void setDefaultValues() {
        // set random ticker as hint
        String[] stringArray = getResources().getStringArray(R.array.default_tickers);
        tickerEditText.setHint(stringArray[new SplittableRandom().nextInt(0, 14)]);
    }

    private void updateWidgets() {
        updateCompanyWidget();
        updateTickerWidget(true);
        updateViewStockInfoWidget();
        updateMonitoringStatusWidget();
        updateMonitoringOptionsWidgets();
        updateNotifiedWidgets();
    }

    private void setListeners() {
        tickerEditText.setOnFocusChangeListener(new KeyboardHandler(alphanumericKeyboard));
        tickerEditText.setOnTouchListener(new KeyboardHandler(alphanumericKeyboard));
        tickerEditText.addTextChangedListener(new TextWatcher() {

            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                // do nothing..
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                String input = charSequence.toString();
                if (StringUtils.isNotEmpty(input)) {
                    PendingIntent pendingResult = activity.createPendingResult(ServiceParams.RequestCode.GET_COMPANY_NAME, new Intent(), 0);
                    Intent intent = new Intent(activity, StockService.class);
                    intent.putExtra(ServiceParams.STOCK_SERVICE, ServiceParams.Operation.GET_COMPANY_NAME);
                    intent.putExtra(ServiceParams.RequestExtra.TICKER, input);
                    intent.putExtra(ServiceParams.PENDING_RESULT, pendingResult);
                    intent.putExtra(ServiceParams.STOCK_FRAGMENT_TAG, getTag());
                    StockService.enqueueWork(activity, intent);
                } else {
                    // empty value is okay so set company name to empty as the given ticker was empty,
                    // set default company name text and set input field border color to default
                    stockMonitoring.setCompanyName("");
                    companyTextView.setText(R.string.enter_ticker_hint);
                    tickerEditText.setBackgroundResource(R.drawable.input_default);
                }

                // enable/disable delete button
                alphanumericKeyboard.enableDeleteButton(StringUtils.isNotEmpty(input));

                // do always save ticker to database as we at this point don't know whether or not
                // the given ticker was correct
                stockMonitoring.setTicker(input);
                getDatabaseManager().update(stockMonitoring);

                // if input was empty we need to also update monitoring status as no intents are
                // sent during these cases and the usual way of updating this is not triggered
                if (StringUtils.isEmpty(input)) {
                    updateViewStockInfoWidget();
                    updateMonitoringStatusWidget();
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {
                // do nothing..
            }
        });

        PriceMonitoring priceMonitoring = stockMonitoring.getMonitoringOptions().getPriceMonitoring();
        PeMonitoring peMonitoring = stockMonitoring.getMonitoringOptions().getPeMonitoring();
        RsiMonitoring rsiMonitoring = stockMonitoring.getMonitoringOptions().getRsiMonitoring();

        priceRadioGroup.setOnCheckedChangeListener(new ReferencedRadioGroupWatcher(this, priceBelowRadioButton, priceAboveRadioButton, priceMonitoring));

        priceEditText.setOnFocusChangeListener(new KeyboardHandler(numericKeyboard));
        priceEditText.setOnTouchListener(new KeyboardHandler(numericKeyboard));
        priceEditText.addTextChangedListener(new ReferencedTextWatcher(this, priceEditText, numericKeyboard, priceMonitoring));

        peRadioGroup.setOnCheckedChangeListener(new ReferencedRadioGroupWatcher(this, peBelowRadioButton, peAboveRadioButton, peMonitoring));

        peEditText.setOnFocusChangeListener(new KeyboardHandler(numericKeyboard));
        peEditText.setOnTouchListener(new KeyboardHandler(numericKeyboard));
        peEditText.addTextChangedListener(new ReferencedTextWatcher(this, peEditText, numericKeyboard, peMonitoring));

        rsiRadioGroup.setOnCheckedChangeListener(new ReferencedRadioGroupWatcher(this, rsiBelowRadioButton, rsiAboveRadioButton, rsiMonitoring));

        rsiEditText.setOnFocusChangeListener(new KeyboardHandler(numericKeyboard));
        rsiEditText.setOnTouchListener(new KeyboardHandler(numericKeyboard));
        rsiEditText.addTextChangedListener(new ReferencedTextWatcher(this, rsiEditText, numericKeyboard, rsiMonitoring));

        viewStockInfoButton.setOnClickListener(view -> {
            PendingIntent pendingResult = activity.createPendingResult(ServiceParams.RequestCode.GET_STOCK_INFO, new Intent(), 0);
            Intent intent = new Intent(activity, StockService.class);
            intent.putExtra(ServiceParams.STOCK_SERVICE, ServiceParams.Operation.GET_STOCK_INFO);
            intent.putExtra(ServiceParams.RequestExtra.TICKER, stockMonitoring.getTicker());
            intent.putExtra(ServiceParams.PENDING_RESULT, pendingResult);
            intent.putExtra(ServiceParams.STOCK_FRAGMENT_TAG, getTag());
            StockService.enqueueWork(activity, intent);

            // show the stock details dialog already but with a spinner, indicating user that we're fetching data
            stockDetailsDialog = new StockDetailsDialog();
            stockDetailsDialog.setTargetFragment(this, (int) stockMonitoring.getId());
            stockDetailsDialog.show(requireFragmentManager(), StockDetailsDialog.FRAGMENT_TAG);
        });

        resetNotificationButton.setOnClickListener(view -> {
            stockMonitoring.resetNotified();
            getDatabaseManager().update(stockMonitoring);
            updateMonitoringStatusWidget();
            updateNotifiedWidgets();
        });

        removeStockMonitoringButton.setOnClickListener(view -> createAndShowRemoveDialog());
    }

    private void createAndShowRemoveDialog() {
        String ticker = stockMonitoring.getTicker();
        String companyName = stockMonitoring.getCompanyName();

        // resolve message
        String resolvedMessage = StringUtils.isBlank(ticker) || StringUtils.isBlank(companyName) ?
                getString(R.string.remove_stock_monitoring_dialog_message_without_stock) :
                getString(R.string.remove_stock_monitoring_dialog_message_with_stock, companyName, ticker);

        // create dialog and store a handle to it, needed in order to listen on events from it
        removeDialog = new ConfirmDialog(
                getString(R.string.remove_stock_monitoring_dialog_title),
                resolvedMessage,
                getString(R.string.remove_stock_monitoring_dialog_remove),
                getString(R.string.remove_stock_monitoring_dialog_cancel));

        removeDialog.setTargetFragment(this, (int) stockMonitoring.getId());
        removeDialog.show(requireFragmentManager(), ConfirmDialog.FRAGMENT_TAG);
    }

    private void updateCompanyWidget() {
        String companyName = stockMonitoring.getCompanyName();
        companyTextView.setText(StringUtils.isNotEmpty(companyName) ? companyName
                : activity.getString(R.string.enter_ticker_hint));
    }

    private void updateTickerWidget(boolean setTicker) {
        String companyName = stockMonitoring.getCompanyName();
        String ticker = stockMonitoring.getTicker();

        if (setTicker) {
            tickerEditText.setText(ticker);
        }

        // set background to ticker input field, company name is kind of the way we recognize
        // if the given ticker was successfully found at yahoo finance as it's only set then
        if (stockMonitoring.hasValidTicker() ||
                (StringUtils.isEmpty(companyName) && StringUtils.isEmpty(ticker))) { // no input os also fine
            tickerEditText.setBackgroundResource(R.drawable.input_default);
        } else {
            tickerEditText.setBackgroundResource(R.drawable.input_error);
        }
    }

    private void updateViewStockInfoWidget() {
        viewStockInfoButton.setEnabled(stockMonitoring.hasValidTicker());
    }

    private void updateMonitoringOptionsWidgets() {
        StockMonitoring.MonitoringOptions monitoringOptions = stockMonitoring.getMonitoringOptions();
        PriceMonitoring priceMonitoring = monitoringOptions.getPriceMonitoring();
        PeMonitoring peMonitoring = monitoringOptions.getPeMonitoring();
        RsiMonitoring rsiMonitoring = monitoringOptions.getRsiMonitoring();

        if (priceMonitoring.getComparator().equals(Criteria.Comparator.BELOW)) {
            priceBelowRadioButton.setChecked(true);
            priceAboveRadioButton.setChecked(false);
        } else {
            priceBelowRadioButton.setChecked(false);
            priceAboveRadioButton.setChecked(true);
        }

        if (priceMonitoring.getPrice() > 0) {
            priceEditText.setText(String.valueOf(priceMonitoring.getPrice()));
        }

        if (peMonitoring.getComparator().equals(Criteria.Comparator.BELOW)) {
            peBelowRadioButton.setChecked(true);
            peAboveRadioButton.setChecked(false);
        } else {
            peBelowRadioButton.setChecked(false);
            peAboveRadioButton.setChecked(true);
        }

        if (peMonitoring.getPe() > 0) {
            peEditText.setText(String.valueOf(peMonitoring.getPe()));
        }

        if (rsiMonitoring.getComparator().equals(Criteria.Comparator.BELOW)) {
            rsiBelowRadioButton.setChecked(true);
            rsiAboveRadioButton.setChecked(false);
        } else {
            rsiBelowRadioButton.setChecked(false);
            rsiAboveRadioButton.setChecked(true);
        }

        if (rsiMonitoring.getRsi() > 0) {
            rsiEditText.setText(String.valueOf(rsiMonitoring.getRsi()));
        }
    }

    private void updateNotifiedWidgets() {
        resetNotificationButton.setEnabled(stockMonitoring.isNotified());
        fragmentView.setBackgroundResource(stockMonitoring.isNotified() ? R.drawable.stock_fragment_notified : R.drawable.stock_fragment);
    }

    private DatabaseManager getDatabaseManager() {
        if (databaseManager == null) {
            databaseManager = new DatabaseManager(activity);
        }
        return databaseManager;
    }

    /**
     * To get parent activity of this fragment
     *
     * @return parent activity of this fragment
     */
    public Skvirrel getParent() {
        return activity;
    }

    /**
     * To update monitoring status widgets within this fragment
     */
    public void updateMonitoringStatusWidget() {
        // set default monitoring status
        String monitoringStatus = getString(R.string.monitoring_status_default);

        // figure out the actual monitoring status
        if (stockMonitoring.hasValidDataForMonitoring()) {
            if (stockMonitoring.isAllNotified()) {
                monitoringStatus = String.format(getString(R.string.monitoring_status_all_notified),
                        AbstractMonitoring.getJoinedTranslatedMonitoringNames(activity, stockMonitoring.getNotifiedMonitorings()));
            } else if (stockMonitoring.isNotified()) {
                monitoringStatus = String.format(getString(R.string.monitoring_status_notified),
                        AbstractMonitoring.getJoinedTranslatedMonitoringNames(activity, stockMonitoring.getNotifiedMonitorings()),
                        AbstractMonitoring.getJoinedTranslatedMonitoringNames(activity, stockMonitoring.getMonitoringsThatShouldBeMonitored()));
            } else {
                monitoringStatus = String.format(getString(R.string.monitoring_status_ok),
                        AbstractMonitoring.getJoinedTranslatedMonitoringNames(activity, stockMonitoring.getValidMonitorings()));
            }
        } else {
            if (!stockMonitoring.hasValidTicker() && stockMonitoring.hasAnyValidMonitoring()) {
                monitoringStatus = getString(R.string.monitoring_status_missing_ticker);
            } else if (stockMonitoring.hasValidTicker() && !stockMonitoring.hasAnyValidMonitoring()) {
                monitoringStatus = getString(R.string.monitoring_status_missing_monitoring_options);
            }
        }

        // finally set monitoring status text
        monitoringStatusTextView.setText(monitoringStatus);
    }

    @Override
    public void onPositiveButtonPressed() {
        if (removeDialog != null) {
            activity.removeStockMonitoringAndFragment(stockMonitoring);
            removeDialog.dismiss();
            removeDialog = null;
        }
    }

    @Override
    public void onNegativeButtonPressed() {
        if (removeDialog != null) {
            removeDialog.dismiss();
            removeDialog = null;
        }
    }

    @Override
    public void onNeutralButtonPressed() {
        if (stockDetailsDialog != null) {
            stockDetailsDialog.dismiss();
            stockDetailsDialog = null;
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode != ServiceParams.ResultCode.COMMON_ERROR) {
            switch (requestCode) {
                case ServiceParams.RequestCode.GET_COMPANY_NAME:
                    // resolve the company name
                    String companyName = "";
                    if (resultCode == ServiceParams.ResultCode.SUCCESS) {
                        companyName = data.getStringExtra(ServiceParams.ResultExtra.COMPANY_NAME);
                    }

                    // update it in db
                    stockMonitoring.setCompanyName(companyName);
                    stockMonitoring = getDatabaseManager().update(stockMonitoring);

                    // and update the ui accordingly
                    updateCompanyWidget();
                    updateTickerWidget(false);
                    updateViewStockInfoWidget();
                    updateMonitoringStatusWidget();

                    break;
                case ServiceParams.RequestCode.GET_STOCK_INFO:
                    if (resultCode == ServiceParams.ResultCode.SUCCESS) {
                        if (stockDetailsDialog != null) {
                            ParcelableStock parcelableStock = data.getParcelableExtra(ServiceParams.ResultExtra.STOCK_INFO);
                            stockDetailsDialog.setParcelableStockAndUpdateDialog(parcelableStock);
                        }
                    }
                    break;
                default:
                    Timber.e("Unsupported request code: %s", requestCode);
            }
        } else { // common error
            Toast.makeText(activity, data.getStringExtra(ServiceParams.ERROR_SITUATION), Toast.LENGTH_LONG).show();
        }
    }
}
