package ax.stardust.skvirrel.service;

/**
 * Utility class containing parameters and constants used for all service calls
 * within the application.
 */
public class ServiceParams {
    public static final String STOCK_SERVICE = "stock_service";
    public static final String PENDING_RESULT = "pending_result";
    public static final String ERROR_SITUATION = "error_situation";

    /**
     * Valid service operations.
     */
    public enum Operation {
        GET_COMPANY_NAME,
        GET_STOCK_INFO,
        UNSUPPORTED;

        public static Operation from(String string) {
            if (GET_COMPANY_NAME.get().equals(string)) {
                return GET_COMPANY_NAME;
            }

            if (GET_STOCK_INFO.get().equals(string)) {
                return GET_STOCK_INFO;
            }

            return UNSUPPORTED;
        }

        public String get() {
            return this.name().toLowerCase();
        }
    }

    /**
     * Valid types of data to pass in on a service request.
     */
    public enum RequestExtra {
        SYMBOL;

        public String get() {
            return this.name().toLowerCase();
        }
    }

    /**
     * Valid codes for the different requests.
     */
    public enum RequestCode {
        GET_COMPANY_NAME(0),
        GET_STOCK_INFO(1);

        private final int code;

        RequestCode(int code) {
            this.code = code;
        }

        public int getCode() {
            return code;
        }

        public String get() {
            return this.name().toLowerCase();
        }
    }

    /**
     * Valid types of data to pass in on a service response.
     */
    public enum ResultExtra {
        COMPANY_NAME,
        STOCK_INFO;

        public String get() {
            return this.name().toLowerCase();
        }
    }

    /**
     * Valid codes for the different responses.
     */
    public enum ResultCode {
        SUCCESS(0),
        ERROR(-1);

        private final int code;

        ResultCode(int code) {
            this.code = code;
        }

        public int getCode() {
            return code;
        }

        public String get() {
            return this.name().toLowerCase();
        }
    }
}
