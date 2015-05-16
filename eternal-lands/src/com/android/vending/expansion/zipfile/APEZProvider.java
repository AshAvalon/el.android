package com.android.vending.expansion.zipfile;

import android.content.ContentProvider;
import android.content.ContentProviderOperation;
import android.content.ContentProviderResult;
import android.content.ContentValues;
import android.content.OperationApplicationException;
import android.content.res.AssetFileDescriptor;
import android.database.Cursor;
import android.database.MatrixCursor;
import android.database.MatrixCursor.RowBuilder;
import android.net.Uri;
import android.os.ParcelFileDescriptor;
import java.io.FileNotFoundException;
import java.util.ArrayList;

public abstract class APEZProvider extends ContentProvider
{
  public static final String[] ALL_FIELDS = { "_id", "ZPFN", "ZFIL", "ZMOD", "ZCRC", "ZCOL", "ZUNL", "ZTYP" };
  public static final int[] ALL_FIELDS_INT = { 0, 1, 2, 3, 4, 5, 6, 7 };
  public static final int COMPLEN_IDX = 5;
  public static final String COMPRESSEDLEN = "ZCOL";
  public static final String COMPRESSIONTYPE = "ZTYP";
  public static final int COMPTYPE_IDX = 7;
  public static final String CRC32 = "ZCRC";
  public static final int CRC_IDX = 4;
  public static final String FILEID = "_id";
  public static final int FILEID_IDX = 0;
  public static final String FILENAME = "ZPFN";
  public static final int FILENAME_IDX = 1;
  public static final String MODIFICATION = "ZMOD";
  public static final int MOD_IDX = 3;
  private static final String NO_FILE = "N";
  public static final int UNCOMPLEN_IDX = 6;
  public static final String UNCOMPRESSEDLEN = "ZUNL";
  public static final String ZIPFILE = "ZFIL";
  public static final int ZIPFILE_IDX = 2;
  private ZipResourceFile mAPKExtensionFile;
  private boolean mInit;

  // ERROR //
  private boolean initIfNecessary()
  {
	  return false;
    // Byte code:
    //   0: aload_0
    //   1: getfield 76	com/android/vending/expansion/zipfile/APEZProvider:mInit	Z
    //   4: ifne +217 -> 221
    //   7: aload_0
    //   8: invokevirtual 80	com/android/vending/expansion/zipfile/APEZProvider:getContext	()Landroid/content/Context;
    //   11: astore_1
    //   12: aload_1
    //   13: invokevirtual 86	android/content/Context:getPackageManager	()Landroid/content/pm/PackageManager;
    //   16: astore_2
    //   17: aload_2
    //   18: aload_0
    //   19: invokevirtual 90	com/android/vending/expansion/zipfile/APEZProvider:getAuthority	()Ljava/lang/String;
    //   22: sipush 128
    //   25: invokevirtual 96	android/content/pm/PackageManager:resolveContentProvider	(Ljava/lang/String;I)Landroid/content/pm/ProviderInfo;
    //   28: astore_3
    //   29: aload_2
    //   30: aload_1
    //   31: invokevirtual 99	android/content/Context:getPackageName	()Ljava/lang/String;
    //   34: iconst_0
    //   35: invokevirtual 103	android/content/pm/PackageManager:getPackageInfo	(Ljava/lang/String;I)Landroid/content/pm/PackageInfo;
    //   38: astore 5
    //   40: aload 5
    //   42: getfield 108	android/content/pm/PackageInfo:versionCode	I
    //   45: istore 6
    //   47: aload_3
    //   48: getfield 114	android/content/pm/ProviderInfo:metaData	Landroid/os/Bundle;
    //   51: ifnull +137 -> 188
    //   54: aload_3
    //   55: getfield 114	android/content/pm/ProviderInfo:metaData	Landroid/os/Bundle;
    //   58: ldc 116
    //   60: iload 6
    //   62: invokevirtual 122	android/os/Bundle:getInt	(Ljava/lang/String;I)I
    //   65: istore 8
    //   67: aload_3
    //   68: getfield 114	android/content/pm/ProviderInfo:metaData	Landroid/os/Bundle;
    //   71: ldc 124
    //   73: iload 6
    //   75: invokevirtual 122	android/os/Bundle:getInt	(Ljava/lang/String;I)I
    //   78: istore 7
    //   80: aload_3
    //   81: getfield 114	android/content/pm/ProviderInfo:metaData	Landroid/os/Bundle;
    //   84: ldc 126
    //   86: ldc 43
    //   88: invokevirtual 130	android/os/Bundle:getString	(Ljava/lang/String;Ljava/lang/String;)Ljava/lang/String;
    //   91: astore 11
    //   93: aconst_null
    //   94: astore 9
    //   96: ldc 43
    //   98: aload 11
    //   100: if_acmpeq +41 -> 141
    //   103: aload_3
    //   104: getfield 114	android/content/pm/ProviderInfo:metaData	Landroid/os/Bundle;
    //   107: ldc 132
    //   109: ldc 43
    //   111: invokevirtual 130	android/os/Bundle:getString	(Ljava/lang/String;Ljava/lang/String;)Ljava/lang/String;
    //   114: astore 12
    //   116: ldc 43
    //   118: aload 12
    //   120: if_acmpeq +54 -> 174
    //   123: iconst_2
    //   124: anewarray 61	java/lang/String
    //   127: astore 9
    //   129: aload 9
    //   131: iconst_0
    //   132: aload 11
    //   134: aastore
    //   135: aload 9
    //   137: iconst_1
    //   138: aload 12
    //   140: aastore
    //   141: aload 9
    //   143: ifnonnull +59 -> 202
    //   146: aload_0
    //   147: aload_1
    //   148: iload 8
    //   150: iload 7
    //   152: invokestatic 138	com/android/vending/expansion/zipfile/APKExpansionSupport:getAPKExpansionZipFile	(Landroid/content/Context;II)Lcom/android/vending/expansion/zipfile/ZipResourceFile;
    //   155: putfield 140	com/android/vending/expansion/zipfile/APEZProvider:mAPKExtensionFile	Lcom/android/vending/expansion/zipfile/ZipResourceFile;
    //   158: aload_0
    //   159: iconst_1
    //   160: putfield 76	com/android/vending/expansion/zipfile/APEZProvider:mInit	Z
    //   163: iconst_1
    //   164: ireturn
    //   165: astore 4
    //   167: aload 4
    //   169: invokevirtual 143	android/content/pm/PackageManager$NameNotFoundException:printStackTrace	()V
    //   172: iconst_0
    //   173: ireturn
    //   174: iconst_1
    //   175: anewarray 61	java/lang/String
    //   178: dup
    //   179: iconst_0
    //   180: aload 11
    //   182: aastore
    //   183: astore 9
    //   185: goto -44 -> 141
    //   188: iload 6
    //   190: istore 7
    //   192: iload 6
    //   194: istore 8
    //   196: aconst_null
    //   197: astore 9
    //   199: goto -58 -> 141
    //   202: aload_0
    //   203: aload 9
    //   205: invokestatic 147	com/android/vending/expansion/zipfile/APKExpansionSupport:getResourceZipFile	([Ljava/lang/String;)Lcom/android/vending/expansion/zipfile/ZipResourceFile;
    //   208: putfield 140	com/android/vending/expansion/zipfile/APEZProvider:mAPKExtensionFile	Lcom/android/vending/expansion/zipfile/ZipResourceFile;
    //   211: goto -53 -> 158
    //   214: astore 10
    //   216: aload 10
    //   218: invokevirtual 148	java/io/IOException:printStackTrace	()V
    //   221: iconst_0
    //   222: ireturn
    //
    // Exception table:
    //   from	to	target	type
    //   29	40	165	android/content/pm/PackageManager$NameNotFoundException
    //   146	158	214	java/io/IOException
    //   158	163	214	java/io/IOException
    //   202	211	214	java/io/IOException
  }

  public ContentProviderResult[] applyBatch(ArrayList<ContentProviderOperation> paramArrayList)
    throws OperationApplicationException
  {
    initIfNecessary();
    return super.applyBatch(paramArrayList);
  }

  public int delete(Uri paramUri, String paramString, String[] paramArrayOfString)
  {
    return 0;
  }

  public abstract String getAuthority();

  public String getType(Uri paramUri)
  {
    return "vnd.android.cursor.item/asset";
  }

  public Uri insert(Uri paramUri, ContentValues paramContentValues)
  {
    return null;
  }

  public boolean onCreate()
  {
    return true;
  }

  public AssetFileDescriptor openAssetFile(Uri paramUri, String paramString)
    throws FileNotFoundException
  {
    initIfNecessary();
    String str = paramUri.getEncodedPath();
    if (str.startsWith("/"))
      str = str.substring(1);
    return this.mAPKExtensionFile.getAssetFileDescriptor(str);
  }

  public ParcelFileDescriptor openFile(Uri paramUri, String paramString)
    throws FileNotFoundException
  {
    initIfNecessary();
    AssetFileDescriptor localAssetFileDescriptor = openAssetFile(paramUri, paramString);
    if (localAssetFileDescriptor != null)
      return localAssetFileDescriptor.getParcelFileDescriptor();
    return null;
  }

  public Cursor query(Uri paramUri, String[] paramArrayOfString1, String paramString1, String[] paramArrayOfString2, String paramString2)
  {
    initIfNecessary();
    ZipResourceFile.ZipEntryRO[] arrayOfZipEntryRO1;
    int[] arrayOfInt;
    MatrixCursor localMatrixCursor = null;
    int k;
    ZipResourceFile.ZipEntryRO[] arrayOfZipEntryRO2;
    int m;
    if (this.mAPKExtensionFile == null)
    {
      arrayOfZipEntryRO1 = new ZipResourceFile.ZipEntryRO[0];
//      if (paramArrayOfString1 != null)
//        break;
      arrayOfInt = ALL_FIELDS_INT;
      paramArrayOfString1 = ALL_FIELDS;
      localMatrixCursor = new MatrixCursor(paramArrayOfString1, arrayOfZipEntryRO1.length);
      k = arrayOfInt.length;
      arrayOfZipEntryRO2 = arrayOfZipEntryRO1;
      m = arrayOfZipEntryRO2.length;
    }
    for (int n = 0; ; n++)
    {
//      if (n >= m)
//        break;
//      ZipResourceFile.ZipEntryRO localZipEntryRO = arrayOfZipEntryRO2[n];
      MatrixCursor.RowBuilder localRowBuilder = localMatrixCursor.newRow();
      int i1 = 0;
//      if (i1 >= k)
//        continue;
//      switch (arrayOfInt[i1])
//      {
//      default:
//      case 0:
//      case 1:
//      case 2:
//      case 3:
//      case 4:
//      case 5:
//      case 6:
//      case 7:
//      }
      while (true)
      {
//        i1++;
//        break;
//        arrayOfZipEntryRO1 = this.mAPKExtensionFile.getAllEntries();
//        break;
        int i = paramArrayOfString1.length;
        arrayOfInt = new int[i];
        int j = 0;
        if (j >= i)
          break;
        if (paramArrayOfString1[j].equals("_id"))
          arrayOfInt[j] = 0;
        while (true)
        {
          j++;
          break;
//          if (paramArrayOfString1[j].equals("ZPFN"))
//          {
//            arrayOfInt[j] = 1;
//            continue;
//          }
//          if (paramArrayOfString1[j].equals("ZFIL"))
//          {
//            arrayOfInt[j] = 2;
//            continue;
//          }
//          if (paramArrayOfString1[j].equals("ZMOD"))
//          {
//            arrayOfInt[j] = 3;
//            continue;
//          }
//          if (paramArrayOfString1[j].equals("ZCRC"))
//          {
//            arrayOfInt[j] = 4;
//            continue;
//          }
//          if (paramArrayOfString1[j].equals("ZCOL"))
//          {
//            arrayOfInt[j] = 5;
//            continue;
//          }
//          if (paramArrayOfString1[j].equals("ZUNL"))
//          {
//            arrayOfInt[j] = 6;
//            continue;
//          }
//          if (!paramArrayOfString1[j].equals("ZTYP"))
//            break label355;
//          arrayOfInt[j] = 7;
        }
//        throw new RuntimeException();
//        localRowBuilder.add(Integer.valueOf(i1));
//        continue;
//        localRowBuilder.add(localZipEntryRO.mFileName);
//        continue;
//        localRowBuilder.add(localZipEntryRO.getZipFileName());
//        continue;
//        localRowBuilder.add(Long.valueOf(localZipEntryRO.mWhenModified));
//        continue;
//        localRowBuilder.add(Long.valueOf(localZipEntryRO.mCRC32));
//        continue;
//        localRowBuilder.add(Long.valueOf(localZipEntryRO.mCompressedLength));
//        continue;
//        localRowBuilder.add(Long.valueOf(localZipEntryRO.mUncompressedLength));
//        continue;
//        localRowBuilder.add(Integer.valueOf(localZipEntryRO.mMethod));
      }
    }
//    return localMatrixCursor;
  }

  public int update(Uri paramUri, ContentValues paramContentValues, String paramString, String[] paramArrayOfString)
  {
    return 0;
  }
}

/* Location:           /private/tmp/eternal_lands_android-1.jar
 * Qualified Name:     com.android.vending.expansion.zipfile.APEZProvider
 * JD-Core Version:    0.6.0
 */