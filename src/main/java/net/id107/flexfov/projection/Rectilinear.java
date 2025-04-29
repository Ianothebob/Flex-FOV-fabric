package net.id107.flexfov.projection;

import net.id107.flexfov.Reader;
import net.minecraft.client.render.GameRenderer;
import net.minecraft.client.render.RenderTickCounter;
import net.minecraft.client.util.math.MatrixStack;

import java.io.IOException;

public class Rectilinear extends Projection {

	@Override
	public String getFragmentShader() throws IOException {
		return Reader.read("shaders/error.fs");
	}
	
	@Override
	public void renderWorld(RenderTickCounter renderTickCounter, boolean tick, GameRenderer gameRenderer) {}
	
	@Override
	public void rotateCamera(MatrixStack matrixStack) {}
	
	@Override
	public void saveRenderPass(GameRenderer gameRenderer) {}
	
	@Override
	public void loadUniforms(float tickDelta) {}
	
	@Override
	public void runShader(float tickDelta) {}
	
	@Override
	public boolean getResizeGui() {
		return false;
	}
	
	@Override
	public boolean shouldRotateParticles() {
		return false;
	}
	
	@Override
	public boolean shouldOverrideFOV() {
		return false;
	}
	
	@Override
	public double getPassFOV(double fovIn) {
		return fovIn;
	}
}
