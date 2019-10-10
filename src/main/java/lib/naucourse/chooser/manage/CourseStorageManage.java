package lib.naucourse.chooser.manage;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.google.gson.reflect.TypeToken;
import lib.naucourse.chooser.io.BaseIO;
import lib.naucourse.chooser.util.Course;
import lib.naucourse.chooser.util.CourseType;
import lib.naucourse.chooser.util.DataPath;
import lib.naucourse.chooser.util.SelectedCourse;

import java.io.File;
import java.io.Serializable;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;

public class CourseStorageManage {
    private static final String FILE_CHOOSE_COURSE_LIST = "ChooseCourseList.json";
    private static final String FILE_SELECTED_COURSE_LIST = "SelectedCourseList.json";
    private final DataPath dataPath;

    /**
     * 数据储存管理
     *
     * @param dataPath 数据路径
     */
    public CourseStorageManage(DataPath dataPath) {
        this.dataPath = dataPath;
    }

    /**
     * 获取文件中的数据
     *
     * @param path 文件路径
     * @return 字符串数据
     */
    private static String getFileData(String path) {
        File file = new File(path);
        if (file.exists()) {
            return BaseIO.readFile(path);
        }
        return null;
    }

    /**
     * 保存文件中的JSON数据
     *
     * @param path   文件路径
     * @param object 数据JavaBean对象
     * @return 是否保存成功
     */
    private static boolean saveFileData(String path, Object object) {
        try {
            String data = new Gson().toJson(object);
            if (data != null) {
                return BaseIO.writeFile(data, path);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * 删除数据文件
     *
     * @param fileName 文件名
     * @return 是否删除成功
     */
    public boolean deleteData(String fileName) {
        return BaseIO.deleteFile(new File(dataPath.getDataPath() + fileName));
    }

    /**
     * 保存JavaBean对象数据
     *
     * @param fileName     文件名
     * @param serializable JavaBean对象
     * @return 是否保存成功
     */
    public boolean saveObjectData(String fileName, Serializable serializable) {
        return saveFileData(dataPath.getDataPath() + fileName, serializable);
    }

    /**
     * 获取JavaBean对象数据
     *
     * @param fileName  文件名
     * @param fileClass JavaBean对象的Class
     * @return JavaBean对象
     */
    public Object getObjectData(String fileName, Type fileClass) {
        Object object = null;
        String content = getFileData(dataPath.getDataPath() + fileName);
        if (content != null) {
            try {
                object = new Gson().fromJson(content, fileClass);
            } catch (JsonSyntaxException e) {
                e.printStackTrace();
            }
        }
        return object;
    }

    /**
     * 保存选课的列表
     *
     * @param chooseCourseList 选课的列表
     * @return 是否保存成功
     */
    public boolean saveChooseCourseList(LinkedHashMap<CourseType, HashMap<String, Course>> chooseCourseList) {
        String filePath = dataPath.getDataPath() + FILE_CHOOSE_COURSE_LIST;
        return saveFileData(filePath, chooseCourseList);
    }

    /**
     * 获取保存的选课列表
     *
     * @return 选课列表
     */
    public LinkedHashMap<CourseType, HashMap<String, Course>> getChooseCourseList() {
        LinkedHashMap<CourseType, HashMap<String, Course>> result = null;
        String filePath = dataPath.getDataPath() + FILE_CHOOSE_COURSE_LIST;
        String content = getFileData(filePath);
        if (content != null) {
            try {
                Type type = new TypeToken<LinkedHashMap<CourseType, HashMap<String, Course>>>() {
                }.getType();
                result = new Gson().fromJson(content, type);
            } catch (JsonSyntaxException e) {
                e.printStackTrace();
            }
        }
        return result;
    }

    /**
     * 保存已选课程列表
     *
     * @param selectedCourseList 已选课程列表
     * @return 是否保存成功
     */
    public boolean saveSelectedCourseList(LinkedHashMap<CourseType, ArrayList<SelectedCourse>> selectedCourseList) {
        String filePath = dataPath.getDataPath() + FILE_SELECTED_COURSE_LIST;
        return saveFileData(filePath, selectedCourseList);
    }

    /**
     * 获取保存的已选课程列表
     *
     * @return 已选课程列表
     */
    public LinkedHashMap<CourseType, ArrayList<SelectedCourse>> getSelectedCourseList() {
        LinkedHashMap<CourseType, ArrayList<SelectedCourse>> result = null;
        String filePath = dataPath.getDataPath() + FILE_SELECTED_COURSE_LIST;
        String content = getFileData(filePath);
        if (content != null) {
            try {
                Type type = new TypeToken<LinkedHashMap<CourseType, ArrayList<SelectedCourse>>>() {
                }.getType();
                result = new Gson().fromJson(content, type);
            } catch (JsonSyntaxException e) {
                e.printStackTrace();
            }
        }
        return result;
    }
}
