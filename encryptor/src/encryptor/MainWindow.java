package encryptor;

import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Label;

import java.io.File;

import javax.crypto.Cipher;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.MessageBox;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.DirectoryDialog;
import org.eclipse.swt.events.MouseAdapter;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Text;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;

public class MainWindow {

	protected Shell shell;
	private Text keyInput;
	private Combo combo;
	private Text filePathLabel;
	/**
	 * Launch the application.
	 * 
	 * @param args
	 */
	public static void main(String[] args) {
		try {
			MainWindow window = new MainWindow();
			window.open();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * Open the window.
	 */
	public void open() {
		Display display = Display.getDefault();
		createContents();
		shell.open();
		shell.layout();
		while (!shell.isDisposed()) {
			if (!display.readAndDispatch()) {
				display.sleep();
			}
		}
	}

	/**
	 * Create contents of the window.
	 */
	protected void createContents() {
		shell = new Shell();
		shell.setSize(626, 419);
		shell.setText("文件加密程序");
		shell.setLayout(null);

		Label label = new Label(shell, SWT.NONE);
		label.setBounds(109, 22, 98, 24);
		label.setText("文件/文件夹");
		
		filePathLabel = new Text(shell, SWT.READ_ONLY | SWT.WRAP | SWT.V_SCROLL);
		filePathLabel.setBounds(235, 74, 273, 81);
		filePathLabel.setText("*");

		Button chooseFileButton = new Button(shell, SWT.NONE);
		chooseFileButton.setBounds(231, 17, 84, 34);
		chooseFileButton.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseDown(MouseEvent e) {
				String filePath = handleChooseFile();
				if(filePath != null) {
					filePathLabel.setText(filePath);
				}
			}
		});
		chooseFileButton.setText("选择文件");

		Button chooseDirectoryButton = new Button(shell, SWT.NONE);
		chooseDirectoryButton.setBounds(321, 17, 102, 34);
		chooseDirectoryButton.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseDown(MouseEvent e) {
				String directoryPath = handleChooseDirectory();
				if(directoryPath != null) {
					filePathLabel.setText(directoryPath);
				}
			}
		});
		chooseDirectoryButton.setText("选择文件夹");

		Label label_1 = new Label(shell, SWT.NONE);
		label_1.setBounds(151, 69, 54, 24);
		label_1.setText("已选择");

		Label label_2 = new Label(shell, SWT.NONE);
		label_2.setBounds(132, 176, 72, 24);
		label_2.setText("加密算法");

		combo = new Combo(shell, SWT.READ_ONLY);
		combo.setItems(new String[] {"AES/ECB/NoPadding", "AES/ECB/PKCS5Padding", "AES/ECB/ISO10126Padding", "AES/CBC/PKCS5Padding", "AES/CTR/NoPadding", "AES/OFB/NoPadding"});
		combo.setBounds(235, 178, 273, 32);
		combo.setText("AES/ECB/NoPadding");

		Label label_3 = new Label(shell, SWT.NONE);
		label_3.setBounds(167, 216, 36, 24);
		label_3.setText("密钥");

		keyInput = new Text(shell, SWT.BORDER | SWT.PASSWORD);
		keyInput.setText("123");
		keyInput.setTextLimit(32);
		keyInput.setBounds(230, 218, 278, 34);

		Button decryptButton = new Button(shell, SWT.NONE);
		decryptButton.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseDown(MouseEvent e) {
				File tempFile = new File(filePathLabel.getText());
				boolean isDirectory = tempFile.isDirectory();
				System.out.println("tempFile" + tempFile.toString());
				if(isDirectory) {
					File dir = new File(Utils.removeEnc_fix(tempFile.toString()));
					if (!dir.exists()){
						dir.mkdirs();
					}
					enterDirectory(tempFile, dir, false);
				}
				else {
					handleDecrypt(filePathLabel.getText());
				}
			}
		});
		decryptButton.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
			}
		});
		decryptButton.setBounds(109, 296, 98, 34);
		decryptButton.setText("解密");

		Button encryptButton = new Button(shell, SWT.NONE);
		encryptButton.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseDown(MouseEvent e) {
				File tempFile = new File(filePathLabel.getText());
				boolean isDirectory = tempFile.isDirectory();
				if(isDirectory) {
					File dir = new File(Utils.addEnc(tempFile.toString()));
					if (!dir.exists()){
						dir.mkdirs();
					}
					enterDirectory(tempFile, dir, true);
				}
				else {
					handleEncrypt(filePathLabel.getText());
				}
			}
		});
		encryptButton.setBounds(410, 296, 98, 34);
		encryptButton.setText("加密");
		/*
		Button button = new Button(shell, SWT.NONE);
		button.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseDown(MouseEvent e) {
				File path = new File(filePathLabel.getText());
				File dir = new File(Utils.addEnc(path.toString()));
				if (!dir.exists()){
					dir.mkdirs();
				}
				enterDirectory(path, dir, true);
			}
		});
		button.setBounds(516, 17, 72, 34);
		button.setText("测试");
		*/
	}

	private String handleChooseFile() {
		FileDialog fileDialog = new FileDialog(shell, SWT.OPEN);
		fileDialog.setFilterExtensions(new String[] { "*.*" });
		fileDialog.setFilterPath("c:\\");
		return fileDialog.open();
	}

	private String handleChooseDirectory() {
		DirectoryDialog directoryDialog = new DirectoryDialog(shell, SWT.OPEN);
		String result = directoryDialog.open();
		System.out.println("文件夹路径" + result);
		return result;
	}

	// 加密
	public void handleEncrypt(String path) {
		try {
			System.out.println(filePathLabel.getText());
			File src_file = new File(path);
			File enc_file = new File(Utils.addEnc(path));
			String key = Utils.padding(keyInput.getText());
	        System.out.println(key);
			int exception_id = Utils.crypto(Cipher.ENCRYPT_MODE, key, src_file, enc_file, "AES", combo.getText());
			if(exception_id == -6) {
				MessageBox box = new MessageBox(shell, SWT.ICON_ERROR | SWT.OK);
				box.setText("文件不存在");
				box.setMessage("文件已被重命名/移动或删除");
				box.open();
			}
			else if (exception_id == -5) {
				MessageBox box = new MessageBox(shell, SWT.ICON_ERROR | SWT.OK);
				box.setText("块大小错误");
				box.setMessage("文件大小必须是16字节的整数倍。\n若对此文件加/解密，请使用带Padding的加密算法");
				box.open();
			}
		}catch(CryptoException ex) {
	        System.out.println(ex.toString());
		}
	}

	// 解密
	private void handleDecrypt(String path){
		try {
			File enc_file = new File(path);
			File src_file = new File(Utils.removeEnc(path));
			String key = Utils.padding(keyInput.getText());
			int exception_id = Utils.crypto(Cipher.DECRYPT_MODE, key, enc_file, src_file, "AES", combo.getText());
			if(exception_id == -6) {
				MessageBox box = new MessageBox(shell, SWT.ICON_ERROR | SWT.OK);
				box.setText("文件不存在");
				box.setMessage("文件已被重命名/移动或删除");
				box.open();
			}
			else if (exception_id == -5) {
				MessageBox box = new MessageBox(shell, SWT.ICON_ERROR | SWT.OK);
				box.setText("块大小错误");
				box.setMessage("文件大小必须是16字节的整数倍。\n若对此文件加/解密，请使用带Padding的加密算法");
				box.open();
			}
			else if (exception_id == -1) {
				MessageBox box = new MessageBox(shell, SWT.ICON_ERROR | SWT.OK);
				box.setText("填充错误");
				box.setMessage("文件大小必须是16字节的整数倍。\n若对此文件加/解密，请使用带Padding的加密算法");
				box.open();
			}
		}catch(CryptoException ex) {
			MessageBox box = new MessageBox(shell, SWT.ICON_ERROR | SWT.OK);
			box.setText("文件不存在");
			box.setMessage("文件已被重命名/移动或删除");
			box.open();
	        System.out.println(ex.toString());
		}
	}
	
	public void enterDirectory(File path, File dst, boolean isEncrypt) {
	      File filesList[] = path.listFiles();
	      if(filesList == null) return;
	      for(File file : filesList) {
	         if(file.isFile()) {
	        	 String filePath = dst + "\\" +file.getName();
	        	 File result = new File(filePath);
	        	 Utils.copyFile(file, result);
	        	 if(isEncrypt) {
	        		 handleEncrypt(filePath);
	        	 }
	        	 else {
	        		 handleDecrypt(filePath);
	        	 }
	        	 
	        	 System.out.println("File path: "+ path + "\\" +file.getName());
	        	 System.out.println("File path: "+ dst + "\\" +file.getName());
	        	 result.delete();
	         } else {
	        	String newPath = dst.toString() + "\\" + file.getName();
	     		File dstDir = new File(newPath);
	    		if(!dstDir.exists()) {
	    			dstDir.mkdirs();
	    		}
	    		enterDirectory(file, new File(newPath), isEncrypt);
	         }
	      }
	}
 }
