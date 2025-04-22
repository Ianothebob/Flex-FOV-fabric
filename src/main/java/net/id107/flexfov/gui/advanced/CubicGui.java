package net.id107.flexfov.gui.advanced;

import net.id107.flexfov.ConfigManager;
import net.id107.flexfov.projection.Cubic;
import net.id107.flexfov.projection.Projection;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.text.Text;

public class CubicGui extends AdvancedGui {

	public CubicGui(Screen parent) {
		super(parent);
		Projection.setProjection(new Cubic());
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
