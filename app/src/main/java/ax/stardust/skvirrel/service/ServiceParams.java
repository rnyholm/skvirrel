package ax.stardust.skvirrel.service;

/**
 * Parameters and constants used for all service calls within the application.
 */
public class ServiceParams {

    public static final int MONITORING_JOB_SERVICE_ID = 0;
    public static final int STOCK_SERVICE_ID = 1;

    // Number of days to fetch historical quotes from yahoo finance for the different
    // stock indicators
    public static final int DAYS_OF_HISTORY = -200;

    public static final String STOCK_SERVICE = "stock_service";
    public static final String PENDING_RESULT = "pending_result";
    public static final String ERROR_SITUATION = "error_situation";
    public static final String STOCK_FRAGMENT_TAG = "stock_fragment_tag";

    /**
     * Valid service operations.
     */
    public static final class Operation {
        public static final String GET_COMPANY_NAME = "get_company_name";
        public static final String GET_STOCK_INFO = "get_stock_info";
        public static final String GET_STOCK_INFOS = "get_stock_infos";
    }

    /**
     * Valid types of data to pass in on a service request.
     */
    public static final class RequestExtra {
        public static final String TICKER = "ticker";
        public static final String TICKERS = "tickers";
    }

    /**
     * Valid codes for the different requests.
     */
    public static final class RequestCode {
        public static final int GET_COMPANY_NAME = 0;
        public static final int GET_STOCK_INFO = 1;
    }

    /**
     * Valid types of data to pass in on a service response.
     */
    public static final class ResultExtra {
        public static final String COMPANY_NAME = "company_name";
        public static final String STOCK_INFO = "stock_info";
        public static final String STOCK_INFOS = "stock_infos";
    }

    /**
     * Valid codes for the different responses.
     */
    public static final class ResultCode {
        public static final int SUCCESS = 0;
        public static final int COMMON_ERROR = -1;
        public static final int STOCK_NOT_FOUND_ERROR = -2;
    }
}
