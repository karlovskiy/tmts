package info.karlovskiy.tmts.processor.transfer;

import info.karlovskiy.tmts.model.Bank;
import info.karlovskiy.tmts.model.Customer;
import info.karlovskiy.tmts.model.transfer.DirectDebitTransfer;
import info.karlovskiy.tmts.processor.Storage;
import info.karlovskiy.tmts.route.TmtsBusinessException;

import java.math.BigDecimal;

import static info.karlovskiy.tmts.route.TmtsBusinessError.TRANSFER_PROCESS_ERROR;

public class DirectDebitProcessor extends BaseProcessor<DirectDebitTransfer> {

    @Override
    protected void validate(DirectDebitTransfer transfer) {
        if (transfer.getAmount() == null) {
            throw new TmtsBusinessException(TRANSFER_PROCESS_ERROR, "Amount is mandatory");
        }
        if (transfer.getCreditor() == null) {
            throw new TmtsBusinessException(TRANSFER_PROCESS_ERROR, "Creditor is mandatory");
        }
        if (transfer.getCreditorBank() == null) {
            throw new TmtsBusinessException(TRANSFER_PROCESS_ERROR, "Creditor bank is mandatory");
        }
        if (transfer.getDebtor() == null) {
            throw new TmtsBusinessException(TRANSFER_PROCESS_ERROR, "Debtor is mandatory");
        }
        if (transfer.getDebtorBank() == null) {
            throw new TmtsBusinessException(TRANSFER_PROCESS_ERROR, "Debtor bank is mandatory");
        }
    }

    @Override
    protected void doProcess(DirectDebitTransfer transfer) {
        Customer debtor = transfer.getDebtor();
        Bank debtorBank = transfer.getDebtorBank();
        Storage.CustomerAccount from = new Storage.CustomerAccount(debtor, debtorBank);
        Customer creditor = transfer.getCreditor();
        Bank creditorBank = transfer.getCreditorBank();
        Storage.CustomerAccount to = new Storage.CustomerAccount(creditor, creditorBank);

        BigDecimal amount = transfer.getAmount();
        Storage.moveMoney(from, to, amount);
    }
}
