package com.alibaba.rsocket.route;

import com.alibaba.rsocket.RSocketExchange;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;

/**
 * rsocket filter chain
 *
 * @author leijuan
 */
public class RSocketFilterChain {
    private boolean filtersPresent;
    private Flux<RSocketFilter> filterFlux;

    public RSocketFilterChain(List<RSocketFilter> filters) {
        if (filters != null && !filters.isEmpty()) {
            this.filtersPresent = true;
            this.filterFlux = Flux.fromIterable(filters);
        }
    }

    public boolean isFiltersPresent() {
        return this.filtersPresent;
    }

    public Mono<Void> filter(RSocketExchange rsocketExchange) {
        return filterFlux
                .filterWhen(rsocketFilter -> rsocketFilter.shouldFilter(rsocketExchange))
                .concatMap(rsocketFilter -> rsocketFilter.run(rsocketExchange))
                .then();
    }

}
