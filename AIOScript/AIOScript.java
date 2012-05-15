import java.awt.Graphics;

import org.rsbot.event.events.MessageEvent;
import org.rsbot.event.listeners.MessageListener;
import org.rsbot.event.listeners.PaintListener;
import org.rsbot.script.Script;
import org.rsbot.script.wrappers.RSPath;
import org.rsbot.script.wrappers.RSTile;


public abstract class AIOScript extends Script implements PaintListener, MessageListener{
	protected int doneItems = 0;
	protected int itemAmount;
	protected String itemName;
	protected String location;
	protected String[] params;
	
	@Override
	public abstract int loop();
	
	public abstract boolean onStart();

	public boolean isFinished()
	{
		return doneItems >= itemAmount;
	}

	@Override
	public abstract void onRepaint(Graphics render);

	@Override
	public abstract void messageReceived(MessageEvent e);
	
	public boolean nearPath(RSTile[] path) {
		return nearPath(walking.newTilePath(path));
	}
	
	public boolean nearPath(RSPath path) {
		log(path.getNext().toString());
		if(calc.distanceTo(path.getNext()) > 15 || path.getNext() == null)
			return false;
		return true;
	}
}
