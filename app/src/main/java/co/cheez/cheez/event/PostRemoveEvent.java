package co.cheez.cheez.event;

import co.cheez.cheez.model.Post;

/**
 * Created by jiho on 6/8/15.
 */
public class PostRemoveEvent {
    public Post post;

    public PostRemoveEvent(Post post) {
        this.post = post;
    }
}
