import com.google.gson.*
import java.lang.reflect.ParameterizedType
import java.lang.reflect.Type
import java.util.*

class OptionalTypeAdapter<T> : JsonSerializer<Optional<T>>, JsonDeserializer<Optional<T>> {
    override fun serialize(src: Optional<T>, typeOfSrc: Type, context: JsonSerializationContext): JsonElement {
        return src.map(context::serialize).orElse(JsonNull.INSTANCE)
    }

    override fun deserialize(json: JsonElement, typeOfT: Type, context: JsonDeserializationContext): Optional<T> {
        return if (json.isJsonNull) Optional.empty() else Optional.of(
            context.deserialize(
                json,
                (typeOfT as ParameterizedType).actualTypeArguments[0]
            )
        )
    }
}