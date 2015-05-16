package us.gorges.android;

public class Animator extends Thread
{
  private boolean active = false;
  private Animation animation;
  private long lastTime = -1L;
  private boolean running = false;
  private GestureImageView view;

  public Animator(GestureImageView paramGestureImageView, String paramString)
  {
    super(paramString);
    this.view = paramGestureImageView;
  }

  public void activate()
  {
//    monitorenter;
//    try
//    {
//      this.lastTime = System.currentTimeMillis();
//      this.active = true;
//      notifyAll();
//      monitorexit;
//      return;
//    }
//    finally
//    {
//      localObject = finally;
//      monitorexit;
//    }
//    throw localObject;
  }

  public void cancel()
  {
    this.active = false;
  }

  public void finish()
  {
//    monitorenter;
//    try
//    {
//      this.running = false;
//      this.active = false;
//      notifyAll();
//      monitorexit;
//      return;
//    }
//    finally
//    {
//      localObject = finally;
//      monitorexit;
//    }
//    throw localObject;
  }

  public void play(Animation paramAnimation)
  {
    if (this.active)
      cancel();
    this.animation = paramAnimation;
    activate();
  }

  // ERROR //
  public void run()
  {
    // Byte code:
    //   0: aload_0
    //   1: iconst_1
    //   2: putfield 20	us/gorges/android/Animator:running	Z
    //   5: aload_0
    //   6: getfield 20	us/gorges/android/Animator:running	Z
    //   9: ifeq +125 -> 134
    //   12: aload_0
    //   13: getfield 22	us/gorges/android/Animator:active	Z
    //   16: ifeq +89 -> 105
    //   19: aload_0
    //   20: getfield 49	us/gorges/android/Animator:animation	Lus/gorges/android/Animation;
    //   23: ifnull +82 -> 105
    //   26: invokestatic 36	java/lang/System:currentTimeMillis	()J
    //   29: lstore 4
    //   31: aload_0
    //   32: aload_0
    //   33: getfield 49	us/gorges/android/Animator:animation	Lus/gorges/android/Animation;
    //   36: aload_0
    //   37: getfield 28	us/gorges/android/Animator:view	Lus/gorges/android/GestureImageView;
    //   40: lload 4
    //   42: aload_0
    //   43: getfield 26	us/gorges/android/Animator:lastTime	J
    //   46: lsub
    //   47: invokeinterface 60 4 0
    //   52: putfield 22	us/gorges/android/Animator:active	Z
    //   55: aload_0
    //   56: getfield 28	us/gorges/android/Animator:view	Lus/gorges/android/GestureImageView;
    //   59: invokevirtual 65	us/gorges/android/GestureImageView:redraw	()V
    //   62: aload_0
    //   63: lload 4
    //   65: putfield 26	us/gorges/android/Animator:lastTime	J
    //   68: aload_0
    //   69: getfield 22	us/gorges/android/Animator:active	Z
    //   72: ifeq -60 -> 12
    //   75: aload_0
    //   76: getfield 28	us/gorges/android/Animator:view	Lus/gorges/android/GestureImageView;
    //   79: ldc2_w 66
    //   82: invokevirtual 71	us/gorges/android/GestureImageView:waitForDraw	(J)Z
    //   85: istore 7
    //   87: iload 7
    //   89: ifeq -21 -> 68
    //   92: goto -80 -> 12
    //   95: astore 6
    //   97: aload_0
    //   98: iconst_0
    //   99: putfield 22	us/gorges/android/Animator:active	Z
    //   102: goto -34 -> 68
    //   105: aload_0
    //   106: monitorenter
    //   107: aload_0
    //   108: getfield 20	us/gorges/android/Animator:running	Z
    //   111: istore_2
    //   112: iload_2
    //   113: ifeq +7 -> 120
    //   116: aload_0
    //   117: invokevirtual 74	java/lang/Object:wait	()V
    //   120: aload_0
    //   121: monitorexit
    //   122: goto -117 -> 5
    //   125: astore_1
    //   126: aload_0
    //   127: monitorexit
    //   128: aload_1
    //   129: athrow
    //   130: astore_3
    //   131: goto -11 -> 120
    //   134: return
    //
    // Exception table:
    //   from	to	target	type
    //   75	87	95	java/lang/InterruptedException
    //   107	112	125	finally
    //   116	120	125	finally
    //   120	122	125	finally
    //   126	128	125	finally
    //   116	120	130	java/lang/InterruptedException
  }
}

/* Location:           /private/tmp/eternal_lands_android-1.jar
 * Qualified Name:     us.gorges.android.Animator
 * JD-Core Version:    0.6.0
 */