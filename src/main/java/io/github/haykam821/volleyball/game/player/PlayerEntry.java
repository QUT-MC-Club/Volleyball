package io.github.haykam821.volleyball.game.player;

import io.github.haykam821.volleyball.game.phase.VolleyballActivePhase;
import io.github.haykam821.volleyball.game.player.team.TeamEntry;
import net.minecraft.server.network.ServerPlayerEntity;
import xyz.nucleoid.plasmid.game.event.GameTickListener;
import xyz.nucleoid.plasmid.map.template.TemplateRegion;

public class PlayerEntry implements GameTickListener {
	private final VolleyballActivePhase phase;
	private final ServerPlayerEntity player;
	private final TeamEntry team;

	public PlayerEntry(VolleyballActivePhase phase, ServerPlayerEntity player, TeamEntry team) {
		this.phase = phase;
		this.player = player;
		this.team = team;
	}

	// Listeners
	@Override
	public void onTick() {
		TemplateRegion area = this.team.getArea();
		if (area != null && !area.getBounds().contains(this.player.getBlockPos())) {
			this.spawn();
		}
	}

	// Getters
	public VolleyballActivePhase getPhase() {
		return this.phase;
	}

	public ServerPlayerEntity getPlayer() {
		return this.player;
	}

	public TeamEntry getTeam() {
		return this.team;
	}

	// Utilities
	public void spawn() {
		this.team.spawn(this.phase.getGameSpace().getWorld(), this.player);
	}

	public void clearInventory() {
		this.player.inventory.clear();

		this.player.currentScreenHandler.sendContentUpdates();
		this.player.playerScreenHandler.onContentChanged(this.player.inventory);
		this.player.updateCursorStack();
	}
}
