package io.github.haykam821.volleyball.game.ball;

import eu.pb4.polymer.virtualentity.api.attachment.HolderAttachment;
import io.github.haykam821.volleyball.game.phase.VolleyballActivePhase;

public class InactiveBallState extends BallState {
	private final HolderAttachment markerAttachment;

	private int ticksUntilSpawn;

	public InactiveBallState(VolleyballActivePhase phase, HolderAttachment markerAttachment) {
		super(phase);

		this.markerAttachment = markerAttachment;
		this.ticksUntilSpawn = phase.getConfig().getResetBallTicks();
	}

	@Override
	public void onTick() {
		this.ticksUntilSpawn -= 1;

		if (this.ticksUntilSpawn <= 0) {
			this.phase.spawnBall();
		}
	}

	@Override
	public void destroy(BallState newState) {
		if (this.markerAttachment != null) {
			this.markerAttachment.holder().destroy();
			this.markerAttachment.destroy();
		}
	}
}
