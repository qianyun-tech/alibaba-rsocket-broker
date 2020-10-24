package com.alibaba.rsocket.events;

import com.alibaba.rsocket.RSocketAppContext;
import com.alibaba.rsocket.ServiceLocator;
import io.cloudevents.v1.CloudEventBuilder;
import io.cloudevents.v1.CloudEventImpl;
import io.rsocket.metadata.WellKnownMimeType;

import java.net.URI;
import java.time.ZonedDateTime;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

/**
 * services hidden event: remove services from routing table
 *
 * @author leijuan
 */
public class ServicesHiddenEvent implements CloudEventSupport<ServicesHiddenEvent> {
    /**
     * app UUID
     */
    private String appId;
    /**
     * hidden services
     */
    private Set<ServiceLocator> services = new HashSet<>();

    public ServicesHiddenEvent() {
    }

    public String getAppId() {
        return appId;
    }

    public void setAppId(String appId) {
        this.appId = appId;
    }

    public Set<ServiceLocator> getServices() {
        return services;
    }

    public void setServices(Set<ServiceLocator> services) {
        this.services = services;
    }

    public void addService(ServiceLocator serviceLocator) {
        this.services.add(serviceLocator);
    }

    public static CloudEventImpl<ServicesHiddenEvent> convertServicesToCloudEvent(Collection<ServiceLocator> serviceLocators) {
        ServicesHiddenEvent servicesHiddenEvent = new ServicesHiddenEvent();
        for (ServiceLocator serviceLocator : serviceLocators) {
            servicesHiddenEvent.addService(serviceLocator);
        }
        servicesHiddenEvent.setAppId(RSocketAppContext.ID);
        return CloudEventBuilder.<ServicesHiddenEvent>builder()
                .withId(UUID.randomUUID().toString())
                .withTime(ZonedDateTime.now())
                .withSource(URI.create("app://" + RSocketAppContext.ID))
                .withType(ServicesExposedEvent.class.getCanonicalName())
                .withDataContentType(WellKnownMimeType.APPLICATION_JSON.getString())
                .withData(servicesHiddenEvent)
                .build();
    }
}
