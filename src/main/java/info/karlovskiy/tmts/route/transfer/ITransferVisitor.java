package info.karlovskiy.tmts.route.transfer;

import info.karlovskiy.tmts.model.transfer.DirectCreditTransfer;
import info.karlovskiy.tmts.model.transfer.DirectDebitTransfer;

public interface ITransferVisitor {

    void visit(DirectDebitTransfer directDebit) throws Exception;

    void visit(DirectCreditTransfer directCredit) throws Exception;
}
