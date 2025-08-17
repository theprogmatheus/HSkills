CREATE TABLE IF NOT EXISTS %table_prefix%player_data (
    id TEXT PRIMARY KEY,
    skill_levels BLOB NOT NULL,
    level INTEGER NOT NULL,
    exp REAL NOT NULL,
    upgrade_points INTEGER NOT NULL
);