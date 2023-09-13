package jason.hermes.utils;

import jason.hermes.exception.*;

import java.io.*;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Locale;

public class FileUtils {

    public static final String BASE_PATH = "cryogenated" + File.separator;

    public static final String MAS_STRUCTURE_FILE_EXTENSION = ".mas2j";
    public static final String AGENT_FOLDER = "asl";
    private static final int FILE_MAX_ATTEMPT_TO_CREATE = 3;
    private static final int FOLDER_MAX_ATTEMPT_TO_CREATE = 3;
    private static final int FILE_MAX_ATTEMPT_TO_DELETE = 3;
    public static final String NEW_LINE = "\n";

    public static File getMasPath(String agentASLSrc) {
        File masFolder = null;
        File file = new File(treatFilePath(agentASLSrc));

        while (file != null && file.exists()) {
            File[] files = file.listFiles();
            if (files != null && files.length > 0) {
                for (File listFile : files) {
                    if (listFile.getName().endsWith(FileUtils.MAS_STRUCTURE_FILE_EXTENSION)) {
                        masFolder = listFile.getParentFile();
                        break;
                    }
                }
            }
            file = file.getParentFile();
        }
        return masFolder;
    }

    public static String getCryogenatedMasPath(String oldMasPath, String masName) {
        String masBasePath = oldMasPath + File.separator + BASE_PATH + LocalDateTime
                .now(ZoneId.of("America/Sao_Paulo"))
                .format(DateTimeFormatter.ofPattern("yyyy-MM-dd_HH-mm-ss-SSS",
                        new Locale("pt", "BR")));
        String path = masBasePath + File.separator + masName;
        File masPath = new File(path);
        int i = 1;
        while(masPath.exists()) {
            masPath = new File(masBasePath + File.separator + masName + i);
            i++;
        }
        return path.toString();
    }

    public static boolean createFolder(File folder) {
        boolean folderWasCreated = false;
        int createFolderAttempt = 0;

        // Verifica se a pasta já existe.
        if (folder.exists()) {
            folderWasCreated = true;
            createFolderAttempt = FOLDER_MAX_ATTEMPT_TO_CREATE;
        }

        while (createFolderAttempt < FOLDER_MAX_ATTEMPT_TO_CREATE) {
            if (folder.mkdirs()) {
                folderWasCreated = true;
                break;
            }
            createFolderAttempt++;
        }
        return folderWasCreated;
    }

    public static boolean createFile(File file) {
        boolean fileWasCreated = false;
        int createFileAttempt = 0;

        // Verifica se o arquivo já existe.
        if (file.exists()) {
            fileWasCreated = true;
            createFileAttempt = FILE_MAX_ATTEMPT_TO_CREATE;
        }

        while (createFileAttempt < FILE_MAX_ATTEMPT_TO_CREATE) {
            try {
                if (file.createNewFile()) {
                    fileWasCreated = true;
                    break;
                }
            } catch (IOException e) {
                throw new ErrorCreatingFileException(file.getName(), e);
            }
            createFileAttempt++;
        }
        return fileWasCreated;
    }

    public static boolean writeFileContent(File file, String content) {
        FileWriter fileWriter = null;
        PrintWriter printWriter = null;
        boolean wasWritten = false;
        try {
            fileWriter = new FileWriter(file, false);
            printWriter = new PrintWriter(fileWriter);
            printWriter.print(content);
            printWriter.flush();
            wasWritten = true;
        } catch (IOException e) {
            throw new ErrorWritingFileContentException(file.getName(), e);
        } finally {
            if (printWriter != null) {
                printWriter.close();
            }
            if (fileWriter != null) {
                try {
                    fileWriter.close();
                } catch (IOException e) {
                    throw new ErrorClosingFileWriterException(file.getName(), e);
                }
            }
        }
        return wasWritten;
    }

    public static boolean deleteAllFilesExcept(String baseFolder, List<String> filesExcept) {
        boolean filesWasDeleted = true;
        File baseFolderFile = new File(baseFolder);
        if (baseFolderFile.exists()) {
            if (baseFolderFile.isDirectory()) {
                File[] files = baseFolderFile.listFiles();
                if (files != null && files.length > 1) {
                    for (File file : files) {
                        if (!filesExcept.contains(file.getName())) {
                            int attempt = 0;
                            while (attempt < FILE_MAX_ATTEMPT_TO_DELETE && file.exists()) {
                                deleteFile(file);
                                attempt++;
                            }
                            if (file.exists()) {
                                filesWasDeleted = false;
                            }
                        }
                    }
                }
            }
        }
        return filesWasDeleted;
    }

    public static boolean deleteFolder(File folder) {
        File[] folderFiles = folder.listFiles();
        if (folderFiles != null && folderFiles.length >= 1) {
            for (File folderFile : folderFiles) {
                if (folderFile.isDirectory()) {
                    deleteFolder(folderFile);
                } else {
                    folderFile.delete();
                }
            }
        }
        folderFiles = folder.listFiles();
        if (folderFiles.length < 1){
            folder.delete();
        }
        return !folder.exists();
    }

    public static boolean deleteFile(File file) {
        if (file.isDirectory()) {
        	deleteFolder(file);
        } else {
            file.delete();
        }
    	
    	return !file.exists();
    }

    public static File getFirstFolderInside(File folder) {
        if (folder.exists() && folder.isDirectory() && folder.listFiles() != null && folder.listFiles().length > 1) {
            if (folder.isDirectory()) {
                for (File fileInsideFolder : folder.listFiles()) {
                    if (fileInsideFolder.isDirectory()) {
                        return fileInsideFolder;
                    }
                }

            }
        }
        return null;
    }

    public static File getFirstFileInside(File folder) {
        if (folder.exists() && folder.isDirectory() && folder.listFiles() != null && folder.listFiles().length > 1) {
            if (folder.isDirectory()) {
                for (File fileInsideFolder : folder.listFiles()) {
                    if (fileInsideFolder.isFile()) {
                        return fileInsideFolder;
                    }
                }
            }
        }
        return null;
    }

    public static String readFileContent(File file){
        StringBuilder stringBuilder = new StringBuilder();
        FileReader fileReader = null;
        BufferedReader bufferedReader = null;

        try {
            fileReader = new FileReader(file);
            bufferedReader = new BufferedReader(fileReader);
            String line = bufferedReader.readLine();
            while (line != null) {
                stringBuilder.append(line).append(NEW_LINE);
                line = bufferedReader.readLine();
            }
        } catch (Exception e) {
            throw new ErrorReadingFileException(file.getName(), e);
        } finally {
            Exception e = null;
            if (bufferedReader != null) {
                try {
                    bufferedReader.close();
                } catch (IOException ioException) {
                    e = ioException;
                }
            }
            if (fileReader != null) {
                try {
                    fileReader.close();
                } catch (IOException ioException) {
                    e = ioException;
                }
            }
            if (e != null) {
                throw new ErrorClosingFileReaderException(file.getName(), e);
            }
        }

        return stringBuilder.toString();
    }

    public static String treatFilePath(String path) {
        if (path.startsWith("file:")) {
            path = path.substring(5);
        }
        return path;
    }

}
