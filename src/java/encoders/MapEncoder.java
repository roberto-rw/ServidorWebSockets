/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package encoders;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import java.util.Map;
import javax.websocket.EncodeException;
import javax.websocket.Encoder;
import javax.websocket.EndpointConfig;


public class MapEncoder implements Encoder.Text<Map<?, ?>>{

        @Override
        public String encode(Map<?, ?> map) throws EncodeException {
            Gson gson = new GsonBuilder().create();
            return gson.toJson(map);
        }

        @Override
        public void init(EndpointConfig endpointConfig) {}

        @Override
        public void destroy() {}

}
