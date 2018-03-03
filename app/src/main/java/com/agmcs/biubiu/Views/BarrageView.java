package com.agmcs.biubiu.Views;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.os.Handler;
import android.os.Looper;
import android.support.v4.view.animation.FastOutSlowInInterpolator;
import android.support.v4.view.animation.LinearOutSlowInInterpolator;
import android.util.AttributeSet;
import android.util.Log;
import android.util.TypedValue;
import android.view.View;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.BounceInterpolator;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.LinearInterpolator;
import android.view.animation.OvershootInterpolator;


import com.agmcs.biubiu.Models.BarrageItem;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

/**
 * Created by agmcs on 2015/6/12.
 */
public class BarrageView extends View{
    Paint textPaint;
    private List<BarrageItem> barrageItems = new ArrayList<BarrageItem>();


    private Random random = new Random();

//    private static final String[] COLORS= new String[]{"#eeF44336","#eeFFC107","#eeEEEEEE","#ee4CAF50","#eeE91E63","#ee3F51B5","#eeFFEB3B"};
    private static final String[] COLORS= new String[]{"#ee339900",
        "#ee990000",
        "#eeFF3366",
        "#ee3366FF",
        "#ee00cc33",
        "#eeF9F9F9",
        "#eeF9F9F9",
        "#eeF9F9F9",
        "#eeCCCCCC",
        "#eeFF0099",
        "#eeFF3030",
        "#eeffcc00",
        "#eeFF9900",
        "#ee00bbdd",
        "#ee00bbdd",
        "#eeFF3333",
        "#ee33FF33",
        "#ee3333FF",
};

    private Rect mBound;

    private int textSize;
    private int line_height;
    private int lines;
    private int[] line_book = new int[100];
//    private Runnable moveBarrageRunnable;


    private AddRemoveCallBack addRemoveCallBack;

    public BarrageView(Context context) {
        this(context, null);
    }

    public BarrageView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public interface AddRemoveCallBack {
        void removeView();
    }

    public void setAddRemoveCallBack(AddRemoveCallBack addRemoveCallBack) {
        this.addRemoveCallBack = addRemoveCallBack;
    }

    public BarrageView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        textPaint = new Paint();
        textPaint.setAntiAlias(true);
        textPaint.setAlpha(50);
        textPaint.setShadowLayer(2, 2, 2, Color.DKGRAY);

        mBound = new Rect();

        textSize = (int)TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP,28,getResources().getDisplayMetrics());
        textPaint.setTextSize(textSize);

        textPaint.getTextBounds("ABCDEFGHIJKLMNO返回的随机数在哪个范围区间内,!~@", 0, 33, mBound);

        line_height = mBound.height();
        invalidate();

//        moveBarrageRunnable = new Thread(new Runnable() {
//            @Override
//            public void run() {
//                BarrageItem barrageItem;
//                while (true){
//                    for (int i=0;i<barrageItems.size();i++){
//                        barrageItem = barrageItems.get(i);
//                        barrageItem.setX(barrageItem.getX() - barrageItem.getSpeed());
//                        if(barrageItem.getX() + barrageItem.getTextLength() <=0){
//                            barrageItems.remove(i);
//                            line_book[barrageItem.getLine()]-=1;
//                        }
//                        postInvalidate();
//                    }
//                    if(barrageItems.size() == 0){
//                        if(addRemoveCallBack != null){
//                            addRemoveCallBack.removeView();
//                        }
//                        return;
//                    }
//                    try {
//                        Thread.sleep(10);
//                    } catch (InterruptedException e) {
//                        e.printStackTrace();
//                    }
//                }
//            }
//        });
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        //可用行数
        lines = getMeasuredHeight() / line_height;
//        barrageItems.clear();
//        Arrays.fill(line_book, 0);
        Log.d("hihi1994", "Lines:  " + lines);
    }


    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        BarrageItem barrageItem;
        int loopCount = barrageItems.size()>35?35:barrageItems.size();

        for(int i=0;i<loopCount;i++){
            barrageItem = barrageItems.get(i);
            textPaint.setColor(barrageItem.getColor());
            canvas.drawText(barrageItem.getText(), barrageItem.getX(), barrageItem.getY(), textPaint);
        }
    }


    private int getBestLine(){
        int line = 1 + random.nextInt(lines);//1-20
        if(line_book[line]==0){
            line_book[line] +=1;
            return line;
        }else {//如果随机行数不为0,从随机行数处开始上下扫描找到次数为0的行或者次数最小的行
            int min = 999;
            int min_index = 1;
            for(int i=line+1;i<=lines;i++){
                if(line_book[i] ==0){
                    line_book[i] +=1;
                    return i;
                }else if(line_book[i]<min){
                    min = line_book[i];
                    min_index = i;
                }
            }

            for(int i=line;i>=1;i--){
                if(line_book[i] ==0){
                    line_book[i] +=1;
                    return i;
                }else if(line_book[i]<min){
                    min = line_book[i];
                    min_index = i;
                }
            }
            line_book[min_index] +=1;
            return min_index;
        }
    }


    public void addBarrage(final BarrageItem barrageItem) {
        Log.d("hihi1998", "added");

        barrageItem.setX(getMeasuredWidth());
        int bestLine = getBestLine();
        barrageItem.setLine(bestLine);

        barrageItem.setY(bestLine * line_height);
        barrageItem.setColor(Color.parseColor(COLORS[random.nextInt(COLORS.length)]));

        textPaint.getTextBounds(barrageItem.getText(), 0, barrageItem.getText().length(), mBound);
        barrageItem.setTextLength(mBound.width());
        barrageItems.add(barrageItem);

        ValueAnimator valueAnimator = ValueAnimator.ofInt(barrageItem.getX(), -barrageItem.getTextLength());
        valueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator valueAnimator) {
                int x = (int) valueAnimator.getAnimatedValue();
                barrageItem.setX(x);
                postInvalidate();
            }
        });
        valueAnimator.setDuration(2500 + barrageItem.getText().length() * 70 + random.nextInt(1500));
        valueAnimator.setInterpolator(new LinearInterpolator());

        valueAnimator.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);
                barrageItems.remove(barrageItem);
                line_book[barrageItem.getLine()]-=1;
                if(barrageItems.size() == 0){
                    if(addRemoveCallBack != null){
                        addRemoveCallBack.removeView();
                    }
                }
            }
        });
        valueAnimator.start();

//        if(barrageItems.size() == 1){
//            new Thread(moveBarrageRunnable).start();
//        }
    }
}
