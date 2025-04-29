package net.id107.flexfov.mixin;

import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.render.BufferBuilderStorage;
import net.minecraft.client.render.RenderTickCounter;
import net.minecraft.client.render.item.HeldItemRenderer;
import net.minecraft.resource.ResourceManager;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import net.id107.flexfov.projection.Projection;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.hud.InGameHud;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.render.GameRenderer;
import net.minecraft.client.util.math.MatrixStack;

import java.io.IOException;

@Mixin(GameRenderer.class)
public abstract class GameRendererMixin {
	@Shadow final MinecraftClient client;
	@Shadow boolean renderingPanorama;
	private boolean renderingPanoramaTemp;
	private double fovTemp;

	public GameRendererMixin() {
		client = null;
	}

	@Inject(method = "getFov(Lnet/minecraft/client/render/Camera;FZ)F", at = @At(value = "RETURN", ordinal = 0), cancellable = true)
	private void panoramaFov(CallbackInfoReturnable<Float> callbackInfo) {
		callbackInfo.setReturnValue(((Double) Projection.getProjection().getPassFOV(90)).floatValue());
	}
	
	@Inject(method = "render(Lnet/minecraft/client/render/RenderTickCounter;Z)V", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/render/GameRenderer;renderWorld(Lnet/minecraft/client/render/RenderTickCounter;)V", ordinal = 0))
	private void renderPre(RenderTickCounter renderTickCounter, boolean tick, CallbackInfo callbackInfo) throws NoSuchFieldException, IllegalAccessException {
		renderingPanoramaTemp = renderingPanorama;
		renderingPanorama = Projection.getProjection().shouldOverrideFOV();
		fovTemp = client.options.getFov().getValue();
		client.options.getFov().setValue((int) Projection.getProjection().getPassFOV(fovTemp));
		GameRenderer gameRendererInstance = (GameRenderer) (Object) this;
        try {
            Projection.getProjection().renderWorld(/*tickDelta, startTime*/renderTickCounter, tick, gameRendererInstance);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
	
	@Inject(method = "render(Lnet/minecraft/client/render/RenderTickCounter;Z)V", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/render/GameRenderer;updateWorldIcon()V", ordinal = 0))
	private void renderPost(RenderTickCounter tickCounter, boolean tick, CallbackInfo ci) throws NoSuchFieldException, IllegalAccessException {
		renderingPanorama = renderingPanoramaTemp;
		client.options.getFov().setValue((int) fovTemp);
		GameRenderer gameRendererInstance = (GameRenderer) (Object) this;
		Projection.getProjection().saveRenderPass(gameRendererInstance);
		Projection.getProjection().loadUniforms(tickCounter.getDynamicDeltaTicks());
		Projection.getProjection().runShader(tickCounter.getDynamicDeltaTicks());
	}
	
	@ModifyVariable(method = "renderWorld(Lnet/minecraft/client/render/RenderTickCounter;)V",
			ordinal = 0,
			at = @At(value = "INVOKE", target = "Lnet/minecraft/client/render/GameRenderer;tiltViewWhenHurt(Lnet/minecraft/client/util/math/MatrixStack;F)V", ordinal = 0))
	private MatrixStack updateCamera(MatrixStack matrixStack) {
		Projection.getProjection().rotateCamera(matrixStack);
		return matrixStack;
	}
	
	@Redirect(method = "render(Lnet/minecraft/client/render/RenderTickCounter;Z)V", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/hud/InGameHud;render(Lnet/minecraft/client/gui/DrawContext;Lnet/minecraft/client/render/RenderTickCounter;)V"))
	private void renderHud(InGameHud inGameHud, DrawContext drawContext, RenderTickCounter tickCounter) {
		if (!Projection.getProjection().getResizeGui()) {
			/*RenderTickCounter.Dynamic dynamicTickCounter = new RenderTickCounter.Dynamic(
					20.0F,                           // Example TPS (ticks per second)
					(long) (tickDelta * 1000), // Calculate timeMillis using startTime and tickDelta
					value -> 1000.0F / value         // Example FloatUnaryOperator
			);*/
			inGameHud.render(drawContext, tickCounter);
		}
	}
	
	@Redirect(method = "render(Lnet/minecraft/client/render/RenderTickCounter;Z)V", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/screen/Screen;renderWithTooltip(Lnet/minecraft/client/gui/DrawContext;IIF)V"))
	private void renderCurrentScreen(Screen currentScreen, DrawContext drawContext, int mouseX, int mouseY, float delta) {
		if (!Projection.getProjection().getResizeGui()) {
			currentScreen.render(drawContext, mouseX, mouseY, delta);
		}
	}
}
