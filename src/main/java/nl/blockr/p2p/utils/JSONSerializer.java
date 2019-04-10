package nl.blockr.p2p.utils;

import com.google.gson.Gson;
import org.springframework.stereotype.Service;

@Service
public class JSONSerializer {
    private Gson gson;

    public JSONSerializer() {
        gson = new Gson();
    }

    public <T> T fromJSON(String data, Class<T> type) {
        return gson.fromJson(data, type);
    }

    public String toJSON(Object object) {
        return gson.toJson(object, object.getClass());
    }
}
