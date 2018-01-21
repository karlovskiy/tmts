package info.karlovskiy.tmts.processor.transfer;

import info.karlovskiy.tmts.model.transfer.ITransfer;

public interface IProcessor<T extends ITransfer> {

    void process(T transfer) throws Exception;
}
