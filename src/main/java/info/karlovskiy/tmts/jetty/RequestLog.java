package info.karlovskiy.tmts.jetty;

import org.eclipse.jetty.server.AbstractNCSARequestLog;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

public class RequestLog extends AbstractNCSARequestLog {

    private static final Logger REQUEST_LOG = LoggerFactory.getLogger("tmts.request_log");

    @Override
    protected boolean isEnabled() {
        return true;
    }

    @Override
    public void write(String requestEntry) throws IOException {
        REQUEST_LOG.info(requestEntry);
    }
}
