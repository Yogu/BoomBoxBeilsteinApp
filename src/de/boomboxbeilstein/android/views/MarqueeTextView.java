package de.boomboxbeilstein.android.views;

import android.content.Context;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.widget.TextView;

public class MarqueeTextView extends TextView {
	public MarqueeTextView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		init();
	}

	public MarqueeTextView(Context context, AttributeSet attrs) {
		super(context, attrs);
		init();
	}

	public MarqueeTextView(Context context) {
		super(context);
		init();
	}
	
	private void init() {
		setMarqueeRepeatLimit(-1);
	}

	@Override
	protected void onFocusChanged(boolean focused, int direction, Rect previouslyFocusedRect) {
		if (focused)
			super.onFocusChanged(focused, direction, previouslyFocusedRect);
	}

	@Override
	public void onWindowFocusChanged(boolean focused) {
		if (focused)
			super.onWindowFocusChanged(focused);
	}

	@Override
	public boolean isFocused() {
		return true;
	}
	
	public void setTextLazily(CharSequence newValue) {
		if (newValue == null)
			newValue = "";
		
		if (!newValue.equals(getText()))
			setText(newValue);
	}
}
