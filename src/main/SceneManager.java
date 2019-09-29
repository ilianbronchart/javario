package src.main;

import java.util.HashMap;
import java.util.Map;
import java.awt.*;

import src.main.basetypes.Scene;
import src.main.scenes.*;

public class SceneManager {
    Map<String, Scene> scenes = new HashMap<String, Scene>();
    String currentScene;

    public SceneManager(){
        scenes.put(Config.Scenes.MAIN_MENU, new MainMenu());
        scenes.put(Config.Scenes.LEVEL_ONE, new LevelOne());
        currentScene = Config.Scenes.MAIN_MENU;

    }

    public void updateScene(){
        scenes.get(currentScene).update();

        String nextScene = scenes.get(currentScene).nextScene;
        if(nextScene != null){
            queueScene(nextScene);
        }
    }

    public void render(Graphics2D g2d) {
        scenes.get(currentScene).render(g2d);
    }

    public void queueScene(String nextScene) {
        currentScene = nextScene;
    }

    public Scene getCurrentScene() {
        return scenes.get(currentScene);
    }
}