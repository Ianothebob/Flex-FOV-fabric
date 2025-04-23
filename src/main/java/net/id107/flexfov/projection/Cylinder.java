package net.id107.flexfov.projection;

import net.id107.flexfov.Reader;

import java.io.IOException;

public class Cylinder extends Projection {

	public static double fovy = 90;
	
	@Override
	public String getFragmentShader() throws IOException {
		return Reader.read("flexfov:shaders/cylinder.fs");
	}
	
	@Override
	public double getFovY() {
		if (fovy == 180) {
			return 179.9999;
		} else {
			return fovy;
		}
	}
}
