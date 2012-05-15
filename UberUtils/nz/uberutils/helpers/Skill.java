package nz.uberutils.helpers;

import com.rsbuddy.script.methods.Skills;
import nz.uberutils.methods.UberSkills;

import java.awt.*;
import java.text.NumberFormat;
import java.util.Locale;

/**
 * Created by IntelliJ IDEA.
 * User: Taylor
 * Date: 3/4/11
 * Time: 2:19 PM
 * Package: nz.artedungeon.utils;
 */
public class Skill
{
    private final long   startime;
    private final int    startxp;
    private final int    startLevel;
    private final int    skillint;
    private       int    lastxp;
    private       int    lastLevel;
    private final String name;

    public static final Skill[] skills = {new Skill(0),
                                          new Skill(1),
                                          new Skill(2),
                                          new Skill(3),
                                          new Skill(4),
                                          new Skill(5),
                                          new Skill(6),
                                          new Skill(7),
                                          new Skill(8),
                                          new Skill(9),
                                          new Skill(10),
                                          new Skill(11),
                                          new Skill(12),
                                          new Skill(13),
                                          new Skill(14),
                                          new Skill(15),
                                          new Skill(16),
                                          new Skill(17),
                                          new Skill(18),
                                          new Skill(19),
                                          new Skill(20),
                                          new Skill(21),
                                          new Skill(22),
                                          new Skill(23),
                                          new Skill(24),};
    public final String shortName;
    public static NumberFormat nf = NumberFormat.getNumberInstance(new Locale("en", "IN"));

    /**
     * Constructs a new Skill
     *
     * @param skillInt the index of the Skill
     */
    public Skill(int skillInt) {
        startime = System.currentTimeMillis();
        startxp = Skills.getCurrentExp(skillInt);
        startLevel = UberSkills.getRealLevel(skillInt);
        skillint = skillInt;
        lastxp = startxp;
        lastLevel = startLevel;
        name = Skills.SKILL_NAMES[skillint].substring(0, 1).toUpperCase() + Skills.SKILL_NAMES[skillint].substring(1);
        shortName = getShortName();
    }

    public int xpGained() {
        return Skills.getCurrentExp(skillint) - startxp;
    }

    public int levelsGained() {
        return curLevel() - startLevel;
    }

    public int xpTL() {
        return UberSkills.getExpToNextLevel(skillint);
    }

    public int percentTL() {
        return UberSkills.getPercentToNextLevel(skillint);
    }

    public int xpPH() {
        return (int) ((xpGained()) * 3600000D / (System.currentTimeMillis() - startime));
    }

    public int curLevel() {
        return UberSkills.getRealLevel(skillint);
    }

    public int curXP() {
        return Skills.getCurrentExp(skillint);
    }

    public String timeToLevel() {
        String TTL = "Calculating..";
        long ttlCalculations;
        if (xpPH() != 0) {
            ttlCalculations = (long) (xpTL() * 3600000D) / xpPH();
            TTL = getTime(ttlCalculations);
        }
        return TTL;
    }

    public String getName() {
        return name;
    }

    public void drawSkill(Graphics2D g, int x, int y) {
        g.drawString(name +
                     ": Gained " +
                     xpGained() +
                     " P/H " +
                     xpPH() +
                     " | TTL: " +
                     timeToLevel() +
                     " | Level " +
                     curLevel() +
                     " (" +
                     levelsGained() +
                     ")", x, y);
    }

    private String getTime(long millis) {
        long time = millis / 1000;
        String seconds = Integer.toString((int) (time % 60));
        String minutes = Integer.toString((int) ((time % 3600) / 60));
        String hours = Integer.toString((int) (time / 3600));
        for (int i = 0; i < 2; i++) {
            if (seconds.length() < 2) {
                seconds = "0" + seconds;
            }
            if (minutes.length() < 2) {
                minutes = "0" + minutes;
            }
            if (hours.length() < 2) {
                hours = "0" + hours;
            }
        }
        return hours + ":" + minutes + ":" + seconds;
    }

    public int getXpMinusLast() {
        int returnXP = curXP() - lastxp;
        lastxp = curXP();
        return returnXP;
    }

    public int getLevelMinusLast() {
        int returnLevel = curLevel() - lastLevel;
        lastLevel = curLevel();
        return returnLevel;
    }

    public int getSkill() {
        return skillint;
    }

    public final String getShortName() {
        switch (skillint) {
            case Skills.AGILITY:
                return "AGIL";
            case Skills.ATTACK:
                return "ATT";
            case Skills.DEFENSE:
                return "DEF";
            case Skills.DUNGEONEERING:
                return "DUNG";
            case Skills.MAGIC:
                return "MAGE";
            case Skills.CONSTITUTION:
                return "HP";
            case Skills.CONSTRUCTION:
                return "CONS";
            case Skills.SMITHING:
                return "SMITH";
            case Skills.CRAFTING:
                return "CRAFT";
            case Skills.SLAYER:
                return "SLAY";
            case Skills.FLETCHING:
                return "FLETCH";
            case Skills.COOKING:
                return "COOK";
            case Skills.FARMING:
                return "FARM";
            case Skills.FIREMAKING:
                return "FMING";
            case Skills.WOODCUTTING:
                return "WCING";
            case Skills.HERBLORE:
                return "HERB";
            case Skills.HUNTER:
                return "HUNT";
            case Skills.PRAYER:
                return "PRAY";
            case Skills.RUNECRAFTING:
                return "RCING";
            case Skills.MINING:
                return "MINING";
            case Skills.THIEVING:
                return "THIEVE";
            case Skills.SUMMONING:
                return "SUMMON";
            case Skills.STRENGTH:
                return "STR";
            default:
                return "UNK";

        }
    }
}
