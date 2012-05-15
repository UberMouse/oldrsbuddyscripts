import java.io.*;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.imageio.ImageIO;

import org.rsbot.event.events.MessageEvent;
import org.rsbot.event.listeners.MessageListener;
import org.rsbot.script.Script;
import org.rsbot.script.ScriptManifest;

import com.sun.org.apache.xerces.internal.impl.dv.util.Base64;

@ScriptManifest(authors = "UberMouse", name = "Remote test", keywords = "Everything", version = 0.1, description = "A true AIO script")
public class remotetest extends Script implements MessageListener {
	public enum eventTypes {
		CHAT, IMAGE_REQUEST, STOP_SCRIPT
	};

	Remote remote;

	public boolean onStart()
	{
		remote = new Remote("UberMouse");
		return remote.getSessionID();
	}

	@Override
	public int loop()
	{
		return 500;
	}

	@Override
	public void messageReceived(final MessageEvent e)
	{
		if (!(e.getID() == e.MESSAGE_CHAT))
			return;
		String message = e.getSender() + ": " + e.getMessage();
		remote.sendMessage(message);

	}

	public class Remote {
		/* remove leading whitespace */
		public String ltrim(String source)
		{
			return source.replaceAll("^\\s+", "");
		}

		/* remove trailing whitespace */
		public String rtrim(String source)
		{
			return source.replaceAll("\\s+$", "");
		}

		private String siteURL = "http://localhost/projects/remotescripts/";
		private String sessionID;
		public String username;
		private EventListenerThread eventThread = new EventListenerThread();

		Remote(String username) {
			this.username = username;
			eventThread.start();
		}

		public boolean getSessionID()
		{
			sessionID = sendData(siteURL + "?page=start", "username=" + username).trim();
			if (sessionID.length() > 0)
				return true;
			return false;
		}

		public void sendMessage(String message)
		{
			addEvent("chat", message);
		}

		private void addEvent(String eventType, String eventData) {
			try {
				sendData(siteURL + "?page=addevent", "eventType="+eventType+"&eventSender=bot&sessionId=" + sessionID + "&eventData=" + URLEncoder.encode(eventData, "UTF-8"));
			}
			catch (UnsupportedEncodingException e) {
				e.printStackTrace();
			}
		}
		
		private String sendData(String URL, String message)
		{
			try {
				URL url = new URL(URL);
				URLConnection connection = url.openConnection();
				connection.setDoOutput(true);
				DataOutputStream printout = new DataOutputStream(connection.getOutputStream());
				printout.writeBytes(message);
				printout.flush();
				printout.close();
				BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
				String returnData = "";
				String temp;
				while ((temp = in.readLine()) != null) {
					returnData += temp + "\n";
				}
				return returnData;
			}
			catch (MalformedURLException e) {
				e.printStackTrace();
			}
			catch (IOException e) {
				e.printStackTrace();
			}
			return "";
		}

		private void handleEvent(String eventType, String eventData)
		{
			switch (eventTypes.valueOf(eventType.toUpperCase()))
			{
				case IMAGE_REQUEST:
					try {
						ByteArrayOutputStream baos = new ByteArrayOutputStream();
						ImageIO.write(env.takeScreenshot(true), "png", baos);
						URL url = new URL("http://api.imgur.com/2/upload");

						//encodes picture with Base64 and inserts api key
						String data = URLEncoder.encode("image", "UTF-8") + "=" + URLEncoder.encode(Base64.encode(baos.toByteArray()), "UTF-8");
						data += "&" + URLEncoder.encode("key", "UTF-8") + "=" + URLEncoder.encode("4cd9fccda3f5c1c0a13e5104f4e74bd1", "UTF-8");

						// opens connection and sends data
						URLConnection conn = url.openConnection();
						conn.setDoOutput(true);
						OutputStreamWriter wr = new OutputStreamWriter(conn.getOutputStream());
						wr.write(data);
						wr.flush();
						BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream()));
						String xml = "";
						String temp;
						while ((temp = in.readLine()) != null) {
							xml += temp + "\n";
						}
						Pattern regex = Pattern.compile("<hash>[a-zA-Z0-9]+</hash>");
						Matcher match = regex.matcher(xml);
						if (match.find()) {
							String imghash = match.group();
							imghash = imghash.replace("<hash>", "");
							imghash = imghash.replace("</hash>", "");
							addEvent("image", imghash);
						}
					}
					catch (Exception e) {
						e.printStackTrace();
					}
				break;

				case CHAT:
					keyboard.sendTextInstant(eventData, true);
				break;

				case STOP_SCRIPT:

				break;
			}
		}

		private class EventListenerThread extends Thread {
			public void run()
			{
				int timeout = 0;
				while (!isRunning() && ++timeout < 25)
					try {
						Thread.sleep(100);
					}
					catch (InterruptedException e1) {
						e1.printStackTrace();
					}
				while (isRunning()) {
					try {
						String events = sendData(siteURL + "?page=events", "eventSender=web&sessionId=" + sessionID);
						if (events.length() > 0) {
							String[] eventsTemp = events.split("~");
							for (String event : eventsTemp) {
								String[] eventTemp = event.split("`");
								handleEvent(eventTemp[0], eventTemp[1]);
							}
						}
						Thread.sleep(500);
					}
					catch (Exception e) {}
				}
			}
		}
	}

}
