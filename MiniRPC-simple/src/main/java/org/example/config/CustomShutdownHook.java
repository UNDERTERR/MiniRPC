package org.example.config;

import lombok.extern.slf4j.Slf4j;

/**
 * TODO最后再写
 */
@Slf4j
public class CustomShutdownHook {
    private static final CustomShutdownHook  CUSTOM_SHUTDOWN_HOOK= new CustomShutdownHook();
    public static CustomShutdownHook getCustomShutdownHook() {
        return CUSTOM_SHUTDOWN_HOOK;
    }

}
