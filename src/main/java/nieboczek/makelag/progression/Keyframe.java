package nieboczek.makelag.progression;

import nieboczek.makelag.module.backend.Key;
import nieboczek.makelag.module.backend.Module;

public record Keyframe<T extends Number>(
        Module module, Key<T> key, int startTick, int endTick, double startValue, double endValue
) {
    public boolean isActive(int tick) {
        return tick >= startTick && tick <= endTick;
    }

    public double getProgress(int tick) {
        if (tick <= startTick) return 0.0;
        if (tick >= endTick) return 1.0;
        return (double) (tick - startTick) / (double) (endTick - startTick);
    }

    @SuppressWarnings("unchecked")
    public T getValue(int tick) {
        double progress = getProgress(tick);
        double value = startValue + (endValue - startValue) * progress;

        if (key.initialValue() instanceof Integer) {
            return (T) Integer.valueOf((int) Math.round(value));
        } else if (key.initialValue() instanceof Float) {
            return (T) Float.valueOf((float) value);
        } else if (key.initialValue() instanceof Double) {
            return (T) Double.valueOf(value);
        }
        throw new IllegalStateException("Unsupported keyframe type: " + key.initialValue().getClass());
    }
}
