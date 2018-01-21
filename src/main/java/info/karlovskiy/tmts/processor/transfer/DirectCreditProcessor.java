package info.karlovskiy.tmts.processor.transfer;

import info.karlovskiy.tmts.model.Bank;
import info.karlovskiy.tmts.model.Customer;
import info.karlovskiy.tmts.model.transfer.DirectCreditTransfer;
import info.karlovskiy.tmts.processor.Storage;
import info.karlovskiy.tmts.route.TmtsBusinessException;

import java.math.BigDecimal;

import static info.karlovskiy.tmts.route.TmtsBusinessError.TRANSFER_PROCESS_ERROR;

public class DirectCreditProcessor extends BaseProcessor<DirectCreditTransfer> {

    @Override
    protected void validate(DirectCreditTransfer transfer) {

        if (transfer.getAmount() == null) {
            throw new TmtsBusinessException(TRANSFER_PROCESS_ERROR, "Amount is mandatory");
        }
        if (transfer.getOrderingCustomer() == null) {
            throw new TmtsBusinessException(TRANSFER_PROCESS_ERROR, "Ordering customer is mandatory");
        }
        if (transfer.getOrderingInstitution() == null) {
            throw new TmtsBusinessException(TRANSFER_PROCESS_ERROR, "Ordering institution is mandatory");
        }
        if (transfer.getBeneficiaryCustomer() == null) {
            throw new TmtsBusinessException(TRANSFER_PROCESS_ERROR, "Beneficiary customer is mandatory");
        }
        if (transfer.getAccountWithInstitution() == null) {
            throw new TmtsBusinessException(TRANSFER_PROCESS_ERROR, "Account with institution bank is mandatory");
        }

    }

    @Override
    protected void doProcess(DirectCreditTransfer transfer) {
        Customer orderingCustomer = transfer.getOrderingCustomer();
        Bank debtorBank = transfer.getOrderingInstitution();
        Storage.CustomerAccount from = new Storage.CustomerAccount(orderingCustomer, debtorBank);
        Customer beneficiaryCustomer = transfer.getBeneficiaryCustomer();
        Bank accountWithInstitution = transfer.getAccountWithInstitution();
        Storage.CustomerAccount to = new Storage.CustomerAccount(beneficiaryCustomer, accountWithInstitution);

        BigDecimal amount = transfer.getAmount();
        Storage.moveMoney(from, to, amount);
    }
}
