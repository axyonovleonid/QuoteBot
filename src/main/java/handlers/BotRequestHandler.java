package handlers;

import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.vk.api.sdk.client.VkApiClient;
import com.vk.api.sdk.client.actors.GroupActor;
import com.vk.api.sdk.exceptions.ApiException;
import com.vk.api.sdk.exceptions.ClientException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class BotRequestHandler {

    private static final Logger log = LoggerFactory.getLogger(BotRequestHandler.class);
    private static final String BODY_FIELD = "body";
    private static final String USER_ID_FIELD = "user_id";
    private static final String QUOTE_PREFIX = "Your message: "; //TODO: auto locale?

    private final VkApiClient apiClient;
    private final GroupActor actor;

    public BotRequestHandler(VkApiClient apiClient, GroupActor actor) {
        this.apiClient = apiClient;
        this.actor = actor;
    }

    private Integer getUserID(JsonObject object) {
        return object.getAsJsonPrimitive(USER_ID_FIELD).getAsInt();
    }

    private String getMessage(JsonObject object) {
        return object.getAsJsonPrimitive(BODY_FIELD).getAsString();
    }

    void handle(JsonObject object) throws JsonParseException {
        int userID = getUserID(object);
        String userMessage = getMessage(object);
        try {
            apiClient.messages().send(actor).message(QUOTE_PREFIX.concat(userMessage)).userId(userID).execute();
        } catch (ApiException e) {
            log.error("INVALID REQUEST", e);
        } catch (ClientException e) {
            log.error("NETWORK ERROR", e);
        }
    }
}
