package info.karlovskiy.tmts.route;

public enum TmtsBusinessError {

    TRANSFER_TYPE_NOT_FOUND(1, "Transfer type not found in the request"),
    WRONG_TRANSFER_TYPE(2, "Wrong transfer type in the request"),
    WRONG_CONTENT_TYPE(3, "Wrong content type for request"),
    TRANSFER_NOT_FOUND(4, "Transfer not found in the request"),
    TRANSFER_PARSE_ERROR(5, "Transfer parse error"),
    TRANSFER_PROCESS_ERROR(6, "An transfer processing error has occurred . Contact the administrator");


    private final int code;
    private final String description;

    TmtsBusinessError(int code, String description) {
        this.code = code;
        this.description = description;
    }

    public int getCode() {
        return code;
    }

    public String getDescription() {
        return description;
    }
}
