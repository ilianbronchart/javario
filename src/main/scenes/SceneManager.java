package src.main.scenes;

import java.awt.*;

import src.main.Config;
import src.main.basetypes.Scene;
import src.main.globals.Keys;

public class SceneManager {
    private Scene currentScene;

    public SceneManager(){
        currentScene = queueScene(Config.Scenes.MAIN_MENU);
    }

    public void updateScene(){
        currentScene.update();

        if(currentScene.getNextScene() != null){
            currentScene = queueScene(currentScene.getNextScene());
            Keys.resetKeys();
        }
    }

    public void render(Graphics2D g2d) {
        currentScene.render(g2d);
    }

    private Scene queueScene(String nextScene) {
        switch (nextScene) {
            case Config.Scenes.MAIN_MENU:
                return new MainMenu();
            case Config.Scenes.LEVEL_ONE:
                return new LevelOne();
        }

        System.out.println("Error: Could not find scene " + nextScene);
        return currentScene;
    }
}