package io.github.haykam821.volleyball.game.ball;

import java.util.Objects;

import io.github.haykam821.volleyball.game.phase.VolleyballActivePhase;
import io.github.haykam821.volleyball.game.player.PlayerEntry;
import io.github.haykam821.volleyball.game.player.team.TeamEntry;
import net.minecraft.entity.Entity;

public abstract class EntityBallState extends BallState {
	protected final Entity ball;

	public EntityBallState(VolleyballActivePhase phase, Entity ball) {
		super(phase);

		this.ball = Objects.requireNonNull(ball);
	}

	/**
	 * @return whether the ball tick was handled by shared logic
	 */
	protected final boolean onEntityTick() {
		if (!this.ball.isAlive()) {
			this.phase.resetBall();
			return true;
		}

		for (TeamEntry team : this.phase.getTeams()) {
			if (team.isBallOnCourt(this.ball)) {
				team.getOtherTeam().incrementScore();
				return true;
			}
		}

		return false;
	}

	@Override
	public final boolean onAttackEntity(PlayerEntry entry, Entity entity) {
		if (entity == this.ball) {
			this.onHitBall(entry);
			return true;
		}

		return false;
	}

	protected abstract void onHitBall(PlayerEntry entry);

	@Override
	public final void destroy(BallState newState) {
		if (!(newState instanceof EntityBallState)) {
			this.ball.discard();
		}
	}
}
