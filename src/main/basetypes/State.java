package src.main.basetypes;

public class State {
    public State onEvent(String event){
        // Returns new state based on event
        return new State();
    }

    public void onEnter(String event) {
        // Performs actions when entering state
    }

    public void update() {
        // Performs actions specific to state when active
    }

    public void onExit() {
        // Performs actions when exiting state
    }
}
