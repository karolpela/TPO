package zad1;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.StandardOpenOption;
import java.nio.file.attribute.BasicFileAttributes;
import java.nio.channels.*;
import java.nio.charset.Charset;

public class Futil {
	public static void processDir(String dirName, String resultFileName) {
		Path dirPath = Paths.get(dirName);
		Path resPath = Paths.get(resultFileName);
		try (FileChannel fcout = FileChannel.open(resPath, StandardOpenOption.CREATE, StandardOpenOption.APPEND)) {
			fcout.truncate(0);
			Files.walkFileTree(dirPath, new SimpleFileVisitor<Path>() {
				public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
					try (FileChannel fcin = FileChannel.open(file)) {
						int size = (int) fcin.size();
						ByteBuffer buf = ByteBuffer.allocate(size);

						fcin.read(buf);
						buf.flip();

						Charset inCharset = Charset.forName("Cp1250");
						CharBuffer cbuf = inCharset.decode(buf);
						Charset outCharset = Charset.forName("UTF-8");

						buf = outCharset.encode(cbuf);
						fcout.write(buf);
					}
					return FileVisitResult.CONTINUE;
				}
			});
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
