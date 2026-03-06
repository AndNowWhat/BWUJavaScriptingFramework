package com.botwithus.bot.api;

import java.util.Collection;
import java.util.Optional;
import java.util.Set;

/**
 * Provides access to all connected game clients.
 */
public interface ClientProvider {

    Optional<Client> getClient(String name);

    Collection<Client> getClients();

    Set<String> getClientNames();
}
