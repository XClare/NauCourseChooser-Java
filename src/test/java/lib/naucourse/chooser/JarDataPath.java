package lib.naucourse.chooser;

import lib.naucourse.chooser.util.DataPath;

import java.io.File;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;

public class JarDataPath implements DataPath {
    private static final String DIR_CACHE = "Cache";
    private static final String DIR_DATA = "Data";
    private final String systemName;

    private final String jarPath;

    /**
     * 用于获取数据目录
     */
    public JarDataPath() {
        this.systemName = System.getProperty("os.name").trim();
        this.jarPath = getJarPath();
    }

    /**
     * 获取本地jar所在的路径
     *
     * @return 路径
     */
    private String getLocalPath() {
        return jarPath + File.separator;
    }

    /**
     * 获取所有数据的数据路径
     *
     * @return 路径
     */
    private String getContentPath() {
        return getLocalPath() + "NauCourseChooser" + File.separator;
    }

    /**
     * 获取数据存储的路径
     *
     * @return 路径
     */
    @Override
    public String getDataPath() {
        return getContentPath() + DIR_DATA + File.separator;
    }

    /**
     * 获取缓存路径
     *
     * @return 路径
     */
    @Override
    public String getCachePath() {
        return getContentPath() + DIR_CACHE + File.separator;
    }

    /**
     * 获取执行的jar所在的目录
     *
     * @return 路径
     */
    private String getJarPath() {
        String path = getClass().getProtectionDomain().getCodeSource().getLocation().getPath();
        int startIndex = 0;
        if (systemName.toLowerCase().startsWith("win")) {
            if (path.startsWith("/")) {
                startIndex = 1;
            }
        }
        path = path.substring(startIndex);
        try {
            path = URLDecoder.decode(path, "utf-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        File file = new File(path);
        if (file.exists()) {
            return file.getParentFile().getAbsolutePath();
        }
        return "";
    }
}
