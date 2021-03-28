package ax.stardust.skvirrel.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import java.util.ArrayList;
import java.util.List;

import ax.stardust.skvirrel.R;
import ax.stardust.skvirrel.component.keyboard.AlphanumericKeyboard;
import ax.stardust.skvirrel.component.keyboard.NumericKeyboard;
import ax.stardust.skvirrel.fragment.StockFragment;
import ax.stardust.skvirrel.monitoring.StockMonitoring;
import ax.stardust.skvirrel.persistence.DatabaseManager;
import ax.stardust.skvirrel.service.ServiceParams;
import ax.stardust.skvirrel.cache.CacheManager;
import timber.log.Timber;

/**
 * Skvirrel, the main activity for the application.
 */
public class Skvirrel extends AppCompatActivity {

    private FragmentManager fragmentManager;
    private FragmentTransaction fragmentTransaction;

    private AlphanumericKeyboard alphanumericKeyboard;
    private NumericKeyboard numericKeyboard;

    private DatabaseManager databaseManager;
    private CacheManager cacheManager;

    private Button addStockMonitoringButton;

    private TextView gettingStartedTitleTextView;
    private TextView gettingStartedTextView;
    private TextView versionNameTextView;

    private List<StockMonitoring> stockMonitorings = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_skvirrel);
        findViews();
        addStockFragments();
        setListeners();
        toggleGettingStartedTextView();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        // suitable place to maintain indicator cache a bit
        getCacheManager().cleanIndicatorCache();
    }

    @Override
    public void onBackPressed() {
        if (alphanumericKeyboard.getVisibility() == View.VISIBLE
                || numericKeyboard.getVisibility() == View.VISIBLE) {
            // hacky way to release focus from any edit text, by releasing it also the keyboard will be closed
            versionNameTextView.requestFocus();
        } else {
            super.onBackPressed();
        }
    }

    private void findViews() {
        addStockMonitoringButton = findViewById(R.id.add_stock_monitoring_btn);
        alphanumericKeyboard = findViewById(R.id.alphanumeric_keyboard);
        numericKeyboard = findViewById(R.id.numeric_keyboard);
        gettingStartedTitleTextView = findViewById(R.id.getting_started_title_tv);
        gettingStartedTextView = findViewById(R.id.getting_started_tv);
        versionNameTextView = findViewById(R.id.version_name_tv);
    }

    private void addStockFragments() {
        stockMonitorings = getDatabaseManager().fetchAllStockMonitorings();
        stockMonitorings.forEach(this::addStockFragment);
    }

    private void setListeners() {
        addStockMonitoringButton.setOnClickListener(view -> {
            // create a new stock monitoring, store it to db, local list and
            // update ui with a new stock fragment
            StockMonitoring stockMonitoring = getDatabaseManager().insert(new StockMonitoring());
            stockMonitorings.add(stockMonitoring);
            toggleGettingStartedTextView();
            addStockFragment(stockMonitoring);
        });
    }

    private void addStockFragment(StockMonitoring stockMonitoring) {
        final StockFragment stockFragment = new StockFragment(this, stockMonitoring, alphanumericKeyboard, numericKeyboard);
        fragmentManager = getSupportFragmentManager();
        fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.add(R.id.stock_fragment_container, stockFragment, String.valueOf(stockMonitoring.getId()));
        fragmentTransaction.commit();
    }

    /**
     * Removes given stock monitoring both from user interface and database
     *
     * @param stockMonitoring stock monitoring to be removed
     */
    public void removeStockMonitoringAndFragment(StockMonitoring stockMonitoring) {
        fragmentManager = getSupportFragmentManager();

        // find fragment for stock monitoring and remove it
        final Fragment fragment = fragmentManager.findFragmentByTag(String.valueOf(stockMonitoring.getId()));
        if (fragment != null) {
            fragmentTransaction = fragmentManager.beginTransaction();
            fragmentTransaction.remove(fragment);
            fragmentTransaction.commit();
        } else {
            Timber.e("No fragment found with tag: %s", stockMonitoring.getId());
        }

        // remove stock monitoring from db and list of stock monitoring within this object
        getDatabaseManager().delete(stockMonitoring);
        stockMonitorings.remove(stockMonitoring);

        // at last toggle getting started text views visibility
        toggleGettingStartedTextView();
    }

    private void toggleGettingStartedTextView() {
        int visibility = stockMonitorings.isEmpty() ? View.VISIBLE : View.GONE;
        gettingStartedTextView.setVisibility(visibility);
        gettingStartedTitleTextView.setVisibility(visibility);
    }

    private DatabaseManager getDatabaseManager() {
        if (databaseManager == null) {
            databaseManager = new DatabaseManager(this);
        }
        return databaseManager;
    }

    private CacheManager getCacheManager() {
        if (cacheManager == null) {
            cacheManager = new CacheManager(this);
        }
        return cacheManager;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (data != null) {
            // figure out what fragment is responsible for intent and let it handle result
            fragmentManager = getSupportFragmentManager();

            final Fragment callingFragment = fragmentManager.findFragmentByTag(data.getStringExtra(ServiceParams.STOCK_FRAGMENT_TAG));
            if (callingFragment != null) {
                callingFragment.onActivityResult(requestCode, resultCode, data);
            }
        }

        super.onActivityResult(requestCode, resultCode, data);
    }
}