package net.id107.flexfov.gui.advanced;

import net.id107.flexfov.ConfigManager;
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
		
		ButtonWidget button = new ButtonWidget(width / 2 - 180, height / 6 + 12, 100, 20,
				Text.literal("Cubic"), (buttonWidget) -> {
					currentGui = 0;
					client.setScreen(new CubicGui(parentScreen));
		});
		if (this instanceof CubicGui) {
			button.active = false;
		}
		addDrawableChild(button);
		
		button = new ButtonWidget(width / 2 - 50, height / 6 + 12, 100, 20,
				Text.literal("Hammer"), (buttonWidget) -> {
					currentGui = 1;
					client.setScreen(new HammerGui(parentScreen));
				});
		if (this instanceof HammerGui) {
			button.active = false;
		}
		addDrawableChild(button);
		
		button = new ButtonWidget(width / 2 + 80, height / 6 + 12, 100, 20,
				Text.literal("Panini"), (buttonWidget) -> {
					currentGui = 2;
					client.setScreen(new PaniniGui(parentScreen));
				});
		if (this instanceof PaniniGui) {
			button.active = false;
		}
		addDrawableChild(button);
		
		button = new ButtonWidget(width / 2 - 180, height / 6 + 36, 100, 20,
				Text.literal("Cylinder"), (buttonWidget) -> {
					currentGui = 3;
					client.setScreen(new CylinderGui(parentScreen));
				});
		if (this instanceof CylinderGui) {
			button.active = false;
		}
		addDrawableChild(button);
		
		button = new ButtonWidget(width / 2 - 50, height / 6 + 36, 100, 20,
				Text.literal("Fisheye"), (buttonWidget) -> {
					currentGui = 4;
					client.setScreen(new FisheyeGui(parentScreen));
				});
		if (this instanceof FisheyeGui) {
			button.active = false;
		}
		addDrawableChild(button);
		
		button = new ButtonWidget(width / 2 + 80, height / 6 + 36, 100, 20,
				Text.literal("Equirectangular"), (buttonWidget) -> {
					currentGui = 5;
					client.setScreen(new EquirectangularGui(parentScreen));
				});
		if (this instanceof EquirectangularGui) {
			button.active = false;
		}
		addDrawableChild(button);
		
		if (!(this instanceof CubicGui)) {
			SimpleOption<double> zoom = new SimpleOption<double>("zoom", -2, 2, 0.05f,
					(gameOptions) -> {return (double) Projection.zoom;},
					(gameOptions, number) -> {Projection.zoom = (float)(double)number; ConfigManager.saveConfig();},
					(gameOptions, SimpleOption<double>) -> {return Text.literal(String.format("Zoom: %.2f", Projection.zoom));});
			addDrawableChild(zoom.createWidget(client.options, width / 2 + 5, height / 6 + 84, 150));
		}
		
		addDrawableChild(new ButtonWidget(width / 2 + 5, height / 6 + 108, 150, 20,
				Text.literal("Resize Gui: " + (Projection.resizeGui ? "ON" : "OFF")), (buttonWidget) -> {
					Projection.resizeGui = !Projection.resizeGui;
					buttonWidget.setMessage(Text.literal("Resize Gui: " + (Projection.resizeGui ? "ON" : "OFF")));
				}));
	}
}
