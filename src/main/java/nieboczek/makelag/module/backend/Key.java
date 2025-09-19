package nieboczek.makelag.module.backend;

import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.arguments.BoolArgumentType;
import com.mojang.brigadier.arguments.FloatArgumentType;
import com.mojang.brigadier.arguments.IntegerArgumentType;

public record Key<T>(ArgumentType<T> argumentType, T initialValue, Class<T> clazz, String id) {
    public static <T> Key<T> of() {
        return new Key<>(null, null, null, null);
    }

    public static <T> Key<T> of(T initialValue) {
        return new Key<>(null, initialValue, null, null);
    }

    public static Key<Float> zeroToOneFloat(String id) {
        return new Key<>(FloatArgumentType.floatArg(0, 1), 0f, Float.class, id);
    }

    public static Key<Integer> notNegativeInt(String id) {
        return new Key<>(IntegerArgumentType.integer(0), 0, Integer.class, id);
    }

    public static Key<Float> notNegativeFloat(String id) {
        return new Key<>(FloatArgumentType.floatArg(0), 0f, Float.class, id);
    }

    public static Key<Boolean> bool(String id) {
        return new Key<>(BoolArgumentType.bool(), false, Boolean.class, id);
    }

    public static Key<Boolean> bool() {
        return of(false);
    }
}
