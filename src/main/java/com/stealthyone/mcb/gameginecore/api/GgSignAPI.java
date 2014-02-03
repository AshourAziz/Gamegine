package com.stealthyone.mcb.gameginecore.api;

import com.stealthyone.mcb.gameginecore.Gamegine;
import com.stealthyone.mcb.gameginecore.backend.signs.GgSign;
import org.apache.commons.lang.Validate;

public class GgSignAPI {

    /**
     * Registers a sign type. Must be done in your game's constructor method.
     *
     * @param clazz Sign class to register.
     * @return True if successful
     *         False if unsuccessful or signs are disabled for the Gamegine instance.
     */
    public static boolean registerSignType(Class<? extends GgSign> clazz) {
        Validate.notNull(clazz, "Class cannot be null.");
        return Gamegine.getInstance().getSignManager().registerSignType(clazz);
    }

}