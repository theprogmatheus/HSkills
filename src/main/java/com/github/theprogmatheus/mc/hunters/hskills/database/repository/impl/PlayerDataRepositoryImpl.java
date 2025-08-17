package com.github.theprogmatheus.mc.hunters.hskills.database.repository.impl;

import com.github.theprogmatheus.mc.hunters.hskills.api.Skill;
import com.github.theprogmatheus.mc.hunters.hskills.database.DatabaseSQLManager;
import com.github.theprogmatheus.mc.hunters.hskills.database.entity.PlayerDataEntity;
import com.github.theprogmatheus.mc.hunters.hskills.database.repository.PlayerDataRepository;
import lombok.Getter;

import java.io.*;
import java.sql.*;
import java.util.*;

@Getter
public class PlayerDataRepositoryImpl implements PlayerDataRepository {

    private final DatabaseSQLManager sqlManager;

    public PlayerDataRepositoryImpl(DatabaseSQLManager sqlManager) {
        this.sqlManager = sqlManager;
        this.createTables();
    }

    @Override
    public void createTables() {
        List<String> queries = this.sqlManager.getSqlQueryLoader().getQueries("create_tables");
        try (Connection connection = this.sqlManager.getDataSource().getConnection();
             Statement statement = connection.createStatement()) {
            for (String query : queries) {
                statement.executeUpdate(query);
            }
        } catch (SQLException e) {
            throw new RuntimeException("Unable to create tables.", e);
        }
    }

    @Override
    public Optional<PlayerDataEntity> findById(UUID id) {
        String query = this.sqlManager.getSqlQueryLoader().getQuery("select_player_data");
        try (Connection connection = this.sqlManager.getDataSource().getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(query)) {

            preparedStatement.setString(1, id.toString());

            PlayerDataEntity playerDataEntity = null;
            try (ResultSet rs = preparedStatement.executeQuery()) {
                if (rs.next()) {
                    playerDataEntity = new PlayerDataEntity(
                            id,
                            deserialize(rs.getBytes("skill_levels")),
                            rs.getInt("player_level"),
                            rs.getDouble("player_exp"),
                            rs.getInt("upgrade_points")
                    );
                }
            }
            return Optional.ofNullable(playerDataEntity);
        } catch (SQLException e) {
            throw new RuntimeException("Unable to find %s.".formatted(id), e);
        }
    }

    @Override
    public boolean save(PlayerDataEntity entity) {
        String query = this.sqlManager.getSqlQueryLoader().getQuery("upsert_player_data");
        try (Connection connection = this.sqlManager.getDataSource().getConnection();
             PreparedStatement ps = connection.prepareStatement(query)) {

            ps.setString(1, entity.id().toString());
            ps.setBytes(2, serialize(entity.skillLevels()));
            ps.setInt(3, entity.level());
            ps.setDouble(4, entity.exp());
            ps.setInt(5, entity.upgradePoints());

            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            throw new RuntimeException("Unable to save %s.".formatted(entity.id()), e);
        }
    }

    @Override
    public boolean save(Collection<PlayerDataEntity> entities) {
        if (entities.isEmpty()) return false;

        String query = this.sqlManager.getSqlQueryLoader().getQuery("upsert_player_data");
        try (Connection connection = this.sqlManager.getDataSource().getConnection();
             PreparedStatement ps = connection.prepareStatement(query)) {

            for (PlayerDataEntity entity : entities) {
                ps.setString(1, entity.id().toString());
                ps.setBytes(2, serialize(entity.skillLevels()));
                ps.setInt(3, entity.level());
                ps.setDouble(4, entity.exp());
                ps.setInt(5, entity.upgradePoints());
                ps.addBatch();
            }

            ps.executeBatch();
            return true;
        } catch (SQLException e) {
            throw new RuntimeException("Unable to batch save entities.", e);
        }
    }


    private Map<Skill, Integer> deserialize(byte[] serialized) {
        try (ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(serialized);
             DataInputStream dataInputStream = new DataInputStream(byteArrayInputStream)) {
            Map<Skill, Integer> map = new HashMap<>();

            int size = dataInputStream.readInt();
            for (int i = 0; i < size; i++) {
                Skill skill = Skill.fromId(dataInputStream.readInt());
                int skillLevel = dataInputStream.readInt();
                map.put(skill, skillLevel);
            }

            return map;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private byte[] serialize(Map<Skill, Integer> skillLevels) {
        try (ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
             DataOutputStream dataOutputStream = new DataOutputStream(byteArrayOutputStream)) {
            dataOutputStream.writeInt(skillLevels.size());
            for (Map.Entry<Skill, Integer> entry : skillLevels.entrySet()) {
                dataOutputStream.writeInt(entry.getKey().getId());
                dataOutputStream.writeInt(entry.getValue());
            }
            return byteArrayOutputStream.toByteArray();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
