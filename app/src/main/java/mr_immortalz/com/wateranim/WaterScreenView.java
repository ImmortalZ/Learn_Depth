package mr_immortalz.com.wateranim;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.util.AttributeSet;
import android.view.View;

import mr_immortalz.com.wateranim.helper.FrameRateCounter;

/**
 * Created by Mr_immortalZ on 2016/10/5.
 * email : mr_immortalz@qq.com
 */

public class WaterScreenView extends View {

    private Water mWater;
    private Renderable[] mRenderables;

    public WaterScreenView(Context context) {
        super(context);
    }

    public WaterScreenView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public WaterScreenView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        if (mRenderables == null && getWidth() != 0) {
            init();
        }
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);
        if (mRenderables == null) {
            init();
        }
    }

    private void init() {
        mRenderables = new Renderable[2];
        Bitmap waterBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.water);
        Bitmap foam = BitmapFactory.decodeResource(getResources(), R.drawable.foam);
        setLayerType(View.LAYER_TYPE_HARDWARE, null);
        mWater = new Water(waterBitmap, foam, getHeight() * 0.65f, getWidth(), getHeight(), 6);
        mRenderables[0] = mWater;
        Bitmap aura = BitmapFactory.decodeResource(getResources(), R.drawable.sun_aura);
        mRenderables[1] = new Renderable(aura, getWidth() * 0.4f, getHeight() * 0.3f);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        float deltaTime = FrameRateCounter.timeStep();

        for (Renderable renderable : mRenderables) {
            renderable.draw(canvas);
            renderable.update(deltaTime);
        }
        if (!isPause) {
            invalidate();
        }
    }

    private boolean isPause = false;
}
