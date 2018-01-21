package info.karlovskiy.tmts.route.transfer;

import info.karlovskiy.tmts.model.transfer.DirectCreditTransfer;
import info.karlovskiy.tmts.model.transfer.DirectDebitTransfer;
import info.karlovskiy.tmts.processor.transfer.DirectCreditProcessor;
import info.karlovskiy.tmts.processor.transfer.DirectDebitProcessor;

public class TransferVisitor implements ITransferVisitor {

    @Override
    public void visit(DirectDebitTransfer directDebit) throws Exception {
        new DirectDebitProcessor().process(directDebit);
    }

    @Override
    public void visit(DirectCreditTransfer directCredit) throws Exception {
        new DirectCreditProcessor().process(directCredit);
    }
}
