package net.id107.flexfov.mixin;

import org.joml.Quaternionf;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

import net.id107.flexfov.projection.Projection;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.entity.EntityRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.Vec3d;

@Mixin(EntityRenderer.class)
public class EntityRendererMixin {

	private Entity currentEntity;
	
	@ModifyVariable(method = "renderLabelIfPresent(Lnet/minecraft/entity/Entity;Lnet/minecraft/text/Text;Lnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/VertexConsumerProvider;I)V",
			at = @At(value = "HEAD"))
	private Entity getEntity(Entity entity) {
		currentEntity = entity;
		return entity;
	}
	
	@ModifyVariable(method = "renderLabelIfPresent(Lnet/minecraft/entity/Entity;Lnet/minecraft/text/Text;Lnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/VertexConsumerProvider;I)V",
			at = @At(value = "INVOKE", ordinal = 0,
			target = "Lnet/minecraft/client/util/math/MatrixStack;multiply(Lnet/minecraft/util/math/Quaternion;)V"))
	private MatrixStack rotateNameplatePre(MatrixStack matrixStack) {
		if (Projection.getProjection().shouldRotateParticles()) {
			matrixStack.push();
		}
		return matrixStack;
	}
	
	@ModifyVariable(method = "renderLabelIfPresent(Lnet/minecraft/entity/Entity;Lnet/minecraft/text/Text;Lnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/VertexConsumerProvider;I)V",
			at = @At(value = "INVOKE", ordinal = 0,
			target = "Lnet/minecraft/client/util/math/MatrixStack;scale(FFF)V"))
	private MatrixStack rotateNameplate(MatrixStack matrixStack) {
		if (Projection.getProjection().shouldRotateParticles()) {
			matrixStack.pop();
			MinecraftClient mc = MinecraftClient.getInstance();
			Entity camera = mc.cameraEntity;
			Vec3d cameraPos = camera.getPos().subtract(camera.lastX, camera.lastY, camera.lastZ).multiply(Projection.getTickDelta()).add(new Vec3d(camera.lastX, camera.lastY, camera.lastZ));
			Vec3d entityPos = new Vec3d(currentEntity.getX(), currentEntity.getY(), currentEntity.getZ()).subtract(new Vec3d(currentEntity.lastX, currentEntity.lastY, currentEntity.lastZ)).multiply(Projection.getTickDelta()).add(new Vec3d(currentEntity.lastX, currentEntity.lastY, currentEntity.lastZ));
			Vec3d dir = cameraPos.subtract(entityPos).normalize();
			Quaternionf quaternion = new Quaternionf(0, 0, 0, 1);
			quaternion.rotateAxis((float)Math.atan2(-dir.x, -dir.z), 0, 1, 0);
			quaternion.rotateAxis((float)Math.asin(dir.y), 1, 0, 0);
			matrixStack.multiply(quaternion);
		}
		return matrixStack;
	}
}
