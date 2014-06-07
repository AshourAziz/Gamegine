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

    }

    public static class NoticeMessages {

        private final static String CATEGORY = "notices";

        private NoticeMessages() { }

        public final static GamegineMessageRef PLUGIN_RELOADED = new GamegineMessageRef(CATEGORY, "plugin_reloaded");

    }

    public static class PluginMessages {

        private final static String CATEGORY = "plugin";

        private PluginMessages() { }

        public final static GamegineMessageRef CMD_GAMES_LIST = new GamegineMessageRef(CATEGORY, "cmd_games_list");
        public final static GamegineMessageRef CMD_GAMES_LIST_ITEM = new GamegineMessageRef(CATEGORY, "cmd_games_list_item");
        public final static GamegineMessageRef CMD_GAMES_LIST_NONE = new GamegineMessageRef(CATEGORY, "cmd_games_list_none");
        public final static GamegineMessageRef PAGE_NOTICE = new GamegineMessageRef(CATEGORY, "page_notice");

    }

}