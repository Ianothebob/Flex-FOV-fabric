package net.id107.flexfov.projection;

import net.id107.flexfov.Reader;

import java.io.IOException;

public class Hammer extends Projection {

	@Override
	public String getFragmentShader() throws IOException {
		return Reader.read("shaders/hammer.fs");
	}
	
	@Override
	public float[] getBackgroundColor(boolean ignored) {
		return super.getBackgroundColor(skyBackground);
	}
	
	@Override
	public double getFovX() {
		return 360;
	}
	
	@Override
	public double getFovY() {
		return 180;
	}
}
