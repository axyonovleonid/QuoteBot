package main;

import com.google.gson.*;
import org.eclipse.jetty.server.Request;
import org.eclipse.jetty.server.handler.AbstractHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.Reader;

class RequestHandler extends AbstractHandler {
    private static final Logger log = LoggerFactory.getLogger(RequestHandler.class);
    private static final String OK_BODY = "ok";
    private static final String TYPE_FIELD = "type";
    private static final Gson gson = new GsonBuilder().create();
    private final BotRequestHandler botRequestHandler;
    private final String confirmationCode;

    RequestHandler(BotRequestHandler handler, String confirmationCode) {
        this.botRequestHandler = handler;
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
                    responseBody = confirmationCode;
                    break;
                case EventTypes.CALLBACK_EVENT_MESSAGE_NEW:
                    botRequestHandler.handle(requestJson.getAsJsonObject("object"));
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
}