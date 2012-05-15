package nz.ubermouse.artehunter.ui;

import com.rsbuddy.script.methods.Game;
import com.rsbuddy.script.methods.Skills;
import nz.ubermouse.artehunter.Hunter;

import java.awt.*;

public abstract class PaintShell {
        private long startEXP = -1;
        private int startLevel = -1;

        public String getTimeTillLevel() {
            if (startEXP == -1) {
                if (Game.isLoggedIn()) {
                    startEXP = Skills.getCurrentExp(Skills.HUNTER);
                }
            }
            long expGained = Skills.getCurrentExp(Skills.HUNTER) - startEXP;
            try {
                long expToNextLevel = Skills.getExpToNextLevel(Skills.HUNTER);
                long expGainedPerHour = (expGained > 0 ? (int) ((expGained * 60 * 60) / (Hunter.timeRun
                        .getElapsed() / 1000))
                        : 0);
                if (expGainedPerHour > 0) {
                    long sTNL = (expToNextLevel) / (expGainedPerHour / 3600);
                    long hTNL = sTNL / (60 * 60);
                    sTNL -= hTNL * (60 * 60);
                    long mTNL = sTNL / 60;
                    sTNL -= mTNL * 60;
                    return hTNL + ":" + mTNL + ":" + sTNL;
                } else
                    return "Calculating..";
            } catch (Exception e) {
                return "Calculating..";
            }
        }

        public int getExpTillLevel() {
            return Skills.getExpToNextLevel(Skills.HUNTER);
        }

        public String getLevelInfo() {
            if (startLevel == -1) {
                if (Game.isLoggedIn()) {
                    startLevel = Skills.getCurrentLevel(Skills.HUNTER);
                }
            }
            if (Game.isLoggedIn()) {
                return "[" + Skills.getCurrentLevel(Skills.HUNTER) + "/"
                        + startLevel + "]";
            }
            return null;
        }

        public void paint(final Graphics render) {
            _paint(render);
        }

        public abstract void _paint(final Graphics render);
    }