package net.id107.flexfov.projection;

import com.mojang.blaze3d.systems.ProjectionType;
import com.mojang.blaze3d.textures.GpuTexture;
import com.mojang.blaze3d.vertex.VertexFormat;
import net.fabricmc.fabric.mixin.client.gametest.screenshot.RenderTickCounterConstantAccessor;
import net.id107.flexfov.GlFramebuffer;
import net.minecraft.client.gl.GlResourceManager;
import net.minecraft.client.gl.SimpleFramebuffer;
import net.minecraft.client.gl.WindowFramebuffer;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.render.*;
import net.minecraft.client.render.RenderTickCounter.*;
import net.minecraft.client.texture.AbstractTexture;
import net.minecraft.client.texture.GlTexture;
import net.minecraft.client.texture.NativeImageBackedTexture;
import net.minecraft.client.util.math.Vector2f;
import org.joml.Matrix3f;
import org.joml.Matrix3fc;
import org.joml.Matrix4f;
import org.joml.Quaternionf;
import org.lwjgl.opengl.*;

import com.mojang.blaze3d.opengl.GlConst;
import com.mojang.blaze3d.opengl.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;

import net.id107.flexfov.BufferManager;
import net.id107.flexfov.Reader;
import net.id107.flexfov.ShaderManager;
import net.id107.flexfov.gui.SettingsGui;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gl.Framebuffer;
import net.minecraft.client.util.Window;
import net.minecraft.client.util.math.MatrixStack;
import org.lwjgl.system.MemoryUtil;

import java.io.IOException;
import java.lang.reflect.Field;
import java.nio.ByteBuffer;
import java.util.Objects;

public abstract class Projection {
	
	private static Projection currentProjection;
	private static ShaderManager shader = new ShaderManager();
	
	public static float backgroundRed;
	public static float backgroundGreen;
	public static float backgroundBlue;
	
	public static double fov = 140f;
	public static int antialiasing = 16;
	public static boolean skyBackground = true;
	public static float zoom = 0;
	public static boolean resizeGui;
	
	protected int renderPass;
	
	private static boolean hudHidden;
	private static float tickDelta;
	
	private static int screenWidth;
	private static int screenHeight;
	
	public static Projection getProjection() {
		if (currentProjection == null) {
			SettingsGui.getGui(null);
		}
		return currentProjection;
	}
	
	public static void setProjection(Projection projection) throws IOException {
		currentProjection = projection;
		if (shader != null) {
			shader.deleteShaderProgram();
			shader.createShaderProgram(projection);
		}
	}
	
	public String getVertexShader() throws IOException {
		return Reader.read("shaders/quad.vs");
	}
	
	public abstract String getFragmentShader() throws IOException;
	
	public void renderWorld(/*float tickDelta, long startTime*/RenderTickCounter renderTickCounter, boolean tick, GameRenderer gameRenderer) throws NoSuchFieldException, IllegalAccessException, IOException {
        try {
            MinecraftClient mc = MinecraftClient.getInstance();
            Projection.tickDelta = renderTickCounter.getDynamicDeltaTicks();
            int displayWidth = mc.getWindow().getWidth();
            int displayHeight = mc.getWindow().getHeight();
            hudHidden = mc.options.hudHidden;

            if (BufferManager.getFramebuffer() == null) {
                BufferManager.createFramebuffer();
                shader.createShaderProgram(getProjection());
                screenWidth = displayWidth;
                screenHeight = displayHeight;
            }

            if (screenWidth != displayWidth || screenHeight != displayHeight) {
                shader.deleteShaderProgram();
                BufferManager.deleteFramebuffer();
                BufferManager.createFramebuffer();
                shader.createShaderProgram(getProjection());
                screenWidth = displayWidth;
                screenHeight = displayHeight;
            }

            if (Math.max(getFovX(), getFovY()) > 90 || zoom < 0) {
                for (renderPass = 1; renderPass < 5; renderPass++) {
                    GL11.glViewport(0, 0, displayWidth, displayHeight);
                    mc.worldRenderer.scheduleTerrainUpdate();
                    /*RenderTickCounter.Dynamic dynamicTickCounter = new RenderTickCounter.Dynamic(
                            20.0F,                           // Example TPS (ticks per second)
                            startTime + (long) (tickDelta * 1000), // Calculate timeMillis using startTime and tickDelta
                            value -> 1000.0F / value         // Example FloatUnaryOperator
                    );*/
                    try {
                        mc.gameRenderer.renderWorld(renderTickCounter);
                    } catch (Exception e) {
						System.out.println("Exception");
                        throw new RuntimeException(e);
                    }
                    saveRenderPass(gameRenderer);
                }
                if (Math.max(getFovX(), getFovY()) > 250d || zoom < 0f) {
                    renderPass = 5;
                    GL11.glViewport(0, 0, displayWidth, displayHeight);
                    mc.worldRenderer.scheduleTerrainUpdate();
                    /*RenderTickCounter.Dynamic dynamicTickCounter = new RenderTickCounter.Dynamic(
                            20.0F,                           // Example TPS (ticks per second)
                            startTime + (long) (tickDelta * 1000), // Calculate timeMillis using startTime and tickDelta
                            value -> 1000.0F / value         // Example FloatUnaryOperator
                    );*/
                    try {
                        mc.gameRenderer.renderWorld(renderTickCounter);
                    } catch (Exception e) {
						System.out.println("Exception");
                        throw new RuntimeException(e);
                    }
                    saveRenderPass(gameRenderer);
                }
            }
            renderPass = 0;
            GL11.glViewport(0, 0, displayWidth, displayHeight);
            mc.worldRenderer.scheduleTerrainUpdate();

            mc.options.hudHidden = hudHidden;
        } catch (IOException e) {
            throw new RuntimeException(e);
        } catch (NoSuchFieldException e) {
            throw new RuntimeException(e);
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }
	
	public void rotateCamera(MatrixStack matrixStack) {
		Matrix4f matrix;
		Matrix3f matrix3f = new Matrix3f();
		switch (renderPass) {
		case 0:
			break;
		case 1:
			new Quaternionf(0, 0.707106781f, 0, 0.707106781f).get(matrix3f);//look right
			matrix = new Matrix4f((Matrix3fc) matrix3f);
			matrixStack.peek().getPositionMatrix().mul(matrix);
			break;
		case 2:
			new Quaternionf(0, -0.707106781f, 0, 0.707106781f).get(matrix3f); //look left
			matrix = new Matrix4f((Matrix3fc) matrix3f);
			matrixStack.peek().getPositionMatrix().mul(matrix);
			break;
		case 3:
			new Quaternionf(0.707106781f, 0, 0, 0.707106781f).get(matrix3f); //look down
			matrix = new Matrix4f((Matrix3fc) matrix3f);
			matrixStack.peek().getPositionMatrix().mul(matrix);
			break;
		case 4:
			new Quaternionf(-0.707106781f, 0, 0, 0.707106781f).get(matrix3f);//look up
			matrix = new Matrix4f((Matrix3fc) matrix3f);
			matrixStack.peek().getPositionMatrix().mul(matrix);
			break;
		case 5:
			new Quaternionf(0, -1, 0, 0).get(matrix3f);
			matrix = new Matrix4f((Matrix3fc) matrix3f); //look back
			matrixStack.peek().getPositionMatrix().mul(matrix);
			break;
		}
	}
	
	public void saveRenderPass(GameRenderer gameRenderer) throws NoSuchFieldException, IllegalAccessException {
		if (getResizeGui() && renderPass == 0) {
			MinecraftClient mc = MinecraftClient.getInstance();
			Window window = mc.getWindow();
			//GL11.glMatrixMode(GL11.GL_PROJECTION);
			//GL11.glLoadIdentity();
			//GL11.glOrtho(0.0D, (double)window.getFramebufferWidth() / window.getScaleFactor(), (double)window.getFramebufferHeight() / window.getScaleFactor(), 0.0D, 1000.0D, 3000.0D);
			/*RenderSystem.setProjectionMatrix(new Matrix4f().ortho(0.0f, (float) (window.getFramebufferWidth()/window.getScaleFactor()), (float) (window.getFramebufferHeight() / window.getScaleFactor()), 0, 1000, 3000), ProjectionType.ORTHOGRAPHIC);
			*/
			// Calculate FOV
			int displayWidth = MinecraftClient.getInstance().getWindow().getFramebufferWidth();
			int displayHeight = MinecraftClient.getInstance().getWindow().getFramebufferHeight();
			double aspectRatio = (double) displayWidth / displayHeight;
			float fovRadians = (float) Math.toRadians(getFovX()); // Convert FOV to radians

// Set up a perspective projection matrix
			Matrix4f perspectiveMatrix = new Matrix4f()
					.perspective(fovRadians, (float) aspectRatio, 0.05f, 1000.0f); // Near and far planes

// Apply this projection matrix
			RenderSystem.setProjectionMatrix(perspectiveMatrix, ProjectionType.PERSPECTIVE);
			/*GL11.glMatrixMode(GL11.GL_MODELVIEW);
			GL11.glLoadIdentity();
			GL11.glTranslatef(0.0F, 0.0F, -2000.0F);
			GL11.glClear(256);*/
			RenderSystem.getModelViewMatrix().set(new Matrix4f().identity());
			RenderSystem.getModelViewMatrix().translate(0, 0, -2000);
            RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
			GlStateManager._clear(256);
			MatrixStack matrixStack = new MatrixStack();
			Dynamic dynamicTickCounter = new Dynamic(
					20.0F,                           // Example TPS (ticks per second)
					(long) (tickDelta * 1000), // Calculate timeMillis using startTime and tickDelta
					value -> 1000.0F / value         // Example FloatUnaryOperator
			);
			mc.inGameHud.render(new DrawContext(mc, VertexConsumerProvider.immediate(mc.getBufferBuilders().getBlockBufferBuilders().get(RenderLayer.getGui()))), dynamicTickCounter);
			/*GL11.glClear(256);*/
			GlStateManager._clear(256);
			if (mc.currentScreen != null) {
				int i = (int)(mc.mouse.getX() * (double)mc.getWindow().getScaledWidth() / (double)mc.getWindow().getWidth());
				int j = (int)(mc.mouse.getY() * (double)mc.getWindow().getScaledHeight() / (double)mc.getWindow().getHeight());
				mc.currentScreen.render(new DrawContext(mc, VertexConsumerProvider.immediate(mc.getBufferBuilders().getBlockBufferBuilders().get(RenderLayer.getGui()))), i, j, (mc.getRenderTickCounter()).getDynamicDeltaTicks());
			}
		}
		
		Framebuffer defaultFramebuffer = MinecraftClient.getInstance().getFramebuffer();
		GlFramebuffer targetFramebuffer = (GlFramebuffer) BufferManager.getFramebuffer();

		GlStateManager._glBindFramebuffer(GlConst.GL_FRAMEBUFFER, targetFramebuffer.fbo);
		int status = GL30.glCheckFramebufferStatus(GL30.GL_FRAMEBUFFER);
		if (status != GL30.GL_FRAMEBUFFER_COMPLETE) {
			throw new RuntimeException("Framebuffer is incomplete or invalid!");
		}
		GlStateManager._glFramebufferTexture2D(GlConst.GL_FRAMEBUFFER, GlConst.GL_COLOR_ATTACHMENT0,
				GL11.GL_TEXTURE_2D, BufferManager.framebufferTextures[renderPass], 0);
		
		/*GL11.glClear(GL11.GL_COLOR_BUFFER_BIT);*/
		GlStateManager._clear(GL11.GL_COLOR_BUFFER_BIT);
		int textureToBind = ((GlTexture) Objects.requireNonNull(defaultFramebuffer.getColorAttachment())).getGlId();
		GlStateManager._bindTexture(textureToBind);
		status = GL30.glCheckFramebufferStatus(GL30.GL_FRAMEBUFFER);
		if (status != GL30.GL_FRAMEBUFFER_COMPLETE) {
			throw new RuntimeException("Framebuffer is incomplete or invalid!");
		}
		int error = GlStateManager._getError();/*GL11.glGetError();*/
		if (error != GL11.GL_NO_ERROR) {
			throw new RuntimeException("OpenGL error: " + error);
		}
		BufferBuilder bufferBuilder = Tessellator.getInstance().begin(VertexFormat.DrawMode.QUADS, VertexFormats.POSITION_TEXTURE);
		{
			bufferBuilder.vertex(-1, -1, 0).texture(0, 0);
			bufferBuilder.vertex(1, -1, 0).texture(1, 0);
			bufferBuilder.vertex(1, 1, 0).texture(1, 1);
			bufferBuilder.vertex(-1, 1, 0).texture(0, 1);
		}
		bufferBuilder.end();
		GlStateManager._bindTexture(0);
		/*GL11.glMatrixMode(GL11.GL_PROJECTION);
		GL11.glPopMatrix();
		GL11.glMatrixMode(GL11.GL_MODELVIEW);
		GL11.glPopMatrix();*/
		
		GlStateManager._glBindFramebuffer(GlConst.GL_FRAMEBUFFER, 0);
	}
	
	public void loadUniforms(float tickDelta) {
		MinecraftClient mc = MinecraftClient.getInstance();
		int shaderProgram = shader.getShaderProgram();
		int displayWidth = MinecraftClient.getInstance().getWindow().getWidth();
		int displayHeight = MinecraftClient.getInstance().getWindow().getHeight();
		GlStateManager._glUseProgram(shaderProgram);/*GL20.glUseProgram(shaderProgram);*/
		
		int aaUniform = GlStateManager._glGetUniformLocation(shaderProgram, "antialiasing")/*GL20.glGetUniformLocation(shaderProgram, "antialiasing")*/;
		GlStateManager._glUniform1i(aaUniform,getAntialiasing());/*GL20.glUniform1i(aaUniform, getAntialiasing())*/
		int pixelOffestUniform;
		if (getAntialiasing() == 16) {
			float left = (-1f+0.25f)/displayWidth;
			float top = (-1f+0.25f)/displayHeight;
			float right = 0.5f/displayWidth;
			float bottom = 0.5f/displayHeight;
			for (int y = 0; y < 4; y++) {
				for (int x = 0; x < 4; x++) {
					pixelOffestUniform = GlStateManager._glGetUniformLocation(shaderProgram,"pixelOffset[" + (y*4+x) + "]"); /*GL20.glGetUniformLocation(shaderProgram, "pixelOffset[" + (y*4+x) + "]");*/
					GlStateManager._glUniform2(pixelOffestUniform, java.nio.FloatBuffer.wrap(new float[]{left + right*x, top + bottom*y}));/*GL20.glUniform2f(pixelOffestUniform, left + right*x, top + bottom*y);*/
				}
			}
		} else if (getAntialiasing() == 4) {
			pixelOffestUniform = GlStateManager._glGetUniformLocation(shaderProgram, "pixelOffset[0]");
			GlStateManager._glUniform2(pixelOffestUniform, java.nio.FloatBuffer.wrap(new float[]{-0.5f/displayWidth,-0.5f/displayHeight})); /*GL20.glUniform2f(pixelOffestUniform, -0.5f/displayWidth, -0.5f/displayHeight);*/
			pixelOffestUniform = GlStateManager._glGetUniformLocation(shaderProgram, "pixelOffset[1]");
			GlStateManager._glUniform2(pixelOffestUniform, java.nio.FloatBuffer.wrap(new float[]{0.5f/displayWidth,-0.5f/displayHeight}));/*GL20.glUniform2f(pixelOffestUniform, 0.5f/displayWidth, -0.5f/displayHeight);*/
			pixelOffestUniform = GlStateManager._glGetUniformLocation(shaderProgram, "pixelOffset[2]");
			GlStateManager._glUniform2(pixelOffestUniform, java.nio.FloatBuffer.wrap(new float[]{-0.5f/displayWidth,0.5f/displayHeight}));/*GL20.glUniform2f(pixelOffestUniform, -0.5f/displayWidth, 0.5f/displayHeight);*/
			pixelOffestUniform = GlStateManager._glGetUniformLocation(shaderProgram, "pixelOffset[3]");
			GlStateManager._glUniform2(pixelOffestUniform, java.nio.FloatBuffer.wrap(new float[]{0.5f/displayWidth,0.5f/displayHeight}));/*GL20.glUniform2f(pixelOffestUniform, 0.5f/displayWidth, 0.5f/displayHeight);*/
		} else { //if (getAntialiasing() == 1)
			pixelOffestUniform = GlStateManager._glGetUniformLocation(shaderProgram, "pixelOffset[0]");
			GlStateManager._glUniform2(pixelOffestUniform, java.nio.FloatBuffer.wrap(new float[]{0,0}));/*GL20.glUniform2f(pixelOffestUniform, 0, 0);*/
		}
		
		int texUniform = GlStateManager._glGetUniformLocation(shaderProgram, "texFront") /*GL20.glGetUniformLocation(shaderProgram, "texFront")*/;
		GlStateManager._glUniform1i(texUniform, 0) /*GL20.glUniform1i(texUniform, 0)*/;
		texUniform = GlStateManager._glGetUniformLocation(shaderProgram, "texBack") /*GL20.glGetUniformLocation(shaderProgram, "texBack")*/;
		GlStateManager._glUniform1i(texUniform, 5) /*GL20.glUniform1i(texUniform, 5)*/;
		texUniform = GlStateManager._glGetUniformLocation(shaderProgram, "texLeft") /*GL20.glGetUniformLocation(shaderProgram, "texLeft")*/;
		GlStateManager._glUniform1i(texUniform, 2) /*GL20.glUniform1i(texUniform, 2)*/;
		texUniform = GlStateManager._glGetUniformLocation(shaderProgram, "texRight") /*GL20.glGetUniformLocation(shaderProgram, "texRight")*/;
		GlStateManager._glUniform1i(texUniform, 1) /*GL20.glUniform1i(texUniform, 1)*/;
		texUniform = GlStateManager._glGetUniformLocation(shaderProgram, "texTop") /*GL20.glGetUniformLocation(shaderProgram, "texTop")*/;
		GlStateManager._glUniform1i(texUniform, 4) /*GL20.glUniform1i(texUniform, 4)*/;
		texUniform = GlStateManager._glGetUniformLocation(shaderProgram, "texBottom") /*GL20.glGetUniformLocation(shaderProgram, "texBottom")*/;
		GlStateManager._glUniform1i(texUniform, 3) /*GL20.glUniform1i(texUniform, 3)*/;
		
		int fovxUniform = GlStateManager._glGetUniformLocation(shaderProgram, "fovx") /*GL20.glGetUniformLocation(shaderProgram, "fovx")*/;
		GlStateManager._glUniform1(fovxUniform, java.nio.FloatBuffer.wrap(new float[]{(float) getFovX()})) /*GL20.glUniform1f(fovxUniform, (float) getFovX())*/;
		int fovyUniform = GlStateManager._glGetUniformLocation(shaderProgram, "fovy") /*GL20.glGetUniformLocation(shaderProgram, "fovy")*/;
		GlStateManager._glUniform1(fovyUniform, java.nio.FloatBuffer.wrap(new float[]{(float) getFovY()})) /*GL20.glUniform1f(fovyUniform, (float) getFovY())*/;
		
		int backgroundUniform = GlStateManager._glGetUniformLocation(shaderProgram, "backgroundColor") /*GL20.glGetUniformLocation(shaderProgram, "backgroundColor")*/;
		float backgroundColor[] = getBackgroundColor(false);
		if (backgroundColor != null) {
			GlStateManager._glUniform4(backgroundUniform, java.nio.FloatBuffer.wrap(new float[]{backgroundColor[0], backgroundColor[1], backgroundColor[2], 1})) /*GL20.glUniform4f(backgroundUniform, backgroundColor[0], backgroundColor[1], backgroundColor[2], 1)*/;
		} else {
			GlStateManager._glUniform4(backgroundUniform, java.nio.FloatBuffer.wrap(new float[]{0, 0, 0, 1})) /*GL20.glUniform4f(backgroundUniform, 0, 0, 0, 1)*/;
		}
		
		int zoomUniform = GlStateManager._glGetUniformLocation(shaderProgram, "zoom") /*GL20.glGetUniformLocation(shaderProgram, "zoom")*/;
		GlStateManager._glUniform1(zoomUniform, java.nio.FloatBuffer.wrap(new float[]{(float)Math.pow(2, -zoom)})) /*GL20.glUniform1f(zoomUniform, (float)Math.pow(2, -zoom))*/;

		int drawCursorUniform = GlStateManager._glGetUniformLocation(shaderProgram, "drawCursor") /*GL20.glGetUniformLocation(shaderProgram, "drawCursor")*/;
		GlStateManager._glUniform1i(drawCursorUniform, (getResizeGui() && mc.currentScreen != null) ? 1 : 0) /*GL20.glUniform1i(drawCursorUniform, (getResizeGui() && mc.currentScreen != null) ? 1 : 0)*/;
		int cursorPosUniform = GlStateManager._glGetUniformLocation(shaderProgram, "cursorPos") /*GL20.glGetUniformLocation(shaderProgram, "cursorPos")*/;
		Window window = mc.getWindow();
		float mouseX = (float)mc.mouse.getX() / (float)window.getWidth();
		float mouseY = (float)mc.mouse.getY() / (float)window.getHeight();
		mouseX = (mouseX - 0.5f) * window.getWidth() / (float)window.getHeight() + 0.5f;
		mouseX = Math.max(0, Math.min(1, mouseX));
		GlStateManager._glUniform2(cursorPosUniform, java.nio.FloatBuffer.wrap(new float[]{mouseX, 1-mouseY})) /*GL20.glUniform2f(cursorPosUniform, mouseX, 1-mouseY)*/;
	}
	
	public void runShader(float tickDelta) {
		int displayWidth = MinecraftClient.getInstance().getWindow().getWidth();
		int displayHeight = MinecraftClient.getInstance().getWindow().getHeight();
		GL13.glActiveTexture(GL13.GL_TEXTURE2);
		int lightmap = GL11.glGetInteger(GL11.GL_TEXTURE_BINDING_2D);

		GL11.glClear(GL11.GL_COLOR_BUFFER_BIT | GL11.GL_DEPTH_BUFFER_BIT);
		GL11.glMatrixMode(GL11.GL_PROJECTION);
		GL11.glPushMatrix();
		GL11.glLoadIdentity();
		GL11.glOrtho(-1, 1, -1, 1, -1, 1);
		GL11.glMatrixMode(GL11.GL_MODELVIEW);
		GL11.glPushMatrix();
		GL11.glLoadIdentity();

		for (int i = 0; i < BufferManager.framebufferTextures.length; i++) {
			GL13.glActiveTexture(GL13.GL_TEXTURE0+i);
			GL11.glBindTexture(GL11.GL_TEXTURE_2D, BufferManager.framebufferTextures[i]);
		}
		GL11.glViewport(0, 0, displayWidth, displayHeight);
		GL11.glBegin(GL11.GL_QUADS);
		{
			GL11.glTexCoord2f(0, 0);
			GL11.glVertex2f(-1, -1);
			GL11.glTexCoord2f(1, 0);
			GL11.glVertex2f(1, -1);
			GL11.glTexCoord2f(1, 1);
			GL11.glVertex2f(1, 1);
			GL11.glTexCoord2f(0, 1);
			GL11.glVertex2f(-1, 1);
		}
		GL11.glEnd();
		for (int i = BufferManager.framebufferTextures.length-1; i >= 0; i--) {
			GL13.glActiveTexture(GL13.GL_TEXTURE0+i);
			GL11.glBindTexture(GL11.GL_TEXTURE_2D, 0);
		}

		GL11.glMatrixMode(GL11.GL_PROJECTION);
		GL11.glPopMatrix();
		GL11.glMatrixMode(GL11.GL_MODELVIEW);
		GL11.glPopMatrix();

		GL13.glActiveTexture(GL13.GL_TEXTURE2);
		GL11.glBindTexture(GL11.GL_TEXTURE_2D, lightmap);
		GL13.glActiveTexture(GL13.GL_TEXTURE0);

		GL20.glUseProgram(0);
	}
	
	protected int getShaderProgram() {
		return shader.getShaderProgram();
	}
	
	public static float getTickDelta() {
		return tickDelta;
	}
	
	public int getAntialiasing() {
		return antialiasing;
	}
	
	public float[] getBackgroundColor(boolean sky) {
		if (sky) {
			return new float[] {backgroundRed, backgroundGreen, backgroundBlue};
		} else {
			return null;
		}
	}
	
	public boolean getResizeGui() {
		return resizeGui && MinecraftClient.getInstance().world != null;
	}
	
	public boolean shouldRotateParticles() {
		return true;
	}
	
	public boolean shouldOverrideFOV() {
		return true;
	}
	
	public double getPassFOV(double fovIn) {
		return BufferManager.getFOV();
	}
	
	public double getFovX() {
		return fov;
	}
	
	public double getFovY() {
		double aspectRatio = (double) MinecraftClient.getInstance().getWindow().getWidth() /
				MinecraftClient.getInstance().getWindow().getHeight();

		// Use a vertical FOV calculation that respects large FOVs
		return 2 * Math.atan(Math.tan(Math.toRadians(getFovX()) / 2) / aspectRatio) * (180 / Math.PI);
	}
}
