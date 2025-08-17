INSERT INTO %table_prefix%player_data (id, skill_levels, player_level, player_exp, upgrade_points)
VALUES (?, ?, ?, ?, ?)
ON CONFLICT(id) DO UPDATE SET
    skill_levels = excluded.skill_levels,
    level = excluded.level,
    exp = excluded.exp,
    upgrade_points = excluded.upgrade_points;