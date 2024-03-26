/**
 *
 */
package de.klassenserver7b.k7bot.threads;

import com.sedmelluq.discord.lavaplayer.source.AudioSourceManager;
import de.klassenserver7b.k7bot.Klassenserver7bbot;
import de.klassenserver7b.k7bot.commands.slash.StableDiffusionCommand;
import de.klassenserver7b.k7bot.commands.types.ServerCommand;
import de.klassenserver7b.k7bot.sql.LiteSQL;
import de.klassenserver7b.k7bot.util.StatsCategoryUtil;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.OnlineStatus;
import net.dv8tion.jda.api.sharding.ShardManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

/**
 * @author Klassenserver7b
 */
public class ConsoleReadThread implements Runnable {

    private final Thread t;
    private final Logger log;
    private final BufferedReader reader;
    private final InputStreamReader sysinr;

    public ConsoleReadThread() {
        log = LoggerFactory.getLogger(this.getClass());

        sysinr = new InputStreamReader(System.in);
        reader = new BufferedReader(sysinr);
        t = new Thread(this, "ConsoleReadThread");
        t.start();
    }

    @Override
    public void run() {

        while (!t.isInterrupted()) {
            try {

                String line;

                if ((line = reader.readLine()) != null) {
                    interpretConsoleContent(line);
                }

            } catch (IOException e) {

                if (e.getMessage().equalsIgnoreCase("Stream closed")) {
                    t.interrupt();
                    break;
                }
                log.info("ConsoleRead Thread interrupted");
            }
        }

    }

    public void interpretConsoleContent(String s) throws IOException {

        String[] commandargs = s.split(" ");

        switch (commandargs[0].toLowerCase()) {
            case "exit", "stop" -> {

                Klassenserver7bbot.getInstance().setexit(true);
                t.interrupt();
                reader.close();
                this.onShutdown();
            }

            case "enablecommand" -> changeCommandState(true, commandargs[1]);

            case "disablecommand" -> changeCommandState(false, commandargs[1]);

            case "addaiuser" -> addAIUser(commandargs[1]);

            case "rmaiuser" -> removeAIUser(commandargs[1]);

            default -> System.out.println("Use exit/stop to Shutdown");

        }

    }

    public void changeCommandState(boolean enable, String command_OR_CommandClassName) {

        Class<?> insertedClassName = getClassFromString(command_OR_CommandClassName);

        if (insertedClassName != null) {

            try {

                if (!ServerCommand.class.isAssignableFrom(insertedClassName)) {
                    log.warn("Invalid CommandClassName");
                    return;
                }

                if (enable) {
                    Klassenserver7bbot.getInstance().getCmdMan().enableCommandsByClass(insertedClassName);
                } else {
                    Klassenserver7bbot.getInstance().getCmdMan().disableCommandsByClass(insertedClassName);
                }

            } catch (IllegalArgumentException | SecurityException e) {
                log.error(e.getMessage(), e);
            }

        } else {

            if (enable) {
                enableCommandByStr(command_OR_CommandClassName);
            } else {
                disableCommandByStr(command_OR_CommandClassName);
            }
        }
    }

    public void onShutdown() {

        log.info("Bot is shutting down!");

        ShardManager shardMgr = Klassenserver7bbot.getInstance().getShardManager();

        for (AudioSourceManager m : Klassenserver7bbot.getInstance().getAudioPlayerManager().getSourceManagers()) {
            m.shutdown();
        }

        if (shardMgr != null) {

            ArrayList<Object> listeners = new ArrayList<>();

            for (JDA jda : shardMgr.getShards()) {
                listeners.addAll(jda.getEventManager().getRegisteredListeners());
            }

            shardMgr.removeEventListener(listeners.toArray());

            Klassenserver7bbot.getInstance().stopLoop();

            Klassenserver7bbot.getInstance().getLoopedEventManager().shutdownLoopedEvents();

            StatsCategoryUtil.onShutdown(Klassenserver7bbot.getInstance().isDevMode());

            shardMgr.setStatus(OnlineStatus.OFFLINE);

            shardMgr.shutdown();
            log.info("Bot offline");

            LiteSQL.disconnect();
            t.interrupt();
            try {
                reader.close();
            } catch (IOException e) {
                log.error(e.getMessage(), e);
            }
            return;

        }

        log.info("ShardMan was null!");

    }

    protected void addAIUser(String uid) {
        Long userid = Long.valueOf(uid);
        StableDiffusionCommand.addAIUser(userid);
        log.info("successfully added " + uid + "to ai allowlist");
    }

    protected void removeAIUser(String uid) {
        Long userid = Long.valueOf(uid);
        StableDiffusionCommand.removeAIUser(userid);
        log.info("successfully removed " + uid + "from ai allowlist");
    }

    protected void disableCommandByStr(String name) {
        if (Klassenserver7bbot.getInstance().getCmdMan().disableCommand(name)) {
            log.info("successfully disabled " + name);
            return;
        }

        log.warn("failed to disable " + name);
    }

    protected void enableCommandByStr(String name) {
        if (Klassenserver7bbot.getInstance().getCmdMan().enableCommand(name)) {
            log.info("successfully enabled " + name);
            return;
        }

        log.warn("failed to enable " + name);
    }


    public Class<?> getClassFromString(String s) {
        try {
            return Class.forName(s);
        } catch (ClassNotFoundException e) {
            return null;
        }
    }

    protected static Set<Class<?>> getAllExtendedOrImplementedInterfacesRecursively(Class<?> clazz) {

        Set<Class<?>> res = new HashSet<>();
        Class<?>[] interfaces = clazz.getInterfaces();

        if (interfaces.length > 0) {
            res.addAll(Arrays.asList(interfaces));

            for (Class<?> interfaze : interfaces) {
                res.addAll(getAllExtendedOrImplementedInterfacesRecursively(interfaze));
            }
        }

        return res;
    }

}
