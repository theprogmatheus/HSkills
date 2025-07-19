package com.github.theprogmatheus.mc.plugin.spigot.plugintemplate.lib.guibuilder;

import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;
import java.util.Map;

@RequiredArgsConstructor
@Data
public class Button {

    private final ItemStack itemStack;
    private final Map<ClickType, ClickExecutor> executors = new HashMap<>();
    private ClickExecutor defaultExecutor;

    public void setExecutor(ClickType clickType, ClickExecutor runnable) {
        this.executors.put(clickType, runnable);
    }

    public ClickExecutor getExecutor(ClickType clickType) {
        return getExecutor(clickType, true);
    }

    public ClickExecutor getExecutor(ClickType clickType, boolean returnDefaultIfNull) {
        var executor = executors.get(clickType);

        if (executor != null || !returnDefaultIfNull)
            return executor;

        if (this.defaultExecutor != null)
            return this.defaultExecutor;

        return executors.values().stream().findFirst().orElse(null);
    }

}
