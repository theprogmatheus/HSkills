INSERT INTO %table_prefix%player_data (id, skill_levels, player_level, player_exp, upgrade_points)
VALUES (?, ?, ?, ?, ?)
ON CONFLICT(id) DO UPDATE SET
    skill_levels = excluded.skill_levels,
    player_level = excluded.player_level,
    player_exp = excluded.player_exp,
    upgrade_points = excluded.upgrade_points;