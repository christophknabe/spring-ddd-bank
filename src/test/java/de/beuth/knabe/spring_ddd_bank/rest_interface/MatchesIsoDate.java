package de.beuth.knabe.spring_ddd_bank.rest_interface;

import org.hamcrest.Description;
import org.hamcrest.Factory;
import org.hamcrest.Matcher;
import org.hamcrest.TypeSafeMatcher;

public class MatchesIsoDate extends TypeSafeMatcher<String> {

    public void describeTo(Description description) {
        description.appendText("an ISO date string like 1999-12-31");
    }

    @Factory
    public static Matcher<String> isAnIsoDateString() {
        return new MatchesIsoDate();
    }

    @Override
    protected boolean matchesSafely(String item) {
        return item.matches("[12][0-9][0-9][0-9]-[0-9][0-9]-[0-9][0-9]");
    }
}
