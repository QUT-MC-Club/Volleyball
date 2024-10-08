package io.github.haykam821.volleyball.game.player.team;

import io.github.haykam821.volleyball.game.map.VolleyballMap;
import io.github.haykam821.volleyball.game.phase.VolleyballActivePhase;
import net.minecraft.entity.Entity;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Vec3d;
import xyz.nucleoid.map_templates.BlockBounds;
import xyz.nucleoid.map_templates.MapTemplate;
import xyz.nucleoid.map_templates.TemplateRegion;
import xyz.nucleoid.plasmid.game.common.team.GameTeamConfig;
import xyz.nucleoid.plasmid.game.common.team.GameTeamKey;

public class TeamEntry implements Comparable<TeamEntry> {
	private static final BlockBounds DEFAULT_BOUNDS = BlockBounds.ofBlock(BlockPos.ORIGIN);

	private final VolleyballActivePhase phase;

	private final GameTeamKey key;
	private final GameTeamConfig config;

	private final TemplateRegion area;
	private final TemplateRegion spawn;
	private final Box courtBox;

	private int score;

	public TeamEntry(VolleyballActivePhase phase, GameTeamKey key, GameTeamConfig config, MapTemplate template) {
		this.phase = phase;

		this.key = key;
		this.config = config;

		this.area = this.getRegion(template, "area");
		this.spawn = this.getRegion(template, "spawn");
		this.courtBox = this.getBoundsOrDefault(template, "court").asBox();
	}

	// Getters
	public TemplateRegion getArea() {
		return this.area;
	}

	// Utilities
	public void spawn(ServerWorld world, ServerPlayerEntity player) {
		Vec3d spawnPos = this.spawn.getBounds().centerBottom();
		float yaw = this.spawn.getData().getFloat(VolleyballMap.FACING_KEY);
	
		player.teleport(world, spawnPos.getX(), spawnPos.getY(), spawnPos.getZ(), yaw, 0);
	}

	public boolean isBallOnCourt(Entity ball) {
		if (!this.courtBox.intersects(ball.getBoundingBox())) {
			return false;
		}

		return ball.isOnGround() || ball.getY() < this.courtBox.minY;
	}

	public void incrementOtherTeamScore() {
		this.getOtherTeam().incrementScore(this);
	}

	private void incrementScore(TeamEntry causer) {
		this.phase.getGameSpace().getPlayers().sendMessage(this.getScoreText());
		this.phase.pling();

		this.phase.resetBall(causer);

		this.score += 1;
		this.phase.getScoreboard().update();
	}

	public boolean hasRequiredScore() {
		return this.score >= this.phase.getConfig().getRequiredScore();
	}

	public Text getScoreText() {
		return Text.translatable("text.volleyball.score", this.getName()).formatted(Formatting.GOLD);
	}

	public Text getWinText() {
		return Text.translatable("text.volleyball.win", this.getName()).formatted(Formatting.GOLD);
	}

	public Text getScoreboardEntryText() {
		return Text.translatable("text.volleyball.scoreboard.entry", this.getName(), this.score);
	}

	public Formatting getFormatting() {
		return this.config.chatFormatting();
	}

	public Text getName() {
		return this.config.name();
	}

	private BlockBounds getBoundsOrDefault(MapTemplate template, String key) {
		TemplateRegion region = this.getRegion(template, key);
		return region == null ? DEFAULT_BOUNDS : region.getBounds();
	}

	private TemplateRegion getRegion(MapTemplate template, String key) {
		return template.getMetadata().getFirstRegion(this.key.id() + "_" + key);
	}

	private TeamEntry getOtherTeam() {
		for (TeamEntry team : this.phase.getTeams()) {
			if (this != team) return team;
		}
		return this;
	}

	@Override
	public int compareTo(TeamEntry other) {
		return this.score - other.score;
	}
}
