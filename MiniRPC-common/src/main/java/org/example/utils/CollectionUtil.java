package org.example.utils;

import java.util.Collection;

public class CollectionUtil {
    public static boolean isEmpty(final Collection<?> collection) { return (collection == null || collection.isEmpty()); }
}
