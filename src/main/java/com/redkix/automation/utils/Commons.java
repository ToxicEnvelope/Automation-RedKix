package com.redkix.automation.utils;

import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

public class Commons {

    public static int getRandom(int min, int max) {
        return ThreadLocalRandom.current().nextInt(min, max + 1);
    }

    public static <T> T getAny(List<T> list) {
        if (list.isEmpty()) {
            throw new IllegalArgumentException("List is empty, so can't return the element");
        }

        return list.get(getRandom(0, list.size() - 1));
    }




}
