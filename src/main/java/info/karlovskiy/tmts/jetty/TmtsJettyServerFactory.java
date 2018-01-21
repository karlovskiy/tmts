package info.karlovskiy.tmts.jetty;

import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.util.thread.ThreadPool;
import spark.embeddedserver.jetty.JettyServerFactory;

public class TmtsJettyServerFactory implements JettyServerFactory {

    @Override
    public Server create(ThreadPool threadPool) {
        Server server = threadPool != null ? new Server(threadPool) : new Server();
        server.setRequestLog(new RequestLog());
        return server;
    }

    @Override
    public Server create(int maxThreads, int minThreads, int threadTimeoutMillis) {
        throw new UnsupportedOperationException("Embedded Jetty creation without thread pool is not supported");
    }
}
