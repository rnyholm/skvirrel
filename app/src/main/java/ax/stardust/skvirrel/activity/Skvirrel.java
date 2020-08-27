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

import ax.stardust.skvirrel.R;
import ax.stardust.skvirrel.component.keyboard.AlphanumericKeyboard;
import ax.stardust.skvirrel.fragment.StockFragment;
import ax.stardust.skvirrel.service.ServiceParams;

public class Skvirrel extends AppCompatActivity {
    private static final String TAG = Skvirrel.class.getSimpleName();

    private FragmentManager fragmentManager;
    private FragmentTransaction fragmentTransaction;

    private Button addStockMonitoringButton;

    private AlphanumericKeyboard alphanumericKeyboard;

    private TextView versionNameTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_skvirrel);
        findViews();
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
            StockFragment stockFragment = new StockFragment(this, alphanumericKeyboard);
            fragmentManager = getSupportFragmentManager();
            fragmentTransaction = fragmentManager.beginTransaction();
            fragmentTransaction.add(R.id.stock_fragment_container, stockFragment, "12345").commit(); // TODO: set correct fragment tag
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        // figure out what fragment is responsible for intent and let it handle result
        Fragment callingFragment = fragmentManager.findFragmentByTag(data.getStringExtra(ServiceParams.STOCK_FRAGMENT_TAG));
        if (callingFragment != null) {
            callingFragment.onActivityResult(requestCode, resultCode, data);
        }

        super.onActivityResult(requestCode, resultCode, data);
    }
}