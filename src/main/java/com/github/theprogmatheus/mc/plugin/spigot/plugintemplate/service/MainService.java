package com.github.theprogmatheus.mc.plugin.spigot.plugintemplate.service;

import com.github.theprogmatheus.mc.plugin.spigot.plugintemplate.core.AbstractService;
import com.github.theprogmatheus.mc.plugin.spigot.plugintemplate.lib.Injector;
import lombok.RequiredArgsConstructor;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.function.ToIntFunction;

@RequiredArgsConstructor(onConstructor_ = @Inject)
@Singleton
public class MainService extends AbstractService {

    private final Injector injector;
    private final List<AbstractService> services = new ArrayList<>();


    /**
     * Register your services here
     */
    public void setupServices() {
        addService(CommandService.class);
        addService(DatabaseService.class, 10, -10);
        // all services here
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
        orderedServices(AbstractService::getStartupPriority)
                .forEach(AbstractService::startup);
    }

    private void shutdownServices() {
        orderedServices(AbstractService::getShutdownPriority)
                .forEach(AbstractService::shutdown);
    }


    private List<AbstractService> orderedServices(ToIntFunction<? super AbstractService> keyExtractor) {
        var services = new ArrayList<>(this.services);
        services.sort(Comparator.comparingInt(keyExtractor)
                .reversed());
        return services;
    }

    private void addService(Class<? extends AbstractService> serviceClass) {
        addService(serviceClass, 1, 1);
    }

    private void addService(Class<? extends AbstractService> serviceClass, int startupPriority, int shutdownPriority) {
        var service = this.injector.getInstance(serviceClass);

        service.setStartupPriority(startupPriority);
        service.setShutdownPriority(shutdownPriority);

        this.services.add(service);
    }


}
