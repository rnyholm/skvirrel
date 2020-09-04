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
import androidx.fragment.app.FragmentActivity;

import org.apache.commons.lang3.StringUtils;

import ax.stardust.skvirrel.R;
import ax.stardust.skvirrel.component.keyboard.AlphanumericKeyboard;
import ax.stardust.skvirrel.component.keyboard.KeyboardHandler;
import ax.stardust.skvirrel.component.widget.KeyboardlessEditText;
import ax.stardust.skvirrel.persistence.DatabaseManager;
import ax.stardust.skvirrel.pojo.StockMonitoring;
import ax.stardust.skvirrel.service.ServiceParams;
import ax.stardust.skvirrel.service.StockService;

public class StockFragment extends Fragment {
    private static final String TAG = StockFragment.class.getSimpleName();

    private DatabaseManager databaseManager;

    // parent of fragment
    private FragmentActivity activity;
    private StockMonitoring stockMonitoring;
    private AlphanumericKeyboard alphanumericKeyboard;

    private TextView companyTextView;
    private Button pollStockButton;
    private KeyboardlessEditText symbolEditText;

    public StockFragment(FragmentActivity activity, StockMonitoring stockMonitoring, AlphanumericKeyboard alphanumericKeyboard) {
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
        setStockInfo();
        setListeners();
        return view;
    }

    private void findViews(View view) {
        companyTextView = view.findViewById(R.id.company_tv);
        pollStockButton = view.findViewById(R.id.poll_stock_btn);
        symbolEditText = view.findViewById(R.id.symbol_et);
    }

    private void setStockInfo() {
        final String companyName = stockMonitoring.getCompanyName();
        companyTextView.setText(StringUtils.isNotEmpty(companyName) ? companyName : activity.getString(R.string.company_name));
        symbolEditText.setText(stockMonitoring.getSymbol());
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
                    Intent intent = new Intent(getActivity(), StockService.class);
                    intent.putExtra(ServiceParams.STOCK_SERVICE, ServiceParams.Operation.GET_COMPANY_NAME);
                    intent.putExtra(ServiceParams.RequestExtra.SYMBOL, getSymbol());
                    intent.putExtra(ServiceParams.PENDING_RESULT, pendingResult);
                    intent.putExtra(ServiceParams.STOCK_FRAGMENT_TAG, getTag());
                    activity.startService(intent);
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

        pollStockButton.setOnClickListener(view -> {
            PendingIntent pendingResult = activity.createPendingResult(ServiceParams.RequestCode.GET_STOCK_INFO, new Intent(), 0);
            Intent intent = new Intent(activity, StockService.class);
            intent.putExtra(ServiceParams.STOCK_SERVICE, ServiceParams.Operation.GET_STOCK_INFO);
            intent.putExtra(ServiceParams.RequestExtra.SYMBOL, getSymbol());
            intent.putExtra(ServiceParams.PENDING_RESULT, pendingResult);
            intent.putExtra(ServiceParams.STOCK_FRAGMENT_TAG, getTag());
            activity.startService(intent);
        });
    }

    private String getSymbol() {
        if (symbolEditText != null) {
            if (symbolEditText.getText() != null) {
                return symbolEditText.getText().toString();
            }
        }

        return "";
    }

    private void setCompanyAndSymbolWidgets(int resultCode, Intent data) {
        String companyName = "";

        if (resultCode == ServiceParams.ResultCode.SUCCESS) {
            companyName = data.getStringExtra(ServiceParams.ResultExtra.COMPANY_NAME);
            companyTextView.setText(companyName);
            symbolEditText.setBackgroundResource(R.drawable.input_default);
        } else if (resultCode == ServiceParams.ResultCode.STOCK_NOT_FOUND_ERROR) {
            companyTextView.setText(R.string.company_name);
            symbolEditText.setBackgroundResource(R.drawable.input_error);
        }

        stockMonitoring.setCompanyName(companyName);
        getDatabaseManager().update(stockMonitoring);
    }

    private DatabaseManager getDatabaseManager() {
        if (databaseManager == null) {
            databaseManager = new DatabaseManager(activity);
        }
        return databaseManager;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode != ServiceParams.ResultCode.COMMON_ERROR) {
            switch (requestCode) {
                case ServiceParams.RequestCode.GET_COMPANY_NAME:
                    setCompanyAndSymbolWidgets(resultCode, data);
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
