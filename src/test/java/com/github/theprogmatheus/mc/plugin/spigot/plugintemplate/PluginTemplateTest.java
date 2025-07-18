package com.github.theprogmatheus.mc.plugin.spigot.plugintemplate;

import be.seeseemelk.mockbukkit.MockBukkit;
import be.seeseemelk.mockbukkit.ServerMock;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertTrue;

public class PluginTemplateTest {

    private ServerMock server;
    private PluginTemplate plugin;

    @BeforeEach
    void setUp() {
        server = MockBukkit.mock();
        plugin = MockBukkit.load(PluginTemplate.class);
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
