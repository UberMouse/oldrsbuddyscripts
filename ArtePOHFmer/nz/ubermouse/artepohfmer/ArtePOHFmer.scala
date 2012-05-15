package nz.ubermouse.artepohfmer

import data.{Constants, Static}
import java.awt.event.{MouseMotionListener, MouseListener}
import com.rsbuddy.event.listeners.{MessageListener, PaintListener}
import com.rsbuddy.event.events.MessageEvent
import nz.uberutils.paint.PaintController
import com.rsbuddy.script.Manifest
import nz.uberutils.helpers._
import stratagies._
import java.awt.Graphics2D
import com.rsbuddy.script.methods.{Npcs, Skills}
import nz.uberutils.paint.paints.UberPaint

/**
 * Created by IntelliJ IDEA.
 * User: Taylor
 * Date: 6/25/11
 * Time: 1:12 PM
 * Package: nz.ubermouse.artepohfmer; */
@Manifest(authors = Array("UberMouse"),
            name = "ArtePOHFmer",
            keywords = Array("Firemaking, house"),
            version = 0.1,
            description = "Burns logs in your house using demon butler")
class ArtePOHFmer extends UberScript with PaintListener with MouseListener with MouseMotionListener with MessageListener
{

   override def onBegin(): Boolean = {
      val p = new UberPaint("ArtePOHFmer", -1, getClass.getAnnotation(classOf[Manifest]).version)
      p.skills.add(new Skill(Skills.FIREMAKING));
      p.infoColumnValues = Array("Time running:", "Status:", "Logs used (p/h):", "Butler paid:");
      Firemaking.burnSuccessCallback = () => Static.logsUsed += 1
      strategies.add(new EnterHouse)
      //    strategies.add(new RingBell)
      strategies.add(new PayButler)
      strategies.add(new GetLogs)
      strategies.add(new BurnLogs)
      Firemaking.logId = 1517;
      paintType = p
      true
   }

   override def paint(g: Graphics2D) {
      (paintType.asInstanceOf[UberPaint]).infoColumnData = Array(PaintController.timeRunning(), status, "" +
                                                                                                        Static
                                                                                                        .logsUsed +
                                                                                                        " (" +
                                                                                                        Utils
                                                                                                        .calcPH(Static
                                                                                                                .logsUsed,
                                                                                                                  PaintController
                                                                                                                  .runTime) +
                                                                                                        ")", "" +
                                                                                                             Static
                                                                                                             .butlerPaid +
                                                                                                             " (" +
                                                                                                             Utils
                                                                                                             .calcPH(Static
                                                                                                                     .butlerPaid,
                                                                                                                       PaintController
                                                                                                                       .runTime) +
                                                                                                             ")");
      g.drawString("Cur pathlength: " + Firemaking.pathLength(UberPlayer.location()), 50, 50);
      ArtePOHFmer.graphics = g
   }

   override def miscLoop() {
      if (Utils.getWidgetWithText("the 10000 coins") != null &&
          Utils.getWidgetWithText("the 10000 coins").getAbsLocation.getX != 0)
         Static.needToPayButler = true
      if (Static.butlerGone && Npcs.getNearest(Constants.DEMON_BUTLER_ID) != null)
         Static.butlerGone = false;
   }

   def messageReceived(p1: MessageEvent) {}
}

object ArtePOHFmer
{
   var graphics: Graphics2D = null
}