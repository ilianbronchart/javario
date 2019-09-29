package src.main.utils;

import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

import src.main.globals.Keys;

public class KeyListener extends KeyAdapter {
    public void keyPressed(KeyEvent evt) {
        //System.out.println(evt.getKeyCode());

        switch (evt.getKeyCode()) {
            case 87:
                Keys.up = true;
                break;
                case 65:
                Keys.left = true;
                break;
            case 83:
                Keys.down = true;
                break;
            case 68:
                Keys.right = true;
                break;
            case 32:
                Keys.space = true;
                break;
            case 10:
                Keys.enter = true;
                break;
        }
    }

    public void keyReleased(KeyEvent evt){
        switch (evt.getKeyCode()) {
            case 87:
                Keys.up = false;
                break;
            case 65:
                Keys.left = false;
                break;
            case 83:
                Keys.down = false;
                break;
            case 68:
                Keys.right = false;
                break;
            case 32:
                Keys.space = false;
                break;
            case 10:
                Keys.enter = false;
                break;
        }
    }
}