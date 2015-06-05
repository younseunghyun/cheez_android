package co.cheez.cheez.view;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.CompoundButton;
import android.widget.ToggleButton;

import com.joanzapata.android.iconify.Iconify;

public class IconToggleButton extends ToggleButton {
    private OnCheckedChangeListener mCheckedChangeListener;


    public IconToggleButton(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init();
    }

    public IconToggleButton(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public IconToggleButton(Context context) {
        super(context);
        init();
    }

    private void init() {
        if (!isInEditMode())
            Iconify.addIcons(this);
        else {
            this.setTextOn(this.getTextOn());
            this.setTextOff(this.getTextOff());
        }

        super.setOnCheckedChangeListener(new OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    setTextOn(getTextOn());
                } else {
                    setTextOff(getTextOff());
                }
                if (mCheckedChangeListener != null) {
                    mCheckedChangeListener.onCheckedChanged(buttonView, isChecked);
                }
            }
        });
    }

    @Override
    public void setTextOn(CharSequence textOn) {
        super.setTextOn(Iconify.compute(textOn));
    }

    @Override
    public void setTextOff(CharSequence textOff) {
        super.setTextOff(Iconify.compute(textOff));
    }

    @Override
    public void setOnCheckedChangeListener(OnCheckedChangeListener listener) {
        this.mCheckedChangeListener = listener;
    }
}