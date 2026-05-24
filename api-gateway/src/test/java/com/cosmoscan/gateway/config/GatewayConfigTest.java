package com.cosmoscan.gateway.config;

import org.junit.jupiter.api.Test;
import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class GatewayConfigTest {

    @Test
    @SuppressWarnings("unchecked")
    void shouldConfigureRoutes() {
        // given
        GatewayConfig config = new GatewayConfig();

        // Создаем моки для всего билдера
        RouteLocatorBuilder.Builder builderMock = mock(RouteLocatorBuilder.Builder.class);
        RouteLocatorBuilder routeLocatorBuilder = mock(RouteLocatorBuilder.class);

        when(routeLocatorBuilder.routes()).thenReturn(builderMock);

        // Создаем моки для route
        RouteLocatorBuilder.Builder routeBuilderMock = mock(RouteLocatorBuilder.Builder.class);
        when(builderMock.route(anyString(), any())).thenReturn(routeBuilderMock);
        when(routeBuilderMock.route(anyString(), any())).thenReturn(routeBuilderMock);

        RouteLocator expectedRouteLocator = mock(RouteLocator.class);
        when(routeBuilderMock.build()).thenReturn(expectedRouteLocator);

        // when
        RouteLocator routeLocator = config.customRouteLocator(routeLocatorBuilder);

        // then
        assertThat(routeLocator).isNotNull();
    }
}