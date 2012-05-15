package nz.uberutils.arte;

import com.rsbuddy.event.events.MessageEvent;
import com.rsbuddy.script.methods.Players;
import com.rsbuddy.script.task.LoopTask;
import com.rsbuddy.script.util.Timer;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.reflect.Method;
import java.net.URL;
import java.util.logging.Logger;

public class ArteNotifier extends LoopTask {

	final Logger log = Logger.getLogger("ArteNotifier");

    private static String feedbackUrl = "http://rsbuddy.com/forum/showthread.php?t=";
    private static String purchaseUrl = "http://artebots.com";
    private static boolean isLiteScript;
    private static String messageOfTheDay = "No news from Artemis Productions today!";
    private static String messageUrl = "http://artebots.com";
    private static final String artebots = "http://artebots.com";
    private static TrayIcon trayIcon = null;
    private static long startMillis;
    private static boolean gns = false, gnu = false;
    private static ActionListener iconListener;
    private static final Timer hTimer = new Timer(3600000), bTimer = new Timer(1800000);

    static boolean[] notifs = new boolean[6];
    String skill, lvl, sender;
    boolean leveled = false, died = false, name = false;
    int hrs, bhrs;

    static CheckboxMenuItem lItem = null;
    static CheckboxMenuItem hItem = null;
    static CheckboxMenuItem bItem = null;
    static CheckboxMenuItem nItem = null;
    static CheckboxMenuItem dItem = null;
    static CheckboxMenuItem oItem = null;

    public static class Notifiers {
        static final int LEVEL_UPS = 0,
                HOURLY = 1,
                BIHOURLY = 2,
                NAME_HEARD = 3,
                DEATHS = 4,
                ON_FINISH = 5;
    }

    public ArteNotifier(int threadID,
                        String purchaseURL,
                        boolean lite,
                        boolean onLevelUp,
                        boolean onHourly,
                        boolean onHalfHourly,
                        boolean onNameHeard,
                        boolean onDeath,
                        boolean onFinish) {
        feedbackUrl = feedbackUrl + String.valueOf(threadID);
        ArteNotifier.purchaseUrl = purchaseURL;
        isLiteScript = lite;
        setNotifs(onLevelUp, onHourly, onHalfHourly, onNameHeard, onDeath, onFinish);
    }

    public ArteNotifier(int threadID) {
        this(threadID, "", false, false, false, false, false, false, false);
    }

    public ArteNotifier(int threadID,
                        boolean onLevelUp,
                        boolean onHourly,
                        boolean onHalfHourly,
                        boolean onNameHeard,
                        boolean onDeath,
                        boolean onFinish) {
        this(threadID, "", false, onLevelUp, onHourly, onHalfHourly, onNameHeard, onDeath, onFinish);
    }

    public ArteNotifier(int threadID, boolean onLevelUp) {
        this(threadID, "", false, onLevelUp, false, false, false, false, false);
    }

    public ArteNotifier(String feedbackURL,
                        String purchaseURL,
                        boolean lite,
                        boolean onLevelUp,
                        boolean onHourly,
                        boolean onHalfHourly,
                        boolean onNameHeard,
                        boolean onDeath,
                        boolean onFinish) {
        ArteNotifier.feedbackUrl = feedbackURL;
        ArteNotifier.purchaseUrl = purchaseURL;
        isLiteScript = lite;
        setNotifs(onLevelUp, onHourly, onHalfHourly, onNameHeard, onDeath, onFinish);
    }

    public ArteNotifier(String feedbackURL) {
        this(feedbackURL, "", false, false, false, false, false, false, false);
    }

    public ArteNotifier(String feedbackURL,
                        boolean onLevelUp,
                        boolean onHourly,
                        boolean onHalfHourly,
                        boolean onNameHeard,
                        boolean onDeath,
                        boolean onFinish) {
        this(feedbackURL, "", false, onLevelUp, onHourly, onHalfHourly, onNameHeard, onDeath, onFinish);
    }

    public ArteNotifier(String feedbackURL, boolean onLevelUp) {
        this(feedbackURL, "", false, onLevelUp, false, false, false, false, false);
    }

    public boolean onStart() {
    	if (System.getProperty("os.name").toLowerCase().contains("mac")) {
    		log.info("ArteNotifier is not supported on Mac; sorry for any inconvenience.");
    		return false;
    	}
        URL url = null;
        URL urll;
        BufferedReader br = null;
        BufferedReader brl = null;
        try {
            url = new URL("http://artebots.com/notify/message");
            br = new BufferedReader(new InputStreamReader(url.openStream()));
            messageOfTheDay = br.readLine();
            urll = new URL("http://artebots.com/notify/location");
            brl = new BufferedReader(new InputStreamReader(urll.openStream()));
            messageUrl = brl.readLine();
            addToTray();
            sendNotification("Starting script.", TrayIcon.MessageType.INFO);
        } catch (Exception e) {
            return false;
        }
        startMillis = System.currentTimeMillis();
        hrs = 0;
        bhrs = 0;
        return true;
    }

    private enum State {
        SLEEP
    }

    private State getState() {
        return State.SLEEP;
    }

    public int loop() {
        try {
            // GLOBAL NOTIFICATION
            if (System.currentTimeMillis() - startMillis >= 29000 && !gns) {
                sendNotification(messageOfTheDay, TrayIcon.MessageType.INFO);
                gns = true;
            }
            if (System.currentTimeMillis() - startMillis >= 59000 && !gnu) {
                messageUrl = "http://artebots.com";
                gnu = true;
            }
            // HOURLY NOTIFICATION
            if (!hTimer.isRunning() && notifs[Notifiers.HOURLY] && hItem.getState()) {
                hrs++;
                switch (hrs) {
                    case 1:
                        sendNotification("Running for 1 hour!", TrayIcon.MessageType.INFO);
                        break;
                    default:
                        sendNotification("Running for " + hrs + " hours!", TrayIcon.MessageType.INFO);
                        break;
                }
                hTimer.reset();
            }
            // BIHOURLY NOTIFICATION
            if (!bTimer.isRunning() && notifs[Notifiers.BIHOURLY] && bItem.getState()) {
                bhrs++;
                switch (bhrs) {
                    case 1:
                        sendNotification("Running for 30 minutes!", TrayIcon.MessageType.INFO);
                        break;
                    default:
                        String bihours = bhrs % 2 == 0 ? Integer.toString(bhrs / 2) : Integer.toString(bhrs / 2) + ".5";
                        if (bihours.equals("1"))
                            sendNotification("Running for 1 hour!", TrayIcon.MessageType.INFO);
                        else
                            sendNotification("Running for " + bihours + " hours!", TrayIcon.MessageType.INFO);
                        break;
                }
                bTimer.reset();
            }
            // DEATH NOTIFICATION
            if (died && notifs[Notifiers.DEATHS] && dItem.getState()) {
                sendNotification("Your character has died.", TrayIcon.MessageType.WARNING);
                died = false;
            }
            // LEVEL NOTIFICATION
            if (leveled && notifs[Notifiers.LEVEL_UPS] && lItem.getState()) {
                sendNotification("Gained " + skill + " level! You are now " + lvl + "!", TrayIcon.MessageType.INFO);
                leveled = false;
            }
            // NAMEHEARD NOTIFICATION
            if (name && notifs[Notifiers.NAME_HEARD] && nItem.getState()) {
                sendNotification("Your name was said by " + sender + ".", TrayIcon.MessageType.WARNING);
                name = false;
            }
            switch (getState()) {
                case SLEEP:
                    return 1;
            }
        } catch (Exception e) {
        }
        return 0;
    }

    public void onFinish() {
        // ON FINISH NOTIFICATION
        if (notifs[Notifiers.ON_FINISH] && oItem.getState()) {
            sendNotification("Script has stopped.", TrayIcon.MessageType.INFO);
            sleep(5000);
        }
        try {
            SystemTray.getSystemTray().remove(trayIcon);
        } catch (Exception e) {
        }
    }

    public void messageReceived(MessageEvent e) {
        String s = e.getMessage();
        if (e.isAutomated()) {
            if (s.contains("You've just advanced a")) {
                skill = s.substring(21, s.indexOf("level") - 1);
                lvl = s.substring(s.indexOf("level "), s.indexOf("."));
                leveled = lItem.getState();
            }
            if (s.contains("Oh dear you are dead.")) {
                died = dItem.getState();
            }
        }
        if (s.contains(Players.getLocal().getName())
            && !e.getSender().equals(Players.getLocal().getName())) {
            name = nItem.getState();
            sender = e.getSender();
        }
    }

    public static boolean addToTray() {
        try {
            if (SystemTray.isSupported()) {
                SystemTray tray = SystemTray.getSystemTray();
                ActionListener listener = new ActionListener() {
                    public void actionPerformed(ActionEvent e) {
                        String s = ((MenuItem) (e.getSource())).getLabel();
                        if (s.equalsIgnoreCase("Leave feedback"))
                            openURL(feedbackUrl);
                        if (s.equalsIgnoreCase("Visit ArteBots.com"))
                            openURL(artebots);
                        if (s.equalsIgnoreCase("Purchase Pro"))
                            openURL(purchaseUrl);
                    }
                };
                PopupMenu popup = new PopupMenu();
                Menu sub = new Menu("Set notifications...");
                lItem = new CheckboxMenuItem("Level-ups", true);
                hItem = new CheckboxMenuItem("Hourly", true);
                bItem = new CheckboxMenuItem("Bihourly", true);
                nItem = new CheckboxMenuItem("Name heard", true);
                dItem = new CheckboxMenuItem("Deaths", true);
                oItem = new CheckboxMenuItem("On finish", true);
                sub.add(lItem);
                sub.add(hItem);
                sub.add(bItem);
                sub.add(nItem);
                sub.add(dItem);
                sub.add(oItem);
                MenuItem sItem = new MenuItem("-");
                MenuItem fItem = new MenuItem("Leave feedback");
                fItem.addActionListener(listener);
                MenuItem pItem = null;
                if (isLiteScript) {
                    pItem = new MenuItem("Purchase Pro");
                    pItem.addActionListener(listener);
                }
                MenuItem aItem = new MenuItem("Visit ArteBots.com");
                aItem.addActionListener(listener);
                popup.add(sub);
                popup.add(sItem);
                popup.add(fItem);
                if (isLiteScript)
                    popup.add(pItem);
                popup.add(aItem);
                trayIcon = new TrayIcon(loadImage(iconBytes), "Artemis Productions", popup);
                iconListener = new ActionListener() {
                    public void actionPerformed(ActionEvent e) {
                        openURL(messageUrl);
                    }
                };
                trayIcon.addActionListener(iconListener);
                tray.add(trayIcon);
                return true;
            }
        } catch (Exception e) {
        }
        return false;
    }

    public static void sendNotification(String text, TrayIcon.MessageType messageType) {
        try {
            trayIcon.displayMessage("Artemis Productions", text, messageType);
        } catch (Exception e) {
        }
    }

    public static void setNotifs(boolean levelups, boolean hourly, boolean bihourly,
                                 boolean nameHeard, boolean deaths, boolean onFinish) {
        notifs[Notifiers.LEVEL_UPS] = levelups;
        notifs[Notifiers.BIHOURLY] = bihourly;
        notifs[Notifiers.HOURLY] = !bihourly && hourly;
        notifs[Notifiers.NAME_HEARD] = nameHeard;
        notifs[Notifiers.DEATHS] = deaths;
        notifs[Notifiers.ON_FINISH] = onFinish;
    }

    public static void openURL(final String url) { 
		final String osName = System.getProperty("os.name");
		try {
			if (osName.startsWith("Mac OS")) {
				final Class<?> fileMgr = Class.forName("com.apple.eio.FileManager");
				final Method openURL = fileMgr.getDeclaredMethod("openURL",new Class[]{String.class});
				openURL.invoke(null, new Object[]{url});
			} else if (osName.startsWith("Windows")) {
				Runtime.getRuntime().exec("rundll32 url.dll,FileProtocolHandler " + url);
			} else {
				final String[] browsers = {"firefox", "opera", "konqueror", "epiphany", "mozilla", "netscape", "google-chrome", "chromium-browser"};
				String browser = null;
				for (int count = 0; count < browsers.length && browser == null; count++) {
					if (Runtime.getRuntime().exec(new String[]{"which", browsers[count]}).waitFor() == 0) {
						browser = browsers[count];
					}
				}
				if (browser == null) {
					throw new Exception("Could not find web browser");
				} else {
					Runtime.getRuntime().exec(new String[]{browser, url});
				}
			}
		} catch (final Exception e) {
		}
	}
    
	public static Image loadImage(byte[] bytes) throws IOException {
		BufferedImage image = ImageIO.read(new ByteArrayInputStream(bytes));
		return image;
	}
    
	public static byte[] iconBytes = {
			-119, 80, 78, 71, 13, 10, 26, 10, 0, 0, 0, 13, 73, 72, 68, 
			82, 0, 0, 0, 16, 0, 0, 0, 16, 8, 6, 0, 0, 0, 31, 
			-13, -1, 97, 0, 0, 2, -35, 73, 68, 65, 84, 120, -38, -91, -109, 
			-37, 79, -110, 113, 24, -57, -71, 104, 110, 94, -11, 31, -8, 15, 116, 
			-47, -42, 114, 102, -89, -75, 90, 7, -69, 112, -107, -51, 78, -42, 44, 
			81, -55, -61, 100, -13, -75, -127, 24, 54, 115, -13, 60, -26, 1, 69, 
			51, -56, 4, -52, 97, 47, 40, 2, -66, -68, 28, 94, 4, 81, 4, 
			4, 67, 69, 4, 52, 53, 77, -99, 117, -35, -20, -30, -37, -117, 110, 
			117, -111, -83, -75, 46, -66, 119, -65, -49, -25, -5, 60, -49, -10, -29, 
			0, -32, -4, 79, -2, -6, -64, -17, -9, 43, 60, 30, -49, 119, -105, 
			-53, 5, -122, 97, 64, -45, 52, -116, 70, 35, 116, 58, 29, 52, 26, 
			-51, -64, 31, -63, 96, 48, 120, -44, -25, -13, 105, 98, -79, 24, -42, 
			-41, 55, 17, 91, 94, 69, 44, -74, -126, 104, 34, -47, 101, -20, -18, 
			126, -123, 74, -91, 58, 28, 14, 4, 2, -41, -68, 94, -17, -89, -107, 
			-107, 21, -20, -20, -20, -64, 51, 29, -64, -124, -37, 11, -89, 107, 26, 
			14, -25, 20, 28, -114, 73, 44, 70, 98, -112, -53, -27, -121, 11, 88, 
			24, -37, -37, -37, -5, 89, 93, 93, -125, -117, -123, -69, -70, 100, -112, 
			74, 59, -47, -34, -34, -127, -42, -42, 54, 44, -124, -93, -112, -55, 100, 
			-65, -61, 125, -52, 110, 10, 73, -110, -96, 40, -118, -123, 87, 17, -113, 
			-57, 49, -98, 104, 29, -97, -126, -35, -31, -122, -51, 62, 1, -38, -22, 
			-60, -4, -62, 18, 43, 106, -3, 5, -86, -100, 95, -110, 21, -74, 29, 
			109, -113, 118, -26, -101, -55, 100, -126, -59, 98, -63, -48, -48, 16, -69, 
			119, 28, 12, 59, 114, -94, 85, 34, -111, -96, -91, -91, 5, -115, -115, 
			-115, -104, -101, -113, -96, -87, -87, -23, 103, -21, -111, 94, -6, -13, 82, 
			63, -77, -123, 126, -5, 6, -102, 94, 27, -112, -104, 66, -83, 86, 99, 
			113, 49, 2, -85, 109, 2, 22, -101, 11, -76, -59, 9, -118, 118, 96, 
			-116, 98, 16, 10, -123, 81, 91, 91, 123, 32, -112, -115, 109, -90, -76, 
			-113, -82, -29, -67, 123, 11, 106, 102, 29, 18, 93, 20, -124, -60, 0, 
			-69, 39, -68, -65, 39, 65, 84, -128, 50, 51, 108, 28, 80, 78, -67, 
			65, -10, -32, 13, -52, -78, 2, -79, 88, 124, 32, 104, 38, 63, -90, 
			55, -109, 113, 40, 109, 107, 104, -48, 44, -94, 67, 31, 65, -91, 34, 
			-128, 7, -107, 74, -10, 104, -19, 24, 28, 28, 4, -105, -101, 15, -125, 
			-55, -118, -117, 125, 103, -112, -42, -111, -122, -32, -20, 2, 4, 2, -63, 
			-127, -96, 90, 21, -50, -84, 81, -121, -47, 109, -116, -31, 121, 95, 16, 
			-27, 61, 94, -44, 40, 3, 32, -70, 39, 113, -73, -92, 1, 117, 117, 
			117, -88, 120, 38, -64, -88, -47, 10, -3, 40, -115, 97, 61, -115, 64, 
			112, 14, -27, -27, -27, 7, 2, -66, 108, -122, -88, 98, 65, 9, 25, 
			70, 89, -105, 7, 5, 18, 39, 114, -22, -83, -32, 54, 51, 120, 92, 
			111, -63, -7, -84, 90, -24, -122, -51, 24, 73, -64, 35, 102, 104, -121, 
			41, -52, 4, -26, 80, 90, 90, 10, 78, -111, -44, -97, -108, -37, -20, 
			-34, 123, -87, -98, -123, 72, -31, -125, 72, -18, -35, 111, 46, 105, 115, 
			34, -65, -59, -122, -53, 79, -33, 65, 84, 79, 66, 111, -80, -78, 18, 
			10, 100, 34, 90, 19, -4, 51, 33, -16, 120, 60, 112, -18, -41, -69, 
			-10, 120, 109, 110, -68, 120, -21, 7, -65, 115, 2, 89, 34, 35, -82, 
			20, 105, 112, -18, 81, 63, 82, 111, -11, -30, -22, 61, 41, -20, -116, 
			11, -95, -71, -56, 126, 107, 2, 76, 100, 99, 99, -117, -67, 11, 23, 
			-100, -21, 85, 102, 83, 78, -99, 29, -39, 98, 10, -89, -97, 12, -20, 
			-91, -34, 81, 16, 39, 110, -66, -54, 60, -98, 33, 77, 63, 118, 73, 
			-110, 34, 20, 85, 103, 8, -123, -62, 15, 4, 65, -128, -49, -25, -93, 
			-72, -72, 24, -123, -123, -123, -56, -53, -53, 67, 110, 110, 110, -104, 115, 
			-95, 76, -105, 124, -74, 64, 99, 58, -11, 80, -71, 119, -14, -74, 60, 
			-23, 95, -65, -13, 15, -60, 52, -25, 127, -17, 51, -31, 6, 0, 0, 
			0, 0, 73, 69, 78, 68, -82, 66, 96, -126, 
	};

}