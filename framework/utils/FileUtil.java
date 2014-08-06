package utils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.SequenceInputStream;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import data.Data;

public class FileUtil {
	public static void channel(File from,File to,boolean isAppend) throws IOException{
		FileChannel fout =new FileOutputStream(to,isAppend).getChannel();
		fout.position(fout.size());
		FileInputStream fin = new FileInputStream(from);
		FileChannel in = fin.getChannel();
		ByteBuffer bf = ByteBuffer.allocate(fin.available());
		in.read(bf);
		bf.flip();
		fout.write(bf);
		System.out.println("file size-->"+fout.size());
		fout.close();
		in.close();
	}
	public static String getNewFilePath(String suffix){
		return Data.FILE_ROOT+System.currentTimeMillis()+suffix;
	}

	public static void amrMergerAndDel(String targetPath, List<String> filePaths) throws IOException {
		FileOutputStream targetFileOutput = new FileOutputStream(new File(targetPath));
		for(int i=0;i<filePaths.size();i++){
			File f = new File(filePaths.get(i));
			FileInputStream fin = new FileInputStream(f);
			byte[] bs = new byte[fin.available()];
			fin.read(bs);
			if(i==0){
				targetFileOutput.write(bs);
			}else{
				targetFileOutput.write(bs,6,bs.length-6);
			}
			targetFileOutput.flush();
			fin.close();
			f.delete();
		}
		targetFileOutput.flush();
		targetFileOutput.close();
	}
}
