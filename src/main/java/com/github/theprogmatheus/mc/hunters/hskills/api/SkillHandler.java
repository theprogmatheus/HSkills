package com.github.theprogmatheus.mc.hunters.hskills.api;

import lombok.Data;
import org.bukkit.event.Event;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import java.util.function.Consumer;

@Data
public class SkillHandler<E extends Event> implements Listener {

    private final Class<E> eventClass;
    private final Consumer<E> eventProcessor;

    @EventHandler
    public void onEvent(E event) {
        if (this.eventClass.isInstance(event)) {
            this.eventProcessor.accept(this.eventClass.cast(event));
        }
    }

}
