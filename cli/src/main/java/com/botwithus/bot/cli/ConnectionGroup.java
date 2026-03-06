package com.botwithus.bot.cli;

import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.Set;

public class ConnectionGroup {

    private final String name;
    private final Set<String> connectionNames = new LinkedHashSet<>();

    public ConnectionGroup(String name) {
        this.name = name;
    }

    public String getName() { return name; }

    public void add(String connectionName) {
        connectionNames.add(connectionName);
    }

    public void remove(String connectionName) {
        connectionNames.remove(connectionName);
    }

    public boolean contains(String connectionName) {
        return connectionNames.contains(connectionName);
    }

    public Set<String> getConnectionNames() {
        return Collections.unmodifiableSet(connectionNames);
    }
}
