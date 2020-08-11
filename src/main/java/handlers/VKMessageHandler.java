package handlers;

import com.google.gson.JsonParseException;
import com.vk.api.sdk.client.VkApiClient;
import com.vk.api.sdk.client.actors.GroupActor;
import com.vk.api.sdk.exceptions.ApiException;
import com.vk.api.sdk.exceptions.ClientException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import vo.VKMessage;

public class VKMessageHandler {

    private static final Logger log = LoggerFactory.getLogger(VKMessageHandler.class);

    private final VkApiClient apiClient;
    private final GroupActor actor;
    private final String prefix;

    public VKMessageHandler(VkApiClient apiClient, GroupActor actor, String prefix) {
        this.apiClient = apiClient;
        this.actor = actor;
        this.prefix = prefix;
    }

    void handle(VKMessage message) throws JsonParseException {
        try {
            apiClient.messages().send(actor).message(prefix.concat(message.getMessage())).userId(message.getUserID()).execute();
        } catch (ApiException e) {
            log.error("INVALID REQUEST", e);
        } catch (ClientException e) {
            log.error("NETWORK ERROR", e);
        }
    }
}
