package net.id107.flexfov.mixin;

import net.minecraft.block.enums.CameraSubmersionType;
import net.minecraft.client.render.Camera;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.entity.Entity;
import org.joml.Vector4f;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import net.id107.flexfov.projection.Projection;
import net.minecraft.client.render.BackgroundRenderer;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

@Mixin(BackgroundRenderer.class)
public abstract class BackgroundRendererMixin {
	
	@Inject(method = "Lnet/minecraft/client/render/BackgroundRenderer;getFogColor(Lnet/minecraft/client/render/Camera;FLnet/minecraft/client/world/ClientWorld;IF)Lorg/joml/Vector4f;",
			at = @At(value = "TAIL"),locals = LocalCapture.PRINT)
	private static void skyColor(Camera camera, float tickProgress, ClientWorld world, int clampedViewDistance, float skyDarkness, CallbackInfoReturnable<Vector4f> cir, CameraSubmersionType cameraSubmersionType, Entity entity, float r, float s, float t) {
		Projection.backgroundRed = r;
		Projection.backgroundGreen = s;
		Projection.backgroundBlue = t;
	}
}
