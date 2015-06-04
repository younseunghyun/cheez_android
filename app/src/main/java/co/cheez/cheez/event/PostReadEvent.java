package co.cheez.cheez.event;

import co.cheez.cheez.model.ReadPostRel;

/**
 * Created by jiho on 5/28/15.
 */
public class PostReadEvent {
    public ReadPostRel data;
    public PostReadEvent(ReadPostRel readPostRel) {
        data = readPostRel;
    }
}
