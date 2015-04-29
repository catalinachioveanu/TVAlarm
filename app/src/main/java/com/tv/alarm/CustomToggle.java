package com.tv.alarm;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Paint.Cap;
import android.graphics.Paint.Style;
import android.graphics.RectF;
import android.support.annotation.NonNull;
import android.util.AttributeSet;
import android.view.View;

import com.facebook.rebound.SimpleSpringListener;
import com.facebook.rebound.Spring;
import com.facebook.rebound.SpringConfig;
import com.facebook.rebound.SpringSystem;
import com.facebook.rebound.SpringUtil;

import com.tv.alarm.R;

public class CustomToggle extends View{
	private Spring spring ;
	private float radius;
	private int onBackgroundColor = Color.parseColor("#27ae60");
	private int offBorderColor = Color.parseColor("#bdc3c7");
	private int offColor = Color.parseColor("#ecf0f1");
	private int circleColor = Color.parseColor("#FFFFFF");
	private int borderColor = offBorderColor;
	private Paint paint ;
	private boolean toggleOn = false;
	private int borderWidth = 2;
	private float centerY;
	private float endX;
	private float spotMinX, spotMaxX;
	private int spotSize ;
	private float spotX;
	private float offLineWidth;
	private RectF rect = new RectF();

	private OnToggleChanged listener;

	public CustomToggle(Context context, AttributeSet attrs, int defStyleAttr) {
		super(context, attrs, defStyleAttr);
		setup(attrs);
	}
	public CustomToggle(Context context, AttributeSet attrs) {
		super(context, attrs);
		setup(attrs);
	}

	@Override
	protected void onDetachedFromWindow() {
		super.onDetachedFromWindow();
		spring.removeListener(springListener);
	}

	public void onAttachedToWindow() {
		super.onAttachedToWindow();
		spring.addListener(springListener);
	}

	public void setup(AttributeSet attrs) {
		paint = new Paint(Paint.ANTI_ALIAS_FLAG);
		paint.setStyle(Style.FILL);
		paint.setStrokeCap(Cap.ROUND);

		SpringSystem springSystem = SpringSystem.create();
		spring = springSystem.createSpring();
		spring.setSpringConfig(SpringConfig.fromOrigamiTensionAndFriction(50, 7));

		this.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				toggle();
			}
		});

		TypedArray typedArray = getContext().obtainStyledAttributes(attrs, R.styleable.ToggleButton);
		offBorderColor = typedArray.getColor(R.styleable.ToggleButton_offBorderColor, offBorderColor);
		onBackgroundColor = typedArray.getColor(R.styleable.ToggleButton_onColor, onBackgroundColor);
		circleColor = typedArray.getColor(R.styleable.ToggleButton_spotColor, circleColor);
		offColor = typedArray.getColor(R.styleable.ToggleButton_offColor, offColor);
		borderWidth = typedArray.getDimensionPixelSize(R.styleable.ToggleButton_borderWidth, borderWidth);
		typedArray.recycle();
	}

	public void toggle() {
		toggleOn = !toggleOn;
		spring.setEndValue(toggleOn ? 1 : 0);
		if(listener != null){
			listener.onToggle(toggleOn);
		}
	}

	public void setToggleOn() {
		toggleOn = true;
		spring.setEndValue(1);
	}


	public void setToggleOff() {
		toggleOn = false;
		spring.setEndValue(0);
	}

    @Override
	protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
		super.onLayout(changed, left, top, right, bottom);

		final int width = getWidth();
		final int height = getHeight();
		radius = Math.min(width, height) * 0.5f;
		centerY = radius;
		float startX = radius;
		endX = width - radius;
		spotMinX = startX + borderWidth;
		spotMaxX = endX - borderWidth;
		spotSize = height - 4 * borderWidth;
		spotX = spotMinX;
		offLineWidth = 0;
	}


	SimpleSpringListener springListener = new SimpleSpringListener(){
		@Override
		public void onSpringUpdate(Spring spring) {
			final double value = spring.getCurrentValue();

			spotX = (float) SpringUtil.mapValueFromRangeToRange(value, 0, 1, spotMinX, spotMaxX);

			offLineWidth = (float) SpringUtil.mapValueFromRangeToRange(1 - value, 0, 1, 10, spotSize);

			final int fb = Color.blue(onBackgroundColor);
			final int fr = Color.red(onBackgroundColor);
			final int fg = Color.green(onBackgroundColor);

			final int tb = Color.blue(offBorderColor);
			final int tr = Color.red(offBorderColor);
			final int tg = Color.green(offBorderColor);

			int sb = (int) SpringUtil.mapValueFromRangeToRange(1 - value, 0, 1, fb, tb);
			int sr = (int) SpringUtil.mapValueFromRangeToRange(1 - value, 0, 1, fr, tr);
			int sg = (int) SpringUtil.mapValueFromRangeToRange(1 - value, 0, 1, fg, tg);

			sb = clamp(sb, 0, 255);
			sr = clamp(sr, 0, 255);
			sg = clamp(sg, 0, 255);

			borderColor = Color.rgb(sr, sg, sb);

			postInvalidate();
		}
	};

	private int clamp(int value, int low, int high) {
		return Math.min(Math.max(value, low), high);
	}


	@Override
	public void draw(@NonNull Canvas canvas) {

		rect.set(0, 0, getWidth(), getHeight());
		paint.setColor(borderColor);
		canvas.drawRoundRect(rect, radius, radius, paint);

		if(offLineWidth > 0){
			final float cy = offLineWidth * 0.5f;
			rect.set(spotX - cy, centerY - cy, endX + cy, centerY + cy);
			paint.setColor(offColor);
			canvas.drawRoundRect(rect, cy, cy, paint);
		}

		rect.set(spotX - 1 - radius, centerY - radius, spotX + 1.1f + radius, centerY + radius);
		paint.setColor(borderColor);
		canvas.drawRoundRect(rect, radius, radius, paint);

		final float spotR = spotSize * 0.5f;
		rect.set(spotX - spotR, centerY - spotR, spotX + spotR, centerY + spotR);
		paint.setColor(circleColor);
		canvas.drawRoundRect(rect, spotR, spotR, paint);

	}

	public void setChecked(boolean repeatingDay)
	{
		if (repeatingDay) setToggleOn();
		else setToggleOff();

	}

	public void setOnCheckedChangeListener(OnToggleChanged onCheckedChangeListener)
	{
		listener = onCheckedChangeListener;
	}

	public interface OnToggleChanged{
		public void onToggle(boolean on);
	}
}