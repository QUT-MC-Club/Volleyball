package io.github.haykam821.volleyball.game.ball;

import io.github.haykam821.volleyball.game.phase.VolleyballActivePhase;

public class InactiveBallState extends BallState {
	private int ticksUntilSpawn;

	public InactiveBallState(VolleyballActivePhase phase) {
		super(phase);

		this.ticksUntilSpawn = phase.getConfig().getResetBallTicks();
	}

	@Override
	public void onTick() {
		this.ticksUntilSpawn -= 1;

		if (this.ticksUntilSpawn <= 0) {
			this.phase.spawnBall();
		}
	}
}
