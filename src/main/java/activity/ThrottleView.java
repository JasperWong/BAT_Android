package activity;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import net.londatiga.android.bluetooth.R;

/**
 * Created by project_x on 2015/4/11.
 */
public class ThrottleView extends View {

    private Context mcontext;

    public static int Value = 0;
    private int Value_height = 0;
    public static int Value_max = 255;


    //
    public int left;
    public int top;
    public int right;
    public int bottom = 1;

    //

    private int Circle_Radius = 0;
    private int Rect_Width = 0;
    private int Rect_Height = 0;


    public int savedPointerIndex;


    Paint p_line = new Paint();
    Paint p_background = new Paint();
    Paint p_active = new Paint();
    Paint paintBall = new Paint();
    Paint p_text = new Paint();

    String youmen_string = "BAT-CRAFT";
    float youmen_text_width = 0;

    int color_line = Color.GRAY;
    int color_background = Color.BLUE;
    int color_active = Color.RED;
    int color_text = R.color.springgreen;

    static boolean isInsideRect(float x, float y, float left, float top, float right, float bottom) {
        if ((x > left) && (x < right) && (y > top) && (y < bottom)) {
            return true;
        }

        return false;
    }

    public ThrottleView(Context context) {
        super(context);
        // TODO Auto-generated constructor stub
    }

    public ThrottleView(Context context, AttributeSet set) {
        super(context, set);
        mcontext = context;

        p_line.setAntiAlias(true);
        p_line.setColor(color_line);

        p_background.setAntiAlias(true);
        p_background.setColor(color_background);
        //p_background.setAlpha(150);

        p_active.setAntiAlias(true);
        p_active.setColor(color_active);

        paintBall.setAntiAlias(true);
        paintBall.setColor(Color.WHITE);
        //p_active.setAlpha(150);

        p_text.setAntiAlias(true);
        p_text.setColor(color_text);
        //p_text.setAlpha(255);
        p_text.setTextSize(30);
        youmen_text_width = p_text.measureText(youmen_string);

    }

    int centerX = 0;
    int centerY = 0;

    @Override
    public void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        Circle_Radius = getWidth()/10;
        left = getLeft();
        top = getTop();
        right =getRight();
        bottom = getBottom();
        centerX = (left+right)/2;
        centerY =(top+ bottom)/2;
        Rect_Width = getWidth()/10;
        Rect_Height = getHeight();


        canvas.drawRect(centerX-Rect_Width/2, top , centerX+Rect_Width/2, Rect_Height, p_line);
        canvas.drawRect(centerX-Rect_Width/2, bottom  -Value_height, centerX+Rect_Width/2, Rect_Height, p_active);
        canvas.drawCircle(centerX,bottom - Value_height,Circle_Radius,paintBall);

//        canvas.drawRect(left, top, right, bottom, p_line);
//        canvas.drawRect(left, bottom - Value_height, right, bottom, p_active);
    }


    public int getValue()
    {
        if( Value_height < 0 )
        {
            Value_height = 0;
        }
        if( 0 == bottom)
        {
            return  0;
        }
        return Value_height * 100 / bottom;
    }


    @SuppressLint("ClickableViewAccessibility")
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        int currentPointerIndex = event.getActionIndex();
        float x = event.getX(currentPointerIndex);
        float y = event.getY(currentPointerIndex);

        switch (event.getActionMasked()) {
            case MotionEvent.ACTION_DOWN:
            case MotionEvent.ACTION_POINTER_DOWN: {
                if (isInsideRect(x, y, left, top+Circle_Radius, right, bottom)) {
                    Value_height = (int) (bottom - y);
                    invalidate();
                    savedPointerIndex = currentPointerIndex;
                }
                break;
            }
            case MotionEvent.ACTION_MOVE: {
                if (currentPointerIndex == savedPointerIndex) {
                    if (isInsideRect(x, y, left, top+Circle_Radius, right, bottom)) {
                        Value_height = (int) (bottom - y);
                        invalidate();
                    }
                }
                break;
            }
            case MotionEvent.ACTION_UP: {
                savedPointerIndex = 255;
                break;
            }
            case MotionEvent.ACTION_POINTER_UP: {
                if (currentPointerIndex == savedPointerIndex) {
                    savedPointerIndex = 255;
                }
                break;
            }
            default:
                break;
        }


        return true;
    }
}

