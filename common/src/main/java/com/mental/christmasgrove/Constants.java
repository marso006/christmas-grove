package com.mental.christmasgrove;

import net.minecraft.resources.ResourceLocation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class Constants {

    public static final String MOD_ID = "christmas_grove";
    public static final String MOD_NAME = "Christmas Grove";
    public static final Logger LOG = LoggerFactory.getLogger(MOD_NAME);

    private Constants() {
    }

    /** Builds a {@code christmas_grove:<path>} id. */
    public static ResourceLocation id(String path) {
        return ResourceLocation.fromNamespaceAndPath(MOD_ID, path);
    }
}
