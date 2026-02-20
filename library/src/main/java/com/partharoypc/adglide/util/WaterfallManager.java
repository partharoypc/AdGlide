package com.partharoypc.adglide.util;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class WaterfallManager {
    private final List<String> networks;
    private int currentIndex;

    public WaterfallManager(List<String> networks) {
        this.networks = new ArrayList<>(networks);
        this.currentIndex = 0;
    }

    public WaterfallManager(String... networks) {
        this.networks = new ArrayList<>(Arrays.asList(networks));
        this.currentIndex = 0;
    }

    public boolean hasNext() {
        return currentIndex < networks.size();
    }

    public String getNext() {
        if (hasNext()) {
            return networks.get(currentIndex++);
        }
        return null;
    }

    public void reset() {
        currentIndex = 0;
    }

    public List<String> getNetworks() {
        return networks;
    }
}

