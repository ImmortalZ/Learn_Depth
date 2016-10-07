package mr_immortalz.com.wateranim;

import android.graphics.Bitmap;
import android.graphics.Path;
import android.graphics.PathMeasure;

import mr_immortalz.com.wateranim.helper.MathHelper;

/**
 * Created by Mr_immortalZ on 2016/10/6.
 * email : mr_immortalz@qq.com
 */

public class Foam extends PathBitmapMesh {

    private float[] foamCoords;
    private float[] easedFoamCoords;
    private int mHorizontalSlices;//水纹水平方向分片
    private float minHeight;//水纹最小高度
    private float maxHeight;//水纹最大高度
    private float verticalOffset;

    public Foam(int horizontalSlices, Bitmap bitmap, float minHeight, float maxHeight, long duration) {
        super(bitmap, duration);
        mHorizontalSlices = horizontalSlices;
        this.minHeight = minHeight;
        this.maxHeight = maxHeight;
        init();
    }

    private void init() {
        foamCoords = new float[mHorizontalSlices];
        easedFoamCoords = new float[mHorizontalSlices];
        for (int i = 0; i < mHorizontalSlices; i++) {
            foamCoords[i] = 0;
            easedFoamCoords[i] = 0;
        }
    }

    /**
     * 随着时间的流逝不断更改
     * @param deltaTime
     */
    public void update(float deltaTime) {
        for (int i = 0; i < foamCoords.length; i++) {
            easedFoamCoords[i] += ((foamCoords[i] - easedFoamCoords[i])) * deltaTime;
        }
    }

    /**
     * 根据传入的最低，最高高度得到一个适合的高度
     */
    public void calcWave() {
        for (int i = 0; i < foamCoords.length; i++) {
            foamCoords[i] = MathHelper.randomRange(minHeight, maxHeight);
        }
    }

    /**
     * 计算水纹的各个顶点坐标
     * @param path
     * @param extraOffset
     */
    public void matchVertsToPath(Path path, float extraOffset) {
        PathMeasure pm = new PathMeasure(path, false);
        int index = 0;
        for (int i = 0; i < staticVerts.length / 2; i++) {
            float orignX = staticVerts[2 * i];
            float orignY = staticVerts[2 * i + 1];
            float percentOffsetX = orignX / bitmap.getWidth();
            float percentOffsetY = orignX / (bitmap.getWidth() + extraOffset);
            percentOffsetY += pathOffsetPercent;
            pm.getPosTan(pm.getLength() * percentOffsetX, coordsX, null);
            pm.getPosTan(pm.getLength() * percentOffsetY, coordsY, null);
            if (orignY == 0) {
                setXY(drawingVerts, i, coordsX[0], coordsY[1]+verticalOffset);
            } else {
                float desiredYCoord = Math.max(coordsY[1], coordsY[1] + easedFoamCoords[Math.min(easedFoamCoords.length - 1, index)]);
                setXY(drawingVerts, i, coordsX[0], desiredYCoord+verticalOffset);
                index += 1;
            }
        }
    }

    public void setVerticalOffset(float verticalOffset) {
        this.verticalOffset = verticalOffset;
    }
}
