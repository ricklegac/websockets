package org.example.websocket;

import java.io.IOException;
import javax.enterprise.context.ApplicationScoped;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;
import javax.json.JsonObject;
import javax.json.spi.JsonProvider;
import javax.websocket.Session;
import org.example.model.Camas;
import org.example.model.Hospital;
//Para la hora y fecha del log
import java.time.format.DateTimeFormatter;  
import java.time.LocalDateTime;    

@ApplicationScoped
public class DeviceSessionHandler {
    DateTimeFormatter dtf = DateTimeFormatter.ofPattern("dd/MM/yyyy-HH:mm:ss");  
    LocalDateTime now = LocalDateTime.now();  
    // Para obtener la hora y fecha
    // dtf.format(now)
    private int hospitalId = 0;
    private final Set<Session> sessions = new HashSet<>();
    private final Set<Hospital> hospitales = new HashSet<>();
    //Debe contener: fecha-hora, hospital, cama, tipo_operacion.
    private String log = new String("Fecha-hora, Hospital, Cama, Tipo de operacion:\n");
    public void addSession(Session session) {
        sessions.add(session);
        for (Hospital hospital : hospitales) {
            JsonObject addMessage = createAddMessage(hospital);
            sendToSession(session, addMessage);
        }

    }

    public void removeSession(Session session) {
        sessions.remove(session);
    }

    // Metodo para crear un nuevo hospital
    public void addHospital(Hospital hospital) {
        hospital.setId(hospitalId);
        hospitales.add(hospital);
        hospitalId++;
        JsonObject addMessage = createAddMessage(hospital);
        sendToAllConnectedSessions(addMessage);
        System.out.println(dtf.format(now) + ", Hospital "+ hospital.getId() + ", -, Creacion de Hospital");
        log = dtf.format(now) + ", Hospital "+ hospital.getId() + ", -, Creano hospial\n";
    }
      
    //Metodo para crear nueva cama a un hospital
    public void addCama(Camas cama, int id) {
        Hospital hospital = getHospitalById(id);
        int idCama = hospital.getCantCamas();
        cama.setId(idCama);
        hospital.setCantCamas(idCama + 1);
        hospital.getCamas().add(cama);
        JsonProvider provider = JsonProvider.provider();
        JsonObject nuevaCamaMessage = provider.createObjectBuilder()
                .add("action", "nuevaCama")
                .add("id", id)
                .add("camas", hospital.getIdCamas(id))
                .build();
        sendToAllConnectedSessions(nuevaCamaMessage);
        System.out.println(dtf.format(now) + ", Hospital "+ hospital.getId() + ",cama "+idCama+",Crear Cama UTI");
        log = dtf.format(now) + ", Hospital "+ hospital.getId() + ",cama "+idCama+",Crear Cama UTI\n";
    }
    
    //Metodo para cambiar de estado una cama
    public void estadoCama(int idHospital , int idCama) {
        Hospital hospital = getHospitalById(idHospital);
        Camas cama = hospital.getCamasById(idCama);
        if(cama != null){
            if(cama.getEstado().equals("DESOCUPADO")){
                cama.setEstado("OCUPADO");
            }else{
                cama.setEstado("DESOCUPADO");
            }
        }
        JsonProvider provider = JsonProvider.provider();
        JsonObject nuevaCamaMessage = provider.createObjectBuilder()
                .add("action", "cambiarEstadoCama")
                .add("id", idHospital)
                .add("camas", hospital.getIdCamas(idHospital))
                .build();
        sendToAllConnectedSessions(nuevaCamaMessage);
        
        System.out.println(dtf.format(now) + ", Hospital "+ hospital.getId() + ",cama "+idCama+",estado: "+cama.getEstado());
        log = dtf.format(now) + ", Hospital "+ hospital.getId() + ",cama "+idCama+",estado: "+cama.getEstado()+"\n";
    }
    
    //Metodo para eliminar una cama
    public void eliminarCama(int idHospital , int idCama) {
        Hospital hospital = getHospitalById(idHospital);
        Camas cama = hospital.getCamasById(idCama);
        if(cama != null){
            hospital.eliminarCamas(cama);
        }
        JsonProvider provider = JsonProvider.provider();
        JsonObject eliminarCamaMessage = provider.createObjectBuilder()
                .add("action", "eliminarCama")
                .add("id", idHospital)
                .add("camas", hospital.getIdCamas(idHospital))
                .build();
        sendToAllConnectedSessions(eliminarCamaMessage);
        System.out.println(dtf.format(now) + ", Hospital "+ hospital.getId() + ",cama "+idCama+",Eliminar Cama UTI ");
        log = dtf.format(now) + ", Hospital "+ hospital.getId() + ",cama "+idCama+",Eliminar Cama UTI\n";
    }

    //Metodo para eliminar un hospital
    public void removeHospital(int id) {
        Hospital hospital = getHospitalById(id);
        if (hospital != null) {
            hospitales.remove(hospital);
            JsonProvider provider = JsonProvider.provider();
            JsonObject removeMessage = provider.createObjectBuilder()
                    .add("action", "remove")
                    .add("id", id)
                    .build();
            sendToAllConnectedSessions(removeMessage);
            System.out.println(dtf.format(now) + ", Hospital "+ id + ", -, Eliminacion de Hospital");
            log = dtf.format(now) + ", Hospital "+ id + ", -, Eliminacion de Hospital\n";
        }
    }

    //Metodo para obtener un hospital a partir del id
    private Hospital getHospitalById(int id) {
        for (Hospital hospital : hospitales) {
            if (hospital.getId() == id) {
                return hospital;
            }
        }
        return null;
    }

    //Metodo para enviar un mensaje con action = add
    private JsonObject createAddMessage(Hospital hospital) {
        JsonProvider provider = JsonProvider.provider();
        JsonObject addMessage = provider.createObjectBuilder()
                .add("action", "add")
                .add("id", hospital.getId())
                .add("camas", hospital.getIdCamas(hospital.getId()))
                .build();
        return addMessage;
    }

    //Metodo para enviar mensaje a todo los dispositivos conectados
    private void sendToAllConnectedSessions(JsonObject message) {
        for (Session session : sessions) {
            sendToSession(session, message);
        }
    }

    //Metodo para enviar mensaje a un dispositivo conectado
    private void sendToSession(Session session, JsonObject message) {
        try {
            session.getBasicRemote().sendText(message.toString());
        } catch (IOException ex) {
            sessions.remove(session);
            Logger.getLogger(DeviceSessionHandler.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

}
