import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by karis on 10/8/2017.
 *
 * This class reads from the environment variables and creates configurations variables for them.
 * The configuration variables are fetched via the public methods which will return null if no variable
 * exists for the requested key.
 */
public class Configs {
    private Map<String, Object> configMap;

    public Configs() {
        this.configMap = new HashMap<String, Object>();
        this.configMap.put("twitterUrl", System.getenv("twitterUrl"));
        this.configMap.put("twitterHost", System.getenv("twitterHost"));
    }

    public String getString(String key) {
        Object value = this.configMap.get(key);
        if(value != null && value instanceof String) return (String) value;
        return null;
    }

    public Boolean getBoolean(String key) {
        Object value = this.configMap.get(key);
        if(value != null && value instanceof Boolean) return (Boolean) value;
        return null;
    }

    public Integer getInteger(String key) {
        Object value = this.configMap.get(key);
        if(value != null && value instanceof Integer) return (Integer) value;
        return null;
    }

    public Double getDouble(String key) {
        Object value = this.configMap.get(key);
        if(value != null && value instanceof Double) return (Double) value;
        return null;
    }

    // This method uses reflection to make sure the Object from the map is of the right type.
    // Read more about it here: https://docs.oracle.com/javase/tutorial/java/generics/index.html
    public <T> List<T> getList(String key, Class<T> innerType) {
        Object value = this.configMap.get(key);
        if(value != null && value instanceof List) {
            List retVal = (List) value;
            if(((List)value).getClass().getName().equals(innerType.getName()))
                return (List<T>) retVal;
        }
        return null;
    }
}
