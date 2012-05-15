package nz.artefalconry;

import com.rsbuddy.event.events.MessageEvent;
import com.rsbuddy.event.listeners.MessageListener;
import com.rsbuddy.event.listeners.PaintListener;
import com.rsbuddy.script.Manifest;
import com.rsbuddy.script.methods.*;
import com.rsbuddy.script.util.Filter;
import com.rsbuddy.script.util.Random;
import com.rsbuddy.script.wrappers.Npc;
import nz.artefalconry.misc.GameConstants;
import nz.artefalconry.stratagies.CatchKebbit;
import nz.artefalconry.stratagies.DropJunk;
import nz.artefalconry.stratagies.ReGetFalcon;
import nz.artefalconry.stratagies.TakeFalcon;
import nz.uberutils.helpers.Options;
import nz.uberutils.helpers.Skill;
import nz.uberutils.helpers.UberScript;
import nz.uberutils.helpers.Utils;
import nz.uberutils.paint.PaintController;
import nz.uberutils.paint.components.PCheckBox;
import nz.uberutils.paint.paints.UberPaint;

import java.util.Arrays;

@Manifest(authors = "UberMouse",
        name = "ArteFalconry",
        keywords = "Hunter",
        version = 1.0,
        description = "Catches kebbits at falconry area")
public class ArteFalconry extends UberScript implements MessageListener, PaintListener
{
    //IDs
    public static int[] kebbitCatchIDs;

    public static int     kebbitsCaught = 0;
    public static boolean hasFalcon     = true;

    public boolean onBegin() {
        threadId = 412;
        UberPaint p = new UberPaint("ArteFalconry", threadId, getClass().getAnnotation(Manifest.class).version());
        p.skills.add(new Skill(Skills.HUNTER));
        p.skills.add(new Skill(Skills.PRAYER));
        strategies.add(new ReGetFalcon());
        strategies.add(new DropJunk());
        strategies.add(new CatchKebbit());
        strategies.add(new TakeFalcon());
        Options.add("spotted", Skills.getRealLevel(Skills.HUNTER) <= 69);
        Options.add("dark", Skills.getRealLevel(Skills.HUNTER) >= 57);
        Options.add("dashing", Skills.getRealLevel(Skills.HUNTER) >= 69);
        p.infoColumnValues = new String[]{"Status:", "Kebbits caught:", "Run time:"};
        p.addFrame("options");
        p.getFrame("options").addComponent(new PCheckBox(8, 356, "Hunt spotted: ", Options.getBoolean("spotted"), 65)
        {
            public void onPress() {
                if (Options.getBoolean("dashing") || Options.getBoolean("dark")) {
                    Options.flip("spotted");
                    setKebbits();
                }
            }
        });
        p.getFrame("options").addComponent(new PCheckBox(8, 371, "Hunt dark: ", Options.getBoolean("dark"), 74)
        {
            public void onPress() {
                if (Options.getBoolean("spotted") || Options.getBoolean("dashing")) {
                    Options.flip("dark");
                    setKebbits();
                }
            }
        });
        p.getFrame("options").addComponent(new PCheckBox(8, 387, "Hunt dashing: ", Options.getBoolean("dashing"), 18)
        {
            public void onPress() {
                if (Options.getBoolean("spotted") || Options.getBoolean("dark")) {
                    Options.flip("dashing");
                    setKebbits();
                }
            }
        });
        setKebbits();
        ReGetFalcon.oldSetting = Settings.get(334);
        paintType = p;
        return true;
    }

    public int random(int min, int max) {
        return Random.nextInt(min, max);
    }


    @Override
    public void miscLoop() {
        if (Widgets.canContinue())
            Widgets.clickContinue();
        Mouse.setSpeed(random(1, 2));
        ((UberPaint) paintType).infoColumnData = new String[]{status,
                                                              "" + kebbitsCaught,
                                                              PaintController.timeRunning()};
    }

    public static Npc getFalcon() {
        return Npcs.getNearest(new Filter<Npc>()
        {
            public boolean accept(Npc npc) {
                if (Utils.arrayContains(GameConstants.falconIDs, npc.getId())) {
                    //                    Set<HintArrow> arrows = Game.getHintArrows();
                    //                    for (HintArrow a : arrows) {
                    //                        if (a.getLocation().equals(npc.getLocation()))
                    //                            return true;
                    //                    }
                    return true;
                }
                return false;
            }
        });
    }

    public void messageReceived(MessageEvent e) {
        String txt = e.getMessage();
        if (txt.contains("creature but it is")) {
            hasFalcon = false;
        }
    }

    public void setKebbits() {
        int[] kebbits = new int[0];
        if (Options.getBoolean("dashing")) {
            kebbits = Arrays.copyOf(kebbits, kebbits.length + 1);
            kebbits[kebbits.length - 1] = GameConstants.DASHING_KEBBIT;
        }
        if (Options.getBoolean("dark")) {
            kebbits = Arrays.copyOf(kebbits, kebbits.length + 1);
            kebbits[kebbits.length - 1] = GameConstants.DARK_KEBBIT;
        }
        if (Options.getBoolean("spotted")) {
            kebbits = Arrays.copyOf(kebbits, kebbits.length + 1);
            kebbits[kebbits.length - 1] = GameConstants.SPOTTED_KEBBIT;
        }
        kebbitCatchIDs = kebbits;
    }
}