package co.cheez.cheez.event;

import com.sothree.slidinguppanel.SlidingUpPanelLayout;

/**
 * Created by jiho on 5/29/15.
 */
public class PanelStateChangedEvent {
    public SlidingUpPanelLayout.PanelState mPanelState;

    public PanelStateChangedEvent(SlidingUpPanelLayout.PanelState state) {
        mPanelState = state;
    }
}
