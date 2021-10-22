package org.example.websocket;

import java.io.StringReader;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.json.Json;
import javax.json.JsonObject;
import javax.json.JsonReader;
import javax.websocket.OnClose;
import javax.websocket.OnError;
import javax.websocket.OnMessage;
import javax.websocket.OnOpen;
import javax.websocket.Session;
import javax.websocket.server.ServerEndpoint;
import org.example.model.Camas;
import org.example.model.Hospital;

@ApplicationScoped
@ServerEndpoint("/actions")
public class DeviceWebSocketServer {

    @Inject
    private DeviceSessionHandler sessionHandler;

    @OnOpen
    public void open(Session session) {
        sessionHandler.addSession(session);
    }

    @OnClose
    public void close(Session session) {
        sessionHandler.removeSession(session);
    }

    @OnError
    public void onError(Throwable error) {
        Logger.getLogger(DeviceWebSocketServer.class.getName()).log(Level.SEVERE, null, error);
    }

    @OnMessage
    public void handleMessage(String message, Session session) {

        try (JsonReader reader = Json.createReader(new StringReader(message))) {
            JsonObject jsonMessage = reader.readObject();

            if ("add".equals(jsonMessage.getString("action"))) {
                Hospital hospital = new Hospital();
                sessionHandler.addHospital(hospital);
            }
            
            if ("nuevaCama".equals(jsonMessage.getString("action"))) {
                Camas cama = new Camas();
                int id = (int) jsonMessage.getInt("id");
                sessionHandler.addCama(cama, id);
            }
            
            if ("cambiarEstadoCama".equals(jsonMessage.getString("action"))) {
                int hospital = (int) jsonMessage.getInt("idHospital");
                int cama = (int) jsonMessage.getInt("idCama");
                sessionHandler.estadoCama(hospital, cama);
            }

            if ("remove".equals(jsonMessage.getString("action"))) {
                int id = (int) jsonMessage.getInt("id");
                sessionHandler.removeHospital(id);
            }
            
            if ("eliminarCama".equals(jsonMessage.getString("action"))) {
                int hospital = (int) jsonMessage.getInt("idHospital");
                int cama = (int) jsonMessage.getInt("idCama");
                sessionHandler.eliminarCama(hospital, cama);
            }

            /*if ("toggle".equals(jsonMessage.getString("action"))) {
                int id = (int) jsonMessage.getInt("id");
                sessionHandler.toggleDevice(id);
            }*/
        }
    }
}
