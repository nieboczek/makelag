package nieboczek.makelag.progression;

import java.util.List;

public record SerializedProgression(List<SerializedKeyframe> timeline) {
    public record SerializedKeyframe(
            String module, String key, int startTick, int endTick, double startValue, double endValue
    ) {}
}
