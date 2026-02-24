package com.demo.msaccounts;

import com.intuit.karate.junit5.Karate;
import org.junit.jupiter.api.Tag;

@Tag("integration")
public class KarateRunner {

    @Karate.Test
    Karate testAccounts() {
        return Karate.run("accounts").relativeTo(getClass());
    }

    @Karate.Test
    Karate testMovements() {
        return Karate.run("movements").relativeTo(getClass());
    }

    @Karate.Test
    Karate testReports() {
        return Karate.run("reports").relativeTo(getClass());
    }
}

