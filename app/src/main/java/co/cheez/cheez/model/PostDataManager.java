package co.cheez.cheez.model;

import java.util.ArrayList;

import co.cheez.cheez.event.PostAddedEvent;
import de.greenrobot.event.EventBus;

/**
 * Created by jiho on 5/18/15.
 */
public class PostDataManager {
    private static PostDataManager mInstance = new PostDataManager();
    private ArrayList<Post> mPostList;

    public static PostDataManager getInstance() {
        return mInstance;
    }

    private PostDataManager() {
        mPostList = new ArrayList<>();
    }

    // insert operations
    public ArrayList<Post> getPostList() {
        return mPostList;
    }

    public void append(Post post) {
        mPostList.add(post);
        EventBus.getDefault().post(
                new PostAddedEvent(PostAddedEvent.LAST)
        );
    }

    public void prepend(Post post) {
        insert(0, post);
    }

    public void insert(int index, Post post) {
        mPostList.add(index, post);
        EventBus.getDefault().post(
                new PostAddedEvent(index)
        );
    }


    public Post getPostAtPosition(int position) {
        return mPostList.get(position);
    }

    public void clear() {
        mPostList.clear();
    }
}
