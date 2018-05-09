package com.trendmicro.materialdesign_note.Utils;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import com.lzy.imagepicker.bean.ImageItem;
import com.trendmicro.materialdesign_note.Adapter.MyDatabaseHelper;
import com.trendmicro.materialdesign_note.bean.Image;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;

/**
 * Created by zheng_liu on 2018/3/9.
 */

public class FilesCopyUtils {

    // 目标文件夹
    static String url2;
    private static final String IMAGES_NAME = "/.Image";

    public static ArrayList<Image> PicCopy(Context context, ArrayList<ImageItem> selImageList,
            ArrayList<Image> list) throws IOException {
        url2 = PhotoBitmapUtils.getPhoneRootPath(context) + IMAGES_NAME;
        MyDatabaseHelper dbHelper = new MyDatabaseHelper(context, "Safe.db", null, 1);
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        File file = new File(url2);
        // 判断文件是否已经存在，不存在则创建
        if (!file.exists()) {
            file.mkdirs();
        }
        ContentValues values = new ContentValues();
        for (int i = 0; i < selImageList.size(); i++) {
            Image item = new Image();
            item.setName(selImageList.get(i).name);
            item.setPath(url2 + "/" + selImageList.get(i).name);
            item.setSize(selImageList.get(i).size);
            item.setWidth(selImageList.get(i).width);
            item.setHeight(selImageList.get(i).height);
            item.setMimeType(selImageList.get(i).mimeType);
            item.setAddTime(selImageList.get(i).addTime);
            if (!list.contains(item)) {
                String url1 = selImageList.get(i).path;
                copyFile(new File(url1), new File(url2 + "/" + selImageList.get(i).name));
                values.put("name", selImageList.get(i).name);
                values.put("path", url2 + "/" + selImageList.get(i).name);
                values.put("size", selImageList.get(i).size);
                values.put("width", selImageList.get(i).width);
                values.put("height", selImageList.get(i).height);
                values.put("mimeType", selImageList.get(i).mimeType);
                values.put("addTime", selImageList.get(i).addTime);
                db.insert("ImgeSafe", null, values); // 插入第一条数据
                values.clear();
                list.add(item);
            }
        }
        return list;
    }

    private void saveTodb(ArrayList<ImageItem> selImageList){

    }

    // 复制文件
    public static void copyFile(File sourceFile, File targetFile)
            throws IOException {
        // 新建文件输入流并对它进行缓冲
        FileInputStream input = new FileInputStream(sourceFile);
        BufferedInputStream inBuff = new BufferedInputStream(input);

        // 新建文件输出流并对它进行缓冲
        FileOutputStream output = new FileOutputStream(targetFile);
        BufferedOutputStream outBuff = new BufferedOutputStream(output);

        // 缓冲数组
        byte[] b = new byte[1024 * 5];
        int len;
        while ((len = inBuff.read(b)) != -1) {
            outBuff.write(b, 0, len);
        }
        // 刷新此缓冲的输出流
        outBuff.flush();

        //关闭流
        inBuff.close();
        outBuff.close();
        output.close();
        input.close();
    }

    // 复制文件夹
    public static void copyDirectiory(String sourceDir, String targetDir)
            throws IOException {
        // 新建目标目录
        (new File(targetDir)).mkdirs();
        // 获取源文件夹当前下的文件或目录
        File[] file = (new File(sourceDir)).listFiles();
        for (int i = 0; i < file.length; i++) {
            if (file[i].isFile()) {
                // 源文件
                File sourceFile = file[i];
                // 目标文件
                File targetFile = new
                        File(new File(targetDir).getAbsolutePath()
                        + File.separator + file[i].getName());
                copyFile(sourceFile, targetFile);
            }
            if (file[i].isDirectory()) {
                // 准备复制的源文件夹
                String dir1 = sourceDir + "/" + file[i].getName();
                // 准备复制的目标文件夹
                String dir2 = targetDir + "/" + file[i].getName();
                copyDirectiory(dir1, dir2);
            }
        }
    }
}
