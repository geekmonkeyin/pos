package com.gkmonk.pos.registry.ai;

import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Component;
import org.yaml.snakeyaml.Yaml;

import java.io.InputStream;
import java.util.*;

@Component
public class CollectionRegistry {

    public record CollectionMeta(String name, String description, Map<String, String> fields) {}

    private final List<CollectionMeta> collections;

    public CollectionRegistry() {
        this.collections = load();
    }

    public List<CollectionMeta> list() { return collections; }

    public Optional<CollectionMeta> get(String name) {
        return collections.stream().filter(c -> c.name().equalsIgnoreCase(name)).findFirst();
    }

    @SuppressWarnings("unchecked")
    private List<CollectionMeta> load() {
        try (InputStream in = new ClassPathResource("collections.yml").getInputStream()) {
            Yaml yaml = new Yaml();
            Map<String, Object> root = yaml.load(in);
            List<Map<String, Object>> items = (List<Map<String, Object>>) root.get("collections");
            List<CollectionMeta> out = new ArrayList<>();
            for (Map<String, Object> it : items) {
                String name = (String) it.get("name");
                String desc = (String) it.get("description");
                Map<String, String> fields = (Map<String, String>) it.getOrDefault("fields", Map.of());
                out.add(new CollectionMeta(name, desc, fields));
            }
            return out;
        } catch (Exception e) {
            throw new RuntimeException("Failed to load collections.yml", e);
        }
    }
}
