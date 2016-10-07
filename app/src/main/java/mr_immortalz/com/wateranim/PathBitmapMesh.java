package mr_immortalz.com.wateranim;

import android.animation.ValueAnimator;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PathMeasure;
import android.view.animation.LinearInterpolator;

/**
 * Created by Mr_immortalZ on 2016/10/5.
 * email : mr_immortalz@qq.com
 */

public class PathBitmapMesh {

    protected static int HORIZONTAL_COUNT = 6;//水平方向分片数
    protected static int VERTICAL_COUNT = 1;//垂直方向分片
    private int mTotalCount;//总共需要计算的网格顶点个数
    protected Bitmap bitmap;
    protected float[] drawingVerts;//需要绘制的Verts网格坐标
    protected float[] staticVerts;//最初始的Verts网格坐标
    private Paint mPaint = new Paint();
    private ValueAnimator mValueAnimator;
    protected float pathOffsetPercent;
    protected float[] coordsX = new float[2];
    protected float[] coordsY = new float[2];

    public PathBitmapMesh(Bitmap bitmap, long duration) {
        mTotalCount = (HORIZONTAL_COUNT + 1) * (VERTICAL_COUNT + 1);
        drawingVerts = new float[mTotalCount * 2];
        staticVerts = new float[mTotalCount * 2];
        this.bitmap = bitmap;
        initVert();
        startValuesAnim(duration);
    }

    private void startValuesAnim(long duration) {
        mValueAnimator = ValueAnimator.ofFloat(0, 1 / 3f);
        mValueAnimator.setDuration(duration);
        mValueAnimator.setRepeatCount(ValueAnimator.INFINITE);
        mValueAnimator.setRepeatMode(ValueAnimator.RESTART);
        mValueAnimator.setInterpolator(new LinearInterpolator());
        mValueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                pathOffsetPercent = (float) animation.getAnimatedValue();
            }
        });
        mValueAnimator.start();
    }

    private void initVert() {
        float bitmapWidth = (float) bitmap.getWidth();
        float bitmapHeight = (float) bitmap.getHeight();
        int index = 0;
        for (int y = 0; y <= VERTICAL_COUNT; y++) {
            float fy = bitmapHeight / VERTICAL_COUNT * y;
            for (int x = 0; x <= HORIZONTAL_COUNT; x++) {
                float fx = bitmapWidth / HORIZONTAL_COUNT * x;
                setXY(drawingVerts, index, fx, fy);
                setXY(staticVerts, index, fx, fy);
                index++;
            }
        }
    }


    protected void setXY(float[] arrys, int index, float x, float y) {
        arrys[2 * index] = x;
        arrys[2 * index + 1] = y;
    }

    public void matchVertsToPath(Path path, float bottomY, float extraOffset) {
        PathMeasure pm = new PathMeasure(path, false);
        for (int i = 0; i < staticVerts.length / 2; i++) {
            float orignX = staticVerts[2 * i];
            float orignY = staticVerts[2 * i + 1];
            float percentOffsetX = orignX / bitmap.getWidth();
            float percentOffsetY = orignX / (bitmap.getWidth() + extraOffset);
            percentOffsetY += pathOffsetPercent;
            pm.getPosTan(pm.getLength() * (percentOffsetX), coordsX, null);
            pm.getPosTan(pm.getLength() * (percentOffsetY), coordsY, null);
            if (orignY == 0) {
                setXY(drawingVerts, i, coordsX[0], coordsY[1]);
            } else {
                setXY(drawingVerts, i, coordsX[0], bottomY);
            }
        }
    }

    public void draw(Canvas canvas) {
        canvas.drawBitmapMesh(bitmap, HORIZONTAL_COUNT, VERTICAL_COUNT, drawingVerts, 0, null, 0, mPaint);
    }

    public Bitmap getBitmap() {
        return bitmap;
    }

    public void setAlpha(int alpha) {
        mPaint.setAlpha(alpha);
    }
}
