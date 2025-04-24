package net.id107.flexfov.mixin;

import net.id107.flexfov.IEntityRendererFieldAdder;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.state.EntityRenderState;
import net.minecraft.text.Text;
import org.joml.Quaternionf;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

import net.id107.flexfov.projection.Projection;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.entity.EntityRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.Vec3d;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

@Mixin(EntityRenderer.class)
public class EntityRendererMixin <T extends Entity, S extends EntityRenderState> implements IEntityRendererFieldAdder {

	// Add the new field for storing Entity
	@Unique
	private Entity cEntity;

	// Implement the getter method
	@Override
	public Entity getCurrentEntity() {
		return cEntity;
	}

	// Implement the setter method
	@Override
	public void setCurrentEntity(Entity entity) {
		this.cEntity = entity;
	}
	private Entity currentEntity;
	
	@Inject(method = "Lnet/minecraft/client/render/entity/EntityRenderer;renderLabelIfPresent(Lnet/minecraft/client/render/entity/state/EntityRenderState;Lnet/minecraft/text/Text;Lnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/VertexConsumerProvider;I)V",
			at = @At(value = "HEAD"))
	private void getEntity(S state, Text text, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, CallbackInfo ci) {
		currentEntity = (Entity) this.getCurrentEntity();
	}
	
	@ModifyVariable(method = "Lnet/minecraft/client/render/entity/EntityRenderer;renderLabelIfPresent(Lnet/minecraft/client/render/entity/state/EntityRenderState;Lnet/minecraft/text/Text;Lnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/VertexConsumerProvider;I)V",
			at = @At(value = "INVOKE", ordinal = 0,
			target = "Lnet/minecraft/client/util/math/MatrixStack;multiply(Lorg/joml/Quaternionfc;)V"))
	private MatrixStack rotateNameplatePre(MatrixStack matrixStack) {
		if (Projection.getProjection().shouldRotateParticles()) {
			matrixStack.push();
		}
		return matrixStack;
	}
	
	@ModifyVariable(method = "Lnet/minecraft/client/render/entity/EntityRenderer;renderLabelIfPresent(Lnet/minecraft/client/render/entity/state/EntityRenderState;Lnet/minecraft/text/Text;Lnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/VertexConsumerProvider;I)V",
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
	@Inject(method = "Lnet/minecraft/client/render/entity/EntityRenderer;updateRenderState(Lnet/minecraft/entity/Entity;Lnet/minecraft/client/render/entity/state/EntityRenderState;F)V", at = @At(value = "HEAD"), locals = LocalCapture.PRINT)
	private void updateRenderState(Entity arg0, EntityRenderState state, float arg2, CallbackInfo ci) {
		System.out.println(arg2);
		this.cEntity=arg0;
	}
}
