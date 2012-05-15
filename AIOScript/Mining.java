///////////////////////////
//////////IMPORTS//////////
///////////////////////////

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridLayout;
import java.awt.Image;
import java.awt.Point;
import java.awt.Polygon;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionListener;

import java.io.*;
import java.lang.reflect.Method;
import java.net.*;

import javax.imageio.ImageIO;
import javax.swing.ComboBoxModel;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;

import org.rsbot.event.events.MessageEvent;
import org.rsbot.event.listeners.PaintListener;
import org.rsbot.event.listeners.MessageListener;
import org.rsbot.script.Script;
import org.rsbot.script.ScriptManifest;
import org.rsbot.script.methods.Skills;
import org.rsbot.script.wrappers.RSArea;
import org.rsbot.script.wrappers.RSComponent;
import org.rsbot.script.wrappers.RSInterface;
import org.rsbot.script.wrappers.RSItem;
import org.rsbot.script.wrappers.RSNPC;
import org.rsbot.script.wrappers.RSObject;
import org.rsbot.script.wrappers.RSPath;
import org.rsbot.script.wrappers.RSTile;

////////////////////////////
//////////MANIFEST//////////
////////////////////////////

@ScriptManifest(authors = { "Zhrz" }, name = "DarkMiner", version = 1.6, description = "Start near mine location with a pickaxe.")
public class Mining extends AIOScript implements MouseMotionListener, MessageListener, PaintListener {
	
	public Mining(String location, String item, int count, String[] params) {
		this.location = location;
		itemName = item;
		itemAmount = count;
		this.params = params;
	}

	// ////////////////////////////////////////////////
	// ////////VARIABLES - INT, BOOLEAN, LONG//////////
	// ////////////////////////////////////////////////

	// for paint calc
	public long startTime = System.currentTimeMillis(); // timer
	long runTime;
	long seconds;
	long minutes = 0;
	long hours = 0;
	int startexp = 0;
	int currentXP;
	int currentLVL;
	int gainedLVL;
	int oresPerHour;
	int expPerHour;
	int startLvl = 0;
	int gainedXP;
	int oresMined = 0;
	int gemsMined = 0;
	int pricePerOre = 0;
	int kMade;
	int kPerHour;
	int antiBanCount = 0;
	int[] gems = { 1621, 1619, 1623, 1617 };
	int randomNum2 = 0;
	int xpPerEssence = 5;

	Image img1;

	// tests to get info otherwise
	int minRunSoRest = 0;
	int stopRest = 0;
	int gotPickOn = 0;

	// different types of modes
	public boolean safeClick = false;
	public boolean pickHold = false;
	public boolean mineEss = false;

	public boolean modeDrop = false;
	public boolean modeMine2 = false;
	public boolean modeHybrid = false;
	public boolean modeBank = false;
	public boolean yesUpdate = false;

	// for rock paint
	public RSObject paintRock;

	String addZero = "";
	String addZero2 = "";

	// rockSpot
	RSTile rockSpot = null;

	// for status
	private String status = "Starting...";
	private String antiBanStatus = "Starting...";

	// for methods
	int rockID[];
	int oreID;
	final int pickID[] = { 1265, 1267, 1269, 1271, 1273, 1275, 14099, 14107, 15259, 15261 };
	final int strangeRockID[] = { 15532, 15533 };

	// for essence
	private int portalID = 2492;
	private int auburyID = 553;

	// code for paint
	Color edgeColor = new Color(153, 153, 153);
	Color transDarkBlue = new Color(102, 153, 255, 120);
	Color transDarkRed = new Color(204, 0, 0, 120);
	Color transDarkGreen = new Color(51, 153, 0, 200);

	public boolean showPaint1 = false; // hover mouse
	public boolean showPaint2 = false;
	public boolean showPaint3 = false;
	public boolean showPaint4 = false;

	private final BasicStroke stroke1 = new BasicStroke(1);

	// game tiles
	RSTile toNW[];
	RSTile toNE[];
	RSTile toSW[];
	RSTile toSE[];

	RSTile toBank[];
	RSTile toMine[];
	RSTile mineTile;
	RSTile bankTile;
	RSTile doorTile = new RSTile(3253, 3398);
	RSTile runeShop = new RSTile(3253, 3401);

	// for GUI
	private static final String[] TYPE_OPTION = new String[] { "Essence", "Clay", "Copper", "Tin", "Iron", "Silver", "Coal", "Gold" };
	private static final String[] SELECT_OPTION = new String[] { "Barbarian Village", "Rimmington", "Al Kharid", "West Varrock", "Varrock (Ess)" };
	private static final String[] MINE_OPTION = new String[] { "Bank", "M2D2", "Drop", "Hybrid" };

	// rock ID's
	public int essence[] = { 2491 };
	public int clay[] = { 11504, 11503, 11505, 9711, 9713, 15503, 15505 };
	public int copper[] = { 11937, 11938, 9710, 9708, 9709 };
	public int tin[] = { 11933, 11934, 11935, 11957, 11958, 11959, 9714, 9716 };
	public int iron[] = { 37309, 37307, 37308, 11955, 11956, 9718, 9717, 9719 };
	public int silver[] = { 37306, 37304, 37305, 11950, 11949, 11948 };
	public int coal[] = { 11930, 11932, 11931 };
	public int gold[] = { 37312, 37310, 9720, 9722 };

	// rstiles for essence mining
	private boolean atNW()
	{
		RSArea NorthWest = new RSArea(new RSTile(2884, 4837), new RSTile(2901, 4859));
		return (NorthWest.contains(getMyPlayer().getLocation()));
	}

	private boolean atVarrock()
	{
		RSArea Varrock = new RSArea(new RSTile(3239, 3388), new RSTile(3263, 3431));
		return (Varrock.contains(getMyPlayer().getLocation()));
	}

	private boolean atNE()
	{
		RSArea NorthEast = new RSArea(new RSTile(2918, 4841), new RSTile(2935, 4857));
		return (NorthEast.contains(getMyPlayer().getLocation()));
	}

	private boolean atSW()
	{
		RSArea SouthWest = new RSArea(new RSTile(2885, 4807), new RSTile(2904, 4823));
		return (SouthWest.contains(getMyPlayer().getLocation()));
	}

	private boolean atSE()
	{
		RSArea SouthEast = new RSArea(new RSTile(2920, 4806), new RSTile(2936, 4823));
		return (SouthEast.contains(getMyPlayer().getLocation()));
	}

	private boolean atDoor()
	{
		RSArea DoorLocation = new RSArea(new RSTile(3251, 3396), new RSTile(3255, 3405));
		return (DoorLocation.contains(getMyPlayer().getLocation()));
	}

	private boolean atBank()
	{
		RSArea Bank = new RSArea(new RSTile(3250, 3419), new RSTile(3257, 3423));
		return (Bank.contains(getMyPlayer().getLocation()));
	}

	private boolean atCenter()
	{
		RSArea Center = new RSArea(new RSTile(2903, 4824), new RSTile(2920, 4840));
		return (Center.contains(getMyPlayer().getLocation()));
	}

	private boolean atShop()
	{
		RSArea Shop = new RSArea(new RSTile(3251, 3403), new RSTile(3254, 3399));
		return (Shop.contains(getMyPlayer().getLocation()));
	}

	// /////////////////////////
	// ////////VERSION//////////
	// /////////////////////////

	private double getVersion()
	{
		return (1.6);
	}

	public void openURL(final String url)
	{ // Credits to Dave who gave credits
		// to
		// some guy who made this.
		final String osName = System.getProperty("os.name");
		try {
			if (osName.startsWith("Mac OS")) {
				final Class<?> fileMgr = Class.forName("com.apple.eio.FileManager");
				final Method openURL = fileMgr.getDeclaredMethod("openURL", new Class[] { String.class });
				openURL.invoke(null, new Object[] { url });
			}
			else if (osName.startsWith("Windows")) {
				Runtime.getRuntime().exec("rundll32 url.dll,FileProtocolHandler " + url);
			}
			else { // assume Unix or Linux
				final String[] browsers = { "firefox", "opera", "konqueror", "epiphany", "mozilla", "netscape" };
				String browser = null;
				for (int count = 0; count < browsers.length && browser == null; count++) {
					if (Runtime.getRuntime().exec(new String[] { "which", browsers[count] }).waitFor() == 0) {
						browser = browsers[count];
					}
				}
				if (browser == null) {
					throw new Exception("Could not find web browser");
				}
				else {
					Runtime.getRuntime().exec(new String[] { browser, url });
				}
			}
		}
		catch (final Exception e) {}
	}

	// /////////////////////////
	// ////////ONSTART//////////
	// /////////////////////////

	public boolean onStart()
	{
		log("Starting up, this may take a few seconds...");

		// gets image
		img1 = getImage("http://cublex.50webs.com/darkminer4.png");

		log("itemName: " + itemName);
		log("location: " + location);
		log("itemAmount: " + itemAmount);
		log("params[0]: " + params[0]);
		// sets up the mining mode
		if (params[0].equals("M2D2")) {
			modeDrop = false;
			modeMine2 = true;
			modeHybrid = false;
			modeBank = true;
		}
		else if (params[0].equals("Drop")) {
			modeDrop = true;
			modeMine2 = false;
			modeHybrid = false;
			modeBank = true;
		}
		else if (params[0].equals("Hybrid")) {
			modeDrop = false;
			modeMine2 = false;
			modeHybrid = true;
			modeBank = true;
		}
		else if (params[0].equals("Bank")) {
			modeDrop = false;
			modeMine2 = false;
			modeHybrid = false;
			modeBank = true;
		}

		// ore type + location +path
		if (itemName.equals("Essence")) {
			mineEss = true;
			rockID = essence;
			oreID = 1436;
			if (location.equals("Varrock (Ess)")) {
				bankTile = new RSTile(3254, 3420);

				toBank = new RSTile[] { new RSTile(3258, 3411), new RSTile(3261, 3422), new RSTile(3254, 3420) };

				toMine = new RSTile[] { new RSTile(3254, 3420), new RSTile(3257, 3412), new RSTile(3257, 3401), new RSTile(3253, 3398) };

				toNW = new RSTile[] { new RSTile(2911, 4831), new RSTile(2906, 4839), new RSTile(2900, 4842), new RSTile(2895, 4846) };

				toNE = new RSTile[] { new RSTile(2911, 4831), new RSTile(2919, 4840), new RSTile(2923, 4844), new RSTile(2926, 4847) };

				toSW = new RSTile[] { new RSTile(2911, 4831), new RSTile(2904, 4824), new RSTile(2899, 4820), new RSTile(2897, 4817) };

				toSE = new RSTile[] { new RSTile(2911, 4831), new RSTile(2918, 4822), new RSTile(2922, 4819), new RSTile(2926, 4819) };
			}
			else {
				log("[Error] = Selected ore and location do not match.");
			}
		}
		else if (itemName.equals("Clay")) {
			rockID = clay;
			oreID = 434;
			if (location.equals("Rimmington")) {
				mineTile = new RSTile(2986, 3240);
				bankTile = new RSTile(3012, 3355);

				toBank = new RSTile[] { new RSTile(2986, 3240), new RSTile(2978, 3242), new RSTile(2986, 3233), new RSTile(2994, 3243), new RSTile(2993, 3256), new RSTile(2993, 3269), new RSTile(3001, 3276), new RSTile(3003, 3289), new RSTile(3003, 3302), new RSTile(3005, 3315), new RSTile(3006, 3327), new RSTile(3007, 3338), new RSTile(3012, 3355) };

				toMine = new RSTile[] { new RSTile(3012, 3355), new RSTile(3007, 3344), new RSTile(3007, 3332), new RSTile(3006, 3321), new RSTile(2997, 3310), new RSTile(2986, 3304), new RSTile(2985, 3293), new RSTile(2986, 3282), new RSTile(2979, 3274), new RSTile(2974, 3263), new RSTile(2977, 3254), new RSTile(2978, 3242), new RSTile(2986, 3240) };

			}
			else if (location.equals("West Varrock")) {
				mineTile = new RSTile(3180, 3371);
				bankTile = new RSTile(3182, 3436);

				toMine = new RSTile[] { new RSTile(3182, 3436), new RSTile(3172, 3425), new RSTile(3171, 3410), new RSTile(3175, 3396), new RSTile(3179, 3383), new RSTile(3184, 3373), new RSTile(3180, 3371) };

				toBank = new RSTile[] { new RSTile(3180, 3371), new RSTile(3184, 3373), new RSTile(3179, 3383), new RSTile(3175, 3396), new RSTile(3171, 3410), new RSTile(3172, 3425), new RSTile(3182, 3436) };
			}
			else {
				log("[Error] = Selected ore and location do not match.");
			}

		}
		else if (itemName.equals("Copper")) {
			rockID = copper;
			oreID = 436;
			if (location.equals("Rimmington")) {
				mineTile = new RSTile(2977, 3246);
				bankTile = new RSTile(3012, 3355);

				toBank = new RSTile[] { new RSTile(2977, 3246), new RSTile(2978, 3242), new RSTile(2986, 3233), new RSTile(2994, 3243), new RSTile(2993, 3256), new RSTile(2993, 3269), new RSTile(3001, 3276), new RSTile(3003, 3289), new RSTile(3003, 3302), new RSTile(3005, 3315), new RSTile(3006, 3327), new RSTile(3007, 3338), new RSTile(3012, 3355) };

				toMine = new RSTile[] { new RSTile(3012, 3355), new RSTile(3007, 3344), new RSTile(3007, 3332), new RSTile(3006, 3321), new RSTile(2997, 3310), new RSTile(2986, 3304), new RSTile(2985, 3293), new RSTile(2986, 3282), new RSTile(2979, 3274), new RSTile(2974, 3263), new RSTile(2977, 3254), new RSTile(2977, 3246) };

			}
			else if (location.equals("Al Kharid")) {
				mineTile = new RSTile(3297, 3314);
				bankTile = new RSTile(3269, 3167);

				toMine = new RSTile[] { new RSTile(3269, 3167), new RSTile(3277, 3176), new RSTile(3282, 3188), new RSTile(3279, 3201), new RSTile(3277, 3215), new RSTile(3280, 3227), new RSTile(3286, 3241), new RSTile(3293, 3253), new RSTile(3295, 3266), new RSTile(3298, 3278), new RSTile(3298, 3287), new RSTile(3298, 3299), new RSTile(3298, 3299), new RSTile(3299, 3311), new RSTile(3297, 3314) };

				toBank = new RSTile[] { new RSTile(3297, 3314), new RSTile(3299, 3311), new RSTile(3298, 3299), new RSTile(3298, 3299), new RSTile(3298, 3287), new RSTile(3298, 3278), new RSTile(3295, 3266), new RSTile(3293, 3253), new RSTile(3286, 3241), new RSTile(3280, 3227), new RSTile(3277, 3215), new RSTile(3279, 3201), new RSTile(3282, 3188), new RSTile(3277, 3176), new RSTile(3269, 3167) };

				safeClick = true;

			}
			else {
				log("[Error] = Selected ore and location do not match.");
			}

		}
		else if (itemName.equals("Tin")) {
			rockID = tin;
			oreID = 438;
			if (location.equals("Barbarian Village")) {
				mineTile = new RSTile(3080, 3419);
				bankTile = new RSTile(3182, 3436);

				toBank = new RSTile[] { new RSTile(3080, 3491), new RSTile(3089, 3420), new RSTile(3101, 3420), new RSTile(3112, 3420), new RSTile(3124, 3420), new RSTile(3134, 3423), new RSTile(3147, 3424), new RSTile(3159, 3423), new RSTile(3172, 3429), new RSTile(3182, 3436) };

				toMine = new RSTile[] { new RSTile(3182, 3436), new RSTile(3172, 3429), new RSTile(3159, 3423), new RSTile(3147, 3424), new RSTile(3134, 3423), new RSTile(3124, 3420), new RSTile(3112, 3420), new RSTile(3101, 3420), new RSTile(3089, 3420), new RSTile(3080, 3491) };

				safeClick = true;

			}
			else if (location.equals("Rimmington")) {
				mineTile = new RSTile(2984, 3236);
				bankTile = new RSTile(3012, 3355);

				toBank = new RSTile[] { new RSTile(2984, 3236), new RSTile(2978, 3242), new RSTile(2986, 3233), new RSTile(2994, 3243), new RSTile(2993, 3256), new RSTile(2993, 3269), new RSTile(3001, 3276), new RSTile(3003, 3289), new RSTile(3003, 3302), new RSTile(3005, 3315), new RSTile(3006, 3327), new RSTile(3007, 3338), new RSTile(3012, 3355) };

				toMine = new RSTile[] { new RSTile(3012, 3355), new RSTile(3007, 3344), new RSTile(3007, 3332), new RSTile(3006, 3321), new RSTile(2997, 3310), new RSTile(2986, 3304), new RSTile(2985, 3293), new RSTile(2986, 3282), new RSTile(2979, 3274), new RSTile(2974, 3263), new RSTile(2977, 3254), new RSTile(2978, 3242), new RSTile(2984, 3236) };

			}
			else if (location.equals("Al Kharid")) {
				mineTile = new RSTile(3301, 3316);
				bankTile = new RSTile(3269, 3167);

				toMine = new RSTile[] { new RSTile(3269, 3167), new RSTile(3277, 3176), new RSTile(3282, 3188), new RSTile(3279, 3201), new RSTile(3277, 3215), new RSTile(3280, 3227), new RSTile(3286, 3241), new RSTile(3293, 3253), new RSTile(3295, 3266), new RSTile(3298, 3278), new RSTile(3298, 3287), new RSTile(3298, 3299), new RSTile(3298, 3299), new RSTile(3299, 3311), new RSTile(3301, 3316) };

				toBank = new RSTile[] { new RSTile(3301, 3316), new RSTile(3299, 3311), new RSTile(3298, 3299), new RSTile(3298, 3299), new RSTile(3298, 3287), new RSTile(3298, 3278), new RSTile(3295, 3266), new RSTile(3293, 3253), new RSTile(3286, 3241), new RSTile(3280, 3227), new RSTile(3277, 3215), new RSTile(3279, 3201), new RSTile(3282, 3188), new RSTile(3277, 3176), new RSTile(3269, 3167) };

				safeClick = true;

			}
			else if (location.equals("West Varrock")) {
				mineTile = new RSTile(3182, 3377);
				bankTile = new RSTile(3182, 3436);

				toMine = new RSTile[] { new RSTile(3182, 3436), new RSTile(3172, 3425), new RSTile(3171, 3410), new RSTile(3175, 3396), new RSTile(3179, 3383), new RSTile(3182, 3376) };

				toBank = new RSTile[] { new RSTile(3182, 3376), new RSTile(3179, 3383), new RSTile(3175, 3396), new RSTile(3171, 3410), new RSTile(3172, 3425), new RSTile(3182, 3436) };

			}
			else {
				log("[Error] = Selected ore and location do not match.");
			}

		}
		else if (itemName.equals("Iron")) {
			rockID = iron;
			oreID = 440;
			if (location.equals("Rimmington")) {
				mineTile = new RSTile(2970, 3240);
				bankTile = new RSTile(3012, 3355);

				toBank = new RSTile[] { new RSTile(2970, 3240), new RSTile(2978, 3242), new RSTile(2986, 3233), new RSTile(2994, 3243), new RSTile(2993, 3256), new RSTile(2993, 3269), new RSTile(3001, 3276), new RSTile(3003, 3289), new RSTile(3003, 3302), new RSTile(3005, 3315), new RSTile(3006, 3327), new RSTile(3007, 3338), new RSTile(3012, 3355) };

				toMine = new RSTile[] { new RSTile(3012, 3355), new RSTile(3007, 3344), new RSTile(3007, 3332), new RSTile(3006, 3321), new RSTile(2997, 3310), new RSTile(2986, 3304), new RSTile(2985, 3293), new RSTile(2986, 3282), new RSTile(2979, 3274), new RSTile(2974, 3263), new RSTile(2977, 3254), new RSTile(2978, 3242), new RSTile(2970, 3240) };

			}
			else if (location.equals("Al Kharid")) {
				mineTile = new RSTile(3298, 3311);
				bankTile = new RSTile(3269, 3167);

				toMine = new RSTile[] { new RSTile(3269, 3167), new RSTile(3277, 3176), new RSTile(3282, 3188), new RSTile(3279, 3201), new RSTile(3277, 3215), new RSTile(3280, 3227), new RSTile(3286, 3241), new RSTile(3293, 3253), new RSTile(3295, 3266), new RSTile(3298, 3278), new RSTile(3298, 3287), new RSTile(3298, 3299), new RSTile(3298, 3311) };

				toBank = new RSTile[] { new RSTile(3298, 3311), new RSTile(3298, 3299), new RSTile(3298, 3287), new RSTile(3298, 3278), new RSTile(3295, 3266), new RSTile(3293, 3253), new RSTile(3286, 3241), new RSTile(3280, 3227), new RSTile(3277, 3215), new RSTile(3279, 3201), new RSTile(3282, 3188), new RSTile(3277, 3176), new RSTile(3269, 3167) };

				safeClick = true;

			}
			else if (location.equals("West Varrock")) {
				mineTile = new RSTile(3175, 3367);
				bankTile = new RSTile(3182, 3436);

				toMine = new RSTile[] { new RSTile(3182, 3436), new RSTile(3172, 3425), new RSTile(3171, 3410), new RSTile(3175, 3396), new RSTile(3179, 3383), new RSTile(3184, 3373), new RSTile(3175, 3367) };

				toBank = new RSTile[] { new RSTile(3175, 3367), new RSTile(3184, 3373), new RSTile(3179, 3383), new RSTile(3175, 3396), new RSTile(3171, 3410), new RSTile(3172, 3425), new RSTile(3182, 3436) };
			}
			else {
				log("[Error] = Selected ore and location do not match.");
			}

		}
		else if (itemName.equals("Silver")) {
			rockID = silver;
			oreID = 442;
			if (location.equals("Al Kharid")) {
				mineTile = new RSTile(3295, 3301);
				bankTile = new RSTile(3269, 3167);

				toMine = new RSTile[] { new RSTile(3269, 3167), new RSTile(3277, 3176), new RSTile(3282, 3188), new RSTile(3279, 3201), new RSTile(3277, 3215), new RSTile(3280, 3227), new RSTile(3286, 3241), new RSTile(3293, 3253), new RSTile(3295, 3266), new RSTile(3298, 3278), new RSTile(3298, 3287), new RSTile(3295, 3301) };

				toBank = new RSTile[] { new RSTile(3295, 3301), new RSTile(3298, 3287), new RSTile(3298, 3278), new RSTile(3295, 3266), new RSTile(3293, 3253), new RSTile(3286, 3241), new RSTile(3280, 3227), new RSTile(3277, 3215), new RSTile(3279, 3201), new RSTile(3282, 3188), new RSTile(3277, 3176), new RSTile(3269, 3167) };

				safeClick = true;

			}
			else if (location.equals("West Varrock")) {
				mineTile = new RSTile(3176, 3368);
				bankTile = new RSTile(3182, 3436);

				toMine = new RSTile[] { new RSTile(3182, 3436), new RSTile(3172, 3425), new RSTile(3171, 3410), new RSTile(3175, 3396), new RSTile(3179, 3383), new RSTile(3184, 3373), new RSTile(3177, 3368) };

				toBank = new RSTile[] { new RSTile(3177, 3368), new RSTile(3184, 3373), new RSTile(3179, 3383), new RSTile(3175, 3396), new RSTile(3171, 3410), new RSTile(3172, 3425), new RSTile(3182, 3436) };
			}
			else {
				log("[Error] = Selected ore and location do not match.");
			}

		}
		else if (itemName.equals("Coal")) {
			rockID = coal;
			oreID = 453;
			if (location.equals("Barbarian Village")) {
				mineTile = new RSTile(3083, 3422);
				bankTile = new RSTile(3182, 3436);

				toBank = new RSTile[] { new RSTile(3083, 3422), new RSTile(3089, 3420), new RSTile(3101, 3420), new RSTile(3112, 3420), new RSTile(3124, 3420), new RSTile(3134, 3423), new RSTile(3147, 3424), new RSTile(3159, 3423), new RSTile(3172, 3429), new RSTile(3182, 3436) };

				toMine = new RSTile[] { new RSTile(3182, 3436), new RSTile(3172, 3429), new RSTile(3159, 3423), new RSTile(3147, 3424), new RSTile(3134, 3423), new RSTile(3124, 3420), new RSTile(3112, 3420), new RSTile(3101, 3420), new RSTile(3089, 3420), new RSTile(3083, 3422) };

				safeClick = true;

			}
			else if (location.equals("Al Kharid")) {
				mineTile = new RSTile(3300, 3299);
				bankTile = new RSTile(3269, 3167);

				toMine = new RSTile[] { new RSTile(3269, 3167), new RSTile(3277, 3176), new RSTile(3282, 3188), new RSTile(3279, 3201), new RSTile(3277, 3215), new RSTile(3280, 3227), new RSTile(3286, 3241), new RSTile(3293, 3253), new RSTile(3295, 3266), new RSTile(3298, 3278), new RSTile(3298, 3287), new RSTile(3300, 3300) };

				toBank = new RSTile[] { new RSTile(3300, 3300), new RSTile(3298, 3287), new RSTile(3298, 3278), new RSTile(3295, 3266), new RSTile(3293, 3253), new RSTile(3286, 3241), new RSTile(3280, 3227), new RSTile(3277, 3215), new RSTile(3279, 3201), new RSTile(3282, 3188), new RSTile(3277, 3176), new RSTile(3269, 3167) };

				safeClick = true;

			}
			else {
				log("[Error] = Selected ore and location do not match.");
			}

		}
		else if (itemName.equals("Gold")) {
			rockID = gold;
			oreID = 444;
			if (location.equals("Rimmington")) {
				mineTile = new RSTile(2977, 3234);
				bankTile = new RSTile(3012, 3355);

				toBank = new RSTile[] { new RSTile(2977, 3234), new RSTile(2978, 3242), new RSTile(2986, 3233), new RSTile(2994, 3243), new RSTile(2993, 3256), new RSTile(2993, 3269), new RSTile(3001, 3276), new RSTile(3003, 3289), new RSTile(3003, 3302), new RSTile(3005, 3315), new RSTile(3006, 3327), new RSTile(3007, 3338), new RSTile(3012, 3355) };

				toMine = new RSTile[] { new RSTile(3012, 3355), new RSTile(3007, 3344), new RSTile(3007, 3332), new RSTile(3006, 3321), new RSTile(2997, 3310), new RSTile(2986, 3304), new RSTile(2985, 3293), new RSTile(2986, 3282), new RSTile(2979, 3274), new RSTile(2974, 3263), new RSTile(2977, 3254), new RSTile(2978, 3242), new RSTile(2977, 3234) };

			}
			else if (location.equals("Al Kharid")) {
				mineTile = new RSTile(3297, 3287);
				bankTile = new RSTile(3269, 3167);

				toMine = new RSTile[] { new RSTile(3269, 3167), new RSTile(3277, 3176), new RSTile(3282, 3188), new RSTile(3279, 3201), new RSTile(3277, 3215), new RSTile(3280, 3227), new RSTile(3286, 3241), new RSTile(3293, 3253), new RSTile(3295, 3266), new RSTile(3298, 3278), new RSTile(3297, 3287) };

				toBank = new RSTile[] { new RSTile(3297, 3287), new RSTile(3298, 3278), new RSTile(3295, 3266), new RSTile(3293, 3253), new RSTile(3286, 3241), new RSTile(3280, 3227), new RSTile(3277, 3215), new RSTile(3279, 3201), new RSTile(3282, 3188), new RSTile(3277, 3176), new RSTile(3269, 3167) };

				safeClick = true;
			}
			else {
				log("[Error] = Selected ore and location do not match.");
			}
		}

		mouse.setSpeed(random(7, 8)); // Lets sort out this mouse
		startTime = System.currentTimeMillis(); // start paint timer
		return true;
	}

	// ///////////////////////////////
	// ////////SERVERMESSAGE//////////
	// ///////////////////////////////

	public void messageReceived(MessageEvent e)
	{
		String svrmsg = e.getMessage();

		if (svrmsg.contains("clay")) {
			oresMined = oresMined + 1;
			doneItems++;
		}
		if (svrmsg.contains("copper")) {
			oresMined = oresMined + 1;
			doneItems++;
		}
		if (svrmsg.contains("tin")) {
			oresMined = oresMined + 1;
			doneItems++;
		}
		if (svrmsg.contains("iron")) {
			oresMined = oresMined + 1;
			doneItems++;
		}
		if (svrmsg.contains("silver")) {
			oresMined = oresMined + 1;
			doneItems++;
		}
		if (svrmsg.contains("coal")) {
			oresMined = oresMined + 1;
			doneItems++;
		}
		if (svrmsg.contains("gold")) {
			oresMined = oresMined + 1;
			doneItems++;
		}

		if (svrmsg.contains("just found")) {
			gemsMined = gemsMined + 1;
		}
	}

	// ///////////////////////
	// /////RESTandRUN////////
	// ///////////////////////

	public boolean letsRun()
	{
		mouse.setSpeed(random(4, 9));
		if (minRunSoRest == 0) {
			if (mineEss) {
				minRunSoRest = random(78, 88);
			}
			else {
				minRunSoRest = random(1, 23);
			}
			stopRest = random(89, 97);
		}
		if (walking.isRunEnabled() == false || walking.getEnergy() < minRunSoRest) {
			if (walking.getEnergy() < stopRest) {
				status = "Resting";
				walking.rest(random(92, 100));
				do {
					sleep(250, 350);
					AntiBanPro();
					sleep(250, 350);
					AntiBanCamera();
				}
				while (walking.getEnergy() < stopRest);
			}
			if (walking.getEnergy() > stopRest) {
				if (walking.isRunEnabled() == false) {
					status = "Turning run on";
					walking.setRun(true);
					sleep(850, 1200);
					minRunSoRest = 0;
				}
				else if (walking.isRunEnabled() == true) {
					sleep(20, 30);
				}
			}
		}
		else {
			sleep(12, 15);
		}
		return true;
	}

	// ///////////////////////
	// ////////BANK///////////
	// ///////////////////////

	public boolean bankGems()
	{
		while (inventory.getItem(gems) != null) {
			RSItem item = inventory.getItem(gems);
			item.doClick(true);
			sleep(500, 600);
		}
		return true;
	}

	public boolean bank()
	{
		mouse.setSpeed(random(5, 8));
		status = "Banking";
		if (bank.isOpen()) {

			if (gotPickOn == 0) { // holding a pickaxe?
				if (inventory.containsOneOf(pickID)) {
					pickHold = false;
				}
				else
					pickHold = true;
			}

			// if yes bank fast
			if (pickHold == true) {
				bank.depositAll();
				sleep(random(250, 500));
			}
			else if (pickHold == false) { // if not bank slowly
				bank.depositAllExcept(pickID);
				bankGems();
				sleep(random(250, 500));
			}

			mouse.move(490, 36, 3, 3); // lets safely close the bank safely
			sleep(200, 500);
			mouse.click(true);
			sleep(random(300, 700));
			AntiBanCamera();
			AntiBanPro();

		}
		else {
			bank.open();
			sleep(500);
			while (players.getMyPlayer().isMoving()) {
				sleep(50);
			}
			sleep(700);
		}
		return true;
	}

	// //////////////////////
	// //////GetImage////////
	// //////////////////////

	private Image getImage(String url)
	{
		try {
			return ImageIO.read(new URL(url));
		}
		catch (IOException e) {
			return null;
		}
	}

	// //////////////////////
	// ////////mTwo//////////
	// //////////////////////

	public void mTwo()
	{
		mouse.setSpeed(random(4, 7));
		status = "Looking for rock";
		RSObject rock = objects.getNearest(rockID);
		if (rock != null) {
			paintRock = rock; // paints rock
			if (safeClick == true) {
				rockSpot = rock.getLocation(); // sets the title rock is on
				mouse.move(calc.tileToScreen(rockSpot), random(-10, 10), random(-10, 10));// hovers over that tile
				if (menu.contains("Mine")) {
					menu.doAction("Mine");
				}
			}
			else {
				rock.doAction("Mine");
			}
			if (calc.distanceTo(mineTile) <= 1) {
				sleep(random(1700, 1800));
			}
			else if (calc.distanceTo(mineTile) <= 2) {
				sleep(random(2100, 2200));
			}
			else if (calc.distanceTo(mineTile) <= 3) {
				sleep(random(2200, 2300));
			}
			else if (calc.distanceTo(mineTile) <= 4) {
				sleep(random(2300, 2500));
			}

			while (!(getMyPlayer().getAnimation() == -1 && objects.getNearest(rockID) != rock)) {
				status = "Mining ore";
				AntiBanPro();
				AntiBanCamera();
				sleep(random(50, 150));
				if (inventory.getCount(oreID) >= 1 && getMyPlayer().getAnimation() != -1) {
					inventory.getItem(oreID).getComponent().doHover();
				}
			}
			if (inventory.getCount(oreID) >= 2) {
				status = "Dropping ore";
				mouse.setSpeed(random(6, 7));
				inventory.dropAllExcept(pickID);
			}
		}
	}

	// //////////////////////
	// ///////hybrid/////////
	// //////////////////////

	private void hybrid()
	{
		mouse.setSpeed(random(5, 7));

		if (inventory.getCountExcept(pickID) >= 1) {
			RSObject rock = objects.getNearest(rockID);

			if (rock != null) {
				paintRock = rock; // first set this up
				rockSpot = rock.getLocation();

				// next click on rock
				status = "Clicking on rock";
				if (safeClick == true) {
					mouse.move(calc.tileToScreen(rockSpot), random(-10, 10), random(-10, 10));// hovers over that tile
					if (menu.contains("Mine")) {
						menu.doAction("Mine");
					}
				}
				else {
					rock.doAction("Mine");
				}
				sleep(100, 150);

				// while walking towards rock drop all ore
				status = "Dropping ore";
				mouse.setSpeed(random(4, 7));
				inventory.dropAllExcept(pickID);

				// then click to actually mine the ore
				mouse.setSpeed(random(5, 8));

				if (safeClick == true) {
					mouse.move(calc.tileToScreen(rockSpot), random(-10, 10), random(-10, 10));// hovers over that tile
					if (menu.contains("Mine")) {
						menu.doAction("Mine");
					}
				}
				else {
					rock.doAction("Mine");
				}

				sleep(random(1700, 1800));
				// only needs as one spot of hopefully

				while (!(getMyPlayer().getAnimation() == -1 && objects.getNearest(rockID) != rock)) {
					status = "Mining ore";
					AntiBanPro();
					AntiBanCamera();
					sleep(random(50, 150));
				}
			}
		}
		else {
			mine();
		}
	}

	// //////////////////////
	// ////////DROP//////////
	// //////////////////////

	private void dropOre()
	{
		mouse.setSpeed(random(7, 8));
		strangeRock();
		status = "Dropping ore";
		inventory.dropAllExcept(pickID);
		sleep(200, 500);
	}

	// ///////////////////////
	// ///////MOUSE///////////
	// ///////////////////////

	public void mouseMoved(MouseEvent e)
	{
		if (e.getX() >= 6 && e.getX() < 6 + 60 && e.getY() >= 250 && e.getY() < 250 + 20) {
			showPaint1 = true;
			showPaint2 = false;
			showPaint3 = false;
			showPaint4 = false;
		}
		if (e.getX() >= 70 && e.getX() < 70 + 60 && e.getY() >= 250 && e.getY() < 250 + 20) {
			showPaint2 = true;
			showPaint1 = false;
			showPaint3 = false;
			showPaint4 = false;
		}
		if (e.getX() >= 134 && e.getX() < 134 + 60 && e.getY() >= 250 && e.getY() < 250 + 20) {
			showPaint3 = true;
			showPaint1 = false;
			showPaint2 = false;
			showPaint4 = false;
		}
		if (e.getX() >= 198 && e.getX() < 198 + 13 && e.getY() >= 250 && e.getY() < 250 + 20) {
			showPaint4 = true;
			showPaint1 = false;
			showPaint2 = false;
			showPaint3 = false;
		}
	} // all for interactive paint say 'aye'

	public void mouseDragged(MouseEvent e)
	{
		// complete utter waste,
	}

	// ///////////////////////
	// ////////WALK///////////
	// ///////////////////////

	public boolean walkToBank()
	{
		mouse.setSpeed(random(4, 9));
		status = "Walking to Bank";
		walking.walkPathMM(toBank, 2, 2);
		AntiBanCamera();
		sleep(700);

		while (players.getMyPlayer().isMoving()) {
			AntiBanCamera();
			sleep(50, 80);
		}
		return true;
	}

	public boolean walkToMine()
	{
		mouse.setSpeed(random(4, 9));
		status = "Walking to Mine";
		walking.walkPathMM(toMine, 2, 2);
		sleep(700);

		while (players.getMyPlayer().isMoving()) {
			AntiBanCamera();
			sleep(50, 80);
		}

		return true;
	}

	public boolean walkToRock()
	{
		mouse.setSpeed(random(4, 9));
		status = "Walking to Essence";
		if (randomNum2 == 0) {
			randomNum2 = random(1, 4);
		}
		if (randomNum2 == 1) {
			walking.walkPathMM(toNE, 2, 2);
		}
		else if (randomNum2 == 2) {
			walking.walkPathMM(toNW, 2, 2);
		}
		else if (randomNum2 == 3) {
			walking.walkPathMM(toSE, 2, 2);
		}
		else if (randomNum2 == 4) {
			walking.walkPathMM(toSW, 2, 2);
		}
		sleep(600, 700);
		while (players.getMyPlayer().isMoving()) {
			AntiBanCamera();
			sleep(50, 80);
		}

		return true;
	}

	// ///////////////////////
	// /////AuburyTele////////
	// ///////////////////////

	public void teleport()
	{
		RSNPC aubury = npcs.getNearest(auburyID);
		if (aubury != null) {

			status = "Teleporting.";
			aubury.doAction("Teleport");
		}
		sleep(2400, 3000);
	}

	// ///////////////////////
	// //////OpenDoor/////////
	// ///////////////////////

	public void openDoor()
	{
		RSObject door = objects.getTopAt(doorTile);
		if (door != null) {
			status = "Opening Door.";
			camera.turnToObject(door, random(2, 3));
			door.doAction("Open");
			sleep(random(500, 650));
		}
		if (door == null) {
			teleport();
		}
	}

	public void openDoor2()
	{
		RSObject door = objects.getTopAt(doorTile);
		if (door != null) {
			status = "Opening Door.";
			camera.turnToObject(door, random(2, 3));
			door.doAction("Open");
			sleep(500, 650);
		}
		else if (door == null) {
			status = "Walking To Bank.";
			sleep(550, 700); // maybe needed
			walkToBank();
		}
	}

	// ///////////////////////
	// ////ClickPortal////////
	// ///////////////////////

	public void portal()
	{
		RSObject Portal = objects.getNearest(portalID);
		randomNum2 = 0;
		if (getMyPlayer().getAnimation() == -1 && Portal.getLocation() != null) {
			status = "Using Portal.";
			camera.setAngle(random(180, 90));
			if (calc.distanceTo(Portal) < 3) {
				Portal.doAction("Enter");
				sleep(5050, 7550);
			}
			else {
				walking.walkTo(Portal.getLocation());
				int i = 1;
				while ((calc.distanceTo(Portal) > 1) || i < 5) {
					sleep(550, 650);
					i++;
				}
			}
		}
	}

	// ///////////////////////
	// ////StrangeRock////////
	// ///////////////////////

	private void strangeRock()
	{
		mouse.setSpeed(random(4, 8));
		RSItem StrangeRock = inventory.getItem(strangeRockID);
		RSInterface inf = interfaces.get(94);
		RSComponent child;
		if (inventory.containsOneOf(strangeRockID)) {
			status = "Destroying strange rock";
			StrangeRock.doAction("Destroy");
			child = inf.getComponent(0);
			if (child != null && child.isValid()) {
				if (child.doAction("Continue")) {
					sleep(900);
				}
			}
		}
	}

	// ///////////////////////
	// ////////MINE///////////
	// ///////////////////////

	public boolean mine()
	{
		mouse.setSpeed(random(4, 7));
		status = "Looking for Rock";
		RSObject rock = objects.getNearest(rockID);
		if (rock != null) {
			paintRock = rock; // paints rock

			if (mineEss) { // so doesn't just stand there
				camera.turnToObject(rock, random(2, 13));
			}

			if (safeClick == true) {
				rockSpot = rock.getLocation(); // sets the title rock is on
				mouse.move(calc.tileToScreen(rockSpot), random(-10, 10), random(-10, 10));// hovers over that tile
				if (menu.contains("Mine")) {
					menu.doAction("Mine");
				}
			}
			else {
				rock.doAction("Mine");
			}
			if (calc.distanceTo(mineTile) <= 1) {
				sleep(random(1700, 1800));
			}
			else if (calc.distanceTo(mineTile) <= 2) {
				sleep(random(2100, 2200));
			}
			else if (calc.distanceTo(mineTile) <= 3) {
				sleep(random(2200, 2300));
			}
			else if (calc.distanceTo(mineTile) <= 4) {
				sleep(random(2300, 2500));
			}

			while (!(getMyPlayer().getAnimation() == -1 && objects.getNearest(rockID) != rock)) {
				status = "Mining Ore";
				AntiBanPro();
				AntiBanCamera();
				sleep(random(50, 150));
			}
		}
		return true;
	}

	// ///////////////////////
	// /////DrawRocks/////////
	// ///////////////////////

	private void drawTile(Graphics render, RSTile tile, Color color,
			boolean drawCardinalDirections)
	{
		Point southwest = calc.tileToScreen(tile, 0, 0, 0);
		Point southeast = calc.tileToScreen(new RSTile(tile.getX() + 1, tile.getY()), 0, 0, 0);
		Point northwest = calc.tileToScreen(new RSTile(tile.getX(), tile.getY() + 1), 0, 0, 0);
		Point northeast = calc.tileToScreen(new RSTile(tile.getX() + 1, tile.getY() + 1), 0, 0, 0);

		if (calc.pointOnScreen(southwest) && calc.pointOnScreen(southeast) && calc.pointOnScreen(northwest) && calc.pointOnScreen(northeast)) {
			render.setColor(Color.BLACK);
			render.drawPolygon(new int[] { (int) northwest.getX(), (int) northeast.getX(), (int) southeast.getX(), (int) southwest.getX() }, new int[] { (int) northwest.getY(), (int) northeast.getY(), (int) southeast.getY(), (int) southwest.getY() }, 4);
			render.setColor(color);
			render.fillPolygon(new int[] { (int) northwest.getX(), (int) northeast.getX(), (int) southeast.getX(), (int) southwest.getX() }, new int[] { (int) northwest.getY(), (int) northeast.getY(), (int) southeast.getY(), (int) southwest.getY() }, 4);

			if (drawCardinalDirections) {
				render.setColor(Color.WHITE);

			}
		}
	}

	private void paintRockModel(final Graphics g, final RSObject o,
			final Color c, String s)
	{
		for (Polygon p : o.getModel().getTriangles()) {
			g.setColor(c);
			g.drawPolygon(p);
			if (c == Color.green) {
				g.setColor(new Color(0, 200, 0, 130));
			}
			else if (c == Color.red) {
				g.setColor(new Color(200, 0, 0, 130));
			}
			else if (c == Color.blue) {
				g.setColor(new Color(0, 0, 200, 70));
			}
			g.fillPolygon(p);
		}
		final Point p = calc.tileToScreen(o.getLocation());
		g.setColor(c);
		g.drawString(s, p.x - 30, p.y - 25);
	}

	// ///////////////////////
	// ////////PAINT//////////
	// ///////////////////////

	public void onRepaint(Graphics g)
	{
		if (game.isLoggedIn() == true) {

			drawTile(g, players.getMyPlayer().getLocation(), new Color(0, 0, 200, 70), true);
			if (paintRock != null) {
				paintRockModel(g, paintRock, Color.blue, "Rock");
			}

			g.setColor(transDarkBlue);
			g.fillRect(6, 250, 60, 20); // interactive square 1
			g.fillRect(70, 250, 60, 20); // interactive square 2
			g.fillRect(134, 250, 60, 20); // interactive square 3
			g.fillRect(198, 250, 13, 20); // interactive square 4

			g.setColor(Color.white); // words for interactive squares
			g.drawString("STATUS", 11, 267);
			g.drawString("PROFIT", 75, 267);
			g.drawString("EXTRA", 139, 267);
			g.drawString("X", 201, 267);

			runTime = System.currentTimeMillis() - startTime;
			seconds = runTime / 1000; // seconds
			if (seconds >= 60) { // minutes
				minutes = seconds / 60;
				seconds -= (minutes * 60);
			}
			if (minutes >= 60) { // hours
				hours = minutes / 60;
				minutes -= (hours * 60);
			}
			if (startexp == 0) {
				startexp = skills.getCurrentExp(14);
			}
			if (startLvl == 0) {
				startLvl = skills.getCurrentLevel(Skills.MINING);
			}
			if (pricePerOre == 0) {
				pricePerOre = grandExchange.getMarketPrice(oreID);
			}

			// adds zero before minutes
			if (minutes >= 10) {
				addZero = "";
			}
			else if (minutes < 10) {
				addZero = "0";
			}

			// adds zero before seconds
			if (seconds >= 10) {
				addZero2 = "";
			}
			else if (seconds < 10) {
				addZero2 = "0";
			}

			kMade = (oresMined * pricePerOre) / 1000;
			gainedXP = skills.getCurrentExp(14) - startexp;
			if (mineEss) {
				oresMined = gainedXP / xpPerEssence;
			}
			currentLVL = skills.getCurrentLevel(Skills.MINING);
			gainedLVL = currentLVL - startLvl;
			oresPerHour = (int) ((3600000.0 / (double) runTime) * oresMined);
			expPerHour = (int) (3600000.0 / (double) runTime * gainedXP);
			kPerHour = (int) (3600000.0 / (double) runTime * kMade);

			if (showPaint1 == true) {

				final int percent = (skills.getPercentToNextLevel(14) * 2);
				g.setColor(transDarkRed);
				g.fillRoundRect(8, 282, 200, 18, 16, 16);
				g.setColor(transDarkGreen);
				g.fillRoundRect(8, 282, percent, 18, 16, 16);
				g.setColor(edgeColor);
				g.drawRoundRect(8, 282, 200, 18, 16, 16);
				g.drawRoundRect(8, 282, percent, 18, 16, 16);
				// Percentage bar till level

				g.setColor(transDarkBlue);
				g.fillRoundRect(4, 273, 208, 63, 16, 16);
				g.setColor(edgeColor);
				((Graphics2D) g).setStroke(stroke1);
				g.drawRoundRect(4, 273, 208, 63, 16, 16);
				g.setColor(Color.white);
				g.drawString("Status: " + status, 23, 295);
				g.drawString("Runtime: " + hours + ":" + addZero + minutes + ":" + addZero2 + seconds, 12, 315);
				g.drawString("XP Gained: " + gainedXP + " (" + expPerHour + "/hour)", 12, 329);
			}

			if (showPaint2 == true) {
				g.setColor(transDarkBlue);
				g.fillRoundRect(4, 273, 208, 63, 16, 16);
				g.setColor(edgeColor);
				((Graphics2D) g).setStroke(stroke1);
				g.drawRoundRect(4, 273, 208, 63, 16, 16);
				g.setColor(Color.white);
				g.drawString("Total Profit Made: " + kMade + "k", 12, 295);
				g.drawString("Profit Per Hour: " + kPerHour + "k", 12, 312);
				g.drawString("Total Ores Mined: " + oresMined + " (" + oresPerHour + "/hour)", 12, 329);
			}

			if (showPaint3 == true) {
				g.setColor(transDarkBlue);
				g.fillRoundRect(4, 273, 208, 63, 16, 16);
				g.setColor(edgeColor);
				((Graphics2D) g).setStroke(stroke1);
				g.drawRoundRect(4, 273, 208, 63, 16, 16);
				g.setColor(Color.white);
				g.drawString("Total Gems Mined: " + gemsMined, 12, 295);
				g.drawString("[Anti-Ban]: " + "Total " + antiBanCount, 12, 312);
				g.drawString(antiBanStatus, 12, 329);
			}
			if (showPaint4 == true) {
				// nothing
			}

			// Draws the mouse
			g.setColor(transDarkBlue);
			g.drawLine(0, (int) (mouse.getLocation().getY()), 800, (int) (mouse.getLocation().getY()));
			g.drawLine((int) (mouse.getLocation().getX()), 0, (int) (mouse.getLocation().getX()), 800);

			// draws banner
			g.drawImage(img1, 1, 170, null);

		}
	}

	// /////////////////////////
	// ////////ANTIBAN//////////
	// /////////////////////////

	private void AntiBanPro()
	{
		int randomProd = random(1, 60);
		if (randomProd == 1) {
			int randomMore = random(1, 5);
			if (randomMore == 1) {
				antiBanStatus = "Checking mining level..";
				antiBanCount = antiBanCount + 1;
				if (game.getCurrentTab() != 1) {
					game.openTab(1);
					sleep(350, 500);
					mouse.move(random(678, 728), random(213, 232));
					sleep(2000, 3500);
				}

				else {
					mouse.move(random(678, 728), random(213, 232));
					sleep(2000, 3500);
				}
			}
			else {
				antiBanStatus = "Lets wait for a while..";
				antiBanCount = antiBanCount + 1;
				sleep(1200, 2500);
			}
		}
		if (randomProd == 2 || randomProd == 3 || randomProd == 4) {
			antiBanStatus = "Pause for a few seconds..";
			antiBanCount = antiBanCount + 1;
			sleep(1000, 2500);
		}
		if (randomProd >= 52) {
			antiBanStatus = "Moving mouse randomly..";
			antiBanCount = antiBanCount + 1;
			mouse.moveRandomly(65, 350);
		}
		else
			sleep(10, 30);
	}

	private void AntiBanCamera()
	{
		int randomNum = random(1, 50);
		if (randomNum == 1 || randomNum == 2 || randomNum == 3) {
			antiBanStatus = "Random camera movement..";
			antiBanCount = antiBanCount + 1;
			camera.moveRandomly(random(2000, 5500));
		}
		if (randomNum == 4 || randomNum == 5) {
			antiBanStatus = "Random camera angle change..";
			antiBanCount = antiBanCount + 1;
			camera.setAngle(random(10, 40));
		}
		if (randomNum == 6) {
			camera.setPitch(random(40, 68));
			antiBanStatus = "Random camera pitch change..";
			antiBanCount = antiBanCount + 1;
		}
		if (randomNum == 7) {
			camera.setPitch(random(20, 45));
			antiBanStatus = "Random camera pitch change..";
			antiBanCount = antiBanCount + 1;
		}
		if (randomNum == 8) {
			camera.setPitch(random(68, 90));
			antiBanStatus = "Random camera pitch change..";
			antiBanCount = antiBanCount + 1;
		}
		else
			sleep(50, 100);
	}

	// ///////////////////////
	// ////////EXTRAS/////////
	// ///////////////////////

	// Will logout if all else fails
	private void failLogOff()
	{
		log("[!Error occured!] To prevent futher errors the script will now stop.");
		sleep(random(1400, 1600));
		if (game.isOnLogoutTab() == true) {
			sleep(random(300, 1200));
			mouse.move(631 + random(-40, 40), 417 + random(-5, 5));
			sleep(random(250, 2500));
			mouse.click(true);
			sleep(random(5000, 10000));
		}
		else {
			mouse.move(754 + random(-3, 3), 9 + random(-3, 3));
			sleep(random(100, 150));
			mouse.click(true);
			sleep(random(300, 1200));
			mouse.move(631 + random(-40, 40), 417 + random(-5, 5));
			sleep(random(250, 2500));
			mouse.click(true);
			sleep(random(5000, 10000));
		}

		stopScript();
	}

	// //////////////////////////
	// ////////ONFINISH//////////
	// //////////////////////////

	public void onFinish()
	{
		log("Thank you for using DarkMiner,");
		log("Please report any bugs and make suggestions on our thread.");
	}

	// //////////////////////
	// ////////LOOP//////////
	// //////////////////////

	@Override
	public int loop()
	{
		
		if(inventory.isFull()) {
			if(!nearPath(toBank)) 
				walking.getPath(bankTile).traverse();
		}
		else {
			if(!nearPath(toMine)) {
				log("Walking to mine");
				RSPath path = walking.getPath(new RSTile(3236,3219));
				path.traverse();
			}
		}
		if (mineEss) {

			// if invent full ---> bank
			if (inventory.isFull() && atBank()) { // bank
				letsRun();
				bank();
			}
			else if (inventory.isFull() && (atNW() || atNE() || atSE() || atSW())) {
				portal();
			}
			else if (inventory.isFull() && !atNE() && !atNW() && !atSE() && !atSW() && !atBank() && !atShop() && !atVarrock()) {
				walkToRock();
			}
			else if (inventory.isFull() && atShop()) {
				openDoor2();
			}
			else if (inventory.isFull()) {
				walkToBank();
			}

			// if invent not full ---> mine
			if ((!inventory.isFull()) && (atNW() || atNE() || atSW() || atSE())) {
				mine();
			}
			else if (!inventory.isFull() && !atNE() && !atNW() && !atSE() && !atSW() && !atBank() && !atShop() && !atVarrock()) {
				walkToRock();

			}
			else if (!inventory.isFull() && !atNE() && !atNW() && !atSE() && !atSW() && !atCenter() && !atShop() && !atDoor()) {
				walkToMine();

			}
			else if (!inventory.isFull() && atDoor()) {
				openDoor();

			}
			else {
				sleep(200, 300);
			}
		}
		else {
			// If inventory full --> bank or drop
			if (inventory.isFull()) {
				if (modeDrop || modeMine2 || modeHybrid == true) {
					dropOre();
				}
				// otherwise bank
				else if (modeBank == true) {
					if (calc.distanceTo(bankTile) < 5) {
						bank();
						return 10;
					}
					else {
						letsRun();
						walkToBank();
						return 10;
					}
				}
			}

			// If inventory not full --> mine
			if (!inventory.isFull()) {
				if (calc.distanceTo(mineTile) < 5) {

					if (modeHybrid == true) {
						hybrid();
					}
					else if (modeMine2 == true) {
						mTwo();
					}
					else {
						mine();
					}
					return 10;
				}

				else {
					letsRun();
					walkToMine();
					return 10;
				}
			}
			else
				failLogOff(); // how the hell you need this?
		}

		return 0;
	}
}
