package net.id107.flexfov.gui.advanced;

import net.id107.flexfov.ConfigManager;
import net.id107.flexfov.projection.Hammer;
import net.id107.flexfov.projection.Projection;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.text.Text;

import java.io.IOException;

public class HammerGui extends AdvancedGui {

	public HammerGui(Screen parent) {
		super(parent);
        try {
            Projection.setProjection(new Hammer());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
	
	@Override
	protected void init() {
		super.init();

		addDrawableChild(ButtonWidget.builder(
						Text.of("Background Color: " + (Projection.skyBackground ? "Sky" : "Black")),
						(buttonWidget) -> {
							Projection.skyBackground = !Projection.skyBackground;
							buttonWidget.setMessage(Text.literal("Background Color: " + (Projection.skyBackground ? "Sky" : "Black")));
							ConfigManager.saveConfig();
						})
				.position(width / 2 - 155, height / 6 + 84)
				.size(150, 20)
				.narrationSupplier((buttonWidget) -> Text.literal("Background Color button. Can be Sky or Black"))
				.build());
	}
}
