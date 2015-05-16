package us.gorges.android;

import java.io.InputStream;
import java.util.concurrent.Semaphore;
import java.util.concurrent.TimeUnit;

import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.ColorFilter;
import android.graphics.Matrix;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;

public class GestureImageView extends ImageView
{
  public static final String GLOBAL_NS = "http://schemas.android.com/apk/res/android";
  public static final String LOCAL_NS = "http://schemas.polites.com/android";
  private int alpha = 255;
  private Animator animator;
  private float centerX;
  private float centerY;
  private ColorFilter colorFilter;
  private View.OnTouchListener customOnTouchListener;
  private int deviceOrientation = -1;
  private int displayHeight;
  private int displayWidth;
  private final Semaphore drawLock = new Semaphore(0);
  private Drawable drawable;
  private float fitScaleHorizontal = 1.0F;
  private float fitScaleVertical = 1.0F;
  private GestureImageViewListener gestureImageViewListener;
  private GestureImageViewTouchListener gestureImageViewTouchListener;
  private int hHeight;
  private int hWidth;
  private int imageOrientation;
  private boolean layout = false;
  private float maxScale = 5.0F;
  boolean measured = false;
  private float minScale = 0.75F;
  private View.OnClickListener onClickListener;
  private boolean recycle = false;
  private int resId = -1;
  private float rotation = 0.0F;
  private float scale = 1.0F;
  private float scaleAdjust = 1.0F;
  private Float startX;
  private Float startY;
  private float startingScale = -1.0F;
  private boolean strict = false;
  private float x = 0.0F;
  private float y = 0.0F;

  public GestureImageView(Context paramContext)
  {
    super(paramContext);
    setScaleType(ImageView.ScaleType.CENTER_INSIDE);
    initImage();
  }

  public GestureImageView(Context paramContext, AttributeSet paramAttributeSet)
  {
    super(paramContext, paramAttributeSet);
    String str1 = paramAttributeSet.getAttributeValue("http://schemas.android.com/apk/res/android", "scaleType");
    if ((str1 == null) || (str1.trim().length() == 0))
      setScaleType(ImageView.ScaleType.CENTER_INSIDE);
    String str2 = paramAttributeSet.getAttributeValue("http://schemas.polites.com/android", "start-x");
    String str3 = paramAttributeSet.getAttributeValue("http://schemas.polites.com/android", "start-y");
    if ((str2 != null) && (str2.trim().length() > 0))
      this.startX = Float.valueOf(Float.parseFloat(str2));
    if ((str3 != null) && (str3.trim().length() > 0))
      this.startY = Float.valueOf(Float.parseFloat(str3));
    setStartingScale(paramAttributeSet.getAttributeFloatValue("http://schemas.polites.com/android", "start-scale", this.startingScale));
    setMinScale(paramAttributeSet.getAttributeFloatValue("http://schemas.polites.com/android", "min-scale", this.minScale));
    setMaxScale(paramAttributeSet.getAttributeFloatValue("http://schemas.polites.com/android", "max-scale", this.maxScale));
    setStrict(paramAttributeSet.getAttributeBooleanValue("http://schemas.polites.com/android", "strict", this.strict));
    setRecycle(paramAttributeSet.getAttributeBooleanValue("http://schemas.polites.com/android", "recycle", this.recycle));
    initImage();
  }

  public GestureImageView(Context paramContext, AttributeSet paramAttributeSet, int paramInt)
  {
    this(paramContext, paramAttributeSet);
  }

  private void requestMeasure()
  {
    try
    {
//      if (this.measured)
//        return;
//      Field localField = getClass().getClassLoader().loadClass("el.android.GameMetadata").getField("CONNECTION");
//      Class localClass1 = getClass().getClassLoader().loadClass("el.protocol.Message");
//      Object localObject1 = localClass1.getConstructor(new Class[] { [B.class }).newInstance(new Object[] { { 60, 1, 0 } });
//      Method localMethod = localField.get(null).getClass().getDeclaredMethod("optimisticSendMessage", new Class[] { localClass1 });
//      localMethod.setAccessible(true);
//      2 local2 = new InvocationHandler(localMethod, localField, localObject1)
//      {
//        public Object invoke(Object paramObject, Method paramMethod, Object[] paramArrayOfObject)
//          throws Throwable
//        {
//          Method localMethod = this.val$m;
//          Object localObject = this.val$field.get(null);
//          Object[] arrayOfObject = new Object[1];
//          arrayOfObject[0] = this.val$o1;
//          localMethod.invoke(localObject, arrayOfObject);
//          return null;
//        }
//      };
//      Class localClass2 = getClass().getClassLoader().loadClass("el.server.MessageSendListener");
//      Object localObject2 = Proxy.newProxyInstance(localClass2.getClassLoader(), new Class[] { localClass2 }, local2);
//      getClass().getClassLoader().loadClass("el.server.ServerConnection").getMethod("addListener", new Class[] { localClass2 }).invoke(localField.get(null), new Object[] { localObject2 });
      this.measured = true;
      return;
    }
    catch (Exception localException)
    {
    }
  }

  public void animationStart(Animation paramAnimation)
  {
    if (this.animator != null)
      this.animator.play(paramAnimation);
  }

  public void animationStop()
  {
    if (this.animator != null)
      this.animator.cancel();
  }

  protected void computeCropScale(int paramInt1, int paramInt2, int paramInt3, int paramInt4)
  {
    this.fitScaleHorizontal = (paramInt3 / paramInt1);
    this.fitScaleVertical = (paramInt4 / paramInt2);
  }

  protected void computeStartingScale(int paramInt1, int paramInt2, int paramInt3, int paramInt4)
  {
//    switch (3.$SwitchMap$android$widget$ImageView$ScaleType[getScaleType().ordinal()])
//    {
//    default:
//      return;
//    case 1:
//      this.startingScale = 1.0F;
//      return;
//    case 2:
//      this.startingScale = Math.max(paramInt4 / paramInt2, paramInt3 / paramInt1);
//      return;
//    case 3:
//    }
//    if (isLandscape())
//    {
//      this.startingScale = this.fitScaleHorizontal;
//      return;
//    }
//    this.startingScale = this.fitScaleVertical;
  }

  public float getCenterX()
  {
    return this.centerX;
  }

  public float getCenterY()
  {
    return this.centerY;
  }

  public int getDeviceOrientation()
  {
    return this.deviceOrientation;
  }

  public Drawable getDrawable()
  {
    return this.drawable;
  }

  public GestureImageViewListener getGestureImageViewListener()
  {
    return this.gestureImageViewListener;
  }

  public int getImageHeight()
  {
    if (this.drawable != null)
      return this.drawable.getIntrinsicHeight();
    return 0;
  }

  public Matrix getImageMatrix()
  {
    if (this.strict)
      throw new UnsupportedOperationException("Not supported");
    return super.getImageMatrix();
  }

  public int getImageWidth()
  {
    if (this.drawable != null)
      return this.drawable.getIntrinsicWidth();
    return 0;
  }

  public float getImageX()
  {
    return this.x;
  }

  public float getImageY()
  {
    return this.y;
  }

  public float getScale()
  {
    return this.scaleAdjust;
  }

  public int getScaledHeight()
  {
    return Math.round(getImageHeight() * getScale());
  }

  public int getScaledWidth()
  {
    return Math.round(getImageWidth() * getScale());
  }

  protected void initImage()
  {
    if (this.drawable != null)
    {
      this.drawable.setAlpha(this.alpha);
      this.drawable.setFilterBitmap(true);
      if (this.colorFilter != null)
        this.drawable.setColorFilter(this.colorFilter);
    }
    this.layout = false;
    requestMeasure();
    requestLayout();
    redraw();
  }

  public void invalidateDrawable(Drawable paramDrawable)
  {
    if (this.strict)
      throw new UnsupportedOperationException("Not supported");
    super.invalidateDrawable(paramDrawable);
  }

  public boolean isLandscape()
  {
    return getImageWidth() >= getImageHeight();
  }

  public boolean isOrientationAligned()
  {
//    int i = 1;
//    if (this.deviceOrientation == 2)
//      i = isLandscape();
//    do
//      return i;
//    while (this.deviceOrientation != i);
    return isPortrait();
  }

  public boolean isPortrait()
  {
    return getImageWidth() <= getImageHeight();
  }

  public boolean isRecycle()
  {
    return this.recycle;
  }

  protected boolean isRecycled()
  {
    if ((this.drawable != null) && ((this.drawable instanceof BitmapDrawable)))
    {
      Bitmap localBitmap = ((BitmapDrawable)this.drawable).getBitmap();
      if (localBitmap != null)
        return localBitmap.isRecycled();
    }
    return false;
  }

  public boolean isStrict()
  {
    return this.strict;
  }

  public void moveBy(float paramFloat1, float paramFloat2)
  {
    this.x = (paramFloat1 + this.x);
    this.y = (paramFloat2 + this.y);
  }

  protected void onAttachedToWindow()
  {
    this.animator = new Animator(this, "GestureImageViewAnimator");
    this.animator.start();
    if ((this.resId >= 0) && (this.drawable == null))
      setImageResource(this.resId);
    super.onAttachedToWindow();
  }

  public int[] onCreateDrawableState(int paramInt)
  {
    if (this.strict)
      throw new UnsupportedOperationException("Not supported");
    return super.onCreateDrawableState(paramInt);
  }

  protected void onDetachedFromWindow()
  {
    if (this.animator != null)
      this.animator.finish();
    if ((this.recycle) && (this.drawable != null) && (!isRecycled()))
    {
      recycle();
      this.drawable = null;
    }
    super.onDetachedFromWindow();
  }

  protected void onDraw(Canvas paramCanvas)
  {
    if (this.layout)
    {
      if ((this.drawable != null) && (!isRecycled()))
      {
        paramCanvas.save();
        float f = this.scale * this.scaleAdjust;
        paramCanvas.translate(this.x, this.y);
        if (this.rotation != 0.0F)
          paramCanvas.rotate(this.rotation);
        if (f != 1.0F)
          paramCanvas.scale(f, f);
        this.drawable.draw(paramCanvas);
        paramCanvas.restore();
      }
      if (this.drawLock.availablePermits() <= 0)
        this.drawLock.release();
    }
  }

  protected void onLayout(boolean paramBoolean, int paramInt1, int paramInt2, int paramInt3, int paramInt4)
  {
    super.onLayout(paramBoolean, paramInt1, paramInt2, paramInt3, paramInt4);
    if ((paramBoolean) || (!this.layout))
      setupCanvas(this.displayWidth, this.displayHeight, getResources().getConfiguration().orientation);
  }

  protected void onMeasure(int paramInt1, int paramInt2)
  {
    if (this.drawable != null)
      if (getResources().getConfiguration().orientation == 2)
      {
        this.displayHeight = View.MeasureSpec.getSize(paramInt2);
        if (getLayoutParams().width == -2)
          this.displayWidth = Math.round(getImageWidth() / getImageHeight() * this.displayHeight);
      }
    while (true)
    {
      setMeasuredDimension(this.displayWidth, this.displayHeight);
//      return;
      this.displayWidth = View.MeasureSpec.getSize(paramInt1);
//      continue;
      this.displayWidth = View.MeasureSpec.getSize(paramInt1);
      if (getLayoutParams().height == -2)
      {
        this.displayHeight = Math.round(getImageHeight() / getImageWidth() * this.displayWidth);
        continue;
      }
      this.displayHeight = View.MeasureSpec.getSize(paramInt2);
//      continue;
      this.displayHeight = View.MeasureSpec.getSize(paramInt2);
      this.displayWidth = View.MeasureSpec.getSize(paramInt1);
    }
  }

  protected void recycle()
  {
    if ((this.recycle) && (this.drawable != null) && ((this.drawable instanceof BitmapDrawable)))
    {
      Bitmap localBitmap = ((BitmapDrawable)this.drawable).getBitmap();
      if (localBitmap != null)
        localBitmap.recycle();
    }
  }

  public void redraw()
  {
    postInvalidate();
  }

  public void reset()
  {
    this.x = this.centerX;
    this.y = this.centerY;
    this.scaleAdjust = this.startingScale;
    redraw();
  }

  public void setAdjustViewBounds(boolean paramBoolean)
  {
    if (this.strict)
      throw new UnsupportedOperationException("Not supported");
    super.setAdjustViewBounds(paramBoolean);
  }

  public void setAlpha(int paramInt)
  {
    this.alpha = paramInt;
    if (this.drawable != null)
      this.drawable.setAlpha(paramInt);
  }

  public void setColorFilter(ColorFilter paramColorFilter)
  {
    this.colorFilter = paramColorFilter;
    if (this.drawable != null)
      this.drawable.setColorFilter(paramColorFilter);
  }

  public void setGestureImageViewListener(GestureImageViewListener paramGestureImageViewListener)
  {
    this.gestureImageViewListener = paramGestureImageViewListener;
  }

  public void setImageBitmap(Bitmap paramBitmap)
  {
    this.drawable = new BitmapDrawable(getResources(), paramBitmap);
    initImage();
  }

  public void setImageDrawable(Drawable paramDrawable)
  {
    this.drawable = paramDrawable;
    initImage();
  }

  public void setImageLevel(int paramInt)
  {
    if (this.strict)
      throw new UnsupportedOperationException("Not supported");
    super.setImageLevel(paramInt);
  }

  public void setImageMatrix(Matrix paramMatrix)
  {
    if (this.strict)
      throw new UnsupportedOperationException("Not supported");
  }

  public void setImageResource(int paramInt)
  {
    if (this.drawable != null)
      recycle();
    if (paramInt >= 0)
    {
      this.resId = paramInt;
      setImageDrawable(getContext().getResources().getDrawable(paramInt));
    }
  }

  public void setImageState(int[] paramArrayOfInt, boolean paramBoolean)
  {
    if (this.strict)
      throw new UnsupportedOperationException("Not supported");
  }

  public void setImageURI(Uri paramUri)
  {
    if ("content".equals(paramUri.getScheme()));
    while (true)
    {
      try
      {
        String[] arrayOfString = { "orientation" };
        Cursor localCursor = getContext().getContentResolver().query(paramUri, arrayOfString, null, null, null);
        if ((localCursor == null) || (!localCursor.moveToFirst()))
          continue;
        this.imageOrientation = localCursor.getInt(localCursor.getColumnIndex(arrayOfString[0]));
        InputStream localInputStream = null;
        try
        {
          localInputStream = getContext().getContentResolver().openInputStream(paramUri);
          Bitmap localBitmap1 = BitmapFactory.decodeStream(localInputStream);
          if (this.imageOrientation == 0)
            continue;
          Matrix localMatrix = new Matrix();
          localMatrix.postRotate(this.imageOrientation);
          Bitmap localBitmap2 = Bitmap.createBitmap(localBitmap1, 0, 0, localBitmap1.getWidth(), localBitmap1.getHeight(), localMatrix, true);
          localBitmap1.recycle();
          setImageDrawable(new BitmapDrawable(getResources(), localBitmap2));
          if (localInputStream == null)
            continue;
          localInputStream.close();
          if (localCursor == null)
            continue;
          localCursor.close();
          if (this.drawable != null)
            continue;
          Log.e("GestureImageView", "resolveUri failed on bad bitmap uri: " + paramUri);
//          return;
          setImageDrawable(new BitmapDrawable(getResources(), localBitmap1));
          continue;
        }
        finally
        {
          if (localInputStream == null)
            continue;
          localInputStream.close();
          if (localCursor == null)
            continue;
          localCursor.close();
        }
      }
      catch (Exception localException)
      {
        Log.w("GestureImageView", "Unable to open content: " + paramUri, localException);
        continue;
      }
//      setImageDrawable(Drawable.createFromPath(paramUri.toString()));
    }
  }

  public void setMaxScale(float paramFloat)
  {
    this.maxScale = paramFloat;
    if (this.gestureImageViewTouchListener != null)
      this.gestureImageViewTouchListener.setMaxScale(paramFloat * this.startingScale);
  }

  public void setMinScale(float paramFloat)
  {
    this.minScale = paramFloat;
    if (this.gestureImageViewTouchListener != null)
      this.gestureImageViewTouchListener.setMinScale(paramFloat * this.fitScaleHorizontal);
  }

  public void setOnClickListener(View.OnClickListener paramOnClickListener)
  {
    this.onClickListener = paramOnClickListener;
//    if (this.gestureImageViewTouchListener != null)
//      this.gestureImageViewTouchListener.setOnClickListener(paramOnClickListener);
  }

  public void setOnTouchListener(View.OnTouchListener paramOnTouchListener)
  {
    this.customOnTouchListener = paramOnTouchListener;
  }

  public void setPosition(float paramFloat1, float paramFloat2)
  {
    this.x = paramFloat1;
    this.y = paramFloat2;
  }

  public void setRecycle(boolean paramBoolean)
  {
    this.recycle = paramBoolean;
  }

  public void setRotation(float paramFloat)
  {
    this.rotation = paramFloat;
  }

  public void setScale(float paramFloat)
  {
    this.scaleAdjust = paramFloat;
  }

  public void setScaleType(ImageView.ScaleType paramScaleType)
  {
    if ((paramScaleType == ImageView.ScaleType.CENTER) || (paramScaleType == ImageView.ScaleType.CENTER_CROP) || (paramScaleType == ImageView.ScaleType.CENTER_INSIDE))
      super.setScaleType(paramScaleType);
    do
      return;
    while (!this.strict);
//    throw new UnsupportedOperationException("Not supported");
  }

  public void setSelected(boolean paramBoolean)
  {
    if (this.strict)
      throw new UnsupportedOperationException("Not supported");
    super.setSelected(paramBoolean);
  }

  public void setStartingPosition(float paramFloat1, float paramFloat2)
  {
    this.startX = Float.valueOf(paramFloat1);
    this.startY = Float.valueOf(paramFloat2);
  }

  public void setStartingScale(float paramFloat)
  {
    this.startingScale = paramFloat;
  }

  public void setStrict(boolean paramBoolean)
  {
    this.strict = paramBoolean;
  }

  protected void setupCanvas(int paramInt1, int paramInt2, int paramInt3)
  {
    if (this.deviceOrientation != paramInt3)
    {
      this.layout = false;
      this.deviceOrientation = paramInt3;
    }
    int k;
    int m;
    if ((this.drawable != null) && (!this.layout))
    {
      int i = getImageWidth();
      int j = getImageHeight();
      this.hWidth = Math.round(i / 2.0F);
      this.hHeight = Math.round(j / 2.0F);
      k = paramInt1 - (getPaddingLeft() + getPaddingRight());
      m = paramInt2 - (getPaddingTop() + getPaddingBottom());
      computeCropScale(i, j, k, m);
      if (this.startingScale <= 0.0F)
        computeStartingScale(i, j, k, m);
      this.scaleAdjust = this.startingScale;
      this.centerX = (k / 2.0F);
      this.centerY = (m / 2.0F);
//      if (this.startX != null)
//        break label332;
//      this.x = this.centerX;
//      if (this.startY != null)
//        break label346;
//      this.y = this.centerY;
//      this.gestureImageViewTouchListener = new GestureImageViewTouchListener(this, k, m);
//      if (!isLandscape())
//        break label360;
      this.gestureImageViewTouchListener.setMinScale(this.minScale * this.fitScaleHorizontal);
    }
    while (true)
    {
      this.gestureImageViewTouchListener.setMaxScale(this.maxScale * this.startingScale);
//      this.gestureImageViewTouchListener.setFitScaleHorizontal(this.fitScaleHorizontal);
//      this.gestureImageViewTouchListener.setFitScaleVertical(this.fitScaleVertical);
//      this.gestureImageViewTouchListener.setCanvasWidth(k);
//      this.gestureImageViewTouchListener.setCanvasHeight(m);
//      this.gestureImageViewTouchListener.setOnClickListener(this.onClickListener);
      this.drawable.setBounds(-this.hWidth, -this.hHeight, this.hWidth, this.hHeight);
//      super.setOnTouchListener(new View.OnTouchListener()
//      {
//        public boolean onTouch(View paramView, MotionEvent paramMotionEvent)
//        {
//          if (GestureImageView.this.customOnTouchListener != null)
//            GestureImageView.this.customOnTouchListener.onTouch(paramView, paramMotionEvent);
//          return GestureImageView.this.gestureImageViewTouchListener.onTouch(paramView, paramMotionEvent);
//        }
//      });
      this.layout = true;
      return;
//      label332: this.x = this.startX.floatValue();
//      break;
//      label346: this.y = this.startY.floatValue();
//      break label183;
//      label360: this.gestureImageViewTouchListener.setMinScale(this.minScale * this.fitScaleVertical);
    }
  }

  public boolean waitForDraw(long paramLong)
    throws InterruptedException
  {
    return this.drawLock.tryAcquire(paramLong, TimeUnit.MILLISECONDS);
  }
}

/* Location:           /private/tmp/eternal_lands_android-1.jar
 * Qualified Name:     us.gorges.android.GestureImageView
 * JD-Core Version:    0.6.0
 */