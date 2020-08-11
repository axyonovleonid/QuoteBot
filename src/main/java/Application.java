import com.vk.api.sdk.client.VkApiClient;
import com.vk.api.sdk.client.actors.GroupActor;
import com.vk.api.sdk.exceptions.ApiException;
import com.vk.api.sdk.exceptions.ClientException;
import com.vk.api.sdk.httpclient.HttpTransportClient;
import handlers.RequestHandler;
import handlers.VKMessageHandler;
import org.eclipse.jetty.server.Server;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class Application {
    private final static String PROPERTIES_FILE = "app.properties";
    private final static Logger log = LoggerFactory.getLogger(Application.class);

    public static void main(String[] args) throws Exception {
        log.info("Init start...");
        Properties properties = readProperties();

        HttpTransportClient client = new HttpTransportClient();
        VkApiClient apiClient = new VkApiClient(client);

        GroupActor actor = initVkApi(apiClient, properties);
        VKMessageHandler messageHandler = new VKMessageHandler(apiClient, actor, properties.getProperty("quote.prefix"));

        Server server = new Server(Integer.parseInt(properties.getProperty("bot.server.port")));

        server.setHandler(new RequestHandler(messageHandler, properties.getProperty("api.confirmationCode")));

        log.info("Server starting ...");

        server.start();
        server.join();
    }

    private static GroupActor initVkApi(VkApiClient apiClient, Properties properties) {
        log.info("Start initializing VK API");
        int groupId = Integer.parseInt(properties.getProperty("api.group.id"));
        String token = properties.getProperty("api.token");
        int serverId = Integer.parseInt(properties.getProperty("api.server.id"));
        if (groupId == 0 || token == null || serverId == 0) {
            throw new RuntimeException("Params are not set");
        }
        GroupActor actor = new GroupActor(groupId, token);

        try {
            apiClient.groups().setCallbackSettings(actor, serverId).messageNew(true).execute();
        } catch (ApiException e) {
            throw new RuntimeException("Api error during init", e);
        } catch (ClientException e) {
            throw new RuntimeException("Client error during init", e);
        }

        log.info("End initializing VK API");
        return actor;
    }

    private static Properties readProperties() throws FileNotFoundException {
        InputStream inputStream = Application.class.getClassLoader().getResourceAsStream(PROPERTIES_FILE);
        if (inputStream == null)
            throw new FileNotFoundException("Property file '" + PROPERTIES_FILE + "' not found in the classpath");
        try {
            Properties properties = new Properties();
            properties.load(inputStream);
            return properties;
        } catch (IOException e) {
            throw new RuntimeException("Incorrect properties file");
        }
    }
}
