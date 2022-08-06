package yaremvladimir.monobank.api.kafka.connect;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.time.Instant;
import java.util.List;

import org.apache.hc.client5.http.classic.HttpClient;
import org.apache.hc.client5.http.classic.methods.HttpGet;
import org.apache.hc.client5.http.impl.classic.HttpClients;
import org.apache.hc.core5.http.ClassicHttpResponse;
import org.apache.hc.core5.http.HttpEntity;
import org.apache.hc.core5.http.ParseException;
import org.apache.hc.core5.http.io.entity.EntityUtils;
import org.apache.kafka.connect.errors.ConnectException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.RequiredArgsConstructor;
import yaremvladimir.monobank.api.kafka.connect.model.MonoBankError;
import yaremvladimir.monobank.api.kafka.connect.model.Operation;

@RequiredArgsConstructor
public class MonoBankHttpClient {
    private static final Logger log = LoggerFactory.getLogger(MonoBankHttpClient.class);

    private static String MONOBANK_API_URL = "https://api.monobank.ua/";
    private final MonoBankSourceConnectorConfig config;
    
    public List<Operation> getOperations(Instant from, Instant to) throws InterruptedException {
        HttpClient httpClient = HttpClients.createDefault();
        try {
            HttpGet request = new HttpGet(new URI(MONOBANK_API_URL + "personal/statement/" + config.getAccountId() + "/" + from.toEpochMilli() + "/" + to.toEpochMilli()));
            request.setHeader("X-Token", config.getAuthToken());
        
            ClassicHttpResponse response = (ClassicHttpResponse) httpClient.execute(request);
            ObjectMapper objectMapper = new ObjectMapper();
            HttpEntity entity = response.getEntity();
            String responseString = EntityUtils.toString(entity);
    
            if (response.getCode() == 429) {
                Thread.sleep(1000 * 60);
                return getOperations(from, to);
            }
            if (response.getCode() != 200) {
                MonoBankError error = objectMapper.readValue(responseString, MonoBankError.class);
                if (error.getErrorDescription() == "Missing one of required headers 'X-Token' or 'X-Key-Id'" ||
                    error.getErrorDescription() == "Unknown 'X-Token'") {
                    throw new ConnectException("Provided auth token is missing invalid. Please verify it on http://api.monobank.ua.");
                }
                throw new ConnectException("Error getting operations: " + responseString);
            }
            
            return objectMapper.readValue(responseString, new TypeReference<List<Operation>>(){});
        } catch (URISyntaxException | IOException | ParseException e) {
            log.error("Error making requests to Monobank API: ", e);
            throw new ConnectException("Problems making requests to Monobank API");
        }
    }
}
