package ax.stardust.skvirrel.component.dialog;

import android.app.Dialog;
import android.content.res.Resources;
import android.graphics.Rect;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TableLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import ax.stardust.skvirrel.R;
import ax.stardust.skvirrel.stock.parcelable.ParcelableStock;
import lombok.NoArgsConstructor;
import timber.log.Timber;

/**
 * A dialog showing more details about a stock.
 */
@NoArgsConstructor
public class StockDetailsDialog extends DialogFragment {

    public static final String FRAGMENT_TAG = StockDetailsDialog.class.getSimpleName();

    private DialogInteractionListener callback;

    private ParcelableStock parcelableStock;

    private LinearLayout fetchingDataLinearLayout;

    private TextView companyTextView;
    private TextView exchangeTickerCurrencyTextView;
    private TextView currentPriceTextView;
    private TextView dayChangeTextView;
    private TextView dateTextView;
    private TextView summaryTitleTextView;
    private TextView previousCloseTextView;
    private TextView openTextView;
    private TextView lowTextView;
    private TextView highTextView;
    private TextView low52WeekTextView;
    private TextView high52WeekTextView;
    private TextView marketCapTextView;
    private TextView volumeTextView;
    private TextView avgVolumeTextView;
    private TextView peTextView;
    private TextView epsTextView;
    private TextView earningsDateTextView;
    private TextView dividendTextView;
    private TextView indicatorTitleTextView;
    private TextView smaTextView;
    private TextView emaTextView;
    private TextView rsiTextView;

    private TableLayout stockDetailsSummaryTableLayout;
    private TableLayout stockDetailsIndicatorsTableLayout;

    private Button closeButton;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        try {
            // try find callback on creation
            callback = (DialogInteractionListener) getTargetFragment();
        } catch (Exception e) {
            IllegalStateException exception = new IllegalStateException("No target fragment set for "
                    + "dialog which is mandatory");
            Timber.e(exception, "Unable to create dialog");
            throw exception;
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.stock_details_dialog, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        findViews(view);
        setListeners();
        toggleVisibility();
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        setWidthPercent95();
    }

    private void setWidthPercent95() {
        // calculate the width
        DisplayMetrics displayMetrics = Resources.getSystem().getDisplayMetrics();
        Rect rect = new Rect(0, 0, displayMetrics.widthPixels, displayMetrics.heightPixels);
        float width = rect.width() * ((float) 95) / 100;

        // set the calculated width to the dialog
        Dialog dialog = getDialog();
        if (dialog != null) {
            Window window = dialog.getWindow();
            if (window != null) {
                dialog.getWindow().setLayout((int) width, ViewGroup.LayoutParams.WRAP_CONTENT);
            }
        }
    }

    /**
     * Sets parcelable stock to this dialog and updates data within the dialog
     *
     * @param parcelableStock parcelable stock to set
     */
    public void setParcelableStockAndUpdateDialog(ParcelableStock parcelableStock) {
        this.parcelableStock = parcelableStock;
        setTexts();
        toggleVisibility();
    }

    private void findViews(View view) {
        fetchingDataLinearLayout = view.findViewById(R.id.fetching_stock_details_ll);

        companyTextView = view.findViewById(R.id.stock_details_company_tv);
        exchangeTickerCurrencyTextView = view.findViewById(R.id.stock_details_exchange_ticker_currency_tv);
        currentPriceTextView = view.findViewById(R.id.stock_details_current_price_tv);
        dayChangeTextView = view.findViewById(R.id.stock_details_day_change_tv);
        dateTextView = view.findViewById(R.id.stock_details_date_tv);
        summaryTitleTextView = view.findViewById(R.id.stock_details_summary_title_tv);
        previousCloseTextView = view.findViewById(R.id.previous_close_tv);
        openTextView = view.findViewById(R.id.open_tv);
        lowTextView = view.findViewById(R.id.low_tv);
        highTextView = view.findViewById(R.id.high_tv);
        low52WeekTextView = view.findViewById(R.id.low_52_week_tv);
        high52WeekTextView = view.findViewById(R.id.high_52_week_tv);
        marketCapTextView = view.findViewById(R.id.market_cap_tv);
        volumeTextView = view.findViewById(R.id.volume_tv);
        avgVolumeTextView = view.findViewById(R.id.volume_avg_tv);
        peTextView = view.findViewById(R.id.pe_tv);
        epsTextView = view.findViewById(R.id.eps_tv);
        earningsDateTextView = view.findViewById(R.id.earnings_date_tv);
        dividendTextView = view.findViewById(R.id.dividend_tv);
        indicatorTitleTextView = view.findViewById(R.id.stock_details_indicators_title_tv);
        smaTextView = view.findViewById(R.id.sma_tv);
        emaTextView = view.findViewById(R.id.ema_tv);
        rsiTextView = view.findViewById(R.id.rsi_tv);

        stockDetailsSummaryTableLayout = view.findViewById(R.id.stock_details_summary_tl);
        stockDetailsIndicatorsTableLayout = view.findViewById(R.id.stock_details_indicators_tl);

        closeButton = view.findViewById(R.id.stock_details_close_btn);
    }

    private void setTexts() {
        companyTextView.setText(parcelableStock.getName());
        exchangeTickerCurrencyTextView.setText(getString(R.string.stock_details_exchange_ticker_currency,
                parcelableStock.getStockExchange(), parcelableStock.getTicker(), parcelableStock.getCurrency()));
        currentPriceTextView.setText(ParcelableStock.toString(parcelableStock.getPrice()));

        // resolve texts and color of day change
        dayChangeTextView.setText(getString(R.string.stock_details_day_change,
                ParcelableStock.toString(parcelableStock.getChange(), "", true),
                ParcelableStock.toString(parcelableStock.getChangePercent(), "%", true)));

        switch (ParcelableStock.ChangeTrend.fromChange(parcelableStock.getChange())) {
            case POSITIVE:
                dayChangeTextView.setTextColor(getResources().getColor(R.color.colorTextGreen));
                break;
            case NEGATIVE:
                dayChangeTextView.setTextColor(getResources().getColor(R.color.colorTextRed));
                break;
            default: // neutral trend
                dayChangeTextView.setTextColor(getResources().getColor(R.color.colorText));
        }

        dateTextView.setText(ParcelableStock.toString(parcelableStock.getLastTrade(),
                parcelableStock.getTimeZone(), ParcelableStock.LAST_TRADE_DATE_PATTERN, true));

        previousCloseTextView.setText(ParcelableStock.toString(parcelableStock.getPreviousClose()));
        openTextView.setText(ParcelableStock.toString(parcelableStock.getOpen()));
        lowTextView.setText(ParcelableStock.toString(parcelableStock.getLow()));
        highTextView.setText(ParcelableStock.toString(parcelableStock.getHigh()));
        low52WeekTextView.setText(ParcelableStock.toString(parcelableStock.getLow52Week()));
        high52WeekTextView.setText(ParcelableStock.toString(parcelableStock.getHigh52Week()));
        marketCapTextView.setText(ParcelableStock.toString(parcelableStock.getMarketCap(), true));
        volumeTextView.setText(ParcelableStock.toString(parcelableStock.getVolume(), true));
        avgVolumeTextView.setText(ParcelableStock.toString(parcelableStock.getAvgVolume(), true));
        peTextView.setText(ParcelableStock.toString(parcelableStock.getPe()));
        epsTextView.setText(ParcelableStock.toString(parcelableStock.getEps()));
        earningsDateTextView.setText(ParcelableStock.toString(parcelableStock.getEarnings(),
                parcelableStock.getTimeZone()));
        dividendTextView.setText(ParcelableStock.toString(parcelableStock.getAnnualYield(),
                parcelableStock.getAnnualYieldPercent(), "%", "%s(%s)"));
        smaTextView.setText(ParcelableStock.toString(parcelableStock.getSma50Close()));
        emaTextView.setText(ParcelableStock.toString(parcelableStock.getEma50Close()));
        rsiTextView.setText(ParcelableStock.toString(parcelableStock.getRsi14Close()));
    }

    private void toggleVisibility() {
        // components that should be visible when no parcelable stock exists
        fetchingDataLinearLayout.setVisibility(parcelableStock == null ? View.VISIBLE : View.GONE);

        // components that should be visible when a parcelable stock exists
        companyTextView.setVisibility(parcelableStock == null ? View.GONE : View.VISIBLE);
        exchangeTickerCurrencyTextView.setVisibility(parcelableStock == null ? View.GONE : View.VISIBLE);
        currentPriceTextView.setVisibility(parcelableStock == null ? View.GONE : View.VISIBLE);
        dayChangeTextView.setVisibility(parcelableStock == null ? View.GONE : View.VISIBLE);
        dateTextView.setVisibility(parcelableStock == null ? View.GONE : View.VISIBLE);
        summaryTitleTextView.setVisibility(parcelableStock == null ? View.GONE : View.VISIBLE);
        indicatorTitleTextView.setVisibility(parcelableStock == null ? View.GONE : View.VISIBLE);

        stockDetailsSummaryTableLayout.setVisibility(parcelableStock == null ? View.GONE : View.VISIBLE);
        stockDetailsIndicatorsTableLayout.setVisibility(parcelableStock == null ? View.GONE : View.VISIBLE);
    }

    private void setListeners() {
        closeButton.setOnClickListener(view -> callback.onNeutralButtonPressed());
    }
}
