package edu.na1.assignment.utils;

import java.io.File;
import java.util.List;


public class FileUtils {
    /**
     * This method searches for the given file by the Peer in the listed
     * directories......
     *
     * @param directories
     * @param fileName
     * @return
     * @throws Exception
     */
    public static File searchDirectoriesForFile(List<String> directories,
                                                String fileName) throws Exception {
        File requestedFile = null;

        if (directories == null || directories.isEmpty()) {
            throw new Exception("Atleast one directory should be passed");
        } else {
            for (String directory : directories) {
                requestedFile = searchFileAndReturnIfFound(directory, 0,
                        fileName);

                if (requestedFile != null) {
                    // Found file
                    return requestedFile;
                }
            }
        }
        return requestedFile;
    }

    /**
     * this method searches for the given file and transfers the file to the
     * client if exist..
     *
     * @param dirPath
     * @param level
     * @param fileName
     * @return
     */
    public static File searchFileAndReturnIfFound(String dirPath, int level,
                                                  String fileName) {
        File parentLocation = new File(dirPath);
        File requestedFile = null;

        if (parentLocation.canRead()) {
            File[] firstLevelFiles = parentLocation.listFiles();
            if (firstLevelFiles != null && firstLevelFiles.length > 0) {
                for (File aFile : firstLevelFiles) {

                    if (aFile.isDirectory()) {

                        requestedFile = searchFileAndReturnIfFound(
                                aFile.getAbsolutePath(), level + 1, fileName);
                        
                        if (requestedFile != null) {
                        	return requestedFile;
                        }

                    } else if (aFile.getName() != null
                                    && fileName.equalsIgnoreCase(aFile.getName())) {
                        return aFile.getAbsoluteFile();
                    }
                }
            }
        }

        return requestedFile;
    }
}
