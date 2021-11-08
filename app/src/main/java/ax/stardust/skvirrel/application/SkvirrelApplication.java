package ax.stardust.skvirrel.application;

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.preference.PreferenceManager;
import android.util.Log;

import org.acra.ACRA;
import org.acra.config.CoreConfigurationBuilder;
import org.acra.config.DialogConfigurationBuilder;
import org.acra.config.MailSenderConfigurationBuilder;
import org.acra.data.StringFormat;
import org.jetbrains.annotations.NotNull;

import ax.stardust.skvirrel.BuildConfig;
import ax.stardust.skvirrel.R;
import ax.stardust.skvirrel.activity.SkvirrelCrashReportDialog;
import ax.stardust.skvirrel.notification.NotificationHandler;
import ax.stardust.skvirrel.persistence.DatabaseManager;
import ax.stardust.skvirrel.schedule.MonitoringScheduler;
import lombok.SneakyThrows;
import timber.log.Timber;

/**
 * Base class for maintaining global application state and different setups.
 */
public class SkvirrelApplication extends Application {

    private static final String VERSION_CODE = "version_code";

    private DatabaseManager databaseManager;

    /**
     * Creates a new instance of skvirrel application
     */
    public SkvirrelApplication() {
        super();
    }

    @Override
    @SneakyThrows
    public void onCreate() {
        // very, very important to call on create
        super.onCreate();

        // plant a debug log tree if not production
        if (BuildConfig.DEBUG) {
            Timber.plant(new Timber.DebugTree());
        } else {
            Timber.plant(new ProductionTree());
        }

        // create notification handler and schedule monitoring job
        NotificationHandler.createNotificationChannel(this);
        MonitoringScheduler.scheduleJob(this);

        // some house keeping if needed
        addMissingMonitoringsIfNeeded();
    }

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);

        // configure acra
        CoreConfigurationBuilder builder = new CoreConfigurationBuilder(this);

        // core configuration
        builder.withBuildConfigClass(BuildConfig.class)
                .withReportFormat(StringFormat.JSON);

        // mail configuration
        builder.getPluginConfigurationBuilder(MailSenderConfigurationBuilder.class)
                .withSubject(getString(R.string.crash_report_email_subject))
                .withMailTo(getString(R.string.skvirrel_email))
                .withReportFileName(getString(R.string.crash_report_filename))
                .withReportAsFile(true)
                //make sure to enable all plugins you want to use:
                .withEnabled(true);

        // dialog configuration
        builder.getPluginConfigurationBuilder(DialogConfigurationBuilder.class)
                .withReportDialogClass(SkvirrelCrashReportDialog.class)
                .withEnabled(true);

        ACRA.init(this, builder);
    }

    private void addMissingMonitoringsIfNeeded() throws PackageManager.NameNotFoundException {
        // get version codes, both from package and shared preferences
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
        int preferencesVersionCode = preferences.getInt(VERSION_CODE, 1);
        int packageVersionCode = getPackageManager().getPackageInfo(this.getPackageName(), 0).versionCode;

        // if app has been updated, add monitorings if missing and store new version code in preferences,
        // this way we get a bit optimized startup by not invoking addMonitoringsIfMissing if not needed
        if (packageVersionCode > preferencesVersionCode) {
            // a bit of house keeping by adding any eventual new monitorings that has been added
            getDatabaseManager().addMonitoringsIfMissing();

            SharedPreferences.Editor editor = preferences.edit();
            editor.putInt(VERSION_CODE, packageVersionCode);
            editor.apply();
        }
    }

    private DatabaseManager getDatabaseManager() {
        if (databaseManager == null) {
            databaseManager = new DatabaseManager(this);
        }
        return databaseManager;
    }

    /**
     * Simple log tree for production purposes. Only log messages with log
     * priorities ERROR and WARNING is logged using this tree.
     */
    private static class ProductionTree extends Timber.DebugTree {

        @Override
        protected void log(int priority, String tag, @NotNull String message, Throwable t) {
            // only log error and warning in production
            if (priority == Log.ERROR || priority == Log.WARN) {
                super.log(priority, tag, message, t);
            }
        }
    }
}
