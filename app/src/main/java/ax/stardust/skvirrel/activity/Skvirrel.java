package ax.stardust.skvirrel.activity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
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
import ax.stardust.skvirrel.fragment.StockFragment;
import ax.stardust.skvirrel.persistence.DatabaseManager;
import ax.stardust.skvirrel.entity.StockMonitoring;
import ax.stardust.skvirrel.service.ServiceParams;

public class Skvirrel extends AppCompatActivity {
    private static final String TAG = Skvirrel.class.getSimpleName();

    private DatabaseManager databaseManager;

    private FragmentManager fragmentManager;
    private FragmentTransaction fragmentTransaction;

    private Button addStockMonitoringButton;

    private AlphanumericKeyboard alphanumericKeyboard;

    private TextView versionNameTextView;

    private List<StockMonitoring> stockMonitorings = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_skvirrel);
        findViews();
        addStockFragments();
        setListeners();
    }

    @Override
    public void onBackPressed() {
        if (alphanumericKeyboard.getVisibility() == View.VISIBLE) {
            // hacky way to release focus from any edit text, by releasing it also the keyboard will be closed
            versionNameTextView.requestFocus();
        } else {
            super.onBackPressed();
        }
    }

    private void findViews() {
        addStockMonitoringButton = findViewById(R.id.add_stock_monitoring_btn);
        alphanumericKeyboard = findViewById(R.id.alphanumeric_keyboard);
        versionNameTextView = findViewById(R.id.version_name_tv);
    }

    private void setListeners() {
        addStockMonitoringButton.setOnClickListener(view -> {
            // create a new stock monitoring, store it to db, local list and
            // update ui with a new stock fragment
            StockMonitoring stockMonitoring = getDatabaseManager().insert(new StockMonitoring());
            stockMonitorings.add(stockMonitoring);
            addStockFragment(stockMonitoring);
        });
    }

    private void addStockFragments() {
        stockMonitorings = getDatabaseManager().fetchAll();
        stockMonitorings.forEach(this::addStockFragment);
    }

    private void addStockFragment(StockMonitoring stockMonitoring) {
        final StockFragment stockFragment = new StockFragment(this, stockMonitoring, alphanumericKeyboard);
        fragmentManager = getSupportFragmentManager();
        fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.add(R.id.stock_fragment_container, stockFragment, String.valueOf(stockMonitoring.getId()));
        fragmentTransaction.commit();
    }

    private DatabaseManager getDatabaseManager() {
        if (databaseManager == null) {
            databaseManager = new DatabaseManager(this);
        }
        return databaseManager;
    }

    public void removeStockMonitoringAndFragment(StockMonitoring stockMonitoring) {
        fragmentManager = getSupportFragmentManager();

        final Fragment fragment = fragmentManager.findFragmentByTag(String.valueOf(stockMonitoring.getId()));
        if (fragment != null) {
            fragmentTransaction = fragmentManager.beginTransaction();
            fragmentTransaction.remove(fragment);
            fragmentTransaction.commit();
        } else {
            Log.e(TAG, "removeStockMonitoringAndFragment(...) -> No fragment found with tag -> " + stockMonitoring.getId());
        }

        getDatabaseManager().delete(stockMonitoring.getId());
        stockMonitorings.remove(stockMonitoring);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        // figure out what fragment is responsible for intent and let it handle result
        fragmentManager = getSupportFragmentManager();

        final Fragment callingFragment = fragmentManager.findFragmentByTag(data.getStringExtra(ServiceParams.STOCK_FRAGMENT_TAG));
        if (callingFragment != null) {
            callingFragment.onActivityResult(requestCode, resultCode, data);
        }

        super.onActivityResult(requestCode, resultCode, data);
    }
}