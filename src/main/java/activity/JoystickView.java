package activity;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.drawable.BitmapDrawable;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import net.londatiga.android.bluetooth.R;

//implements Runnable
public class JoystickView extends View  {
	// Constants
	private final double RAD = 57.2957795;
	public final static long DEFAULT_LOOP_INTERVAL = 100; // 100 ms
	public final static int FRONT = 3;
	public final static int FRONT_RIGHT = 4;
	public final static int RIGHT = 5;
	public final static int RIGHT_BOTTOM = 6;
	public final static int BOTTOM = 7;
	public final static int BOTTOM_LEFT = 8;
	public final static int LEFT = 1;
	public final static int LEFT_FRONT = 2;
	// Variables
//	private OnJoystickMoveListener onJoystickMoveListener; // Listener
//	private Thread thread = new Thread(this);
	private long loopInterval = DEFAULT_LOOP_INTERVAL;
	private int xPosition = 0; // Touch x position
	private int yPosition = 0; // Touch y position
	private double centerX = 0; // Center view x position
	private double centerY = 0; // Center view y position
	private Paint mainCircle;
	private Paint secondaryCircle;
	private Paint button;
	private Paint horizontalLine;
	private Paint verticalLine;
	private int joystickRadius;
	private int buttonRadius;
	private int lastAngle = 0;
	private int lastPower = 0;

	public JoystickView(Context context) {
		super(context);
	}

	public JoystickView(Context context, AttributeSet attrs) {
		super(context, attrs);
		initJoystickView();
	}

	public JoystickView(Context context, AttributeSet attrs, int defaultStyle) {
		super(context, attrs, defaultStyle);
		initJoystickView();
	}

//    public void Setup()
//    {
//        // 1.we only need to setup for one time
////        if (bHasSetup)
////        {
////            return;
////        }
//
//        // 2. get view parameter
////        m_ViewWidth = RockerView.this.getWidth();
////        m_ViewHeight = RockerView.this.getHeight();
////        m_ViewOriginX = m_ViewWidth / 2;
////        m_ViewOriginY = m_ViewHeight / 2;
//
////        // 3. !!!:we should use square/circle bitmap
////        BitmapDrawable drawable = (BitmapDrawable) getContext().getResources().getDrawable(R.drawable.table);
////        Bitmap bmp = drawable.getBitmap();
//////        int density = bmp.getDensity();
////        int height = bmp.getHeight();
//////        float Dpi = ((float) density) / 240;
////        // suppose height == width
////        float WidthScaleRate = (float) m_ViewWidth / height;
////        float HeightScaleRate = (float) m_ViewHeight / height;
////        float MinScaleRate = WidthScaleRate < HeightScaleRate ? WidthScaleRate : HeightScaleRate;
////        if (MinScaleRate > (float) 2.0)
////        {
////            MinScaleRate = (float) 2.0;
////        }
////        Matrix matrix = new Matrix();
////        matrix.postScale(MinScaleRate, MinScaleRate);
////        bmpTouchNothing = Bitmap.createBitmap(bmp, 0, 0, bmp.getWidth(), bmp.getHeight(), matrix, true);
////
////        BitmapWidth = bmpTouchNothing.getWidth();
////        BitmapHeight = bmpTouchNothing.getHeight();
////
////        // 4
////        drawable = (BitmapDrawable) getContext().getResources().getDrawable(R.drawable.ic_rocker_click);
////        bmp = drawable.getBitmap();
////        bmpTouching = Bitmap.createBitmap(bmp, 0, 0, bmp.getWidth(), bmp.getHeight(), matrix, true);
////
////        // 5
////        bHasSetup = true;
//    }



    protected void initJoystickView() {
		mainCircle = new Paint(Paint.ANTI_ALIAS_FLAG);
		mainCircle.setColor(Color.WHITE);
		mainCircle.setStyle(Paint.Style.FILL_AND_STROKE);

		secondaryCircle = new Paint();
		secondaryCircle.setColor(Color.GREEN);
		secondaryCircle.setStyle(Paint.Style.STROKE);

		verticalLine = new Paint();
		verticalLine.setStrokeWidth(5);
		verticalLine.setColor(Color.RED);

		horizontalLine = new Paint();
		horizontalLine.setStrokeWidth(2);
		horizontalLine.setColor(Color.BLACK);

		button = new Paint(Paint.ANTI_ALIAS_FLAG);
		button.setColor(Color.RED);
		button.setStyle(Paint.Style.FILL);
	}

	@Override
	protected void onFinishInflate() {
	}

	@Override
	protected void onSizeChanged(int xNew, int yNew, int xOld, int yOld) {
		super.onSizeChanged(xNew, yNew, xOld, yOld);
		// before measure, get the center of view
		xPosition = (int) getWidth() / 2;
		yPosition = (int) getWidth() / 2;
		int d = Math.min(xNew, yNew);
		buttonRadius = (int) (d / 2 * 0.25);
		joystickRadius = (int) (d / 2 * 0.75);

	}

	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		// setting the measured values to resize the view to a certain width and
		// height
		int d = Math.min(measure(widthMeasureSpec), measure(heightMeasureSpec));

		setMeasuredDimension(d, d);

	}

	private int measure(int measureSpec) {
		int result = 0;

		// Decode the measurement specifications.
		int specMode = MeasureSpec.getMode(measureSpec);
		int specSize = MeasureSpec.getSize(measureSpec);

		if (specMode == MeasureSpec.UNSPECIFIED) {
			// Return a default size of 200 if no bounds are specified.
			result = 200;
		} else {
			// As you want to fill the available space
			// always return the full available bounds.
			result = specSize;
		}
		return result;
	}

	@Override
	protected void onDraw(Canvas canvas) {
		super.onDraw(canvas);
		centerX = (getWidth()) / 2;
		centerY = (getHeight()) / 2;

		// painting the main circle
		canvas.drawCircle((int) centerX, (int) centerY, joystickRadius,mainCircle);
		// painting the move button
		canvas.drawCircle(xPosition, yPosition, buttonRadius, button);
	}


	@Override
	public boolean onTouchEvent(MotionEvent event) {
		xPosition = (int) event.getX();
		yPosition = (int) event.getY();


		double abs = Math.sqrt((xPosition - centerX) * (xPosition - centerX)
                + (yPosition - centerY) * (yPosition - centerY));
		if (abs > joystickRadius) {
			xPosition = (int) ((xPosition - centerX) * joystickRadius / abs + centerX);
			yPosition = (int) ((yPosition - centerY) * joystickRadius / abs + centerY);
		}
		invalidate();
		if (event.getAction() == MotionEvent.ACTION_UP) {
			xPosition = (int) centerX;
			yPosition = (int) centerY;
//			thread.interrupt();
//			if (onJoystickMoveListener != null)
//				onJoystickMoveListener.onValueChanged(getAngle(), getPower(),
//						getDirection());
		}
//		if (onJoystickMoveListener != null
//				&& event.getAction() == MotionEvent.ACTION_DOWN) {
//			if (thread != null && thread.isAlive()) {
//				thread.interrupt();
//			}
//			thread = new Thread(this);
//			thread.start();
//			if (onJoystickMoveListener != null)
//				onJoystickMoveListener.onValueChanged(getAngle(), getPower(),
//						getDirection());
//		}
		return true;
	}

//	private int getAngle() {
//		if (xPosition > centerX) {
//			if (yPosition < centerY) {
//				return lastAngle = (int) (Math.atan((yPosition - centerY)
//                        / (xPosition - centerX))
//						* RAD + 90);
//			} else if (yPosition > centerY) {
//				return lastAngle = (int) (Math.atan((yPosition - centerY)
//                        / (xPosition - centerX)) * RAD) + 90;
//			} else {
//				return lastAngle = 90;
//			}
//		} else if (xPosition < centerX) {
//			if (yPosition < centerY) {
//				return lastAngle = (int) (Math.atan((yPosition - centerY)
//                        / (xPosition - centerX))
//						* RAD - 90);
//			} else if (yPosition > centerY) {
//				return lastAngle = (int) (Math.atan((yPosition - centerY)
//                        / (xPosition - centerX)) * RAD) - 90;
//			} else {
//				return lastAngle = -90;
//			}
//		} else {
//			if (yPosition <= centerY) {
//				return lastAngle = 0;
//			} else {
//				if (lastAngle < 0) {
//					return lastAngle = -180;
//				} else {
//					return lastAngle = 180;
//				}
//			}
//		}
//	}

    public double getRoll()
    {
        return 20.0 * (xPosition - centerX) / joystickRadius;
    }

    public double getPitch()
    {
        return 20.0 *  (centerY - yPosition ) / joystickRadius;
    }



    private int getPower() {
		return (int) (100 * Math.sqrt((xPosition - centerX)
                * (xPosition - centerX) + (yPosition - centerY)
                * (yPosition - centerY)) / joystickRadius);
	}

	private int getDirection() {
		if (lastPower == 0 && lastAngle == 0) {
			return 0;
		}
		int a = 0;
		if (lastAngle <= 0) {
			a = (lastAngle * -1) + 90;
		} else if (lastAngle > 0) {
			if (lastAngle <= 90) {
				a = 90 - lastAngle;
			} else {
				a = 360 - (lastAngle - 90);
			}
		}

		int direction = (int) (((a + 22) / 45) + 1);

		if (direction > 8) {
			direction = 1;
		}
		return direction;
	}

//	public void setOnJoystickMoveListener(OnJoystickMoveListener listener,
//			long repeatInterval) {
//		this.onJoystickMoveListener = listener;
//		this.loopInterval = repeatInterval;
//	}

//	public static interface OnJoystickMoveListener {
//		public void onValueChanged(int angle, int power, int direction);
//	}





//	@Override
//	public void run() {
//		while (!Thread.interrupted()) {
//			post(new Runnable() {
//				public void run() {
//					if (onJoystickMoveListener != null)
//						onJoystickMoveListener.onValueChanged(getAngle(),
//								getPower(), getDirection());
//				}
//			});
//			try {
//				Thread.sleep(loopInterval);
//			} catch (InterruptedException e) {
//				break;
//			}
//		}
//	}
}