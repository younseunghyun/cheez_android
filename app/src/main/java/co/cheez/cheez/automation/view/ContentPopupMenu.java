package co.cheez.cheez.automation.view;

import android.content.Context;
import android.support.v7.widget.PopupMenu;
import android.view.MenuItem;
import android.view.View;

import co.cheez.cheez.R;

/**
 * Created by jiho on 5/22/15.
 */
public class ContentPopupMenu extends PopupMenu implements PopupMenu.OnMenuItemClickListener {
    private long mPostId;

    public ContentPopupMenu(Context context, View view, long postId) {
        super(context, view);
        mPostId = postId;
        initialize();
    }

    private void initialize() {
        inflate(R.menu.menu_content);
        setOnMenuItemClickListener(this);
    }


    @Override
    public boolean onMenuItemClick(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_report:

                break;
            case R.id.menu_show_tag:

                break;
            case R.id.menu_save:

                break;
        }
        return true;
    }
}
