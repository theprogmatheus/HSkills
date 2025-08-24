package com.github.theprogmatheus.mc.hunters.hskills.service;

import com.github.theprogmatheus.mc.hunters.hskills.lib.PluginService;
import lombok.RequiredArgsConstructor;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.ToIntFunction;


@RequiredArgsConstructor
public class MainService extends PluginService {

    private static final Map<Class<?>, PluginService> services = new ConcurrentHashMap<>();

    private final JavaPlugin plugin;

    /**
     * Register your services here
     */
    public void setupServices() {
        addService(new ConfigurationService(this.plugin, this.plugin.getLogger()), 10, 0);
        addService(new MessageService(this.plugin, this.plugin.getLogger()), 9, 0);
        addService(new DatabaseSQLService(this.plugin), 8, 0);
        addService(new CommandService(this.plugin));
        addService(new ListenerService(this.plugin));
        addService(new APIService(this.plugin));
    }

    @Override
    public void startup() {
        setupServices();
        startupServices();
    }

    @Override
    public void shutdown() {
        shutdownServices();
    }

    private void startupServices() {
        orderedServices(PluginService::getStartupPriority)
                .forEach(PluginService::startup);
    }

    private void shutdownServices() {
        orderedServices(PluginService::getShutdownPriority)
                .forEach(PluginService::shutdown);
    }


    private List<PluginService> orderedServices(ToIntFunction<? super PluginService> keyExtractor) {
        var services = new ArrayList<>(MainService.services.values());
        services.sort(Comparator.comparingInt(keyExtractor)
                .reversed());
        return services;
    }

    private void addService(PluginService service) {
        addService(service, 1, 1);
    }

    private void addService(PluginService service, int startupPriority, int shutdownPriority) {
        service.setStartupPriority(startupPriority);
        service.setShutdownPriority(shutdownPriority);
        services.put(service.getClass(), service);
    }

    public static <S extends PluginService> S getService(Class<S> serviceClass) {
        return (S) services.get(serviceClass);
    }

}
