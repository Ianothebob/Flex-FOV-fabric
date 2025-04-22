package net.id107.flexfov.gui;

import net.id107.flexfov.ConfigManager;
import net.id107.flexfov.projection.Flex;
import net.id107.flexfov.projection.Projection;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.option.SimpleOption;
import net.minecraft.text.Text;

public class FlexGui extends SettingsGui {
	
	public FlexGui(Screen parent) {
		super(parent);
		Projection.setProjection(new Flex());
	}
	
	@Override
	protected void init() {
		super.init();
		
		SimpleOption<Double> FOV = new SimpleOption<Double>("flexFov", 0, 360, 1,
				(gameOptions) -> {return Projection.getProjection().getFovX();},
				(gameOptions, number) -> {Projection.fov = number; ConfigManager.saveConfig();},
				(gameOptions, SimpleOption<Double>) -> {return Text.literal("FOV: " + (int)Projection.getProjection().getFovX());});
		addDrawableChild(FOV.createWidget(client.options, width / 2 - 180, height / 6 + 36, 360));
	}
}
