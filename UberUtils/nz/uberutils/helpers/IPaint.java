package nz.uberutils.helpers;

import java.awt.*;
import java.awt.event.KeyListener;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.util.ArrayList;

public interface IPaint extends MouseListener, MouseMotionListener, KeyListener
{
    public boolean paint(Graphics g);
}
