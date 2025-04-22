package net.id107.flexfov.gui.advanced;

import net.id107.flexfov.ConfigManager;
import net.id107.flexfov.projection.Hammer;
import net.id107.flexfov.projection.Projection;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.text.Text;

public class HammerGui extends AdvancedGui {

	public HammerGui(Screen parent) {
		super(parent);
		Projection.setProjection(new Hammer());
	}
	
	@Override
	protected void init() {
		super.init();
		
		addDrawableChild(new ButtonWidget(width / 2 - 155, height / 6 + 84, 150, 20,
				Text.literal("Background Color: " + (Projection.skyBackground ? "Sky" : "Black")), (buttonWidget) -> {
					Projection.skyBackground = !Projection.skyBackground;
					buttonWidget.setMessage(Text.literal("Background Color: " + (Projection.skyBackground ? "Sky" : "Black")));
					ConfigManager.saveConfig();
				}));
	}
}
