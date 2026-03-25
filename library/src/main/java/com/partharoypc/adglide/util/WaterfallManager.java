package com.partharoypc.adglide.util;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class WaterfallManager {
    private final List<String> networks;
    private int currentIndex;

    public WaterfallManager() {
        this.networks = new ArrayList<>();
        this.currentIndex = 0;
    }

    public WaterfallManager(List<String> networks) {
        this.networks = new ArrayList<>(networks);
        this.currentIndex = 0;
    }

    public WaterfallManager(String... networks) {
        this.networks = new ArrayList<>(Arrays.asList(networks));
        this.currentIndex = 0;
    }

    public synchronized boolean hasNext() {
        return currentIndex < networks.size();
    }

    public synchronized String getNext() {
        if (currentIndex < networks.size()) {
            return networks.get(currentIndex++);
        }
        return null;
    }

    public synchronized void setNetworks(List<String> networks) {
        this.networks.clear();
        if (networks != null) {
            this.networks.addAll(networks);
        }
        this.currentIndex = 0;
    }

    public synchronized void reset() {
        currentIndex = 0;
    }

    public synchronized List<String> getNetworks() {
        return new ArrayList<>(networks);
    }
}
