package com.stealthyone.mcb.gamegine.backend.hooks;

import com.stealthyone.mcb.gamegine.GameginePlugin;
import com.stealthyone.mcb.gamegine.api.hooks.Hook;
import com.stealthyone.mcb.gamegine.api.hooks.HookManager;
import com.stealthyone.mcb.gamegine.api.logging.GamegineLogger;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

@RequiredArgsConstructor
public class GgHookManager implements HookManager {

    private final GameginePlugin plugin;

    /* All loaded hooks. */
    private Map<Class<? extends Hook>, Hook> hooks = new HashMap<>();

    /* Index of hook names to their respective classes. */
    private Map<String, Class<? extends Hook>> hookNameIndex = new HashMap<>();

    /* Set of hooks that failed to load properly. */
    private Set<Class<? extends Hook>> disabledHooks = new HashSet<>();

    @Override
    public <T extends Hook> T getHook(@NonNull Class<T> hookClass) {
        return (T) hooks.get(hookClass);
    }

    @Override
    public boolean isEnabled(@NonNull Hook hook) {
        return isEnabled(hook.getClass());
    }

    @Override
    public boolean isEnabled(@NonNull Class<? extends Hook> hookClass) {
        return hooks.containsKey(hookClass) && !disabledHooks.contains(hookClass);
    }

    @Override
    public boolean isEnabled(@NonNull String hookName) {
        Class<? extends Hook> clazz = hookNameIndex.get(hookName.toLowerCase());
        return clazz != null && isEnabled(clazz);
    }

    @Override
    public void registerHook(@NonNull Hook hook) {
        hooks.put(hook.getClass(), hook);
        hookNameIndex.put(hook.getName(), hook.getClass());
        try {
            hook.load();
            GamegineLogger.info("Successfully loaded hook '" + hook.toString() + "'");
        } catch (Exception ex) {
            GamegineLogger.warning("Failed to load hook '" + hook.toString() + "': " + ex.getMessage());
            disabledHooks.add(hook.getClass());
        }
    }

}