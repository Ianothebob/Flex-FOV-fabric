package net.id107.flexfov.gui.advanced;

import net.id107.flexfov.ConfigManager;
import net.id107.flexfov.projection.Equirectangular;
import net.id107.flexfov.projection.Projection;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.text.Text;

import java.io.IOException;

public class EquirectangularGui extends AdvancedGui {

	public EquirectangularGui(Screen parent) {
		super(parent);
        try {
            Projection.setProjection(new Equirectangular());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
	
	@Override
	protected void init() {
		super.init();
		
		addDrawableChild(ButtonWidget.builder(
				Text.literal("Show Circle: " + (Equirectangular.drawCircle ? "ON" : "OFF")),
				(buttonWidget) -> {
					Equirectangular.drawCircle = !Equirectangular.drawCircle;
					buttonWidget.setMessage(Text.literal("Show Circle: " + (Equirectangular.drawCircle ? "ON" : "OFF")));
					ConfigManager.saveConfig();
				})
				.position(width / 2 - 155, height / 6 + 84)
				.size(150, 20)
				.narrationSupplier((buttonWidget) -> Text.literal("Show Circle button. Can be On or OFF"))
				.build());
		addDrawableChild(ButtonWidget.builder(
				Text.literal("Stabilize Pitch: " + (Equirectangular.stabilizePitch ? "ON" : "OFF")),
				(buttonWidget) -> {
					Equirectangular.stabilizePitch = !Equirectangular.stabilizePitch;
					buttonWidget.setMessage(Text.literal("Stabilize Pitch: " + (Equirectangular.stabilizePitch ? "ON" : "OFF")));
					ConfigManager.saveConfig();
				})
				.position(width / 2 - 155, height / 6 + 132)
				.size(150, 20)
				.narrationSupplier((buttonWidget) -> Text.literal("Stabilize Pitch button. Can be On or OFF"))
				.build());
		addDrawableChild(ButtonWidget.builder(
				Text.literal("Stabilize Yaw: " + (Equirectangular.stabilizeYaw ? "ON" : "OFF")),
				(buttonWidget) -> {
					Equirectangular.stabilizeYaw = !Equirectangular.stabilizeYaw;
					buttonWidget.setMessage(Text.literal("Stabilize Yaw: " + (Equirectangular.stabilizeYaw ? "ON" : "OFF")));
					ConfigManager.saveConfig();
				})
				.position(width / 2 + 5, height / 6 + 132)
				.size(150, 20)
				.narrationSupplier((buttonWidget) -> Text.literal("Stabilize Yaw button. Can be On or OFF"))
				.build());
	}
}
