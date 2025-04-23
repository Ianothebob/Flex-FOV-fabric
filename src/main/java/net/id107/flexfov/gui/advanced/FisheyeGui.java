package net.id107.flexfov.gui.advanced;

import net.id107.flexfov.ConfigManager;
import net.id107.flexfov.projection.Fisheye;
import net.id107.flexfov.projection.Projection;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.option.SimpleOption;
import net.minecraft.text.Text;

import java.io.IOException;

public class FisheyeGui extends AdvancedGui {
	
	public FisheyeGui(Screen parent) {
		super(parent);
        try {
            Projection.setProjection(new Fisheye());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
	
	@Override
	protected void init() {
		super.init();
		
		ButtonWidget button = ButtonWidget.builder(
					Text.literal("Orthographic"),
					(buttonWidget) -> {
						Fisheye.fisheyeType = 0;
						client.setScreen(new FisheyeGui(parentScreen));
					})
				.position(width / 2 - 190, height / 6 + 60)
				.size(76, 20)
				.narrationSupplier((buttonWidget) -> Text.literal("Orthographic Button"))
				.build();
		if (Fisheye.fisheyeType == 0) {
			button.active = false;
		}
		addDrawableChild(button);
		
		button = ButtonWidget.builder(
					Text.literal("Thoby"),
					(buttonWidget) -> {
						Fisheye.fisheyeType = 1;
						client.setScreen(new FisheyeGui(parentScreen));
					})
				.position(width / 2 - 114, height / 6 + 60)
				.size(76, 20)
				.narrationSupplier((buttonWidget) -> Text.literal("Thoby Button"))
				.build();
		if (Fisheye.fisheyeType == 1) {
			button.active = false;
		}
		addDrawableChild(button);
		
		button = ButtonWidget.builder(
					Text.literal("Equisolid"),
					(buttonWidget) -> {
						Fisheye.fisheyeType = 2;
						client.setScreen(new FisheyeGui(parentScreen));
					})
				.position(width / 2 - 38, height / 6 + 60)
				.size(76, 20)
				.narrationSupplier((buttonWidget) -> Text.literal("Equisolid Button"))
				.build();
		if (Fisheye.fisheyeType == 2) {
			button.active = false;
		}
		addDrawableChild(button);
		
		button = ButtonWidget.builder(
					Text.literal("Equidistant"),
					(buttonWidget) -> {
						Fisheye.fisheyeType = 3;
						client.setScreen(new FisheyeGui(parentScreen));
					})
				.position(width / 2 + 38, height / 6 + 60)
				.size(76, 20)
				.narrationSupplier((buttonWidget) -> Text.literal("Equidistant Button"))
				.build();
		if (Fisheye.fisheyeType == 3) {
			button.active = false;
		}
		addDrawableChild(button);
		
		button = ButtonWidget.builder(
					Text.literal("Stereographic"),
					(buttonWidget) -> {
						Fisheye.fisheyeType = 4;
						client.setScreen(new FisheyeGui(parentScreen));
					})
				.position(width / 2 + 114, height / 6 + 60)
				.size(76, 20)
				.narrationSupplier((buttonWidget) -> Text.literal("Stereographic Button"))
				.build();
		if (Fisheye.fisheyeType == 4) {
			button.active = false;
		}
		addDrawableChild(button);
		
		int fovSliderLimit = 360;
		if (Fisheye.fisheyeType == 1) fovSliderLimit = (int)Math.ceil(fovSliderLimit*0.713); //Thoby 256.68 degrees, slider goes up to 257
		if (Fisheye.fisheyeType == 0) fovSliderLimit = 180; //Orthographic
		final int finalSliderLimit = fovSliderLimit;
		SimpleOption<Double> FOV = new SimpleOption<>(
				"fisheyeFov", // Identifier for localization
				SimpleOption.emptyTooltip(),
				(gameOptions, value) -> Text.literal("FOV: " + (int) (value * finalSliderLimit)), // Map [0,1] to [0, fovSliderLimit] for display
				SimpleOption.DoubleSliderCallbacks.INSTANCE, // Use DoubleSliderCallbacks with range [0,1]
				Math.min(finalSliderLimit, Projection.getProjection().getFovX()) / fovSliderLimit, // Default normalized to [0,1]
				(value) -> {
					Projection.fov = value * finalSliderLimit; // Map [0,1] to [0, fovSliderLimit] and save
					ConfigManager.saveConfig();
				}
		);addDrawableChild(FOV.createWidget(client.options, width / 2 - 180, height / 6 + 132, fovSliderLimit));
		
		addDrawableChild(ButtonWidget.builder(
					Text.literal("Background Color: " + (Projection.skyBackground ? "Sky" : "Black")),
					(buttonWidget) -> {
						Projection.skyBackground = !Projection.skyBackground;
						buttonWidget.setMessage(Text.literal("Background Color: " + (Projection.skyBackground ? "Sky" : "Black")));
						ConfigManager.saveConfig();
					})
				.position(width / 2 - 155, height / 6 + 84)
				.size(150, 20)
				.narrationSupplier((buttonWidget) -> Text.literal("Background Color button. Can be Sky or Black"))
				.build());
        addDrawableChild(ButtonWidget.builder(
                    Text.literal("Full Frame: " + (Fisheye.fullFrame ? "ON" : "OFF")),
                    (buttonWidget) -> {
                        Fisheye.fullFrame = !Fisheye.fullFrame;
                        buttonWidget.setMessage(Text.literal("Full Frame: " + (Fisheye.fullFrame ? "ON" : "OFF")));
                        ConfigManager.saveConfig();
                    })
                .position(width / 2 - 155, height / 6 + 108)
                .size(150, 20)
                .narrationSupplier((buttonWidget) -> Text.literal("Full Frame button. Can be ON or OFF"))
                .build());
	}
}
