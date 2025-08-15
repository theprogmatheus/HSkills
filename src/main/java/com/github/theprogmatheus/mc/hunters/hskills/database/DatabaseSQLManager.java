package com.github.theprogmatheus.mc.hunters.hskills.database;

import com.github.theprogmatheus.mc.hunters.hskills.database.query.SqlQueryLoader;
import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.sql.SQLException;

@Getter
@RequiredArgsConstructor
public class DatabaseSQLManager {

    private final HikariConfig databaseConfig;
    private final SqlQueryLoader sqlQueryLoader;

    private HikariDataSource dataSource;

    public void openConnection() throws SQLException {
        this.dataSource = new HikariDataSource(this.databaseConfig);
    }

    public void closeConnection() throws Exception {
        this.dataSource.close();
    }

}
