package de.dorsax.ShountDown.Bungee;

import net.md_5.bungee.api.ProxyServer;

public class ShutdownManager {
    private static int i_type;
    private static String s_comment;
    private static boolean b_whitelist;
    public ShutdownManager() {
        //intentionally left empty, because most things are static
    }

    public static void setType (int type) {
        i_type = type;
    }

    public static void setComment (String comment) {
        s_comment = comment;
    }

    public static void setWhitelist (boolean whitelist) {
        b_whitelist = whitelist;
    }


    public void run() {
        if (s_comment.isEmpty()) {
            ProxyServer.getInstance().stop();
        } else {
            ProxyServer.getInstance().stop(s_comment);
        }
    }
}
