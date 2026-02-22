package com.partharoypc.adglide.util;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class WaterfallManager {
    private final List<String> networks = new ArrayList<>();
    private int currentIndex = 0;
    private final AdLoadStrategy strategy;

    public WaterfallManager() {
        this.strategy = AdLoadStrategy.getInstance();
    }

    public WaterfallManager(String... networks) {
        this();
        this.networks.addAll(Arrays.asList(networks));
    }

    public WaterfallManager(List<String> networks) {
        this();
        if (networks != null) {
            this.networks.addAll(networks);
        }
    }

    /**
     * Adds a network to the waterfall if it's not already present.
     */
    public WaterfallManager addNetwork(String network) {
        if (network != null && !network.isEmpty() && !networks.contains(network)) {
            networks.add(network);
        }
        return this;
    }

    /**
     * Checks if there are more ad networks to try.
     */
    public boolean hasNext() {
        return currentIndex < networks.size();
    }

    /**
     * Gets the next available ad network, skipping those in cooldown.
     *
     * @return The next network ID, or null if none available.
     */
    public String getNext() {
        while (currentIndex < networks.size()) {
            String network = networks.get(currentIndex++);
            if (strategy.isNetworkAvailable(network)) {
                return network;
            }
        }
        return null;
    }

    /**
     * Resets the waterfall to the beginning.
     */
    public void reset() {
        currentIndex = 0;
    }

    /**
     * Returns the full list of networks in this waterfall.
     */
    public List<String> getNetworks() {
        return networks;
    }
}
