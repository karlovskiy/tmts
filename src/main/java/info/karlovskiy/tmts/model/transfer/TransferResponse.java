package info.karlovskiy.tmts.model.transfer;

import info.karlovskiy.tmts.route.TmtsBusinessError;
import info.karlovskiy.tmts.route.TmtsBusinessException;

public class TransferResponse {

    private boolean success;
    private String description;
    private Integer errorCode;

    public static TransferResponse success(String description) {
        TransferResponse response = new TransferResponse();
        response.setSuccess(true);
        response.setDescription(description);
        return response;
    }

    public static TransferResponse error(TmtsBusinessException exception) {
        TransferResponse response = new TransferResponse();
        response.setSuccess(false);
        response.setErrorCode(exception.getError().getCode());
        response.setDescription(exception.getMessage());
        return response;
    }

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Integer getErrorCode() {
        return errorCode;
    }

    public void setErrorCode(Integer errorCode) {
        this.errorCode = errorCode;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("TransferResponse{");
        sb.append("success=").append(success);
        sb.append(", errorCode=").append(errorCode);
        sb.append(", description=").append(description);
        sb.append('}');
        return sb.toString();
    }
}
