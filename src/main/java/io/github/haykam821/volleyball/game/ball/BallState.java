package io.github.haykam821.volleyball.game.ball;

import java.util.Objects;

import io.github.haykam821.volleyball.game.phase.VolleyballActivePhase;
import io.github.haykam821.volleyball.game.player.PlayerEntry;
import net.minecraft.entity.Entity;

public abstract class BallState {
	protected final VolleyballActivePhase phase;

	public BallState(VolleyballActivePhase phase) {
		this.phase = Objects.requireNonNull(phase);
	}

	public abstract void onTick();

	public boolean onAttackEntity(PlayerEntry entry, Entity entity) {
		return false;
	}

	public void destroy(BallState newState) {
		return;
	}
}
