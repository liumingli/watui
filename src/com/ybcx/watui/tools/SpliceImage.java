package com.ybcx.watui.tools;

import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Iterator;
import java.util.Random;

import javax.imageio.IIOImage;
import javax.imageio.ImageIO;
import javax.imageio.ImageWriteParam;
import javax.imageio.ImageWriter;
import javax.imageio.plugins.jpeg.JPEGImageWriteParam;
import javax.imageio.stream.ImageOutputStream;

import org.apache.log4j.Logger;

import com.sun.image.codec.jpeg.JPEGCodec;
import com.sun.image.codec.jpeg.JPEGImageDecoder;

public class SpliceImage {

	private static Logger log = Logger.getLogger(SpliceImage.class);

	public static String spliceImage(String imagePath, String primaryLong,
			String endingLong) {

		String fileName = new File(primaryLong).getName();
		int position = fileName.lastIndexOf(".");
		// 后缀 .jpg
		String extend = fileName.substring(position);
		// 随机数
		Random r = new Random();
		String num = String.valueOf(r.nextInt(100));

		String destImg = imagePath + File.separator
				+ fileName.substring(0, position) + "_weibo" + num + extend;
		File outFile = new File(destImg);
		
		ImageOutputStream ios=null;
		try {
			// 读取第一张图片
			File fileOne = new File(primaryLong);
     		//BufferedImage ImageOne = ImageIO.read(fileOne);
	        JPEGImageDecoder decoder = JPEGCodec.createJPEGDecoder(new FileInputStream(fileOne));  
	        BufferedImage ImageOne = decoder.decodeAsBufferedImage(); 
			
			int width = ImageOne.getWidth();// 图片宽度
			int height = ImageOne.getHeight();// 图片高度
			// 从图片中读取RGB
			int[] ImageArrayOne = new int[width * height];
			ImageArrayOne = ImageOne.getRGB(0, 0, width, height, ImageArrayOne,0, width);

			// 对第二张图片做相同的处理
			File fileTwo = new File(endingLong);
			//BufferedImage ImageTwo = ImageIO.read(fileTwo);
			JPEGImageDecoder decoder2 = JPEGCodec.createJPEGDecoder(new FileInputStream(fileTwo));  
		    BufferedImage ImageTwo = decoder2.decodeAsBufferedImage(); 
			int width2 = ImageTwo.getWidth();// 图片宽度
			int height2 = ImageTwo.getHeight();// 图片高度
			int[] ImageArrayTwo = new int[width2 * height2];
			ImageArrayTwo = ImageTwo.getRGB(0, 0, width2, height2, ImageArrayTwo, 0, width2);

			// 新的宽度以小的为准
			int newWidth = 0;
			if (width > width2) {
				newWidth = width2;
			} else {
				newWidth = width;
			}

			// 新高度累加
			int newHeight = height + height2;

			log.info("new image width:" + newWidth + "; height:" + newHeight);

			// 生成新图片
			BufferedImage ImageNew = new BufferedImage(newWidth, newHeight,
					BufferedImage.TYPE_INT_RGB);
			ImageNew.getGraphics().drawImage(
					ImageNew.getScaledInstance(newWidth, newHeight,
							Image.SCALE_SMOOTH), 0, 0, null);

			ImageNew.setRGB(0, 0, newWidth, height, ImageArrayOne, 0, width);// 设置上半部分的RGB
			ImageNew.setRGB(0, height, newWidth, height2, ImageArrayTwo, 0,
					width2);// 设置下半部分的RGB

			// 保存画质
			ImageWriter writer = null;

			Iterator<ImageWriter> iter = ImageIO
					.getImageWritersByFormatName("jpg");

			if (iter.hasNext()) {
				writer = (ImageWriter) iter.next();
			}

			ios = ImageIO.createImageOutputStream(outFile);
			writer.setOutput(ios);

			ImageWriteParam param = new JPEGImageWriteParam(
					java.util.Locale.getDefault());

			param.setCompressionMode(ImageWriteParam.MODE_EXPLICIT);

			param.setCompressionQuality(0.90f);

			writer.write(null, new IIOImage(ImageNew, null, null), param);

			// ImageIO.write(ImageNew, "jpg", outFile);//写图片

		} catch (Exception e) {
			e.printStackTrace();
		}finally{
			try {
				ios.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

		if (outFile.exists()) {
			return destImg;
		} else {
			log.info("Outfile is not exist, Splice image dest file path :"
					+ destImg);
			return "";
		}
	}

}
