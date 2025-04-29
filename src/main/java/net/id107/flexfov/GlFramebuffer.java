package net.id107.flexfov;

import net.minecraft.client.gl.SimpleFramebuffer;
import org.jetbrains.annotations.Nullable;
import org.lwjgl.opengl.GL30;

public class GlFramebuffer extends SimpleFramebuffer {
    public final int fbo;
    public GlFramebuffer(@Nullable String name, int width, int height, boolean useDepthAttachment) {
        super(name, width, height, useDepthAttachment);
        fbo = GL30.glGenFramebuffers();
        GL30.glBindFramebuffer(GL30.GL_FRAMEBUFFER, fbo);
        // Create a color attachment
        int colorAttachment = GL30.glGenTextures();
        GL30.glBindTexture(GL30.GL_TEXTURE_2D, colorAttachment);
        GL30.glTexImage2D(GL30.GL_TEXTURE_2D, 0, GL30.GL_RGBA, width, height, 0, GL30.GL_RGBA, GL30.GL_UNSIGNED_BYTE, 0);
        GL30.glTexParameteri(GL30.GL_TEXTURE_2D, GL30.GL_TEXTURE_MIN_FILTER, GL30.GL_LINEAR);
        GL30.glTexParameteri(GL30.GL_TEXTURE_2D, GL30.GL_TEXTURE_MAG_FILTER, GL30.GL_LINEAR);
        GL30.glFramebufferTexture2D(GL30.GL_FRAMEBUFFER, GL30.GL_COLOR_ATTACHMENT0, GL30.GL_TEXTURE_2D, colorAttachment, 0);
        
        // Optionally create a depth attachment
        if (useDepthAttachment) {
            int depthAttachment = GL30.glGenRenderbuffers();
            GL30.glBindRenderbuffer(GL30.GL_RENDERBUFFER, depthAttachment);
            GL30.glRenderbufferStorage(GL30.GL_RENDERBUFFER, GL30.GL_DEPTH_COMPONENT, width, height);
            GL30.glFramebufferRenderbuffer(GL30.GL_FRAMEBUFFER, GL30.GL_DEPTH_ATTACHMENT, GL30.GL_RENDERBUFFER, depthAttachment);
        }
        
        // Verify framebuffer completeness
        if (GL30.glCheckFramebufferStatus(GL30.GL_FRAMEBUFFER) != GL30.GL_FRAMEBUFFER_COMPLETE) {
            throw new RuntimeException("Framebuffer is not complete");
        }
        
        // Unbind the framebuffer
        GL30.glBindFramebuffer(GL30.GL_FRAMEBUFFER, 0);
    }
}
