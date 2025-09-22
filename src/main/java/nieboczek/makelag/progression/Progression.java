package nieboczek.makelag.progression;

import java.util.List;

public record Progression(String id, List<SerializedKeyFrame> timeline) {
    public record SerializedKeyFrame(
            String module, String key, int startTick, int endTick, double startValue, double endValue
    ) {}
}
