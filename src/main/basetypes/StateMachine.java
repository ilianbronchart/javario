package src.main.basetypes;

public class StateMachine {
    // Manages states of GameObjects
    public State state;

    public StateMachine(State initialState) {
        state = initialState;
    }

    public void onEvent(String event) {
        State newState = state.onEvent(event);
        if (!newState.equals(state)) {
            state.onExit();
            state = newState;
            state.onEnter(event);
        }
    }

    public void update() {
        state.update();
    }
}