package main;

import com.vk.api.sdk.client.VkApiClient;
import com.vk.api.sdk.client.actors.GroupActor;
import com.vk.api.sdk.exceptions.ApiException;
import com.vk.api.sdk.exceptions.ClientException;
import com.vk.api.sdk.httpclient.HttpTransportClient;
import org.eclipse.jetty.server.Server;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class MainService {
    private final static String PROPERTIES_FILE = "app.properties";
    private final static Logger log = LoggerFactory.getLogger(MainService.class);

    public static void main(String[] args) throws Exception {
        Properties properties = readProperties();

        HttpTransportClient client = new HttpTransportClient();
        VkApiClient apiClient = new VkApiClient(client);

        GroupActor actor = initVkApi(apiClient, properties);
        BotRequestHandler botHandler = new BotRequestHandler(apiClient, actor);

        Server server = new Server(Integer.parseInt(properties.getProperty("bot.server.port")));

        server.setHandler(new RequestHandler(botHandler, properties.getProperty("api.confirmationCode")));

        server.start();
        server.join();
    }

//    private static String getConfirmationCode(VkApiClient apiClient, GroupActor actor) {
//        try {
//            return apiClient.groups().getCallbackConfirmationCode(actor).execute().getCode();
//        } catch (ApiException e) {
//            throw new RuntimeException("Api error during init", e);
//        } catch (ClientException e) {
//            throw new RuntimeException("Client error during init", e);
//        }
//    }

    private static GroupActor initVkApi(VkApiClient apiClient, Properties properties) {
        int groupId = Integer.parseInt(properties.getProperty("api.group.id"));
        String token = properties.getProperty("api.token");
        int serverId = Integer.parseInt(properties.getProperty("api.server.id"));
        if (groupId == 0 || token == null || serverId == 0)
            throw new RuntimeException("Params are not set");
        GroupActor actor = new GroupActor(groupId, token);

        try {
            apiClient.groups().setCallbackSettings(actor, serverId).messageNew(true).execute();
        } catch (ApiException e) {
            throw new RuntimeException("Api error during init", e);
        } catch (ClientException e) {
            throw new RuntimeException("Client error during init", e);
        }

        return actor;
    }

    private static Properties readProperties() throws FileNotFoundException {
        InputStream inputStream = MainService.class.getClassLoader().getResourceAsStream(PROPERTIES_FILE);
        if (inputStream == null)
            throw new FileNotFoundException("property file '" + PROPERTIES_FILE + "' not found in the classpath");
        try {
            Properties properties = new Properties();
            properties.load(inputStream);
            return properties;
        } catch (IOException e) {
            throw new RuntimeException("Incorrect properties file");
        }
    }
}
