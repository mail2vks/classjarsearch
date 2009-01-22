package com.vivek.file;

import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.util.Enumeration;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

//import org.apache.log4j.Logger;

public class DirectoryReader {

	//private static Logger logger = Logger.getLogger(DirectoryReader.class);

	public static void main(String args[]) {
		if (args.length < 2) {
			System.out
					.println("Usage: java -jar jarSearch.jar <Location to Search> <Class File Name>");
		} else {
			DirectoryReader.search(new JarFilter(), args[1], args[0]);
		}
	}

	public static void search(FilenameFilter filter, String searchString,
			String directory) {

		File file = new File(directory);

		long startTime = System.currentTimeMillis();

		parse(file, filter, searchString);

		long endTime = System.currentTimeMillis();

		long timeTaken = (endTime - startTime) / 1000;
		System.out.println("Time taken " + timeTaken + "s");
		//logger.info("Time taken " + timeTaken + "s");

	}

	private static void parse(File f, FilenameFilter filter, String searchString) {

		if (f.isFile()) {
			//logger.debug("Now Searching File : " + f.getPath());
			searchClassInJar(f, searchString);

		} else if (f.isDirectory() && f.isHidden() == false) {
			//logger.debug("Now Parsing Directory : " + f.getPath());
			getContent(f, filter, searchString);
		}

	}

	private static void getContent(File f, FilenameFilter filter,
			String searchString) {

		File[] fileArr = f.listFiles(filter);

		if (fileArr != null) {
			for (File fi : fileArr) {
				if (fi.isFile()) {
					searchClassInJar(fi, searchString);
					//logger.debug(fi.getPath());
				}

			}
		}

		File[] dirArr = f.listFiles();

		if (dirArr != null) {
			for (File fi : dirArr) {
				if (fi.isDirectory())
					parse(fi, filter, searchString);
			}
		}

	}

	private static void searchClassInJar(File f, String searchString) {

		String fileName = null;
		//logger
				//.debug("Searching for \"" + searchString + "\" in "
						//+ f.getPath());
		try {
			if (f.canRead()) {

				JarFile jarFile = new JarFile(f);

				Enumeration<JarEntry> entries = jarFile.entries();

				while (entries.hasMoreElements()) {
					fileName = entries.nextElement().getName();
					//logger.debug(fileName);
					if (fileName.indexOf(searchString) != -1) {
						System.out.println("Found " + searchString + " as "
								+ fileName + " in " + f.getPath());
						//logger.info("Found " + searchString + " as " + fileName
								//+ " in " + f.getPath());
					}
				}

			}
		} catch (IOException e) {
			e.printStackTrace();
			//logger.error("Error Occurred when parsing " + f.getPath(), e
					//.getCause());
		}

	}
}
