package com.trendmicro.materialdesign_note.act;

import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.GridView;
import android.widget.TextView;
import android.widget.Toast;

import com.lzy.imagepicker.ImagePicker;
import com.lzy.imagepicker.bean.ImageItem;
import com.lzy.imagepicker.ui.ImageGridActivity;
import com.lzy.imagepicker.ui.ImagePreviewDelActivity;
import com.lzy.imagepicker.ui.ImagePreviewActivity;
import com.lzy.imagepicker.view.CropImageView;
import com.trendmicro.materialdesign_note.Adapter.Image;
import com.trendmicro.materialdesign_note.Adapter.ImageAdapter;
import com.trendmicro.materialdesign_note.Adapter.ImagePickerAdapter;
import com.trendmicro.materialdesign_note.Adapter.Message;
import com.trendmicro.materialdesign_note.Adapter.MyDatabaseHelper;
import com.trendmicro.materialdesign_note.R;
import com.trendmicro.materialdesign_note.Utils.FilesCopyUtils;
import com.trendmicro.materialdesign_note.Utils.GlideImageLoader;
import com.trendmicro.materialdesign_note.Utils.PhotoBitmapUtils;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by zheng_liu on 2018/3/8.
 */

public class PhotoViewActivity extends BaseActivity {
    public static final int IMAGE_ITEM_ADD = 0;
    public static final int REQUEST_CODE_SELECT = 100;
    public static final int REQUEST_CODE_PREVIEW = 101;
    private boolean isOrigin = false;  //是否选中原图
    private Handler handler=new Handler(); //在主线程中创建handler

    private ImageAdapter adapter;
    private ArrayList<ImageItem> selImageList; //当前选择的所有图片
    private int maxImgCount = 60;             //允许选择图片最大数
    private MyDatabaseHelper dbHelper;
    private SQLiteDatabase db;
    private ArrayList<Image> List=new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_photo);
        //最好放到 Application oncreate执行
        initImagePicker();
        initWidget();
    }

    private void initImagePicker() {
        ImagePicker imagePicker = ImagePicker.getInstance();
        imagePicker.setImageLoader(new GlideImageLoader());   //设置图片加载器
        imagePicker.setShowCamera(false);                      //显示拍照按钮
        imagePicker.setCrop(true);                           //允许裁剪（单选才有效）
        imagePicker.setSaveRectangle(true);                   //是否按矩形区域保存
        imagePicker.setSelectLimit(maxImgCount);              //选中数量限制
        imagePicker.setStyle(CropImageView.Style.RECTANGLE);  //裁剪框的形状
        imagePicker.setFocusWidth(800);                       //裁剪框的宽度。单位像素（圆形自动取宽高最小值）
        imagePicker.setFocusHeight(800);                      //裁剪框的高度。单位像素（圆形自动取宽高最小值）
        imagePicker.setOutPutX(1000);                         //保存文件的宽度。单位像素
        imagePicker.setOutPutY(1000);                         //保存文件的高度。单位像素
    }

    private void initWidget() {
        dbHelper = new MyDatabaseHelper(this, "ImgeSafe.db", null, 1);
        db = dbHelper.getWritableDatabase();
        initMessage();
        RecyclerView recyclerView = findViewById(R.id.recyclerView);
        adapter = new ImageAdapter(this, List);
        adapter.setOnItemClickListener(new ImageAdapter.OnRecyclerViewItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                switch (position) {
                    case IMAGE_ITEM_ADD:
                        Intent intent1 = new Intent(PhotoViewActivity.this, ImageGridActivity.class);
                                /* 如果需要进入选择的时候显示已经选中的图片，
                                 * 详情请查看ImagePickerActivity
                                 * */
//                              intent1.putExtra(ImageGridActivity.EXTRAS_IMAGES,images);
                        startActivityForResult(intent1, REQUEST_CODE_SELECT);
                        break;
                    default:
                        //打开预览
                        Intent intentPreview = new Intent(PhotoViewActivity.this, ImagePreviewActivity.class);
                        intentPreview.putExtra(ImagePicker.EXTRA_IMAGE_ITEMS, (ArrayList<Image>) adapter.getImages());
                        intentPreview.putExtra(ImagePicker.EXTRA_SELECTED_IMAGE_POSITION, position);
                        intentPreview.putExtra(ImagePicker.EXTRA_FROM_ITEMS, true);
                        startActivity(intentPreview);
                        break;
                }
            }
        });

        recyclerView.setLayoutManager(new GridLayoutManager(this, 4));
        recyclerView.setHasFixedSize(true);
        recyclerView.setAdapter(adapter);
    }

    private void initMessage() {
        Image fitstItem=new Image();
        fitstItem.setName("first");
        fitstItem.setPath(PhotoBitmapUtils.getPhoneRootPath(context)+"/face/face_first");
        List.add(fitstItem);
        Cursor cursor = db.query("ImgeSafe", null, null, null, null, null, null);
        if (cursor.moveToFirst()) {
            do {
                // 遍历Cursor对象，取出数据并打印
                Image item=new Image();
                item.setName(cursor.getString(cursor.getColumnIndex("name")));
                item.setPath(cursor.getString(cursor.getColumnIndex("path")));
                List.add(item);
            } while (cursor.moveToNext());
        }
        cursor.close();
    }

    ArrayList<ImageItem> images = null;

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == ImagePicker.RESULT_CODE_ITEMS) {
            //添加图片返回
            if (data != null && requestCode == REQUEST_CODE_SELECT) {
                images = (ArrayList<ImageItem>) data.getSerializableExtra(ImagePicker.EXTRA_RESULT_ITEMS);
                if (images != null) {
                    Toast.makeText(PhotoViewActivity.this,"start",Toast.LENGTH_SHORT).show();
                    new Thread(){//创建一个新的线程
                        public void run(){
                            try {
                                FilesCopyUtils.PicCopy(getApplicationContext(),images);
                                handler.post(new Runnable() {
                                    public void run() {
                                        Toast.makeText(PhotoViewActivity.this,"success",Toast.LENGTH_SHORT).show();
                                    }
                                });

                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                    }.start();
                }
            }
        }
    }
}
