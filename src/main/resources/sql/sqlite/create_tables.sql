CREATE TABLE IF NOT EXISTS %table_prefix%player_data (
    id TEXT PRIMARY KEY,
    skill_levels BLOB NOT NULL,
    player_level INTEGER NOT NULL,
    player_exp REAL NOT NULL,
    upgrade_points INTEGER NOT NULL
);

CREATE INDEX IF NOT EXISTS idx_player_level_exp
    ON %table_prefix%player_data (player_level DESC, player_exp DESC);