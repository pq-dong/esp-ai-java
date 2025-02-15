package ai.esp.com.data;

import java.util.HashMap;
import java.util.Map;


public class CurrentRequest {
    
    public static final String KEY_DEVICE = "device";
    
    private static final ThreadLocal<Map<String, Object>> store = new ThreadLocal<>();
    
    public static void init() {
        store.set(new HashMap<>());
    }

    public static DeviceSession getDevice() {
        return (DeviceSession) store.get().get(KEY_DEVICE);
    }

    public static void setDevice(DeviceSession deviceInfo) {
        store.get().put(KEY_DEVICE, deviceInfo);
    }
    
    public static void remove(String key) {
        Map<String, Object> map = store.get();
        if (map != null) {
            map.remove(key);
        }
    }

}
