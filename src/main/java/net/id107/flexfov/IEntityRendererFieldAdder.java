package net.id107.flexfov;

import net.minecraft.entity.Entity;

public interface IEntityRendererFieldAdder {
	Entity getCurrentEntity();
	void setCurrentEntity(Entity entity);
}
