package net.id107.flexfov.gui.advanced;

import net.id107.flexfov.ConfigManager;
import net.id107.flexfov.projection.Panini;
import net.id107.flexfov.projection.Projection;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.option.SimpleOption;
import net.minecraft.text.Text;

import java.util.function.Consumer;

import static net.minecraft.client.option.GameOptions.getGenericValueText;

public class PaniniGui extends AdvancedGui {
	
	public PaniniGui(Screen parent) {
		super(parent);
		Projection.setProjection(new Panini());
	}
	
	@Override
	protected void init() {
		super.init();

		SimpleOption<Double> FOV = new SimpleOption<>(
				"paniniFov", // Identifier for localization
				SimpleOption.emptyTooltip(),
				(gameOptions, value) -> Text.literal("FOV: " + (int) (value * 360)), // Map [0,1] to [0,360] for display
				SimpleOption.DoubleSliderCallbacks.INSTANCE, // Use DoubleSliderCallbacks with range [0,1]
				Projection.getProjection().getFovX() / 360, // Default value normalized to [0,1]
				(value) -> {
					Projection.fov = value * 360; // Map [0,1] to [0,360] and save
					ConfigManager.saveConfig();
				}
		);
		/*SimpleOption<Integer> FOP = new SimpleOption("paniniFov",SimpleOption.emptyTooltip(),(optionText, value) -> {
			Text var10000;
			switch (value) {
				case 0 -> var10000 = Text.literal("0");
				case 360 -> var10000 = Text.literal("360");
				default -> var10000 = getGenericValueText(optionText, value);
			}

			return var10000;
		}, new SimpleOption.ValidatingIntSliderCallbacks(0,360),180, )*/
		addDrawableChild(FOV.createWidget(client.options, width / 2 - 180, height / 6 + 60, 360));
	}
}
