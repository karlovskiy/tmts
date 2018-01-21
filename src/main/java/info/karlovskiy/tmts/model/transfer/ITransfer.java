package info.karlovskiy.tmts.model.transfer;

import info.karlovskiy.tmts.route.transfer.ITransferVisitor;

public interface ITransfer {

    void accept(ITransferVisitor visitor) throws Exception;

    String description();
}
