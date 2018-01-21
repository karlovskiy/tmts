package info.karlovskiy.tmts;

import info.karlovskiy.tmts.config.ServerConfiguration;
import info.karlovskiy.tmts.model.transfer.TransferType;
import info.karlovskiy.tmts.processor.Storage;
import info.karlovskiy.tmts.utils.JsonUtils;
import org.apache.commons.lang3.StringUtils;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import spark.Spark;

import java.io.DataOutputStream;
import java.math.BigDecimal;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

import static info.karlovskiy.tmts.config.ServerConfiguration.PORT;
import static info.karlovskiy.tmts.model.transfer.TransferType.TRANSFER_TYPE_HEADER;
import static org.junit.Assert.assertEquals;

public class TransferTest {

    @Before
    public void setUp() {
        Server.start();
    }

    @After
    public void tearDown() {
        Spark.stop();
        // we don't have in spark awaitTermination method like awaitInitialisation one,
        // see https://github.com/perwendel/spark/issues/705
        while (true) {
            try {
                Spark.port();
                Thread.sleep(100);
            } catch (IllegalStateException ise) {
                break;
            } catch (InterruptedException ie) {
                Thread.currentThread().interrupt();
                break;
            }
        }
    }

    @Test
    public void testWrongContentType() throws Exception {
        TestHttpResponse response = sendTransfer(new HashMap<String, String>() {{
            put("Content-Type", "application/x-www-form-urlencoded");
        }});
        assertEquals(200, response.getCode());
        assertEquals("{  \"success\" : false,  \"description\" : \"Wrong content type for request\",  \"errorCode\" : 3}", response.getResponse());

    }

    @Test
    public void testNoTransferType() throws Exception {
        TestHttpResponse response = sendTransfer();
        assertEquals(200, response.getCode());
        assertEquals("{  \"success\" : false,  \"description\" : \"Transfer type not found in the request\",  \"errorCode\" : 1}", response.getResponse());
    }

    @Test
    public void testWrongTransferType() throws Exception {
        TestHttpResponse response = sendTransfer(new HashMap<String, String>() {{
            put(TRANSFER_TYPE_HEADER, "BAD");
        }});
        assertEquals(200, response.getCode());
        assertEquals("{  \"success\" : false,  \"description\" : \"Wrong transfer type in the request\",  \"errorCode\" : 2}", response.getResponse());
    }

    @Test
    public void testTransferNotFound() throws Exception {
        TestHttpResponse response = sendTransfer(TransferType.DIRECT_DEBIT);
        assertEquals(200, response.getCode());
        assertEquals("{  \"success\" : false,  \"description\" : \"Transfer not found in the request\",  \"errorCode\" : 4}", response.getResponse());
    }

    @Test
    public void testTransferParseError() throws Exception {
        TestHttpResponse response = sendTransfer(TransferType.DIRECT_DEBIT, "{{{}}");
        assertEquals(200, response.getCode());
        assertEquals("{  \"success\" : false,  \"description\" : \"Transfer parse error\",  \"errorCode\" : 5}", response.getResponse());
    }

    @Test
    public void testTransfer() throws Exception {
        // before transfers balances
        assertDecimalEquals("10.34", Storage.balance(Storage.NEO));
        assertDecimalEquals("1000.43", Storage.balance(Storage.MATRIX));
        assertDecimalEquals("300.00", Storage.balance(Storage.MR_ANDERSON));

        // Matrix trying to charge membership fee from Neo
        String dd = TestUtils.loadFile("/dd_matrix_fee.json");
        TestHttpResponse response = sendTransfer(TransferType.DIRECT_DEBIT, dd);
        assertEquals(200, response.getCode());
        // Neo doesn't have money so we have error here
        assertEquals("{  \"success\" : false,  \"description\" : \"No sufficient fund on account BH81CITI00000123456789, CITIBHBX, Neo\",  \"errorCode\" : 6}", response.getResponse());

        // Mr Anderson pay to Neo to find the ONE
        String dc = TestUtils.loadFile("/dc_find_the_one.json");
        response = sendTransfer(TransferType.DIRECT_CREDIT, dc);

        assertEquals(200, response.getCode());
        // Neo now have money
        assertEquals("{  \"success\" : true,  \"description\" : \"Transfer 'Direct credit' successfully processed\",  \"errorCode\" : null}", response.getResponse());

        // Matrix trying to charge membership fee from Neo again
        response = sendTransfer(TransferType.DIRECT_DEBIT, dd);
        assertEquals(200, response.getCode());
        assertEquals("{  \"success\" : true,  \"description\" : \"Transfer 'Direct debit' successfully processed\",  \"errorCode\" : null}", response.getResponse());

        // before transfers balances
        assertDecimalEquals("109.82", Storage.balance(Storage.NEO));         // 10.34 + 200.00 - 100.52
        assertDecimalEquals("1100.95", Storage.balance(Storage.MATRIX));     // 1000.43 + 100.52
        assertDecimalEquals("100.00", Storage.balance(Storage.MR_ANDERSON)); // 300.00 - 200.00
    }

    private TestHttpResponse sendTransfer() throws Exception {
        return sendTransfer(new HashMap<>(), StringUtils.EMPTY);
    }

    private TestHttpResponse sendTransfer(TransferType transferType) throws Exception {
        Map<String, String> headers = new HashMap<>();
        headers.put(TRANSFER_TYPE_HEADER, transferType.getCode());
        return sendTransfer(headers, StringUtils.EMPTY);
    }

    private TestHttpResponse sendTransfer(Map<String, String> headers) throws Exception {
        return sendTransfer(headers, StringUtils.EMPTY);
    }

    private TestHttpResponse sendTransfer(TransferType transferType, String body) throws Exception {
        Map<String, String> headers = new HashMap<>();
        headers.put(TRANSFER_TYPE_HEADER, transferType.getCode());
        return sendTransfer(headers, body);
    }

    private TestHttpResponse sendTransfer(Map<String, String> headers, String body) throws Exception {
        URL url = new URL("http://localhost:" + ServerConfiguration.getProperty(PORT) + "/transfer");
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();

        connection.setRequestMethod("POST");
        connection.setDoOutput(true);
        connection.setDoInput(true);
        headers.putIfAbsent("Content-Type", JsonUtils.JSON_CONTENT_TYPE);
        headers.forEach(connection::setRequestProperty);
        DataOutputStream writer = new DataOutputStream(connection.getOutputStream());
        writer.writeBytes(body);
        writer.flush();
        writer.close();

        TestHttpResponse response = TestHttpResponse.readFrom(connection);
        return response;
    }

    private void assertDecimalEquals(String expected, BigDecimal actual) {
        Assert.assertTrue("Expected '" + expected + "' but actual is '" + actual.toPlainString() + "'",
                new BigDecimal(expected).compareTo(actual) == 0);
    }

}
