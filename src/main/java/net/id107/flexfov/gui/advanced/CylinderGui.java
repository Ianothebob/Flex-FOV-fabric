package net.id107.flexfov.gui.advanced;

import net.id107.flexfov.ConfigManager;
import net.id107.flexfov.projection.Cylinder;
import net.id107.flexfov.projection.Projection;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.option.SimpleOption;
import net.minecraft.text.Text;

public class CylinderGui extends AdvancedGui {

	public CylinderGui(Screen parent) {
		super(parent);
		Projection.setProjection(new Cylinder());
	}
	
	@Override
	protected void init() {
		super.init();

		SimpleOption<Double> FOVX = new SimpleOption<>(
				"cylinderFovX", // Refer to advancedGui
				SimpleOption.emptyTooltip(),
				(gameOptions, value) -> Text.literal("Horizontal FOV: " + Math.round(value * 360)), // Map [0,1] to [0,360] for display
				SimpleOption.DoubleSliderCallbacks.INSTANCE, // Use the DoubleSliderCallbacks [0,1]
				Projection.getProjection().getFovX() / 360, // Default value normalized to [0,1]
				(value) -> {
					Projection.fov = value * 360; // Map [0,1] to [0,360] and save it
					ConfigManager.saveConfig();
				}
		);addDrawableChild(FOVX.createWidget(client.options, width / 2 - 180, height / 6 + 60, 360));

		SimpleOption<Double> FOVY = new SimpleOption<>(
				"cylinderFovY", // Identifier for localization
				SimpleOption.emptyTooltip(),
				(gameOptions, value) -> Text.literal("Vertical FOV: " + Math.round(value * 180)), // Map [0, 1] to [0, 180] for display
				SimpleOption.DoubleSliderCallbacks.INSTANCE, // Use the slider with [0, 1] range
				Projection.getProjection().getFovY() / 180, // Default value normalized to [0, 1]
				(value) -> {
					Cylinder.fovy = value * 180; // Map [0, 1] to [0, 180] and save
					ConfigManager.saveConfig();
				}
		);addDrawableChild(FOVY.createWidget(client.options, width / 2 - 180, height / 6 + 84, 180));
	}
}
