package net.id107.flexfov.gui;

import net.id107.flexfov.ConfigManager;
import net.id107.flexfov.gui.advanced.AdvancedGui;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.text.Text;

public abstract class SettingsGui extends Screen {

	protected final Screen parentScreen;
	
	public static int currentGui = 1;
	
	public SettingsGui(Screen parent) {
		super(Text.literal("Flex FOV Settings"));
		parentScreen = parent;
		ConfigManager.saveConfig();
	}
	
	public static SettingsGui getGui(Screen parent) {
		switch (currentGui) {
		case 0:
		default:
			return new RectilinearGui(parent);
		case 1:
			return new FlexGui(parent);
		case 2:
			return AdvancedGui.getGui(parent);
		}
	}
	
	@Override
	protected void init() {
		ButtonWidget button = ButtonWidget.builder(Text.of("Default"), (buttonWidget) -> {
					currentGui = 0;
					client.setScreen(new RectilinearGui(parentScreen));
		}).dimensions(width / 2 - 190, height / 6 - 12, 120, 20)
		  .narrationSupplier((buttonWidget) -> Text.literal("Default button"))
		  .build();
		if (this instanceof RectilinearGui) {
			button.active = false;
		}
		addDrawableChild(button);
		
		button = ButtonWidget.builder(Text.of("Flex"), (buttonWidget) -> {
					currentGui = 1;
					client.setScreen(new FlexGui(parentScreen));
				}).dimensions(width / 2 - 60, height / 6 - 12, 120, 20)
		  .narrationSupplier((buttonWidget) -> Text.literal("Flex button"))
		  .build();
		if (this instanceof FlexGui) {
			button.active = false;
		}
		addDrawableChild(button);
		
		button = ButtonWidget.builder(Text.of("Advanced"), (buttonWidget) -> {
					currentGui = 2;
					client.setScreen(AdvancedGui.getGui(parentScreen));
				}).dimensions(width / 2 + 70, height / 6 - 12, 120, 20)
		  .narrationSupplier((buttonWidget) -> Text.literal("Advanced button"))
		  .build();
		if (this instanceof AdvancedGui) {
			button.active = false;
		}
		addDrawableChild(button);
		
		addDrawableChild(ButtonWidget.builder(Text.of("DONE"), (buttonWidget) -> {
			client.setScreen(parentScreen);
		}).dimensions(this.width / 2 - 100, this.height / 6 + 168, 200, 20)
		  .narrationSupplier((buttonWidget) -> Text.literal("Done button"))
		  .build());
	}
	
	@Override
	public void render(DrawContext context, int mouseX, int mouseY, float delta) {
		this.renderBackground(context,mouseX,mouseY,delta);
		context.drawCenteredTextWithShadow(this.textRenderer, this.title, this.width / 2, 15, 16777215);
		super.render(context, mouseX, mouseY, delta);
	}
}
