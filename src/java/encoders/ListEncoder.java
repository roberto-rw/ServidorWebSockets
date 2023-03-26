/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package encoders;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import java.util.List;
import javax.websocket.EncodeException;
import javax.websocket.Encoder;
import javax.websocket.EndpointConfig;

public class ListEncoder implements Encoder.Text<List<?>>{

    @Override
    public String encode(List<?> list) throws EncodeException {
        Gson gson = new GsonBuilder().create();
            return gson.toJson(list);
    }

    @Override
    public void init(EndpointConfig config) {}

    @Override
    public void destroy() {}
    
}
