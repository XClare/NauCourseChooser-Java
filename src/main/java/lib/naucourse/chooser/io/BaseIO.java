package lib.naucourse.chooser.io;

import java.io.*;

/**
 * 文件读写相关操作
 */
public class BaseIO {
    /**
     * 写入字符串到文件
     *
     * @param content 需要写入的内容
     * @param path    文件路径
     * @return 是否写入成功
     */
    public static boolean writeFile(String content, String path) {
        File file = new File(path);
        try {
            if (checkFile(file, true)) {
                return false;
            }
            OutputStream writer = new FileOutputStream(file);
            byte[] bytes = content.getBytes();
            writer.write(bytes);
            writer.flush();
            writer.close();
            return true;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * 从文件中读取字符串
     *
     * @param path 文件路径
     * @return 读取的字符串
     */
    public static String readFile(String path) {
        File file = new File(path);
        try {
            if (checkFile(file, false)) {
                return null;
            }
            InputStream fileStream = new FileInputStream(file);
            BufferedReader reader = new BufferedReader(new InputStreamReader(fileStream));
            String line;
            StringBuilder result = new StringBuilder();
            while ((line = reader.readLine()) != null) {
                result.append(line).append("\n");
            }
            reader.close();
            fileStream.close();
            return result.toString();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 创建路径中的文件夹
     *
     * @param file File
     * @return 是否创建成功
     */
    private static boolean createPath(File file) {
        if (file.getParent().trim().length() != 1) {
            File filePath = file.getParentFile();
            if (!filePath.exists()) {
                return filePath.mkdirs();
            }
        }
        return true;
    }

    /**
     * 检查并创建文件夹或文件
     *
     * @param file   File
     * @param delete 是否删除发现重复的文件或文件夹
     * @return 是否检查成功
     * @throws IOException IOException
     */
    private static boolean checkFile(File file, boolean delete) throws IOException {
        if (file.exists()) {
            if (file.isFile()) {
                if (delete && file.canWrite() && file.canRead()) {
                    return file.delete();
                }
                return false;
            }
        } else {
            return !createPath(file) || !file.createNewFile();
        }
        return true;
    }

    /**
     * 删除文件
     *
     * @param file File
     * @return 是否删除成功
     */
    public static boolean deleteFile(File file) {
        if (file.exists()) {
            return file.delete();
        }
        return false;
    }

}
