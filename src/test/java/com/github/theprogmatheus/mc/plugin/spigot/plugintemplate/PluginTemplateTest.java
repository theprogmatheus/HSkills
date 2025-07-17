package com.github.theprogmatheus.mc.plugin.spigot.plugintemplate;

import be.seeseemelk.mockbukkit.MockBukkit;
import be.seeseemelk.mockbukkit.ServerMock;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class PluginTemplateTest {

    private ServerMock server;
    private PluginTemplate plugin;

    @BeforeEach
    void setUp() {
        // Cria um servidor fake
        server = MockBukkit.mock();

        // Carrega seu plugin dentro do servidor fake
        plugin = MockBukkit.load(PluginTemplate.class);
    }

    @AfterEach
    void tearDown() {
        // Finaliza o servidor mockado e descarrega o plugin
        MockBukkit.unmock();
    }

    @Test
    void pluginShouldBeEnabled() {
        assertTrue(plugin.isEnabled(), "O plugin deveria estar habilitado");
    }

    @Test
    void pluginShouldHaveCorrectName() {
        assertEquals("PluginTemplate", plugin.getName(), "Nome do plugin errado");
    }
}
