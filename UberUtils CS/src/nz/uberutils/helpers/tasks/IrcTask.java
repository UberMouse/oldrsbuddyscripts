package nz.uberutils.helpers.tasks;

import com.rsbuddy.script.methods.Environment;
import com.rsbuddy.script.task.LoopTask;
import nz.uberutils.irc.Client;
import nz.uberutils.irc.gui.Gui;
import nz.uberutils.irc.pircbot.IrcException;

import java.io.IOException;

/**
 * Created by IntelliJ IDEA.
 * User: Taylor
 * Date: 6/4/11
 * Time: 8:40 PM
 * Package: nz.uberutils.helpers.tasks;
 */
public class IrcTask extends LoopTask
{
    private final  String  server;
    private final  String  channel;
    private final  String  name;
    private static IrcTask instance;

    public static IrcTask instance() {
        return instance;
    }

    public IrcTask(String server, String channel, String name) {
        this.server = server;
        this.channel = channel;
        this.name = name;
        instance = this;
    }

    public IrcTask(String channel, String name) {
        this("irc.strictfp.com", channel, name);
    }

    public IrcTask(String channel) {
        this("irc.strictfp.com", channel, Environment.getUsername());
    }

    @Override
    public int loop() {
        return 100;
    }

    public void onFinish() {
        if (Client.instance() != null)
            Client.instance().closeGui();
    }

    public void openGui() {
        if (Client.instance() != null)
            Client.instance().showGui();
    }

    public void connect() {
        if (Client.instance() == null) {
            new Thread(new Runnable()
            {
                public void run() {
                    try {
                        new Client(server, channel, name);
                    } catch (IOException e) {
                        e.printStackTrace();
                    } catch (IrcException e) {
                        e.printStackTrace();
                    }
                }
            }).start();
            while (Client.instance() == null)
                sleep(50);
        }
        Client.instance().showGui();
        if(Client.instance().isConnected())
            return;
        try {
            while (Gui.instance() == null || Gui.instance().userListPane == null)
                sleep(50);
            Client.instance().join();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (IrcException e) {
            e.printStackTrace();
        }
    }

    public void clearGUI() {
        if (Gui.instance() != null) {
            Gui.instance().userListPane.setText("");
            Gui.instance().contentPane.setText("");
        }
    }
}
