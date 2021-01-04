package ax.stardust.skvirrel.fragment;

import android.app.PendingIntent;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import org.apache.commons.lang3.StringUtils;

import ax.stardust.skvirrel.R;
import ax.stardust.skvirrel.activity.Skvirrel;
import ax.stardust.skvirrel.component.keyboard.AlphanumericKeyboard;
import ax.stardust.skvirrel.component.keyboard.KeyboardHandler;
import ax.stardust.skvirrel.component.widget.KeyboardlessEditText;
import ax.stardust.skvirrel.entity.StockMonitoring;
import ax.stardust.skvirrel.persistence.DatabaseManager;
import ax.stardust.skvirrel.schedule.MonitoringScheduler;
import ax.stardust.skvirrel.service.ServiceParams;
import ax.stardust.skvirrel.service.StockService;

public class StockFragment extends Fragment {
    private static final String TAG = StockFragment.class.getSimpleName();

    // parent of fragment
    private Skvirrel activity;
    private StockMonitoring stockMonitoring;
    private AlphanumericKeyboard alphanumericKeyboard;

    private DatabaseManager databaseManager;

    private TextView companyTextView;

    private KeyboardlessEditText symbolEditText;
    private KeyboardlessEditText priceEditText;

    private Button pollStockButton;
    private Button resetNotificationButton;
    private Button removeStockMonitoringButton;

    /**
     * Creates a new instance of {@link StockFragment}
     *
     * @param activity             parent of this fragment
     * @param stockMonitoring      stock monitoring belonging to this fragment
     * @param alphanumericKeyboard alpha numeric keyboard of the application
     */
    public StockFragment(final Skvirrel activity, final StockMonitoring stockMonitoring, final AlphanumericKeyboard alphanumericKeyboard) {
        if (activity == null || stockMonitoring == null || alphanumericKeyboard == null) {
            String errorMessage = "Cannot instantiate fragment with null activity, stockMonitoring or alphanumeric keyboard";
            Log.e(TAG, "StockFragment(...) -> " + errorMessage);
            throw new IllegalArgumentException(errorMessage);
        }

        this.activity = activity;
        this.stockMonitoring = stockMonitoring;
        this.alphanumericKeyboard = alphanumericKeyboard;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.stock_content_card, container, false);
        findViews(view);
        updateStockInfo();
        setListeners();
        return view;
    }

    private void findViews(View view) {
        companyTextView = view.findViewById(R.id.company_tv);
        symbolEditText = view.findViewById(R.id.symbol_et);
        priceEditText = view.findViewById(R.id.price_et);
        pollStockButton = view.findViewById(R.id.poll_stock_btn);
        resetNotificationButton = view.findViewById(R.id.reset_notification_btn);
        removeStockMonitoringButton = view.findViewById(R.id.remove_stock_monitoring_btn);
    }

    private void updateStockInfo() {
        updateCompanyWidget(stockMonitoring);
        updateSymbolWidget(stockMonitoring, true);
        updateMonitoringOptionsWidgets(stockMonitoring);
    }

    private void setListeners() {
        symbolEditText.setOnFocusChangeListener(new KeyboardHandler(alphanumericKeyboard));
        symbolEditText.setOnTouchListener(new KeyboardHandler(alphanumericKeyboard));
        symbolEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                // do nothing..
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                String inputText = charSequence.toString();
                if (StringUtils.isEmpty(inputText)) {
                    companyTextView.setText(R.string.company_name);
                } else {
                    PendingIntent pendingResult = activity.createPendingResult(ServiceParams.RequestCode.GET_COMPANY_NAME, new Intent(), 0);
                    Intent intent = new Intent(activity, StockService.class);
                    intent.putExtra(ServiceParams.STOCK_SERVICE, ServiceParams.Operation.GET_COMPANY_NAME);
                    intent.putExtra(ServiceParams.RequestExtra.SYMBOL, getSymbol());
                    intent.putExtra(ServiceParams.PENDING_RESULT, pendingResult);
                    intent.putExtra(ServiceParams.STOCK_FRAGMENT_TAG, getTag());
                    StockService.enqueueWork(activity, intent);
//                    activity.startService(intent);
                }

                // TODO: check enable delete button that it's not set within keyboard handler
                alphanumericKeyboard.enableDeleteButton(StringUtils.isNotEmpty(inputText));

                stockMonitoring.setSymbol(getSymbol());
                getDatabaseManager().update(stockMonitoring);
            }

            @Override
            public void afterTextChanged(Editable editable) {
                // do nothing..
            }
        });

        priceEditText.setOnFocusChangeListener(new KeyboardHandler(alphanumericKeyboard));
        priceEditText.setOnTouchListener(new KeyboardHandler(alphanumericKeyboard));
        priceEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                // do nothing..
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                String priceText = charSequence.toString();
                if (StringUtils.isNotEmpty(priceText)) {
                    try {
                        final double price = Double.parseDouble(priceText);
                        priceEditText.setBackgroundResource(R.drawable.input_default);
                        stockMonitoring.getMonitoringOptions().setPrice(price);
                        getDatabaseManager().update(stockMonitoring);
                    } catch (NumberFormatException e) {
                        priceEditText.setBackgroundResource(R.drawable.input_error);
                    }
                } else {
                    priceEditText.setBackgroundResource(R.drawable.input_error);
                }

                // TODO: check enable delete button that it's not set within keyboard handler
                alphanumericKeyboard.enableDeleteButton(StringUtils.isNotEmpty(priceText));
            }

            @Override
            public void afterTextChanged(Editable editable) {
                // do nothing..
            }
        });

        pollStockButton.setOnClickListener(view -> {
            PendingIntent pendingResult = activity.createPendingResult(ServiceParams.RequestCode.GET_STOCK_INFO, new Intent(), 0);
            Intent intent = new Intent(activity, StockService.class);
            intent.putExtra(ServiceParams.STOCK_SERVICE, ServiceParams.Operation.GET_STOCK_INFO);
            intent.putExtra(ServiceParams.RequestExtra.SYMBOL, getSymbol());
            intent.putExtra(ServiceParams.PENDING_RESULT, pendingResult);
            intent.putExtra(ServiceParams.STOCK_FRAGMENT_TAG, getTag());
            StockService.enqueueWork(activity, intent);
//            activity.startService(intent);
        });

        resetNotificationButton.setOnClickListener(view -> {
            // TODO: implement me :)
            MonitoringScheduler.scheduleJob(activity);
            Toast.makeText(activity, "Reset notification button pressed", Toast.LENGTH_LONG).show();
        });

        removeStockMonitoringButton.setOnClickListener(view -> activity.removeStockMonitoringAndFragment(stockMonitoring));
    }

    private DatabaseManager getDatabaseManager() {
        if (databaseManager == null) {
            databaseManager = new DatabaseManager(activity);
        }
        return databaseManager;
    }

    private String getSymbol() {
        if (symbolEditText != null) {
            if (symbolEditText.getText() != null) {
                return symbolEditText.getText().toString();
            }
        }
        return "";
    }

    private void updateStockMonitoringAndSetStockInfo(final int resultCode, final Intent data) {
        // resolve the company name
        String companyName = "";
        if (resultCode == ServiceParams.ResultCode.SUCCESS) {
            companyName = data.getStringExtra(ServiceParams.ResultExtra.COMPANY_NAME);
        }

        // update it in db
        stockMonitoring.setCompanyName(companyName);
        stockMonitoring = getDatabaseManager().update(stockMonitoring);

        // and update the ui accordingly
        updateCompanyWidget(stockMonitoring);
        updateSymbolWidget(stockMonitoring, false);
    }

    private void updateCompanyWidget(final StockMonitoring stockMonitoring) {
        final String companyName = stockMonitoring.getCompanyName();
        companyTextView.setText(StringUtils.isNotEmpty(companyName) ? companyName : activity.getString(R.string.company_name));
    }

    private void updateSymbolWidget(final StockMonitoring stockMonitoring, final boolean alsoSetSymbol) {
        final String companyName = stockMonitoring.getCompanyName();
        final String symbol = stockMonitoring.getSymbol();

        if (alsoSetSymbol) {
            symbolEditText.setText(symbol);
        }

        if (StringUtils.isNotEmpty(companyName) ||
                (StringUtils.isEmpty(companyName) && StringUtils.isEmpty(symbol))) {
            symbolEditText.setBackgroundResource(R.drawable.input_default);
        } else {
            symbolEditText.setBackgroundResource(R.drawable.input_error);
        }
    }

    private void updateMonitoringOptionsWidgets(final StockMonitoring stockMonitoring) {
        final StockMonitoring.MonitoringOptions monitoringOptions = stockMonitoring.getMonitoringOptions();
        if (monitoringOptions.getPrice() > 0) {
            priceEditText.setText(String.valueOf(monitoringOptions.getPrice()));
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode != ServiceParams.ResultCode.COMMON_ERROR) {
            switch (requestCode) {
                case ServiceParams.RequestCode.GET_COMPANY_NAME:
                    updateStockMonitoringAndSetStockInfo(resultCode, data);
                    break;
                case ServiceParams.RequestCode.GET_STOCK_INFO:
                    break;
                default:
                    Log.e(TAG, "onActivityResult(...) -> Unsupported request code -> " + requestCode);
            }
        } else { // common error
            Toast.makeText(activity, data.getStringExtra(ServiceParams.ERROR_SITUATION), Toast.LENGTH_LONG).show();
        }
    }
}
