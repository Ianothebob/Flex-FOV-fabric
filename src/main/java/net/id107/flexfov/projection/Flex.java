package net.id107.flexfov.projection;

import net.id107.flexfov.Reader;

import java.io.IOException;

public class Flex extends Projection {

	@Override
	public String getFragmentShader() throws IOException {
		return Reader.read("shaders/flex.fs");
	}
	
	@Override
	public double getPassFOV(double fovIn) {
		double fov = getFovX();
		if (fov <= 90) {
			if (fov == 0) {
				fov = 0.0001f;
			}
			return fov;
		}
		return super.getPassFOV(fovIn);
	}
	
	@Override
	public boolean getResizeGui() {
		return false;
	}
}
