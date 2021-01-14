package se.edinjakupovic.server.api;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;
import lombok.Value;

import java.util.UUID;

@Value
@Builder(toBuilder = true)
@AllArgsConstructor
@NoArgsConstructor(force = true, access = AccessLevel.PRIVATE)
public class Report {
    UUID id;
    boolean good;
    String name;
    int age;
    String a;
    String b;
    String c;
}
