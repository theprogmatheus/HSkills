package com.github.theprogmatheus.mc.hunters.hskills;

import be.seeseemelk.mockbukkit.MockBukkit;
import be.seeseemelk.mockbukkit.ServerMock;
import org.junit.jupiter.api.*;

import static org.junit.jupiter.api.Assertions.assertTrue;

public class HSkillsTest {

    private ServerMock server;
    private HSkills plugin;


    @BeforeAll
    static void setConfig() {
        System.setProperty("BUKKIT_PLUGIN_DB_IN_TEST_MODE", "true");
    }

    @AfterAll
    static void clearConfig() {
        System.clearProperty("BUKKIT_PLUGIN_DB_IN_TEST_MODE");
    }

    @BeforeEach
    void setUp() {
        server = MockBukkit.mock();
        plugin = MockBukkit.load(HSkills.class);
    }

    @AfterEach
    void tearDown() {
        MockBukkit.unmock();
    }

    @Test
    void pluginShouldBeEnabled() {
        assertTrue(plugin.isEnabled(), "O plugin deveria estar habilitado");
    }

}
