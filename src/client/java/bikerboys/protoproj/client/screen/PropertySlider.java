package bikerboys.protoproj.client.screen;

import net.minecraft.client.gui.components.*;
import net.minecraft.network.chat.*;

import java.util.function.*;

public class PropertySlider extends AbstractSliderButton {
    private final float minValue;
    private final float maxValue;
    private final Consumer<Float> onChange;
    private final Component label;

    public PropertySlider(int x, int y, int width, int height, Component label,
                          float minValue, float maxValue, float initialValue,
                          Consumer<Float> onChange) {
        super(x, y, width, height, Component.empty(),
                normalizeValue(initialValue, minValue, maxValue));

        this.minValue = minValue;
        this.maxValue = maxValue;
        this.onChange = onChange;
        this.label = label;

        updateMessage();
    }

    @Override
    protected void updateMessage() {
        float actual = getActualValue();
        this.setMessage(Component.literal(label.getString() + String.format("%.2f", actual)));
    }

    @Override
    protected void applyValue() {
        // DO NOT modify this.value here
        float actual = getActualValue();
        onChange.accept(actual);
    }

    public void setValue(float newValue) {
        this.value = normalizeValue(newValue, minValue, maxValue);
        updateMessage();
    }

    private float getActualValue() {
        return denormalizeValue((float) this.value, minValue, maxValue);
    }

    private static float normalizeValue(float value, float min, float max) {
        return (value - min) / (max - min);
    }

    private static float denormalizeValue(float normalized, float min, float max) {
        return min + (normalized * (max - min));
    }
}
