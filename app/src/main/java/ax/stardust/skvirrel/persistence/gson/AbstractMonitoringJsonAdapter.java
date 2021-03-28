package ax.stardust.skvirrel.persistence.gson;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;

import java.lang.reflect.Type;

import ax.stardust.skvirrel.monitoring.AbstractMonitoring;
import timber.log.Timber;

/**
 * Custom Json adapter needed for proper serialization and deserialization.
 */
public class AbstractMonitoringJsonAdapter implements
        JsonSerializer<AbstractMonitoring>, JsonDeserializer<AbstractMonitoring> {

    private static final String MONITORING_CLASS = "monitoringClass";
    private static final String DATA = "data";

    @Override
    public JsonElement serialize(AbstractMonitoring abstractMonitoring, Type type, JsonSerializationContext context) {
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty(MONITORING_CLASS, abstractMonitoring.getClass().getName());
        jsonObject.add(DATA, context.serialize(abstractMonitoring));
        return jsonObject;
    }

    @Override
    public AbstractMonitoring deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) {
        final JsonObject member = (JsonObject) json;
        JsonElement type = member.get(MONITORING_CLASS);
        JsonElement data = member.get(DATA);

        if (type == null || data == null) {
            String errorMessage = type == null ? "Unable to find monitoring type property in json object"
                    : "Unable to find monitoring data in json object";
            JsonParseException exception = new JsonParseException(errorMessage);
            Timber.e(exception, "Unable to deserialize");
            throw exception;
        }

        try {
            return context.deserialize(data, Class.forName(type.getAsString()));
        } catch (ClassNotFoundException e) {
            JsonParseException exception = new JsonParseException(e);
            Timber.e(exception, "Unable to deserialize");
            throw exception;
        }
    }
}
