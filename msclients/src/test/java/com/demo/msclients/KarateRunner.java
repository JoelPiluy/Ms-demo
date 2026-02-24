package com.demo.msclients;

import com.intuit.karate.junit5.Karate;
import org.junit.jupiter.api.Tag;

@Tag("integration")
public class KarateRunner {

    @Karate.Test
    Karate testClients() {
        return Karate.run("clients").relativeTo(getClass());
    }
}
