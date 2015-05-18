package co.cheez.cheez.event;

/**
 * Created by jiho on 5/18/15.
 */
public class PostAddedEvent {
    public static final int LAST = -1;
    public int index;

    public PostAddedEvent(int index) {
        this.index = index;
    }
}
