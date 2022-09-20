package encryptor;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.security.Key;

import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.MessageBox;

import java.io.IOException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;

/*
 * Packaged utils for encrypt/decrypt or lite functions for string processing
 * Supported algorithm:
 * | ECB mode cannot use IV
 * - AES/ECB/NoPadding
 * - AES/ECB/PKCS5Padding
 * - AES/ECB/ISO10126Padding 
 * | Require IV
 * - AES/CBC/PKCS5Padding
 * - AES/CTR/NoPadding
 * - AES/OFB/NoPadding
 */

public class Utils {
	public static int crypto(int cipherMode, String key, File inputFile, File outputFile, String algorithm,
			String tranformation) throws CryptoException {
		try {
			Key secretKey = new SecretKeySpec(key.getBytes(), algorithm);
			Cipher cipher = Cipher.getInstance(tranformation);
			// AES/CBC/PKCS5Padding
			if(!tranformation.substring(0, 7).equals("AES/ECB") ) {
				final IvParameterSpec iv = new IvParameterSpec(new byte[16]);
				try {
					cipher.init(cipherMode, secretKey, iv);
					System.out.println("Seems to be successful");
				}
				catch(Exception ex) {
					ex.printStackTrace();
					throw new CryptoException("Error NoSuchPaddingException", ex);
				}
			}
			else {
				cipher.init(cipherMode, secretKey);
			}
			
			FileInputStream inputStream = new FileInputStream(inputFile);
			byte[] inputBytes = new byte[(int) inputFile.length()];
			inputStream.read(inputBytes);
			
			byte[] outputBytes = cipher.doFinal(inputBytes);
			
			FileOutputStream outputStream = new FileOutputStream(outputFile);
			outputStream.write(outputBytes);
			
			inputStream.close();
			outputStream.close();
		}
		catch(NoSuchPaddingException ex) {
			ex.printStackTrace();
			return -1;
		}
		catch(NoSuchAlgorithmException ex) {
			ex.printStackTrace();
			return -2;
		}
		catch(InvalidKeyException ex) {
			ex.printStackTrace();
			return -3;
		}
		catch(BadPaddingException ex) {
			ex.printStackTrace();
			return -4;
		}
		catch(IllegalBlockSizeException ex) {
			ex.printStackTrace();
			return -5;
		}
		catch(IOException ex) {
			ex.printStackTrace();
			return -6;
		}
		return 0;
	}
	
	public static String addEnc(String filePath) {
		int index = filePath.length() - 1;
		while(index >= 0 && filePath.charAt(index) != '.') {
			index--;
		}
		// 有的文件是无后缀的。无后缀则直接加enc，有就在文件名后加
		return index == -1 ? filePath + "enc" : filePath.substring(0, index) + "enc" + filePath.substring(index); 
	}
	
	public static String removeEnc(String filePath) {
		if(filePath.length() < 3) return filePath;
		int index = filePath.length() - 1;
		while(index >= 0 && filePath.charAt(index) != '.') {
			index--;
		}
		return index == -1 ? filePath.substring(filePath.length() - 3): filePath.substring(0, index - 3) + filePath.substring(index); 
	}
	
	public static String removeEnc_fix(String filePath) {
		if(filePath.length() < 3) return filePath;
		String end = filePath.substring(filePath.length() - 3);
		if(end.equals("enc")) {
			return filePath.substring(0, filePath.length() - 3);
		}
		return filePath;
	}
	
	public static String padding(String src) {
		int[] avBits = {16, 24, 32};
		for(int index = 0; index < avBits.length; index++) {
			if(src.length() <= avBits[index]) {
		        StringBuffer strBuff = new StringBuffer();
		        strBuff.append(src);
		        for (int i = 0; i < avBits[index] - src.length(); i++) {
		        	strBuff.append("=");
		        }
		        return strBuff.substring(0, strBuff.length());
			}
		}
		return src.substring(0, 32);
	}
	public static void copyFile(File inputFile, File outputFile) {
		try {
			FileInputStream inputStream = new FileInputStream(inputFile);
			byte[] inputBytes = new byte[(int) inputFile.length()];
			inputStream.read(inputBytes);
			
			FileOutputStream outputStream = new FileOutputStream(outputFile);
			outputStream.write(inputBytes);
			
			inputStream.close();
			outputStream.close();
		}
		catch(Exception ex){
			System.out.println("文件复制错误");
		}
	}
}
