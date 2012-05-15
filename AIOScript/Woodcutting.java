import java.awt.Graphics;

import org.rsbot.event.events.MessageEvent;
import org.rsbot.event.listeners.MessageListener;
import org.rsbot.event.listeners.PaintListener;
import org.rsbot.script.wrappers.RSObject;

public class Woodcutting extends AIOScript implements MessageListener, PaintListener {
	private final int[] treeIDs = { 5004, 5005, 5045, 3879, 3881, 3882, 3883, 3885, 3886, 3887, 3888, 3889, 3890, 3891, 3892, 3893, 3928, 3967, 3968, 4048, 4049, 4050, 4051, 4052, 4053, 4054, 3033, 3034, 3035, 3036, 2409, 2447, 2448, 1330, 1331, 1332, 1310, 1305, 1304, 1303, 1301, 1276, 1277, 1278, 1279, 1280, 8742, 8743, 8973, 8974, 1315, 1316 };

	public Woodcutting(String location, String item, int amount) {
		this.location = location;
		itemName = item;
		itemAmount = amount;
	}

	@Override
	public int loop()
	{
		if(!getMyPlayer().isIdle())
			return 400;
		RSObject tree = objects.getNearest(treeIDs);
		if (tree != null && tree.doAction("Chop")) {}
		return random(400, 500);
	}

	@Override
	public void onRepaint(Graphics render)
	{
		// TODO Auto-generated method stub

	}

	@Override
	public void messageReceived(MessageEvent e)
	{
		String txt = e.getMessage();
		if (txt.contains("logs")) {
			doneItems++;
		}
	}

	@Override
	public boolean onStart()
	{
		// TODO Auto-generated method stub
		return false;
	}
}
