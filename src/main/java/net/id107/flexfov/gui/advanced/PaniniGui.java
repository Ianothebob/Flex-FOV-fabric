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

		DoubleOption FOV = new DoubleOption("paniniFov", 0, 360, 1,
				(gameOptions) -> {return Projection.getProjection().getFovX();},
				(gameOptions, number) -> {Projection.fov = number; ConfigManager.saveConfig();},
				(gameOptions, doubleOption) -> {return new LiteralText("FOV: " + (int)Projection.getProjection().getFovX());});
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
