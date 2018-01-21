package info.karlovskiy.tmts;

import info.karlovskiy.tmts.config.ServerConfiguration;
import info.karlovskiy.tmts.jetty.TmtsJettyServerFactory;
import info.karlovskiy.tmts.route.TmtsBusinessException;
import info.karlovskiy.tmts.route.transfer.TransferRoute;
import org.eclipse.jetty.util.thread.QueuedThreadPool;
import org.eclipse.jetty.util.thread.ThreadPool;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import spark.embeddedserver.EmbeddedServers;
import spark.embeddedserver.jetty.EmbeddedJettyFactory;

import static spark.Spark.after;
import static spark.Spark.awaitInitialization;
import static spark.Spark.before;
import static spark.Spark.exception;
import static spark.Spark.initExceptionHandler;
import static spark.Spark.path;
import static spark.Spark.port;
import static spark.Spark.post;

public class Server {
    private static final Logger log = LoggerFactory.getLogger(Server.class);

    public static void main(String[] args) {
        start();
    }

    static void start() {
        log.info("Starting TMTS server");

        // configuration
        try {
            ServerConfiguration.load();
        } catch (Exception e) {
            exit("Error loading server configuration ", e, ExitCode.SERVER_CONFIGURATION_ERROR);
        }
        // server initialisation
        try {
            initExceptionHandler((e) -> exit("Error initialise server ", e, ExitCode.SERVER_INITIALISATION_ERROR));

            int port = ServerConfiguration.getIntProperty(ServerConfiguration.PORT);
            port(port);

            int maxThreads = ServerConfiguration.getIntProperty(ServerConfiguration.MAX_THREADS);
            int minThreads = ServerConfiguration.getIntProperty(ServerConfiguration.MIN_THREADS);
            int idleTimeoutInSeconds = ServerConfiguration.getIntProperty(ServerConfiguration.IDLE_TIMEOUT_SECONDS);
            ThreadPool threadPool = new QueuedThreadPool(maxThreads, minThreads, idleTimeoutInSeconds * 1000);

            EmbeddedJettyFactory embeddedJettyFactory = new EmbeddedJettyFactory(new TmtsJettyServerFactory()).withThreadPool(threadPool);
            EmbeddedServers.add(EmbeddedServers.Identifiers.JETTY, embeddedJettyFactory);

            before("/transfer", TransferRoute::preProcess);
            post("/transfer", TransferRoute::process);
            after("/transfer",TransferRoute::postProcess);
            exception(TmtsBusinessException.class, TransferRoute::businessException);

            after(((request, response) -> {
                boolean gzipEnabled = ServerConfiguration.getOptionalBooleanProperty(ServerConfiguration.GZIP_ENABLED);
                if (gzipEnabled) {
                    response.header("Content-Encoding", "gzip");
                }
                String version = ServerConfiguration.getProperty(ServerConfiguration.VERSION);
                response.header("Server", "TMTS (" + version + ")");
            }));

            awaitInitialization();

            log.info("TMTS server started");

        } catch (Exception e) {
            exit("Error starting server ", e, ExitCode.SERVER_INITIALISATION_ERROR);
        }
    }

    private static void exit(String message, Throwable t, ExitCode exitCode) {
        log.error(message, t);
        System.exit(exitCode.getCode());
    }

    private enum ExitCode {
        SERVER_CONFIGURATION_ERROR(10),
        SERVER_INITIALISATION_ERROR(11);

        private int code;

        ExitCode(int code) {
            this.code = code;
        }

        public int getCode() {
            return code;
        }
    }
}
