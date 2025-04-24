package net.id107.flexfov.mixin;

import net.minecraft.client.render.Camera;
import net.minecraft.client.render.VertexConsumer;
import org.joml.Quaternionf;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;

import net.id107.flexfov.projection.Projection;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.particle.BillboardParticle;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.Vec3d;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

@Mixin(value = BillboardParticle.class, priority = 1500)
public abstract class BillboardParticleMixin extends Particle {

	protected BillboardParticleMixin(ClientWorld world, double x, double y, double z) {
		super(world, x, y, z);
	}

	@Inject(method = "render(Lnet/minecraft/client/render/VertexConsumer;Lnet/minecraft/client/render/Camera;F)V",
			at = @At(value = "INVOKE", ordinal = 0,
			target = "Lorg/joml/Quaternionf;rotateZ(F)Lorg/joml/Quaternionf;"),locals = LocalCapture.CAPTURE_FAILSOFT)
	private void rotateParticle(VertexConsumer vertexConsumer, Camera camera, float tickProgress, CallbackInfo ci, Quaternionf quaternionf) {
		if (Projection.getProjection().shouldRotateParticles()) {
			MinecraftClient mc = MinecraftClient.getInstance();
			Entity camera1 = mc.cameraEntity;
			Vec3d cameraPos = camera.getPos().subtract(camera1.lastX, camera1.lastY, camera1.lastZ).multiply(Projection.getTickDelta()).add(new Vec3d(camera1.lastX, camera1.lastY, camera1.lastZ));
			Vec3d particlePos = new Vec3d(x, y, z).subtract(new Vec3d(lastX, lastY, lastZ)).multiply(Projection.getTickDelta()).add(new Vec3d(lastX, lastY, lastZ));
			Vec3d dir = cameraPos.subtract(particlePos).normalize();
			quaternionf.set(0, 0, 0, 1);
			quaternionf.rotateAxis((float)Math.atan2(-dir.x, -dir.z), 0, 1, 0);
			quaternionf.rotateAxis((float)Math.asin(dir.y), 1, 0, 0);
		}
	}
}
