package net.id107.flexfov.mixin;

import org.joml.Quaternionf;
import org.joml.Vector3f;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

import net.id107.flexfov.projection.Projection;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.particle.BillboardParticle;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.Vec3d;

@Mixin(value = BillboardParticle.class, priority = 1500)
public abstract class BillboardParticleMixin extends Particle {

	protected BillboardParticleMixin(ClientWorld world, double x, double y, double z) {
		super(world, x, y, z);
	}

	@ModifyVariable(method = "buildGeometry(Lnet/minecraft/client/render/VertexConsumer;Lnet/minecraft/client/render/Camera;F)V",
			ordinal = 0,
			at = @At(value = "INVOKE_ASSIGN", ordinal = 0,
			target = "Lnet/minecraft/client/render/Camera;getRotation()Lnet/minecraft/util/math/Quaternion;"))
	private Quaternionf rotateParticle(Quaternionf quaternion) {
		if (Projection.getProjection().shouldRotateParticles()) {
			MinecraftClient mc = MinecraftClient.getInstance();
			Entity camera = mc.cameraEntity;
			Vec3d cameraPos = camera.getPos().subtract(camera.lastX, camera.lastY, camera.lastZ).multiply(Projection.getTickDelta()).add(new Vec3d(camera.lastX, camera.lastY, camera.lastZ));
			Vec3d particlePos = new Vec3d(x, y, z).subtract(new Vec3d(lastX, lastY, lastZ)).multiply(Projection.getTickDelta()).add(new Vec3d(lastX, lastY, lastZ));
			Vec3d dir = cameraPos.subtract(particlePos).normalize();
			quaternion.set(0, 0, 0, 1);
			quaternion.rotateAxis((float)Math.atan2(-dir.x, -dir.z), 0, 1, 0);
			quaternion.rotateAxis((float)Math.asin(dir.y), 1, 0, 0);
		}
		return quaternion;
	}
}
