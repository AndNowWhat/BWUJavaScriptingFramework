package com.botwithus.bot.api.query;

import java.util.LinkedHashMap;
import java.util.Map;

public final class EntityFilter {
    private final Map<String, Object> params;

    private EntityFilter(Map<String, Object> params) {
        this.params = Map.copyOf(params);
    }

    public Map<String, Object> toParams() { return params; }

    public static Builder builder() { return new Builder(); }

    public static final class Builder {
        private final Map<String, Object> params = new LinkedHashMap<>();

        private Builder() {}

        public Builder type(String type) { params.put("type", type); return this; }
        public Builder typeId(int id) { params.put("type_id", id); return this; }
        public Builder nameHash(int hash) { params.put("name_hash", hash); return this; }
        public Builder namePattern(String pattern) { params.put("name_pattern", pattern); return this; }
        public Builder matchType(String matchType) { params.put("match_type", matchType); return this; }
        public Builder caseSensitive(boolean cs) { params.put("case_sensitive", cs); return this; }
        public Builder plane(int plane) { params.put("plane", plane); return this; }
        public Builder tileX(int x) { params.put("tile_x", x); return this; }
        public Builder tileY(int y) { params.put("tile_y", y); return this; }
        public Builder radius(int radius) { params.put("radius", radius); return this; }
        public Builder visibleOnly(boolean v) { params.put("visible_only", v); return this; }
        public Builder movingOnly(boolean v) { params.put("moving_only", v); return this; }
        public Builder stationaryOnly(boolean v) { params.put("stationary_only", v); return this; }
        public Builder inCombat(boolean v) { params.put("in_combat", v); return this; }
        public Builder notInCombat(boolean v) { params.put("not_in_combat", v); return this; }
        public Builder sortByDistance(boolean v) { params.put("sort_by_distance", v); return this; }
        public Builder maxResults(int max) { params.put("max_results", max); return this; }

        public EntityFilter build() { return new EntityFilter(params); }
    }
}
