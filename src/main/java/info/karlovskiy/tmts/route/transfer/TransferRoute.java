package info.karlovskiy.tmts.route.transfer;

import info.karlovskiy.tmts.model.transfer.ITransfer;
import info.karlovskiy.tmts.model.transfer.TransferResponse;
import info.karlovskiy.tmts.model.transfer.TransferType;
import info.karlovskiy.tmts.route.TmtsBusinessException;
import info.karlovskiy.tmts.utils.JsonUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.Validate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import spark.Request;
import spark.Response;

import java.util.Optional;

import static info.karlovskiy.tmts.route.TmtsBusinessError.TRANSFER_NOT_FOUND;
import static info.karlovskiy.tmts.route.TmtsBusinessError.TRANSFER_PARSE_ERROR;
import static info.karlovskiy.tmts.route.TmtsBusinessError.TRANSFER_PROCESS_ERROR;
import static info.karlovskiy.tmts.route.TmtsBusinessError.TRANSFER_TYPE_NOT_FOUND;
import static info.karlovskiy.tmts.route.TmtsBusinessError.WRONG_CONTENT_TYPE;
import static info.karlovskiy.tmts.route.TmtsBusinessError.WRONG_TRANSFER_TYPE;

public class TransferRoute {

    private static final Logger log = LoggerFactory.getLogger(TransferRoute.class);

    /**
     * New transfer processing
     *
     * @param request  request
     * @param response response
     * @return result
     * @throws Exception if error while processing
     */
    public static Object process(Request request, Response response) throws Exception {
        log.debug("Processing new transfer");

        // validate
        String transferTypeHeader = request.headers(TransferType.TRANSFER_TYPE_HEADER);
        if (transferTypeHeader == null) {
            throw new TmtsBusinessException(TRANSFER_TYPE_NOT_FOUND);
        }
        Optional<TransferType> transferTypeOptional = TransferType.valueByCode(transferTypeHeader);
        if (!transferTypeOptional.isPresent()) {
            throw new TmtsBusinessException(WRONG_TRANSFER_TYPE);
        }
        TransferType transferType = transferTypeOptional.get();
        String transferString = request.body();
        if (StringUtils.isEmpty(transferString)) {
            throw new TmtsBusinessException(TRANSFER_NOT_FOUND);
        }

        // parse
        ITransfer transfer;
        try {
            transfer = JsonUtils.mapper.readValue(transferString, transferType.getClazz());
            Validate.notNull(transfer, "Transfer is null after parsing");
        } catch (Exception e) {
            throw new TmtsBusinessException(TRANSFER_PARSE_ERROR, e);
        }

        // process
        log.info("Processing new {}: {}", transfer.description(), transfer);
        try {
            ITransferVisitor visitor = new TransferVisitor();
            transfer.accept(visitor);
            TransferResponse transferResponse = TransferResponse.success("Transfer '" + transfer.description() + "' successfully processed");
            return JsonUtils.toJSON(transferResponse);
        } catch (TmtsBusinessException tbe) {
            throw tbe;
        } catch (Exception e) {
            throw new TmtsBusinessException(TRANSFER_PROCESS_ERROR, e);
        }
    }

    /**
     * Transfer pre processing
     *
     * @param request  request
     * @param response response
     * @throws Exception if error while pre processing
     */
    public static void preProcess(Request request, Response response) throws Exception {
        log.debug("Before processing new transfer");
        String contentType = request.contentType();
        if (!JsonUtils.JSON_CONTENT_TYPE.equals(contentType)) {
            throw new TmtsBusinessException(WRONG_CONTENT_TYPE);
        }
    }

    /**
     * Transfer post processing
     *
     * @param request  request
     * @param response response
     * @throws Exception if error while post processing
     */
    public static void postProcess(Request request, Response response) throws Exception {
        log.debug("After processing new transfer");
    }

    /**
     * Business exception handling
     *
     * @param exception business exception
     * @param request   request
     * @param response  response
     */
    public static void businessException(TmtsBusinessException exception, Request request, Response response) {
        log.error(exception.getMessage(), exception);
        TransferResponse errorResponse = TransferResponse.error(exception);
        String body = JsonUtils.toJSON(errorResponse);
        response.body(body);
    }
}
