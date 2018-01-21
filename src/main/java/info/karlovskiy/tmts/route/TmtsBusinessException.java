package info.karlovskiy.tmts.route;

import org.apache.commons.lang3.StringUtils;

public class TmtsBusinessException extends RuntimeException {

    private final TmtsBusinessError error;

    public TmtsBusinessException(TmtsBusinessError error) {
        this(error, "");
    }

    public TmtsBusinessException(TmtsBusinessError error, String message) {
        super(StringUtils.defaultIfEmpty(message, error.getDescription()));
        this.error = error;
    }

    public TmtsBusinessException(TmtsBusinessError error, Throwable cause) {
        this(error, "", cause);
    }

    public TmtsBusinessException(TmtsBusinessError error, String message, Throwable cause) {
        super(StringUtils.defaultIfEmpty(message, error.getDescription()), cause);
        this.error = error;
    }

    public TmtsBusinessError getError() {
        return error;
    }
}
