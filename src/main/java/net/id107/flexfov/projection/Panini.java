package net.id107.flexfov.projection;

import net.id107.flexfov.Reader;

import java.io.IOException;

public class Panini extends Projection {

	@Override
	public String getFragmentShader() throws IOException {
		return Reader.read("flexfov:shaders/panini.fs");
	}
}
