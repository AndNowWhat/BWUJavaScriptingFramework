package com.botwithus.bot.api.query;

import java.util.LinkedHashMap;
import java.util.Map;

public final class ComponentFilter {
    private final Map<String, Object> params;

    private ComponentFilter(Map<String, Object> params) {
        this.params = Map.copyOf(params);
    }

    public Map<String, Object> toParams() { return params; }

    public static Builder builder() { return new Builder(); }

    public static final class Builder {
        private final Map<String, Object> params = new LinkedHashMap<>();

        private Builder() {}

        public Builder interfaceId(int id) { params.put("interface_id", id); return this; }
        public Builder itemId(int id) { params.put("item_id", id); return this; }
        public Builder spriteId(int id) { params.put("sprite_id", id); return this; }
        public Builder type(int type) { params.put("type", type); return this; }
        public Builder textPattern(String pattern) { params.put("text_pattern", pattern); return this; }
        public Builder matchType(String matchType) { params.put("match_type", matchType); return this; }
        public Builder caseSensitive(boolean cs) { params.put("case_sensitive", cs); return this; }
        public Builder optionPattern(String pattern) { params.put("option_pattern", pattern); return this; }
        public Builder optionMatchType(String matchType) { params.put("option_match_type", matchType); return this; }
        public Builder visibleOnly(boolean v) { params.put("visible_only", v); return this; }
        public Builder maxResults(int max) { params.put("max_results", max); return this; }

        public ComponentFilter build() { return new ComponentFilter(params); }
    }
}
