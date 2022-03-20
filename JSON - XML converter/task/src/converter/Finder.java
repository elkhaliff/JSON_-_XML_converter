package converter;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Finder {
    private class FindMatcher {
        private final Matcher matcher;

        public FindMatcher(Pattern pattern) {
            matcher = pattern
                    .matcher(source)
                    .useAnchoringBounds(true);
        }

        public boolean find() {
            if (check()) {
                cursor = matcher.end();
                return true;
            }
            return false;
        }

        public boolean check() {
            return matcher.region(cursor, source.length()).find();
        }

        public Matcher getMatcher() {
            return matcher;
        }
    }

    private int cursor;
    private Matcher matcher;
    private final String source;
    private final Map<Pattern, FindMatcher> findMatchers = new HashMap<>();

    public Finder(String source) {
        this(source, 0);
    }

    public Finder(String source, int cursor) {
        this.source = source;
        this.cursor = cursor;
    }

    private FindMatcher getFindMatcher(Pattern pattern) {
        if (!findMatchers.containsKey(pattern)) {
            findMatchers.put(pattern, new FindMatcher(pattern));
        }
        return findMatchers.get(pattern);
    }

    public Matcher getMatcher() {
        return matcher;
    }

    public boolean next(Pattern pattern) {
        FindMatcher findMatcher = getFindMatcher(pattern);
        matcher = findMatcher.find() ? findMatcher.getMatcher() : null;
        return matcher != null;
    }

    public boolean next(String pattern) {
        FindMatcher findMatcher = new FindMatcher(Pattern.compile(pattern));
        matcher = findMatcher.find() ? findMatcher.getMatcher() : null;
        return matcher != null;
    }

    public boolean check(Pattern pattern) {
        FindMatcher findMatcher = getFindMatcher(pattern);
        matcher = findMatcher.check() ? findMatcher.getMatcher() : null;
        return matcher != null;
    }
}