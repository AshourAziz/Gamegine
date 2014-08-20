/*
 * Gamegine - Game compatibility API and creation library for Bukkit
 * Copyright (C) 2013-2014 Stealth2800 <stealth2800@stealthyone.com>
 * Website: <http://stealthyone.com/>
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package com.stealthyone.mcb.gamegine.messages;

public class Messages {

    private Messages() { }

    public static class AlertMessages {

        private final static String CATEGORY = "alerts";

        private AlertMessages() { }


    }

    public static class ErrorMessages {

        private final static String CATEGORY = "errors";

        private ErrorMessages() { }

        public final static GamegineMessageRef NO_PERMISSION = new GamegineMessageRef(CATEGORY, "no_permission");
        public final static GamegineMessageRef RELOAD_ERROR = new GamegineMessageRef(CATEGORY, "reload_error");
        public final static GamegineMessageRef SAVE_ERROR = new GamegineMessageRef(CATEGORY, "save_error");
        public final static GamegineMessageRef UNKNOWN_COMMAND = new GamegineMessageRef(CATEGORY, "unknown_command");

    }

    public static class NoticeMessages {

        private final static String CATEGORY = "notices";

        private NoticeMessages() { }

        public final static GamegineMessageRef PLUGIN_RELOADED = new GamegineMessageRef(CATEGORY, "plugin_reloaded");
        public final static GamegineMessageRef PLUGIN_SAVED = new GamegineMessageRef(CATEGORY, "plugin_saved");

    }

    public static class PluginMessages {

        private final static String CATEGORY = "plugin";

        private PluginMessages() { }

        public final static GamegineMessageRef CMD_GAMES_LIST = new GamegineMessageRef(CATEGORY, "cmd_games_list");
        public final static GamegineMessageRef CMD_GAMES_LIST_ITEM = new GamegineMessageRef(CATEGORY, "cmd_games_list_item");
        public final static GamegineMessageRef CMD_GAMES_LIST_NONE = new GamegineMessageRef(CATEGORY, "cmd_games_list_none");
        public final static GamegineMessageRef PAGE_NOTICE = new GamegineMessageRef(CATEGORY, "page_notice");

    }

    public static class UsageMessages {

        private final static String CATEGORY = "usages";

        private UsageMessages() { }

        public final static GamegineMessageRef GAMEGINE_HELP = new GamegineMessageRef(CATEGORY, "gamegine_help");
        public final static GamegineMessageRef GAMES_HELP = new GamegineMessageRef(CATEGORY, "games_help");

    }

}