package com.letv.lecloud.solar.utils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import android.util.Log;

//import com.letv.lecloud.solar.upload.Configuration;

public class FileID {

	private String name;
	private String id;
	private long size;
	private static String encoding;

	public static final int BUFFER_SIZE = 512 * 1024;// 计算文件指纹时块大小（不要更改）
	public static final int EACH_CHIP_SIZE = 64 * 1024;// 计算文件指纹时每片大小（不要更改）
	public static final int BLOCK_COUNTER = 8;// 计算文件指纹的片数（不要更改）


	public static String byte2hex(byte[] b) {
		String stmp = "";
		StringBuilder sb = new StringBuilder("");
		for (int n = 0; n < b.length; n++) {
			stmp = Integer.toHexString(b[n] & 0xFF);
			sb.append((stmp.length() == 1) ? "0" + stmp : stmp);
		}
		return sb.toString().trim();
	}
	
	/**
	 * 文件指纹生成
	 * @param filename
	 * @return
	 * @throws IOException
	 * @throws FileNotFoundException
	 * @throws NoSuchAlgorithmException
	 */
	public static String calc(String filename)
			throws IOException, FileNotFoundException, NoSuchAlgorithmException {

		encoding = System.getProperty("file.encoding");//获取文件编码

		FileInputStream in = null;
		RandomAccessFile raf = null;
		MessageDigest messagedigest = null;
		String robj = new String();
		String hex = null;

		File f = new File(new String(filename.getBytes("UTF-8"), encoding));

		if (!(f.exists())) {
			return hex;
		}

		if (filename == null || filename.equals("")) {
			return hex;
		}

		try {
			messagedigest = MessageDigest.getInstance("SHA-1");

			if (f.length() <= BUFFER_SIZE) {
				int counter = 0;
				int iRet = 0;
				in = new FileInputStream(f);
				byte[] buf = new byte[BUFFER_SIZE];
				while (counter < f.length()) {
					iRet = in.read(buf);
					if (iRet > 0) {
						messagedigest.update(buf, 0, iRet);
						counter += iRet;
					} else {
						break;
					}
				}
				buf = null;
				System.gc();

			} else {
				long average = 0;
				int iRet = 0;
				int counter = 0;
				raf = new RandomAccessFile(f, "r");
				byte[] buf = new byte[EACH_CHIP_SIZE];

				if ((f.length() % 8) == 0) {
					average = f.length() / 8;
				} else {
					average = f.length() / 8 + 1;
				}
				for (int i = 0; i < BLOCK_COUNTER; i++) {
					raf.seek(average * i);
					counter = 0;
					while (counter < EACH_CHIP_SIZE) {
						iRet = raf.read(buf);
						if (iRet > 0) {
							messagedigest.update(buf, 0, iRet);
							counter += iRet;
						} else {
							break;
						}
					}
				}
				messagedigest.update(String.valueOf(f.length()).getBytes());
				buf = null;
				System.gc();
			}

			hex = FileID.byte2hex(messagedigest.digest());


		} catch (FileNotFoundException e) {
			Log.e("error", e.getMessage(), e);
			throw e;
		} catch (NoSuchAlgorithmException e) {
			Log.e("error", e.getMessage(), e);
			throw e;
		} finally {
			if (null != in) {
				in.close();
				in = null;
			}
			if (null != raf) {
				raf.close();
				raf = null;
			}
		}

		return hex;
	}

	private FileID() {
		this.name = null;
		this.id = null;
		this.size = -1;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public long getSize() {
		return size;
	}

	public void setSize(long size) {
		this.size = size;
	}
}
