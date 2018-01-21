package info.karlovskiy.tmts.model.transfer;

import java.util.Arrays;
import java.util.Optional;

public enum TransferType {

    DIRECT_DEBIT("DD", DirectDebitTransfer.class),
    DIRECT_CREDIT("DC", DirectCreditTransfer.class);

    public static final String TRANSFER_TYPE_HEADER = "TMTS_TRANSFER_TYPE";

    private final String code;
    private final Class<? extends ITransfer> clazz;

    TransferType(String code, Class<? extends ITransfer> clazz) {
        this.code = code;
        this.clazz = clazz;
    }

    public String getCode() {
        return code;
    }

    public Class<? extends ITransfer> getClazz() {
        return clazz;
    }

    public static Optional<TransferType> valueByCode(String code) {
        return Arrays.stream(values())
                .filter(bankOperation -> bankOperation.getCode().equalsIgnoreCase(code))
                .findFirst();
    }
}
