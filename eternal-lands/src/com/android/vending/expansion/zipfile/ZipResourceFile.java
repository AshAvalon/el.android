package com.android.vending.expansion.zipfile;

import android.content.res.AssetFileDescriptor;
import android.os.ParcelFileDescriptor;
import android.util.Log;
import java.io.EOFException;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.channels.FileChannel.MapMode;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Vector;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

import com.android.vending.expansion.zipfile.ZipResourceFile.ZipEntryRO;

public class ZipResourceFile
{
  static final boolean LOGV = false;
  static final String LOG_TAG = "zipro";
  static final int kCDECRC = 16;
  static final int kCDECommentLen = 32;
  static final int kCDECompLen = 20;
  static final int kCDEExtraLen = 30;
  static final int kCDELen = 46;
  static final int kCDELocalOffset = 42;
  static final int kCDEMethod = 10;
  static final int kCDEModWhen = 12;
  static final int kCDENameLen = 28;
  static final int kCDESignature = 33639248;
  static final int kCDEUncompLen = 24;
  static final int kCompressDeflated = 8;
  static final int kCompressStored = 0;
  static final int kEOCDFileOffset = 16;
  static final int kEOCDLen = 22;
  static final int kEOCDNumEntries = 8;
  static final int kEOCDSignature = 101010256;
  static final int kEOCDSize = 12;
  static final int kLFHExtraLen = 28;
  static final int kLFHLen = 30;
  static final int kLFHNameLen = 26;
  static final int kLFHSignature = 67324752;
  static final int kMaxCommentLen = 65535;
  static final int kMaxEOCDSearch = 65557;
  static final int kZipEntryAdj = 10000;
  private HashMap<String, ZipEntryRO> mHashMap = new HashMap();
  ByteBuffer mLEByteBuffer = ByteBuffer.allocate(4);
  public HashMap<File, ZipFile> mZipFiles = new HashMap();

  public ZipResourceFile(String paramString)
    throws IOException
  {
    addPatchFile(paramString);
  }

  private static int read4LE(RandomAccessFile paramRandomAccessFile)
    throws EOFException, IOException
  {
    return swapEndian(paramRandomAccessFile.readInt());
  }

  private static int swapEndian(int paramInt)
  {
    return ((paramInt & 0xFF) << 24) + ((0xFF00 & paramInt) << 8) + ((0xFF0000 & paramInt) >>> 8) + (0xFF & paramInt >>> 24);
  }

  private static int swapEndian(short paramShort)
  {
    return (paramShort & 0xFF) << 8 | (0xFF00 & paramShort) >>> 8;
  }

  void addPatchFile(String paramString)
    throws IOException
  {
    File localFile = new File(paramString);
    RandomAccessFile localRandomAccessFile = new RandomAccessFile(localFile, "r");
    long l1 = localRandomAccessFile.length();
    if (l1 < 22L)
      throw new IOException();
    long l2 = 65557L;
    if (l2 > l1)
      l2 = l1;
    localRandomAccessFile.seek(0L);
    int i = read4LE(localRandomAccessFile);
    if (i == 101010256)
    {
      Log.i("zipro", "Found Zip archive, but it looks empty");
      throw new IOException();
    }
    if (i != 67324752)
    {
      Log.v("zipro", "Not a Zip archive");
      throw new IOException();
    }
    localRandomAccessFile.seek(l1 - l2);
    ByteBuffer localByteBuffer1 = ByteBuffer.allocate((int)l2);
    byte[] arrayOfByte1 = localByteBuffer1.array();
    localRandomAccessFile.readFully(arrayOfByte1);
    localByteBuffer1.order(ByteOrder.LITTLE_ENDIAN);
    int k;
    long l3;
    long l4;
    for (int j = -22 + arrayOfByte1.length; ; j--)
    {
      if ((j >= 0) && ((arrayOfByte1[j] != 80) || (localByteBuffer1.getInt(j) != 101010256)))
        continue;
      if (j < 0)
        Log.d("zipro", "Zip: EOCD not found, " + paramString + " is not zip");
      k = localByteBuffer1.getShort(j + 8);
      l3 = 0xFFFFFFFF & localByteBuffer1.getInt(j + 12);
      l4 = 0xFFFFFFFF & localByteBuffer1.getInt(j + 16);
      if (l4 + l3 <= l1)
        break;
      Log.w("zipro", "bad offsets (dir " + l4 + ", size " + l3 + ", eocd " + j + ")");
      throw new IOException();
    }
    if (k == 0)
    {
      Log.w("zipro", "empty archive?");
      throw new IOException();
    }
    MappedByteBuffer localMappedByteBuffer = localRandomAccessFile.getChannel().map(FileChannel.MapMode.READ_ONLY, l4, l3);
    localMappedByteBuffer.order(ByteOrder.LITTLE_ENDIAN);
    byte[] arrayOfByte2 = new byte[65535];
    int m = 0;
    ByteBuffer localByteBuffer2 = ByteBuffer.allocate(30);
    localByteBuffer2.order(ByteOrder.LITTLE_ENDIAN);
    for (int n = 0; n < k; n++)
    {
      if (localMappedByteBuffer.getInt(m) != 33639248)
      {
        Log.w("zipro", "Missed a central dir sig (at " + m + ")");
        throw new IOException();
      }
      int i1 = 0xFFFF & localMappedByteBuffer.getShort(m + 28);
      int i2 = 0xFFFF & localMappedByteBuffer.getShort(m + 30);
      int i3 = 0xFFFF & localMappedByteBuffer.getShort(m + 32);
      localMappedByteBuffer.position(m + 46);
      localMappedByteBuffer.get(arrayOfByte2, 0, i1);
      localMappedByteBuffer.position(0);
      String str = new String(arrayOfByte2, 0, i1);
      ZipEntryRO localZipEntryRO = new ZipEntryRO(paramString, localFile, str);
      localZipEntryRO.mMethod = (0xFFFF & localMappedByteBuffer.getShort(m + 10));
      localZipEntryRO.mWhenModified = (0xFFFFFFFF & localMappedByteBuffer.getInt(m + 12));
      localZipEntryRO.mCRC32 = (0xFFFFFFFF & localMappedByteBuffer.getLong(m + 16));
      localZipEntryRO.mCompressedLength = (0xFFFFFFFF & localMappedByteBuffer.getLong(m + 20));
      localZipEntryRO.mUncompressedLength = (0xFFFFFFFF & localMappedByteBuffer.getLong(m + 24));
      localZipEntryRO.mLocalHdrOffset = (0xFFFFFFFF & localMappedByteBuffer.getInt(m + 42));
      localByteBuffer2.clear();
      localZipEntryRO.setOffsetFromFile(localRandomAccessFile, localByteBuffer2);
      this.mHashMap.put(str, localZipEntryRO);
      m += i3 + (i2 + (i1 + 46));
    }
  }

  public ZipEntryRO[] getAllEntries()
  {
    Collection<ZipEntryRO> localCollection = this.mHashMap.values();
    return (ZipEntryRO[])localCollection.toArray(new ZipEntryRO[localCollection.size()]);
  }

  public AssetFileDescriptor getAssetFileDescriptor(String paramString)
  {
    ZipEntryRO localZipEntryRO = (ZipEntryRO)this.mHashMap.get(paramString);
    if (localZipEntryRO != null)
      return localZipEntryRO.getAssetFileDescriptor();
    return null;
  }

  ZipEntryRO[] getEntriesAt(String paramString)
  {
    Vector<ZipEntryRO> localVector = new Vector<ZipEntryRO>();
    Collection<ZipEntryRO> localCollection = this.mHashMap.values();
    if (paramString == null)
      paramString = "";
    int i = paramString.length();
    Iterator<ZipEntryRO> localIterator = localCollection.iterator();
    while (localIterator.hasNext())
    {
      ZipEntryRO localZipEntryRO = (ZipEntryRO)localIterator.next();
      if ((!localZipEntryRO.mFileName.startsWith(paramString)) || (-1 != localZipEntryRO.mFileName.indexOf('/', i)))
        continue;
      localVector.add(localZipEntryRO);
    }
    return (ZipEntryRO[])localVector.toArray(new ZipEntryRO[localVector.size()]);
  }

  public InputStream getInputStream(String paramString)
    throws IOException
  {
    ZipEntryRO localZipEntryRO = (ZipEntryRO)this.mHashMap.get(paramString);
    if (localZipEntryRO != null)
    {
      if (localZipEntryRO.isUncompressed())
        return localZipEntryRO.getAssetFileDescriptor().createInputStream();
      ZipFile localZipFile = (ZipFile)this.mZipFiles.get(localZipEntryRO.getZipFile());
      if (localZipFile == null)
      {
        localZipFile = new ZipFile(localZipEntryRO.getZipFile(), 1);
        this.mZipFiles.put(localZipEntryRO.getZipFile(), localZipFile);
      }
      ZipEntry localZipEntry = localZipFile.getEntry(paramString);
      if (localZipEntry != null)
        return localZipFile.getInputStream(localZipEntry);
    }
    return null;
  }

  public static final class ZipEntryRO
  {
    public long mCRC32;
    public long mCompressedLength;
    public final File mFile;
    public final String mFileName;
    public long mLocalHdrOffset;
    public int mMethod;
    public long mOffset = -1L;
    public long mUncompressedLength;
    public long mWhenModified;
    public final String mZipFileName;

    public ZipEntryRO(String paramString1, File paramFile, String paramString2)
    {
      this.mFileName = paramString2;
      this.mZipFileName = paramString1;
      this.mFile = paramFile;
    }

    public AssetFileDescriptor getAssetFileDescriptor()
    {
      if (this.mMethod == 0)
        try
        {
          AssetFileDescriptor localAssetFileDescriptor = new AssetFileDescriptor(ParcelFileDescriptor.open(this.mFile, 268435456), getOffset(), this.mUncompressedLength);
          return localAssetFileDescriptor;
        }
        catch (FileNotFoundException localFileNotFoundException)
        {
          localFileNotFoundException.printStackTrace();
        }
      return null;
    }

    public long getOffset()
    {
      return this.mOffset;
    }

    public File getZipFile()
    {
      return this.mFile;
    }

    public String getZipFileName()
    {
      return this.mZipFileName;
    }

    public boolean isUncompressed()
    {
      return this.mMethod == 0;
    }

    public void setOffsetFromFile(RandomAccessFile paramRandomAccessFile, ByteBuffer paramByteBuffer)
      throws IOException
    {
      long l = this.mLocalHdrOffset;
      try
      {
        paramRandomAccessFile.seek(l);
        paramRandomAccessFile.readFully(paramByteBuffer.array());
        if (paramByteBuffer.getInt(0) != 67324752)
        {
          Log.w("zipro", "didn't find signature at start of lfh");
          throw new IOException();
        }
      }
      catch (FileNotFoundException localFileNotFoundException)
      {
        localFileNotFoundException.printStackTrace();
//        return;
        int i = 0xFFFF & paramByteBuffer.getShort(26);
        int j = 0xFFFF & paramByteBuffer.getShort(28);
        this.mOffset = (30L + l + i + j);
        return;
      }
      catch (IOException localIOException)
      {
        localIOException.printStackTrace();
      }
    }
  }
}

/* Location:           /private/tmp/eternal_lands_android-1.jar
 * Qualified Name:     com.android.vending.expansion.zipfile.ZipResourceFile
 * JD-Core Version:    0.6.0
 */