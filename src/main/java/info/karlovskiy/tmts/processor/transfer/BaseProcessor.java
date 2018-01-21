package info.karlovskiy.tmts.processor.transfer;

import info.karlovskiy.tmts.model.transfer.ITransfer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

abstract class BaseProcessor<T extends ITransfer> implements IProcessor<T> {

    protected final Logger log = LoggerFactory.getLogger(getClass());

    protected abstract void validate(T transfer);

    protected abstract void doProcess(T transfer);

    @Override
    public void process(T transfer) throws Exception {
        log.info("Start processing transfer: {}", transfer);
        validate(transfer);
        log.info("Transfer validated: {}", transfer);
        doProcess(transfer);
        log.info("Finish processing transfer: {}", transfer);
    }
}
