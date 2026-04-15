package com.novibe.common.data_sources;

import org.springframework.stereotype.Service;

import java.util.function.Predicate;

@Service
public class HostsOverrideListsLoader extends ListLoader<HostsOverrideListsLoader.BypassRoute> {

    public record BypassRoute(String ip, String website) {}

    @Override
    protected Predicate<String> filterRelatedLines() {
        return line -> {
            // убираем inline-комментарии
            int commentIndex = line.indexOf("#");
            if (commentIndex != -1) {
                line = line.substring(0, commentIndex).trim();
            }

            if (line.isBlank()) return false;

            String[] parts = line.split("\\s+");

            // нужен формат: IP domain
            if (parts.length < 2) return false;

            String ip = parts[0];

            // игнор блокирующих IP (по твоей логике REDIRECT)
            return !ip.equals("0.0.0.0") && !ip.equals("127.0.0.1");
        };
    }

    @Override
    protected BypassRoute toObject(String line) {
        // убираем inline-комментарии (дублируем, т.к. filter не меняет строку)
        int commentIndex = line.indexOf("#");
        if (commentIndex != -1) {
            line = line.substring(0, commentIndex).trim();
        }

        String[] parts = line.split("\\s+");

        // тут уже гарантированно >= 2
        return new BypassRoute(parts[0], parts[1]);
    }

    @Override
    protected String listType() {
        return "Override";
    }
}
