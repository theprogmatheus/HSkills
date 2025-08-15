package com.github.theprogmatheus.mc.hunters.hskills.config;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

@RequiredArgsConstructor
@Getter
@Setter
public class ConfigurationHolder<T> {

    private final String path;
    private final Class<T> type;
    private Object value;

    public T getValue() {
        return this.type.cast(this.value);
    }
}
