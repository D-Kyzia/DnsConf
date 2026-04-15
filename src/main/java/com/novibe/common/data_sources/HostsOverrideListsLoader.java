package com.novibe.common.data_sources;

import org.springframework.stereotype.Service;

import java.util.Objects;
import java.util.regex.Pattern;
import java.util.stream.Stream;

@Service
public class HostsOverrideListsLoader extends ListLoader<HostsOverrideListsLoader.BypassRoute> {

    public record BypassRoute(String ip, String website) {
    }

    @Override
    protected Stream<BypassRoute> lineParser(String urlList) {
        return Pattern.compile("\\r?\\n").splitAsStream(urlList)
                .parallel()
                .map(String::strip)
                .filter(str -> !str.isBlank())
                .filter(line -> !line.startsWith("#"))
                .map(this::mapLine)
                .filter(Objects::nonNull);
    }

    @Override
    protected String listType() {
        return "Override";
    }

    private BypassRoute mapLine(String line) {
        // убираем inline-комментарии
        int commentIndex = line.indexOf("#");
        if (commentIndex != -1) {
            line = line.substring(0, commentIndex).trim();
        }

        if (line.isBlank()) {
            return null;
        }

        String[] parts = line.split("\\s+");

        // формат: "IP domain"
        if (parts.length >= 2) {
            return new BypassRoute(parts[0], parts[1]);
        }

        // формат: "domain" (как в GeoHideDNS)
        if (parts.length == 1) {
            return new BypassRoute("0.0.0.0", parts[0]);
        }

        return null;
    }
}
