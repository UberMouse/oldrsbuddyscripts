import java.awt.Graphics;
import java.util.ArrayList;

import org.rsbot.event.events.MessageEvent;
import org.rsbot.event.listeners.MessageListener;
import org.rsbot.event.listeners.PaintListener;
import org.rsbot.script.Script;
import org.rsbot.script.ScriptManifest;

@ScriptManifest(authors = "UberMouse", name = "AIO Script", keywords = "Everything", version = 0.1, description = "A true AIO script")
public class GlobalController extends Script implements MessageListener,
		PaintListener {
	private ArrayList<AIOScript> scriptList = new ArrayList<AIOScript>();
	private int currentScript = 0;
	private boolean repeatTasks = true;
	private boolean firstRun = true;

	public boolean onStart()
	{
		scriptList.add(new Mining("West Varrock", "Clay", 10, new String[] {"Drop"}));
		scriptList.add(new Mining("West Varrock", "Iron", 10, new String[] {"Drop"}));
		return true;
	}

	@Override
	public int loop()
	{
		if (firstRun) {
			scriptList.get(currentScript).init(this);
			firstRun = false;
		}
		if (scriptList.get(currentScript).isFinished()) {
			currentScript++;
			if (!repeatTasks && scriptList.size() == currentScript) {
				log("Finished all tasks, logging out");
				stopScript(true);
			}
			else if(scriptList.size() == currentScript) {
				currentScript = 0;
				for(int i = 0;i < scriptList.size();i++)
					scriptList.get(i).doneItems = 0;
			}
			scriptList.get(currentScript).init(this);
			log("Switching scripts");
		}
		return scriptList.get(currentScript).loop();
	}

	@Override
	public void onRepaint(Graphics render)
	{
		scriptList.get(currentScript).onRepaint(render);
	}

	@Override
	public void messageReceived(MessageEvent e)
	{
		scriptList.get(currentScript).messageReceived(e);
	}

}
