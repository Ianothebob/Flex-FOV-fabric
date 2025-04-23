package net.id107.flexfov.gui;

import net.id107.flexfov.projection.Projection;
import net.id107.flexfov.projection.Rectilinear;
import net.minecraft.client.gui.screen.Screen;

import java.io.IOException;

public class RectilinearGui extends SettingsGui {

	public RectilinearGui(Screen parent) {
		super(parent);
        try {
            Projection.setProjection(new Rectilinear());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
