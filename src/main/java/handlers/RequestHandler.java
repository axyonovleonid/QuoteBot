package handlers;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import org.eclipse.jetty.server.Request;
import org.eclipse.jetty.server.handler.AbstractHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import vo.VKMessage;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.Reader;

public class RequestHandler extends AbstractHandler {
    private static final Logger log = LoggerFactory.getLogger(RequestHandler.class);
    private static final String OK_BODY = "ok";
    private static final String TYPE_FIELD = "type";
    private static final Gson gson = new GsonBuilder().create();
    private final VKMessageHandler messageHandler;
    private final String confirmationCode;

    public RequestHandler(VKMessageHandler handler, String confirmationCode) {
        this.messageHandler = handler;
        this.confirmationCode = confirmationCode;
    }

    public void handle(String target, Request baseRequest, HttpServletRequest request, HttpServletResponse response)
            throws IOException, ServletException {

        if (!"POST".equalsIgnoreCase(request.getMethod())) {
            throw new ServletException("This method is unsupported");
        }

        Reader reader = request.getReader();

        try {
            JsonObject requestJson = gson.fromJson(reader, JsonObject.class);
            String type = requestJson.get(TYPE_FIELD).getAsString();
            if (type == null || type.isEmpty()) throw new ServletException("No type in json");

            final String responseBody;

            switch (type) {
                case EventTypes.CALLBACK_EVENT_CONFIRMATION:
                    log.info("Confirmation code processing");
                    responseBody = confirmationCode;
                    break;
                case EventTypes.CALLBACK_EVENT_MESSAGE_NEW:
                    log.info("Message processing");
                    VKMessage message = gson.fromJson(requestJson.getAsJsonObject("object"), VKMessage.class);
                    messageHandler.handle(message);
                    responseBody = OK_BODY;
                    break;
                default:
                    responseBody = OK_BODY; // in case we get another event
                    break;
            }

            response.setContentType("text/html;charset=utf-8");
            response.setStatus(HttpServletResponse.SC_OK);

            baseRequest.setHandled(true);

            response.getWriter().println(responseBody);
        } catch (JsonParseException e) {
            throw new ServletException("Incorrect json", e);
        }
    }


    static class EventTypes {
        public static final String CALLBACK_EVENT_MESSAGE_NEW = "message_new";
        public static final String CALLBACK_EVENT_CONFIRMATION = "confirmation";
    }
}

