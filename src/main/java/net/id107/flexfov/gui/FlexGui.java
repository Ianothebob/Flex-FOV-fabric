package net.id107.flexfov.gui;

import net.id107.flexfov.ConfigManager;
import net.id107.flexfov.projection.Flex;
import net.id107.flexfov.projection.Projection;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.option.SimpleOption;
import net.minecraft.text.Text;

import java.io.IOException;

public class FlexGui extends SettingsGui {
	
	public FlexGui(Screen parent) {
		super(parent);
        try {
            Projection.setProjection(new Flex());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
	
	@Override
	protected void init() {
		super.init();

		SimpleOption<Double> FOV = new SimpleOption<>(
				"flexFov", // Identifier for localization
				SimpleOption.emptyTooltip(),
				(gameOptions, value) -> Text.literal("FOV: " + (int) (value * 360)), // Map [0,1] to [0,360] for display
				SimpleOption.DoubleSliderCallbacks.INSTANCE, // Use DoubleSliderCallbacks with range [0,1]
				Projection.getProjection().getFovX() / 360, // Default value normalized to [0,1]
				(value) -> {
					Projection.fov = value * 360; // Map [0,1] to [0,360] when saving
					ConfigManager.saveConfig();
				}
		);
		addDrawableChild(FOV.createWidget(client.options, width / 2 - 180, height / 6 + 36, 360));
	}
}
