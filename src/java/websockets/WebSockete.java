/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package websockets;

import com.google.gson.Gson;
import encoders.ListEncoder;
import encoders.MapEncoder;
import entidades.Contacto;
import entidades.Peticion;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.websocket.EncodeException;
import javax.websocket.OnClose;
import javax.websocket.OnError;
import javax.websocket.OnMessage;
import javax.websocket.OnOpen;
import javax.websocket.Session;
import javax.websocket.server.PathParam;
import javax.websocket.server.ServerEndpoint;

/**
 *
 * @author lv1013
 * Es un ejemplo de websocket donde cada mensaje que llega un cliente
 * es replicado a quienes estén conectados
 */
@ServerEndpoint(value = "/websocketendpoint/{usuario}", encoders = {MapEncoder.class, ListEncoder.class})
public class WebSockete {
	//para guardar la sesión de cada cliente y poder replicar el mensaje a cada uno
	//se hace una colección sincronizada para el manejo de la concurrencia
    private static Map<Session, String> mapaClientes = 
            Collections.synchronizedMap(new HashMap<Session, String>());    
    
    @OnOpen
    public void onOpen(Session sesion, @PathParam("usuario") String usuario){
        System.out.println("Open Connection ...");
        //Al conectarse un cliente se abre el websocket y se guarda su sesión.
        mapaClientes.put(sesion, usuario);
        
        //Envia la lista actualizada a todos los clientes conectados
        enviarListaClientes();
    }
     
    @OnClose
    public void onClose(Session sesion){
        System.out.println("Close Connection ...");
		//Al cerrarse la conexión por parte del cliente se elimina su sesión en el servidor
        mapaClientes.remove(sesion);
        
        //Envia la lista actualizada a todos los clientes conectados
        enviarListaClientes();
    }
     
    @OnMessage
    public void onMessage(String message, Session sesion){                
        Gson gson = new Gson();
        Peticion peticion = gson.fromJson(message, Peticion.class);
        this.messageHandler(peticion, sesion);
    }
 
    @OnError
    public void onError(Throwable e){
        e.printStackTrace();
    }    
    
    public void enviarListaClientes(){
        // Crear el objeto de mensaje que contiene el tipo y los datos
        Map<String, Object> mensaje = new HashMap<>();
        List<Map<String, String>> usuarios = new ArrayList<>();

        for (Map.Entry<Session, String> u : mapaClientes.entrySet()) {
            Map<String, String> user = new HashMap<>();
            user.put("id", u.getKey().getId());
            user.put("usuario", u.getValue());
            System.out.println(u.getValue());
            System.out.println(u.getKey().getId());
            
            usuarios.add(user);
        }
        mensaje.put("tipo", "listaUsuarios");
        mensaje.put("datos", usuarios);

        for (Map.Entry<Session, String> client : mapaClientes.entrySet()) {
            try {
                Session s = client.getKey();
                s.getBasicRemote().sendObject(mensaje);
            } catch (IOException ex) {
                Logger.getLogger(WebSockete.class.getName()).log(Level.SEVERE, null, ex);
            } catch (EncodeException ex) {
                Logger.getLogger(WebSockete.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }
    
    public void messageHandler(Peticion peticion, Session sesion){
        System.out.println(peticion);
        System.out.println(peticion.datos);
        
        if(peticion.tipo.equals("mensaje") || peticion.tipo.equals("conexion")){
            if(peticion.alcance.equals("TODOS")){
                enviarMensajeTodos((String)peticion.datos, sesion);
            }else{
                enviarMensajeIndividual((String)peticion.datos, peticion.alcance);
            }
        }else if(peticion.tipo.equals("contacto")){
            Gson gson = new Gson();
            Contacto contacto = gson.fromJson(peticion.datos, Contacto.class);
                
            if(peticion.alcance.equals("TODOS")){
                enviarEntidadTodos(contacto, sesion);
            }else{
                enviarEntidadIndividual(contacto, peticion.alcance);
            }
        }
    }

    public void enviarEntidadTodos(Contacto datos, Session sesion) {
        synchronized(mapaClientes){
            // Se itera sobre la sesiones (clientes) guardados para transmitir el mensaje
            for(Map.Entry<Session, String> client : mapaClientes.entrySet()){
                if (!client.getKey().equals(sesion)){
                    try {
                        Map<String, Object> mensaje = new HashMap<>();
                        mensaje.put("tipo", "contacto");
                        mensaje.put("datos", datos);
                        System.out.println(datos);
                        client.getKey().getBasicRemote().sendObject(mensaje);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    }

    public void enviarEntidadIndividual(Contacto datos, String alcance){
        for(Map.Entry<Session, String> client : mapaClientes.entrySet()){
            if (client.getKey().getId().equals(alcance)){
                try {
                    Map<String, Object> mensaje = new HashMap<>();
                    mensaje.put("tipo", "contacto");
                    mensaje.put("datos", datos);

                    client.getKey().getBasicRemote().sendObject(mensaje);
                }catch (Exception e){
                    e.printStackTrace();
                }
            }
        }
    }

    public void enviarMensajeTodos(String datos, Session sesion){
        //se hace un bloque sincronizado para manejar la concurrencia, tal como en los sockets e hilos
        synchronized(mapaClientes){
            // Se itera sobre la sesiones (clientes) guardados para transmitir el mensaje
            for(Map.Entry<Session, String> client : mapaClientes.entrySet()){
                if (!client.getKey().equals(sesion)){
                    try {
                        Map<String, Object> mensaje = new HashMap<>();
                        mensaje.put("tipo", "mensaje");
                        mensaje.put("datos", datos);
                        client.getKey().getBasicRemote().sendObject(mensaje);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    }
    
    public void enviarMensajeIndividual(String datos, String alcance){
        for(Map.Entry<Session, String> client : mapaClientes.entrySet()){
                if (client.getKey().getId().equals(alcance)){
                    try {
                        Map<String, Object> mensaje = new HashMap<>();
                        mensaje.put("tipo", "mensaje");
                        mensaje.put("datos", datos);

                        client.getKey().getBasicRemote().sendObject(mensaje);
                    }catch (Exception e){
                        e.printStackTrace();
                    }
                }
        }
    }
}


