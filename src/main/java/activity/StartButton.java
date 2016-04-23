package activity;

/**
 * Created by Administrator on 2015/4/19.
 */

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.View;
import android.content.Context;
import android.content.res.TypedArray;
import android.widget.Button;
import android.graphics.Path;

import net.londatiga.android.bluetooth.R;

public class StartButton extends View {

    private Paint mPaint = null;
    private int ButtonColor;
    private Drawable mThumbDrawable = null;
    private final String TAG = "StartButton";
    private Context mContext = null;
    private int mViewHeight = 0;
    private int mViewWidth = 0;
    private int mStartButtonSize = 0;
    private int mStartButtonRadius =0;
    private int mStartButtonCenterX = 0;
    private int mStartButtonCenterY = 0;
    private int start = 1;
    private Bitmap bmp = BitmapFactory.decodeResource(this.getResources(), R.drawable.thumb_normal);

    private OnButtonChangeListener mOnButtonChangeListener = null;

    public interface OnButtonChangeListener {

        void onStartTrackingTouch();

        void onStopTrackingTouch(int ButtonState);
    }

    public StartButton(Context context) {
        super(context);
    }

    public StartButton(Context context,AttributeSet attr){
        super(context, attr);
    }


    private void initViewAttrs(AttributeSet attrs){
        TypedArray a = mContext.obtainStyledAttributes(attrs, R.styleable.StartButton);
        ButtonColor = a.getColor(R.styleable.StartButton_Button_color,Color.WHITE);
        a.recycle();
    }


    @Override
    protected void onDraw(Canvas canvas){
        super.onDraw(canvas);


        mViewWidth = getWidth();
        mViewHeight = getHeight();

        mStartButtonSize = mViewWidth > mViewHeight ? mViewHeight : mViewWidth;

        mStartButtonCenterX = mViewWidth / 2;
        mStartButtonCenterY = mViewHeight / 2;

        mStartButtonRadius = mStartButtonSize / 2 - bmp.getWidth() / 2;

        mPaint = new Paint();
        mPaint.setColor(Color.WHITE);
        mPaint.setStrokeWidth(5);
        mPaint.setStyle(Paint.Style.STROKE);

        canvas.drawCircle(mStartButtonCenterX, mStartButtonCenterY, mStartButtonRadius,
                mPaint);

        if (start == 1)
        {
            Path path = new Path();
            path.moveTo(mStartButtonCenterX - mStartButtonRadius/3, mStartButtonCenterY - mStartButtonRadius/2);
            path.lineTo(mStartButtonCenterX - mStartButtonRadius/3, mStartButtonCenterY + mStartButtonRadius/2);
            path.lineTo(mStartButtonCenterX + mStartButtonRadius/2,mStartButtonCenterY);
            path.lineTo(mStartButtonCenterX - mStartButtonRadius/3,mStartButtonCenterY - mStartButtonRadius/2);
            canvas.drawPath(path, mPaint);
        }else {
            canvas.drawRect(mStartButtonCenterX - 4* mStartButtonRadius/8, mStartButtonCenterY - mStartButtonRadius/2,
                    mStartButtonCenterX - mStartButtonRadius/8,mStartButtonCenterY + mStartButtonRadius/2, mPaint);
            canvas.drawRect(mStartButtonCenterX + 1* mStartButtonRadius/8,mStartButtonCenterY - mStartButtonRadius/2,
                    mStartButtonCenterX +4* mStartButtonRadius/8,mStartButtonCenterY + mStartButtonRadius/2,mPaint);
        }

    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        float eventX = event.getX();
        float eventY = event.getY();
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                break ;

            case MotionEvent.ACTION_UP:
                refresh();
                break ;
        }
        return true;
    }

    private void refresh(){
        start = ~start;
        if(null != mOnButtonChangeListener){
            mOnButtonChangeListener.onStopTrackingTouch(start);
        }
        invalidate();
    }

    public void setOnButtonChangeListener(OnButtonChangeListener onButtonChangeListener){
        mOnButtonChangeListener = onButtonChangeListener;
    }

}
