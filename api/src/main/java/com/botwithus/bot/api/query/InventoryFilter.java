package com.botwithus.bot.api.query;

import java.util.LinkedHashMap;
import java.util.Map;

public final class InventoryFilter {
    private final Map<String, Object> params;

    private InventoryFilter(Map<String, Object> params) {
        this.params = Map.copyOf(params);
    }

    public Map<String, Object> toParams() { return params; }

    public static Builder builder() { return new Builder(); }

    public static final class Builder {
        private final Map<String, Object> params = new LinkedHashMap<>();

        private Builder() {}

        public Builder inventoryId(int id) { params.put("inventory_id", id); return this; }
        public Builder itemId(int id) { params.put("item_id", id); return this; }
        public Builder minQuantity(int qty) { params.put("min_quantity", qty); return this; }
        public Builder nonEmpty(boolean v) { params.put("non_empty", v); return this; }
        public Builder maxResults(int max) { params.put("max_results", max); return this; }

        public InventoryFilter build() { return new InventoryFilter(params); }
    }
}
