package mr_immortalz.com.wateranim;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Path;

/**
 * Created by Mr_immortalZ on 2016/10/5.
 * email : mr_immortalz@qq.com
 */

public class Water extends Renderable {

    private float mWidth;
    private float mHeight;
    private PathBitmapMesh mWaterMesh;
    private float mWaveHeight;
    private Path mWaterPath = new Path();
    private int mNumWaves;
    private Foam[] foams = new Foam[4];
    long lastEmit;
    private int emitInterWall = 1000;

    /**
     *
     * @param water water图像
     * @param foam 海浪图像
     * @param y 海浪起始左上角坐标的y值
     * @param width 海浪显示的宽度
     * @param height 海浪显示的高度
     * @param numWaves 海浪整个宽度被分成多少份
     */
    public Water(Bitmap water, Bitmap foam, float y, float width, float height, int numWaves) {
        super(water, 0, y);
        mWidth = width;
        mHeight = height;
        mWaterMesh = new PathBitmapMesh(water, 1500);
        mWaveHeight = height / 25;
        mNumWaves = numWaves;
        foams[0] = new Foam(PathBitmapMesh.HORIZONTAL_COUNT, foam, 0, height / 12, 1500);
        foams[1] = new Foam(PathBitmapMesh.HORIZONTAL_COUNT, foam, -height / 5, height / 5, 1500);
        foams[1].setAlpha(100);
        foams[2] = new Foam(PathBitmapMesh.HORIZONTAL_COUNT, foam, -height / 12, height / 12, 1450);
        foams[2].setVerticalOffset(height / 7);
        foams[3] = new Foam(PathBitmapMesh.HORIZONTAL_COUNT, foam, -height / 12, height / 12, 1400);
        foams[3].setVerticalOffset(height / 4);
        lastEmit = System.currentTimeMillis();
        createPath();
    }


    private void createPath() {
        mWaterPath.reset();
        mWaterPath.moveTo(0, y);
        int step = (int) (mWidth / mNumWaves);
        boolean changeDirection = true;
        for (int i = 0; i < mNumWaves; i++) {
            if (changeDirection) {
                mWaterPath.cubicTo(x + step * i, y, x + step * i + step / 2f, y + mWaveHeight, x + step * i + step, y);
            } else {
                mWaterPath.cubicTo(x + step * i, y, x + step * i + step / 2f, y - mWaveHeight, x + step * i + step, y);
            }
            changeDirection = !changeDirection;
        }
    }

    @Override
    public void draw(Canvas canvas) {
        mWaterMesh.draw(canvas);
        for (Foam foam : foams) {
            foam.draw(canvas);
        }
    }

    @Override
    public void update(float deltaTime) {
        mWaterMesh.matchVertsToPath(mWaterPath, mHeight, ((bitmap.getWidth() / mNumWaves) * 4f));
        for (Foam foam : foams) {
            foam.update(deltaTime);
        }
        for (Foam foam : foams) {
            foam.matchVertsToPath(mWaterPath, ((foam.getBitmap().getWidth() / mNumWaves) * 4f));
        }
        if (lastEmit + emitInterWall < System.currentTimeMillis()) {
            for (Foam foam : foams) {
                foam.calcWave();
            }
            lastEmit = System.currentTimeMillis();
        }
    }
}
