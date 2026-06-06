package org.springframework.samples.petclinic.api.ratelimiting;

import io.github.bucket4j.Bandwidth;
import io.github.bucket4j.Bucket;
import io.github.bucket4j.Refill;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.factory.AbstractGatewayFilterFactory;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

import java.net.InetSocketAddress;
import java.time.Duration;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

@Component
public class RateLimitingGatewayFilterFactory extends AbstractGatewayFilterFactory<RateLimitingGatewayFilterFactory.Config> {

    private final RateLimitingProperties properties;
    private final ConcurrentMap<String, Bucket> buckets = new ConcurrentHashMap<>();

    public RateLimitingGatewayFilterFactory(RateLimitingProperties properties) {
        super(Config.class);
        this.properties = properties;
    }

    @Override
    public GatewayFilter apply(Config config) {
        return (exchange, chain) -> {
            if (!properties.isEnabled()) {
                return chain.filter(exchange);
            }

            InetSocketAddress remoteAddress = exchange.getRequest().getRemoteAddress();
            String ip = remoteAddress != null ? remoteAddress.getAddress().getHostAddress() : "unknown";

            Bucket bucket = buckets.computeIfAbsent(ip, k -> createBucket());

            if (bucket.tryConsume(1)) {
                return chain.filter(exchange);
            }

            exchange.getResponse().setStatusCode(HttpStatus.TOO_MANY_REQUESTS);
            return exchange.getResponse().setComplete();
        };
    }

    private Bucket createBucket() {
        Refill refill = Refill.greedy(properties.getRequestsPerMinute(), Duration.ofMinutes(1));
        Bandwidth limit = Bandwidth.classic(properties.getRequestsPerMinute(), refill);
        return Bucket.builder().addLimit(limit).build();
    }

    public static class Config {
    }
}
