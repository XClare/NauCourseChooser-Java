package lib.naucourse.chooser.util;

/**
 * 用于定义数据存储目录
 */
public interface DataPath {
    /**
     * 获取数据存储的路径
     *
     * @return 路径
     */
    String getDataPath();

    /**
     * 获取缓存路径
     *
     * @return 路径
     */
    String getCachePath();
}
