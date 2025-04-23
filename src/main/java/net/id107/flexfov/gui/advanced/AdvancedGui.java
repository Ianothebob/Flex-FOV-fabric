package net.id107.flexfov.gui.advanced;

import net.id107.flexfov.ConfigManager;
import net.id107.flexfov.gui.RectilinearGui;
import net.id107.flexfov.gui.SettingsGui;
import net.id107.flexfov.projection.Projection;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.option.SimpleOption;
import net.minecraft.text.Text;

public class AdvancedGui extends SettingsGui {

	public static int currentGui = 5;
	
	public AdvancedGui(Screen parent) {
		super(parent);
	}
	
	public static AdvancedGui getGui(Screen parent) {
		switch(currentGui) {
		case 0:
		default:
			return new CubicGui(parent);
		case 1:
			return new HammerGui(parent);
		case 2:
			return new PaniniGui(parent);
		case 3:
			return new CylinderGui(parent);
		case 4:
			return new FisheyeGui(parent);
		case 5:
			return new EquirectangularGui(parent);
		}
	}
	
	@Override
	protected void init() {
		super.init();
		
		ButtonWidget button = ButtonWidget.builder(Text.of("Cubic"), (buttonWidget) -> {
					currentGui = 0;
					client.setScreen(new CubicGui(parentScreen));
				}).dimensions(width / 2 - 180, height / 6 + 12, 100, 20)
				.narrationSupplier((buttonWidget) -> Text.literal("Cubic button"))
				.build();
		if (this instanceof CubicGui) {
			button.active = false;
		}
		addDrawableChild(button);
		
		button = ButtonWidget.builder(Text.of("Hammer"), (buttonWidget) -> {
					currentGui = 1;
					client.setScreen(new HammerGui(parentScreen));
				}).dimensions(width / 2 - 50, height / 6 + 12, 100, 20)
				.narrationSupplier((buttonWidget) -> Text.literal("Hammer button"))
				.build();
		if (this instanceof HammerGui) {
			button.active = false;
		}
		addDrawableChild(button);
		
		button = ButtonWidget.builder(Text.of("Panini"), (buttonWidget) -> {
					currentGui = 2;
					client.setScreen(new PaniniGui(parentScreen));
				}).dimensions(width / 2 + 80, height / 6 + 12, 100, 20)
				.narrationSupplier((buttonWidget) -> Text.literal("Panini button"))
				.build();
		if (this instanceof PaniniGui) {
			button.active = false;
		}
		addDrawableChild(button);
		
		button = ButtonWidget.builder(Text.literal("Cylinder"), (buttonWidget) -> {
					currentGui = 3;
					client.setScreen(new CylinderGui(parentScreen));
				}).dimensions(width / 2 - 180, height / 6 + 36, 100, 20)
				.build();
		if (this instanceof CylinderGui) {
			button.active = false;
		}
		addDrawableChild(button);
		
		button = ButtonWidget.builder(Text.literal("Fisheye"), (buttonWidget) -> {
					currentGui = 4;
					client.setScreen(new FisheyeGui(parentScreen));
				}).dimensions(width / 2 - 50, height / 6 + 36, 100, 20)
				.build();
		if (this instanceof FisheyeGui) {
			button.active = false;
		}
		addDrawableChild(button);
		
		button = ButtonWidget.builder(Text.literal("Equirectangular"), (buttonWidget) -> {
					currentGui = 5;
					client.setScreen(new EquirectangularGui(parentScreen));
				}).dimensions(width / 2 + 80, height / 6 + 36, 100, 20)
				.build();
		if (this instanceof EquirectangularGui) {
			button.active = false;
		}
		addDrawableChild(button);
		
		if (!(this instanceof CubicGui)) {
			SimpleOption<Double> zoom = new SimpleOption<Double>(
                    "zoom", // Identifier for localization
                    SimpleOption.emptyTooltip(), // Slider callbacks for handling double values
					(optionText, value) -> Text.literal(String.format("Zoom: %.2f", value * 4 - 2)), // Tooltip or text update
                    SimpleOption.DoubleSliderCallbacks.INSTANCE,// Use DoubleSliderCallbacks because i dont wanna screw with anything else and this  is already there
                    0.5, // Default value// Default value mapped to 0 (in [-2,2]) => 0.5 in [0,1]
                    (value) -> {
						Projection.zoom = (float)((value * 4) - 2); // Map slider's range [0,1] -> [-2,2]
                        ConfigManager.saveConfig();
                    } // Save handler
            );
			addDrawableChild(zoom.createWidget(client.options, width / 2 + 5, height / 6 + 84, 150));
		}
		
		addDrawableChild(ButtonWidget.builder(
				Text.of("Resize Gui: " + (Projection.resizeGui ? "ON" : "OFF")),
				(buttonWidget) -> {
					Projection.resizeGui = !Projection.resizeGui;
					buttonWidget.setMessage(Text.literal("Resize Gui: " + (Projection.resizeGui ? "ON" : "OFF")));
				})
				.dimensions(width / 2 + 5, height / 6 + 108, 150, 20)
				.narrationSupplier((buttonWidget) -> Text.literal("Resize Gui button"))
				.build());
	}
}
