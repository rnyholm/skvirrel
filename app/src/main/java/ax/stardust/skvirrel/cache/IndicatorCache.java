package ax.stardust.skvirrel.cache;

import java.sql.Timestamp;
import java.util.Date;

import ax.stardust.skvirrel.BuildConfig;
import ax.stardust.skvirrel.util.SkvirrelUtils;
import lombok.Getter;
import lombok.Setter;

/**
 * Small cache of some indicator values.
 */
@Getter
@Setter
public class IndicatorCache {

    // time to live for this cache in minutes
    private static final int TIME_TO_LIVE_DEBUG = 10;
    private static final int TIME_TO_LIVE_RELEASE = 60;

    private long id;

    private String ticker;

    private double sma = SkvirrelUtils.UNSET;
    private double ema = SkvirrelUtils.UNSET;
    private double rsi = SkvirrelUtils.UNSET;

    private Timestamp expires = null;

    /**
     * Creates a new indicator cache with given ticker
     *
     * @param ticker ticker of indicator cache
     */
    public IndicatorCache(String ticker) {
        this.ticker = ticker;
    }

    /**
     * Creates a new indicator cache with given id and ticker
     *
     * @param id     id of indicator cache
     * @param ticker ticker of indicator cache
     */
    public IndicatorCache(long id, String ticker) {
        this(ticker);
        this.id = id;
    }

    public void setExpires(Date expires) {
        this.expires = new Timestamp(expires.getTime() + resolveTimeToLive());
    }

    public void setExpires(long timeInMilliseconds) {
        expires = Integer.MIN_VALUE != timeInMilliseconds ? new Timestamp(timeInMilliseconds) : null;
    }

    /**
     * Resolves time to live for cache. Different times are calculated
     * depending on if it's a debug or release build. Debug builds has shorter time to live
     *
     * @return resolved time to live
     */
    private long resolveTimeToLive() {
        return 60 * 1000 * (BuildConfig.DEBUG ? TIME_TO_LIVE_DEBUG : TIME_TO_LIVE_RELEASE);
    }

    /**
     * To decide whether or not this cache needs to be refreshed
     *
     * @return true if refresh is needed else false
     */
    public boolean needsRefresh() {
        return isMissingData() || hasExpired();
    }

    /**
     * To find out if this cache has expired or not
     *
     * @return true if cache has expired else false
     */
    public boolean hasExpired() {
        return expires == null || expires.before(new Date());
    }

    /**
     * To find out if this cache contains all data it's supposed to have
     *
     * @return true if cache contains alla data else false
     */
    public boolean isMissingData() {
        return SkvirrelUtils.UNSET == sma || SkvirrelUtils.UNSET == ema
                || SkvirrelUtils.UNSET == rsi || expires == null;
    }
}
