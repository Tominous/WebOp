// 
// Decompiled by Procyon v0.5.36
// 

package me.jayfella.webop.datastore;

import me.jayfella.webop.PluginContext;
import me.jayfella.webop.WebOpPlugin;
import me.jayfella.webop.core.SocketSubscription;
import me.jayfella.webop.core.WebOpUser;
import org.apache.commons.lang.StringEscapeUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Marker;
import org.apache.logging.log4j.core.Filter;
import org.apache.logging.log4j.core.LogEvent;
import org.apache.logging.log4j.core.Logger;
import org.apache.logging.log4j.message.Message;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.logging.Level;

public class ConsoleMonitor extends SocketSubscription {
    private final PluginContext context;

    public ConsoleMonitor(final PluginContext context) {
        this.context = context;
        ConsoleFilter filter = new ConsoleFilter();
        ((org.apache.logging.log4j.core.Logger) LogManager.getRootLogger()).addFilter(filter);
    }

    public void executeCommand(String command, final boolean asConsole, final String username) {
        if (asConsole) {
            final boolean isOp = this.context.getPlugin().getServer().getOfflinePlayer(username).isOp();
            if (isOp | this.context.getSessionManager().canExecuteConsoleOpCommands(username)) {
                final String resp = ChatColor.DARK_GREEN + "Player " + ChatColor.GREEN + username + ChatColor.DARK_GREEN + " issued console command: " + ChatColor.GOLD + command;
                this.context.getPlugin().getLogger().log(Level.INFO, resp);

                if (command.startsWith("/")) command = command.substring(1);
                String finalCommand = command;
                Bukkit.getScheduler().callSyncMethod(context.getPlugin(), () ->
                        Bukkit.dispatchCommand(this.context.getPlugin().getServer().getConsoleSender(), finalCommand));
            } else {
                final String errorString = ChatColor.RED + "Player " + ChatColor.GREEN + username + ChatColor.RED + " tried to execute a command as console without permission: " + ChatColor.GOLD + command;
                this.context.getPlugin().getLogger().log(Level.WARNING, errorString);
            }
        } else {
            final Player player = this.context.getPlugin().getServer().getPlayerExact(username);
            if (player == null) {
                final String errorString = ChatColor.RED + "Player " + ChatColor.GREEN + username + ChatColor.RED + " tried to issue a command whilst not logged in: " + ChatColor.GOLD + command;
                this.context.getPlugin().getLogger().log(Level.WARNING, errorString);
            } else {
                if (command.startsWith("/")) {
                    String finalCommand1 = command;
                    Bukkit.getScheduler().callSyncMethod(context.getPlugin(), () ->
                            Bukkit.dispatchCommand(player, finalCommand1.substring(1)));
                } else {
                    String finalCommand2 = command;
                    Bukkit.getScheduler().runTask(context.getPlugin(), () -> player.chat(finalCommand2));
                }
            }
        }
    }

    private String parseMcColors(String string) {
        string = string

                .replace("u00A70", "<span style='color: #c0c0c0'>") // &0
                .replace("u00A71", "<span style='color: #0000aa'>") // &1
                .replace("u00A72", "<span style='color: #00aa00'>") // &2
                .replace("u00A73", "<span style='color: #00aaaa'>") // &3
                .replace("u00A74", "<span style='color: #aa0000'>") // &4
                .replace("u00A75", "<span style='color: #aa00aa'>") // &5
                .replace("u00A76", "<span style='color: #ffaa00'>") // &6
                .replace("u00A77", "<span style='color: #aaaaaa'>") // &7
                .replace("u00A78", "<span style='color: #555555'>") // &8
                .replace("u00A79", "<span style='color: #5555ff'>") // &9
                .replace("u00A7a", "<span style='color: #55ff55'>") // &A
                .replace("u00A7A", "<span style='color: #55ff55'>") // &A
                .replace("u00A7b", "<span style='color: #55ffff'>") // &b
                .replace("u00A7B", "<span style='color: #55ffff'>") // &b
                .replace("u00A7c", "<span style='color: #ff5555'>") // &c
                .replace("u00A7C", "<span style='color: #ff5555'>") // &c
                .replace("u00A7d", "<span style='color: #ff55ff'>") // &d
                .replace("u00A7D", "<span style='color: #ff55ff'>") // &d
                .replace("u00A7e", "<span style='color: #ffff55'>") // &e
                .replace("u00A7E", "<span style='color: #ffff55'>") // &e
                .replace("u00A7f", "<span style='color: #ffffff'>") // &f
                .replace("u00A7F", "<span style='color: #ffffff'>") // &f
                .replace("u00A7r", "<span style='color: #ffffff'>") // &r

                .replace("u00A7k", "") // &k
                .replace("u00A7K", "") // &k
                .replace("u00A7l", "<span style='font-weight: bold'>") // &l
                .replace("u00A7L", "<span style='font-weight: bold'>") // &l
                .replace("u00A7o", "<span style='font-style: italic'>") // &o
                .replace("u00A7O", "<span style='font-style: italic'>") // &o
                .replace("u00A7m", "<span style='text-decoration: line-through'>") // &m
                .replace("u00A7M", "<span style='text-decoration: line-through'>") // &m
                .replace("u00A7n", "<span style='text-decoration: underline'>") // &n
                .replace("u00A7M", "<span style='text-decoration: underline'>") // &n

                .replace("\\u001B[m", "<span style='color: #c0c0c0'>") // &0
                .replace("\\u001B[0;34;22m", "<span style='color: #0000aa'>") // &1
                .replace("\\u001B[0;32;22m", "<span style='color: #00aa00'>") // &2
                .replace("\\u001B[0;36;22m", "<span style='color: #00aaaa'>") // &3
                .replace("\\u001B[0;31;22m", "<span style='color: #aa0000'>") // &4
                .replace("\\u001B[0;35;22m", "<span style='color: #aa00aa'>") // &5
                .replace("\\u001B[0;33;22m", "<span style='color: #ffaa00'>") // &6
                .replace("\\u001B[0;37;22m", "<span style='color: #aaaaaa'>") // &7
                .replace("\\u001B[0;30;1m", "<span style='color: #555555'>") // &8
                .replace("\\u001B[0;34;1m", "<span style='color: #5555ff'>") // &9
                .replace("\\u001B[0;32;1m", "<span style='color: #55ff55'>") // &A
                .replace("\\u001B[0;36;1m", "<span style='color: #55ffff'>") // &b
                .replace("\\u001B[0;31;1m", "<span style='color: #ff5555'>") // &c
                .replace("\\u001B[0;35;1m", "<span style='color: #ff55ff'>") // &d
                .replace("\\u001B[0;33;1m", "<span style='color: #ffff55'>") // &e
                .replace("\\u001B[0;37;1m", "<span style='color: #ffffff'>") // &f
                .replace("\\", "") // escaped strings

                //removing acentuation
                .replaceAll("(u00E1|u00E3)", "a")
                .replaceAll("(u00C1|u00C3)", "A")
                .replaceAll("(u00E9|u00B4|u00EA)", "e")
                .replaceAll("(u00C9|u00CA)", "E")
                .replaceAll("u00CD", "I")
                .replaceAll("u00ED", "i")
                .replaceAll("(u00D3|u00D4|u00D5)", "O")
                .replaceAll("(u00F3|u00F4|u00F5)", "o")
                .replaceAll("u00DA", "U")
                .replaceAll("u00FA", "u")
                .replaceAll("u00C7", "C")
                .replaceAll("u00E7", "c")
                .replaceAll("u00F1", "n")
                .replaceAll("u00D1", "N");

        // close all spans.
        int count = 0;
        String term = "<span style=";
        int result = string.indexOf(term);
        while (result != -1) {
            result = string.indexOf(term, result + 1);
            count++;
        }
        StringBuilder stringBuilder = new StringBuilder(string);
        for (int i = 0; i < count; i++) {
            stringBuilder.append("</span>");
        }
        return stringBuilder.toString();
    }

    public class ConsoleFilter implements Filter {

        SimpleDateFormat formatter = new SimpleDateFormat("HH:mm:ss");

        @Override
        public Result getOnMismatch() {
            return null;
        }

        @Override
        public Result getOnMatch() {
            return null;
        }

        /**
         * Filter an event.
         *
         * @param logger The Logger.
         * @param level  The event logging Level.
         * @param marker The Marker for the event or null.
         * @param msg    String text to filter on.
         * @param params An array of parameters or null.
         * @return the Result.
         */
        @Override
        public Result filter(Logger logger, org.apache.logging.log4j.Level level, Marker marker, String msg, Object... params) {
            return null;
        }

        /**
         * Filter an event.
         *
         * @param logger  The Logger.
         * @param level   the event logging level.
         * @param marker  The Marker for the event or null.
         * @param message The message.
         * @param p0      the message parameters
         * @return the Result.
         */
        @Override
        public Result filter(Logger logger, org.apache.logging.log4j.Level level, Marker marker, String message, Object p0) {
            return null;
        }

        /**
         * Filter an event.
         *
         * @param logger  The Logger.
         * @param level   the event logging level.
         * @param marker  The Marker for the event or null.
         * @param message The message.
         * @param p0      the message parameters
         * @param p1      the message parameters
         * @return the Result.
         */
        @Override
        public Result filter(Logger logger, org.apache.logging.log4j.Level level, Marker marker, String message, Object p0, Object p1) {
            return null;
        }

        /**
         * Filter an event.
         *
         * @param logger  The Logger.
         * @param level   the event logging level.
         * @param marker  The Marker for the event or null.
         * @param message The message.
         * @param p0      the message parameters
         * @param p1      the message parameters
         * @param p2      the message parameters
         * @return the Result.
         */
        @Override
        public Result filter(Logger logger, org.apache.logging.log4j.Level level, Marker marker, String message, Object p0, Object p1, Object p2) {
            return null;
        }

        /**
         * Filter an event.
         *
         * @param logger  The Logger.
         * @param level   the event logging level.
         * @param marker  The Marker for the event or null.
         * @param message The message.
         * @param p0      the message parameters
         * @param p1      the message parameters
         * @param p2      the message parameters
         * @param p3      the message parameters
         * @return the Result.
         */
        @Override
        public Result filter(Logger logger, org.apache.logging.log4j.Level level, Marker marker, String message, Object p0, Object p1, Object p2, Object p3) {
            return null;
        }

        /**
         * Filter an event.
         *
         * @param logger  The Logger.
         * @param level   the event logging level.
         * @param marker  The Marker for the event or null.
         * @param message The message.
         * @param p0      the message parameters
         * @param p1      the message parameters
         * @param p2      the message parameters
         * @param p3      the message parameters
         * @param p4      the message parameters
         * @return the Result.
         */
        @Override
        public Result filter(Logger logger, org.apache.logging.log4j.Level level, Marker marker, String message, Object p0, Object p1, Object p2, Object p3, Object p4) {
            return null;
        }

        /**
         * Filter an event.
         *
         * @param logger  The Logger.
         * @param level   the event logging level.
         * @param marker  The Marker for the event or null.
         * @param message The message.
         * @param p0      the message parameters
         * @param p1      the message parameters
         * @param p2      the message parameters
         * @param p3      the message parameters
         * @param p4      the message parameters
         * @param p5      the message parameters
         * @return the Result.
         */
        @Override
        public Result filter(Logger logger, org.apache.logging.log4j.Level level, Marker marker, String message, Object p0, Object p1, Object p2, Object p3, Object p4, Object p5) {
            return null;
        }

        /**
         * Filter an event.
         *
         * @param logger  The Logger.
         * @param level   the event logging level.
         * @param marker  The Marker for the event or null.
         * @param message The message.
         * @param p0      the message parameters
         * @param p1      the message parameters
         * @param p2      the message parameters
         * @param p3      the message parameters
         * @param p4      the message parameters
         * @param p5      the message parameters
         * @param p6      the message parameters
         * @return the Result.
         */
        @Override
        public Result filter(Logger logger, org.apache.logging.log4j.Level level, Marker marker, String message, Object p0, Object p1, Object p2, Object p3, Object p4, Object p5, Object p6) {
            return null;
        }

        /**
         * Filter an event.
         *
         * @param logger  The Logger.
         * @param level   the event logging level.
         * @param marker  The Marker for the event or null.
         * @param message The message.
         * @param p0      the message parameters
         * @param p1      the message parameters
         * @param p2      the message parameters
         * @param p3      the message parameters
         * @param p4      the message parameters
         * @param p5      the message parameters
         * @param p6      the message parameters
         * @param p7      the message parameters
         * @return the Result.
         */
        @Override
        public Result filter(Logger logger, org.apache.logging.log4j.Level level, Marker marker, String message, Object p0, Object p1, Object p2, Object p3, Object p4, Object p5, Object p6, Object p7) {
            return null;
        }

        /**
         * Filter an event.
         *
         * @param logger  The Logger.
         * @param level   the event logging level.
         * @param marker  The Marker for the event or null.
         * @param message The message.
         * @param p0      the message parameters
         * @param p1      the message parameters
         * @param p2      the message parameters
         * @param p3      the message parameters
         * @param p4      the message parameters
         * @param p5      the message parameters
         * @param p6      the message parameters
         * @param p7      the message parameters
         * @param p8      the message parameters
         * @return the Result.
         */
        @Override
        public Result filter(Logger logger, org.apache.logging.log4j.Level level, Marker marker, String message, Object p0, Object p1, Object p2, Object p3, Object p4, Object p5, Object p6, Object p7, Object p8) {
            return null;
        }

        /**
         * Filter an event.
         *
         * @param logger  The Logger.
         * @param level   the event logging level.
         * @param marker  The Marker for the event or null.
         * @param message The message.
         * @param p0      the message parameters
         * @param p1      the message parameters
         * @param p2      the message parameters
         * @param p3      the message parameters
         * @param p4      the message parameters
         * @param p5      the message parameters
         * @param p6      the message parameters
         * @param p7      the message parameters
         * @param p8      the message parameters
         * @param p9      the message parameters
         * @return the Result.
         */
        @Override
        public Result filter(Logger logger, org.apache.logging.log4j.Level level, Marker marker, String message, Object p0, Object p1, Object p2, Object p3, Object p4, Object p5, Object p6, Object p7, Object p8, Object p9) {
            return null;
        }

        /**
         * Filter an event.
         *
         * @param logger The Logger.
         * @param level  The event logging Level.
         * @param marker The Marker for the event or null.
         * @param msg    Any Object.
         * @param t      A Throwable or null.
         * @return the Result.
         */
        @Override
        public Result filter(Logger logger, org.apache.logging.log4j.Level level, Marker marker, Object msg, Throwable t) {
            return null;
        }

        /**
         * Filter an event.
         *
         * @param logger The Logger.
         * @param level  The event logging Level.
         * @param marker The Marker for the event or null.
         * @param msg    The Message
         * @param t      A Throwable or null.
         * @return the Result.
         */
        @Override
        public Result filter(Logger logger, org.apache.logging.log4j.Level level, Marker marker, Message msg, Throwable t) {
            return null;
        }

        @Override
        public Result filter(LogEvent logEvent) {
            Object[] params = logEvent.getMessage().getParameters();
            String output = logEvent.getMessage().getFormattedMessage();

            if (params != null && params.length > 0) {
                for (int i = 0; i < params.length; ++i) {
                    output = output.replace("{" + i + "}", params[i].toString());
                }
            }
            output = output.replace("\t", "&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;");
            output = output.replace("\r\n", "<br/>");
            output = output.replace("\r", "<br/>");
            output = output.replace("\n", "<br/>");
            output = StringEscapeUtils.escapeJava(output);
            output = "[" + formatter.format(new Date(logEvent.getTimeMillis())) + " " + logEvent.getLevel() + "] " + output;
            output = parseMcColors(output);
            output = output.trim();

            if (output.toLowerCase().contains("issued server command: /login") ||
                    output.toLowerCase().contains("issued server command: /register") ||
                    output.toLowerCase().contains("issued server command: /changepass")) return null;

            for (final WebOpUser user : context.getSessionManager().getLoggedInUsers()) {
                if (isSubscriber(user.getName())) {
                    if (user.getWebSocketSession() == null) {
                        continue;
                    }
                    if (!user.getWebSocketSession().isOpen()) {
                        continue;
                    }
                    final String response = "case=consoleData;<span class='consoleLine'>" + output + "<br/></span>";
                    WebOpPlugin.PluginContext.getPlugin().getServer().getScheduler().runTask(WebOpPlugin.PluginContext.getPlugin(), () -> {
                        if (user.getWebSocketSession() == null || !user.getWebSocketSession().isOpen()) {
                            return;
                        }
                        try {
                            user.getWebSocketSession().getRemote().sendString(response);
                        } catch (IOException ignored) {
                        }
                    });
                }
            }
            return null;
        }

        @Override
        public State getState() {
            return null;
        }

        @Override
        public void initialize() {

        }

        @Override
        public void start() {

        }

        @Override
        public void stop() {

        }

        @Override
        public boolean isStarted() {
            return false;
        }

        @Override
        public boolean isStopped() {
            return false;
        }
    }
    private class ConsoleLogHandler extends Handler
    {
        @Override
        public synchronized void publish(final LogRecord record) {
            final Object[] params = record.getParameters();
            String output = record.getMessage();
            if (params != null && params.length > 0) {
                for (int i = 0; i < params.length; ++i) {
                    output = output.replace("{" + i + "}", params[i].toString());
                }
            }
            output = output.replace("\t", "&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;");
            output = output.replace("\r\n", "<br/>");
            output = output.replace("\r", "<br/>");
            output = output.replace("\n", "<br/>");
            output = StringEscapeUtils.escapeJava(output);
            output = "[" + record.getLevel().getName() + "] " + output;
            output = this.parseMcColors(output);
            output = output.trim();
            for (final WebOpUser user : context.getSessionManager().getLoggedInUsers()) {
                if (isSubscriber(user.getName())) {
                    if (user.getWebSocketSession() == null) {
                        continue;
                    }
                    if (!user.getWebSocketSession().isOpen()) {
                        continue;
                    }
                    final String response = "case=consoleData;<span class='consoleLine'>" + output + "<br/></span>";
                    WebOpPlugin.PluginContext.getPlugin().getServer().getScheduler().runTask((Plugin) WebOpPlugin.PluginContext.getPlugin(), (Runnable)new Runnable() {
                        @Override
                        public void run() {
                            if (user.getWebSocketSession() == null || !user.getWebSocketSession().isOpen()) {
                                return;
                            }
                            try {
                                user.getWebSocketSession().getRemote().sendString(response);
                            }
                            catch (IOException ignored) {}
                        }
                    });
                }
            }
        }
        
        @Override
        public void flush() {
        }
        
        @Override
        public void close() throws SecurityException {
        }
        
        private String parseMcColors(String string) {
            string = string.replace("u00A70", "<span style='color: #c0c0c0'>").replace("u00A71", "<span style='color: #0000aa'>").replace("u00A72", "<span style='color: #00aa00'>").replace("u00A73", "<span style='color: #00aaaa'>").replace("u00A74", "<span style='color: #aa0000'>").replace("u00A75", "<span style='color: #aa00aa'>").replace("u00A76", "<span style='color: #ffaa00'>").replace("u00A77", "<span style='color: #aaaaaa'>").replace("u00A78", "<span style='color: #555555'>").replace("u00A79", "<span style='color: #5555ff'>").replace("u00A7a", "<span style='color: #55ff55'>").replace("u00A7A", "<span style='color: #55ff55'>").replace("u00A7b", "<span style='color: #55ffff'>").replace("u00A7B", "<span style='color: #55ffff'>").replace("u00A7c", "<span style='color: #ff5555'>").replace("u00A7C", "<span style='color: #ff5555'>").replace("u00A7d", "<span style='color: #ff55ff'>").replace("u00A7D", "<span style='color: #ff55ff'>").replace("u00A7e", "<span style='color: #ffff55'>").replace("u00A7E", "<span style='color: #ffff55'>").replace("u00A7f", "<span style='color: #ffffff'>").replace("u00A7F", "<span style='color: #ffffff'>").replace("u00A7", "<span style='color: #ffffff'>").replace("\\u001B[m", "<span style='color: #c0c0c0'>").replace("\\u001B[0;34;22m", "<span style='color: #0000aa'>").replace("\\u001B[0;32;22m", "<span style='color: #00aa00'>").replace("\\u001B[0;36;22m", "<span style='color: #00aaaa'>").replace("\\u001B[0;31;22m", "<span style='color: #aa0000'>").replace("\\u001B[0;35;22m", "<span style='color: #aa00aa'>").replace("\\u001B[0;33;22m", "<span style='color: #ffaa00'>").replace("\\u001B[0;37;22m", "<span style='color: #aaaaaa'>").replace("\\u001B[0;30;1m", "<span style='color: #555555'>").replace("\\u001B[0;34;1m", "<span style='color: #5555ff'>").replace("\\u001B[0;32;1m", "<span style='color: #55ff55'>").replace("\\u001B[0;36;1m", "<span style='color: #55ffff'>").replace("\\u001B[0;31;1m", "<span style='color: #ff5555'>").replace("\\u001B[0;35;1m", "<span style='color: #ff55ff'>").replace("\\u001B[0;33;1m", "<span style='color: #ffff55'>").replace("\\u001B[0;37;1m", "<span style='color: #ffffff'>").replace("\\", "");
            int count = 0;
            final String term = "<span style=";
            for (int result = string.indexOf(term); result != -1; result = string.indexOf(term, result + 1), ++count) {}
            for (int i = 0; i < count; ++i) {
                string += "</span>";
            }
            return string;
        }
    }
}
