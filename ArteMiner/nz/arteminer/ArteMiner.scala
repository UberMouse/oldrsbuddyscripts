package nz.arteminer

import com.rsbuddy.script.ActiveScript
import helpers.StrategyContainer
import misc.{Module, GameConstants}
import modules.GenericMine
import com.rsbuddy.script.Manifest
import nz.uberutils.paint.PaintController
import java.awt.event._
import java.awt._
import com.rsbuddy.event.listeners.{MessageListener, PaintListener}
import com.rsbuddy.event.events.MessageEvent
import nz.uberutils.paint.abstracts.PComponent
import nz.uberutils.paint.components.{PColumnLayout, PSkill, PFancyButton, PFrame}
import nz.uberutils.helpers.{Options, Utils}
import com.rsbuddy.script.methods.{Walking, Skills, Mouse}
import stratagies.generic.{HoverRock, Bank, Mine}

/**
 * Created by IntelliJ IDEA.
 * User: Taylor
 * Date: 4/28/11
 * Time: 2:02 AM
 * Package: nz.arteminer; */
@Manifest(authors = Array("UberMouse"),
          name = "ArteMiner",
          keywords = Array("Mining, Magic, Smithing"),
          version = 0.1,
          description = "Mines")
class ArteMiner extends ActiveScript with
                        PaintListener with
                        MouseListener with
                        MouseMotionListener with
                        KeyListener with
                        MessageListener
{
  var status = "Loading.."
  var module: Module = null

  private final val infoFrame: PFrame = new PFrame(("info"))
  {
    override def shouldPaint: Boolean = {
      return ArteMiner.menuIndex == 0
    }

    override def shouldHandleMouse: Boolean = {
      return shouldPaint
    }
  }
  private final val optionFrame: PFrame = new PFrame(("options"))
  {
    override def shouldPaint: Boolean = {
      return ArteMiner.menuIndex == 1
    }

    override def shouldHandleMouse: Boolean = {
      return shouldPaint
    }
  }

  override def onStart(): Boolean = {
    var ores: Array[Int] = Array(0, 1)
    ores = GameConstants.COAL_ID
    StrategyContainer.addStrategy("bank", new Bank(GameConstants.RESOUCE_DUNGEON_BANK_TILE))
    StrategyContainer.addStrategy("mine", new Mine(ores))
    StrategyContainer.addStrategy("hover", new HoverRock(ores:_*))
    module = new GenericMine
    Mouse.setSpeed(1)
    Options.setNode("ArteMiner")


    PaintController.clearComps()


    PaintController.addComponent(new PFancyButton(8, 450, "ArteBots", PFancyButton.ColorScheme.GRAPHITE)
    {
      override def onPress() {
        Utils.openURL("http://artebots.com")
      }
    })


    PaintController.addComponent(new PFancyButton(59, 450, "Feedback", PFancyButton.ColorScheme.GRAPHITE)
    {
      override def onPress() {
        Utils.openURL("http://doesnotexist.com")
      }
    })


    infoFrame.addComponent(new PSkill(8, 346, Skills.MINING, PSkill.ColorScheme.GRAPHITE))
    PaintController.addComponent(infoFrame)


    PaintController.addComponent(optionFrame)


    PaintController.addComponent(new PFancyButton(440, 345, "Toggle paint", PFancyButton.ColorScheme.GRAPHITE)
    {
      override def onStart() {
        forceMouse = true
        forcePaint = true
      }

      override def onPress() {
        ArteMiner.togglePaint()
        PaintController.toggleEvents()
      }
    })


    PaintController.addComponent(new PFancyButton(440, 365, 73, -1, "Info", PFancyButton.ColorScheme.GRAPHITE)
    {
      override def onStart() {
        setHovered(true)
      }

      override def onPress() {
        ArteMiner.menuIndex = 0
      }

      override def mouseMoved(mouseEvent: MouseEvent) {
        setHovered(pointInButton(mouseEvent.getPoint) || ArteMiner.menuIndex == 0)
      }
    })


    PaintController.addComponent(new PFancyButton(440, 385, 73, -1, "Options", PFancyButton.ColorScheme.GRAPHITE)
    {
      override def onPress() {
        ArteMiner.menuIndex = 1
      }

      override def mouseMoved(mouseEvent: MouseEvent) {
        setHovered(pointInButton(mouseEvent.getPoint) || ArteMiner.menuIndex == 1)
      }
    })


    PaintController.startTimer()
    return true;
  }

  override def loop: Int = {
    try {
      if(!Walking.isRunEnabled && Walking.getEnergy >= Utils.random(70,80))
        Walking.setRun(true)
      status = module.loop()
    } catch {
      case e: Exception => {}
    }
    Utils.random(400, 500)
  }

  def keyTyped(keyEvent: KeyEvent) {
    PaintController.keyTyped(keyEvent)
  }

  def keyPressed(keyEvent: KeyEvent) {
    PaintController.keyPressed(keyEvent)
  }

  def keyReleased(keyEvent: KeyEvent) {
    PaintController.keyReleased(keyEvent)
  }

  def mouseClicked(mouseEvent: MouseEvent) {
    PaintController.mouseClicked(mouseEvent)
  }

  def mousePressed(mouseEvent: MouseEvent) {
    PaintController.mousePressed(mouseEvent)
  }

  def mouseReleased(mouseEvent: MouseEvent) {
    PaintController.mouseReleased(mouseEvent)
  }

  def mouseEntered(mouseEvent: MouseEvent) {
    PaintController.mouseEntered(mouseEvent)
  }

  def mouseExited(mouseEvent: MouseEvent) {
    PaintController.mouseExited(mouseEvent)
  }

  def mouseDragged(mouseEvent: MouseEvent) {
    PaintController.mouseDragged(mouseEvent)
  }

  def mouseMoved(mouseEvent: MouseEvent) {
    PaintController.mouseMoved(mouseEvent)
  }

  def onRepaint(graphics: Graphics) {
    try {
      var clayout: PComponent = null
      try {
        clayout = new PColumnLayout(215,
                                    354,
                                    Array[String]("Status:", "Ores mined (hr):", "Run time:"),
                                    Array[String](status, "" +
                                                          ArteMiner.oresMined +
                                                          " (" +
                                                          Utils.calcPH(ArteMiner.oresMined, PaintController.runTime) +
                                                          ")", PaintController.timeRunning),
                                    new Font("Arial", 0, 9),
                                    PColumnLayout.ColorScheme.GRAPHITE)
      } catch {
        case e: Exception => {
          e.printStackTrace()
        }
      }
      if (ArteMiner.showPaint) {
        val g: Graphics2D = graphics match {
          case x: Graphics2D => x
          case _ => null
        }
        val p: Paint = g.getPaint
        g.setPaint(new GradientPaint(7, 345, new Color(55, 55, 55, 240), 512, 472, new Color(15, 15, 15, 240)))
        g.fillRect(7, 345, 505, 127)
        g.setPaint(p)
        if (clayout != null)
          PaintController.addComponent(clayout)
        PaintController.onRepaint(graphics)
        if (clayout != null)
          PaintController.removeComponent(clayout)
      }
    } catch {
      case ignored: Exception => {}
    }
  }

  def messageReceived(m: MessageEvent) {
    if (m.isAutomated) {
      if (m.getMessage.contains("You manage to mine some"))
        ArteMiner.oresMined += 1
    }
  }
}

object ArteMiner
{
  var menuIndex: Int = 0
  var showPaint = true
  var oresMined = 0

  def togglePaint() {
    showPaint = !showPaint
  }
}