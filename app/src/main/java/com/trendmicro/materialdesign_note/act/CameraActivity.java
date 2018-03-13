package com.trendmicro.materialdesign_note.act;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.StrictMode;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.arcsoft.facedetection.AFD_FSDKEngine;
import com.arcsoft.facedetection.AFD_FSDKError;
import com.arcsoft.facedetection.AFD_FSDKFace;
import com.arcsoft.facerecognition.AFR_FSDKEngine;
import com.arcsoft.facerecognition.AFR_FSDKError;
import com.arcsoft.facerecognition.AFR_FSDKFace;
import com.arcsoft.facerecognition.AFR_FSDKMatching;
import com.trendmicro.materialdesign_note.R;
import com.trendmicro.materialdesign_note.Utils.ImageUtils;
import com.trendmicro.materialdesign_note.Utils.PhotoBitmapUtils;
import com.trendmicro.materialdesign_note.Utils.RSAUtils;
import com.trendmicro.materialdesign_note.Utils.TimeUtils;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.security.KeyPair;
import java.util.ArrayList;
import java.util.List;

import javax.crypto.Cipher;

/**
 * Created by Axes on 2017/4/9.
 */

public class CameraActivity extends BaseActivity{
	private static final int PHOTO_CAPTURE = 0x11;
	private static String photoName;
	private LinearLayout lin;
	Uri imageUri = Uri.fromFile(new File(Environment
			.getExternalStorageDirectory(), "image.jpg"));
	private Button photo, sc_photo;
	private ImageView img_photo;
	AFR_FSDKFace face1,face2;
	AFR_FSDKEngine AFR_FSDKEngine;
	AFD_FSDKEngine AFD_FSDKEngine;
	AFR_FSDKError error;
	Bitmap obmp0,obmp1;
	String APPID="7mhznvm3cRy7DCL6FxABfmQbL4dGV8juRbSgdqAfKTyC";
	String FD_SDKKEY="Fomsk9JM8Hhz1YadaRoeSXzhuDzHQUYvGp6DbMRDwEYk";
	String FR_SDKKEY="Fomsk9JM8Hhz1YadaRoeSXzq4dFSC8HUc4ADd3KY2vJh";
	List<AFD_FSDKFace> result = new ArrayList<AFD_FSDKFace>();	// 用来存放检测到的人脸信息列表
	List<AFD_FSDKFace> info = new ArrayList<AFD_FSDKFace>();	// 用来存放检测到的人脸信息列表

	private static String FACE_FIRST;

	@Override
	protected void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);
		setContentView(R.layout.camera_post);
		initEngine();
		photo =  findViewById(R.id.photo);
		sc_photo = findViewById(R.id.photo_save);
		sc_photo.setOnClickListener(new sc_photo());
		img_photo = findViewById(R.id.imt_photo);
		lin=findViewById(R.id.lin_first);
		if (flag) {
			lin.setVisibility(View.VISIBLE);
		}else {
			sc_photo.setText("比对");
			FACE_FIRST=PhotoBitmapUtils.getPhotoFileName(getApplicationContext(),true);
			if(FACE_FIRST!=null){
				obmp0 = PhotoBitmapUtils.amendRotatePhoto(FACE_FIRST,this);
			}
			FD_FACE(obmp0);
		}
		// android.os.NetworkOnMainThreadException
		StrictMode.setThreadPolicy(new StrictMode.ThreadPolicy.Builder()
				.detectDiskReads()
				.detectDiskWrites()
				.detectNetwork()
				.penaltyLog()
				.build());
		StrictMode.setVmPolicy(new StrictMode.VmPolicy.Builder()
				.detectLeakedSqlLiteObjects()
				.penaltyLog()
				.penaltyDeath()
				.build());
		photo.setOnClickListener(new photo());
		face1 = new AFR_FSDKFace();
		face2 = new AFR_FSDKFace();
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		//销毁人脸识别引擎
		error = AFR_FSDKEngine.AFR_FSDK_UninitialEngine();
		Log.d("com.arcsoft", "AFR_FSDK_UninitialEngine : " + error.getCode());
		//销毁人脸检测引擎
		AFD_FSDKError err = AFD_FSDKEngine.AFD_FSDK_UninitialFaceEngine();
		Log.d("com.arcsoft", "AFD_FSDK_UninitialFaceEngine =" + err.getCode());
	}

	class sc_photo implements View.OnClickListener {

		@Override
		public void onClick(View arg0) {
			dialog();
		}

	}

	class photo implements View.OnClickListener {

		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			if (flag) {
				photoName=PhotoBitmapUtils.getPhotoFileName(getApplicationContext(),true);
			}else {
				photoName=PhotoBitmapUtils.getPhotoFileName(getApplicationContext(),false);
			}
			Intent intent = new Intent("android.media.action.IMAGE_CAPTURE");
			File file = new File(photoName);
			imageUri = Uri.fromFile(file);
			intent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri); // 这样就将文件的存储方式和uri指定到了Camera应用中
			startActivityForResult(intent, PHOTO_CAPTURE);
		}
	}
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		// TODO Auto-generated method stub
		super.onActivityResult(requestCode, resultCode, data);
		String sdStatus = Environment.getExternalStorageState();
		switch (requestCode) {
			case PHOTO_CAPTURE:
				if (!sdStatus.equals(Environment.MEDIA_MOUNTED)) {
					Log.i("内存卡错误", "请检查您的内存卡");
				} else {
					BitmapFactory.Options op = new BitmapFactory.Options();
					if(photoName!=null){
						obmp1 = PhotoBitmapUtils.amendRotatePhoto(photoName,this);
					}
					int width = obmp1.getWidth();
					int height = obmp1.getHeight();
					if (!flag) FD_FACE(obmp1);
					// 设置想要的大小
					int newWidth = 540;
					int newHeight = 960;// 计算缩放比例
					float scaleWidth = ((float) newWidth) / width;
					float scaleHeight = ((float) newHeight) / height;
					// 取得想要缩放的matrix参数
					Matrix matrix = new Matrix();
					matrix.postScale(scaleWidth, scaleHeight);
					// 得到新的图片
					Bitmap obmp = Bitmap.createBitmap(obmp1, 0, 0, width, height,
							matrix, true);
					// canvas.drawBitmap(bitMap, 0, 0, paint)
					// 防止内存溢出
					op.inSampleSize = 1; // 这个数字越大,图片大小越小.
					Bitmap pic = BitmapFactory.decodeFile(photoName, op);
					img_photo.setImageBitmap(obmp); // 这个ImageView是拍照完成后显示图片
					FileOutputStream b = null;
					try {
						b = new FileOutputStream(photoName);
					} catch (FileNotFoundException e) {
						e.printStackTrace();
					}
					if (pic != null) {
						pic.compress(Bitmap.CompressFormat.JPEG, 50, b);
					}
				}
				break;
			default:
				return;
		}
	}

	protected void dialog() {
		AlertDialog.Builder builder = new AlertDialog.Builder(CameraActivity.this);
		if (flag) {
			builder.setMessage("是否上传？");
		}else {
			builder.setMessage("开始比对？");
		}

		builder.setTitle("提示");

		builder.setPositiveButton("确认", new DialogInterface.OnClickListener() {

			@Override
			public void onClick(DialogInterface dialog, int which) {
				if (flag) {
					editor = preference.edit();
					//将登录标志位设置为false，下次登录时不在显示首次登录界面
					editor.putBoolean("firststart", false);
					try {
						GetKey();
						//将登录标志位设置为false，下次登录时不在显示首次登录界面
						editor.putString("PublicKey", RSAUtils.getKeyString(Key1));
						editor.putString("PrivateKey", RSAUtils.getKeyString(Key2));
						editor.commit();
					} catch (Exception e) {
						e.printStackTrace();
					}
					Intent intent = new Intent(CameraActivity.this,MainActivity.class);
					startActivity(intent);
					finish();
				}else {
					FR_FACE();
				}
			}
		});
		builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {

			@Override
			public void onClick(DialogInterface dialog, int which) {
				dialog.dismiss();

			}
		});
		builder.create().show();
	}


	private void initEngine(){
		AFR_FSDKEngine = new AFR_FSDKEngine();
		AFD_FSDKEngine= new AFD_FSDKEngine();
		error= AFR_FSDKEngine.AFR_FSDK_InitialEngine(APPID,FR_SDKKEY);
		Log.d("com.arcsoft", "AFR_FSDK_InitialEngine = " + error.getCode());
	}
	private void FD_FACE(Bitmap bitmap){
		//初始化人脸检测引擎，使用时请替换申请的APPID和SDKKEY
		AFD_FSDKError err = AFD_FSDKEngine.AFD_FSDK_InitialFaceEngine(APPID,FD_SDKKEY, AFD_FSDKEngine.AFD_OPF_0_HIGHER_EXT, 12,3);
		Log.d("com.arcsoft", "AFD_FSDK_InitialFaceEngine = " + err.getCode());
		//输入的data数据为NV21格式（如Camera里NV21格式的preview数据），其中height不能为奇数，人脸检测返回结果保存在result。
		err = AFD_FSDKEngine.AFD_FSDK_StillImageFaceDetection(ImageUtils.getNV21(bitmap.getWidth(),bitmap.getHeight(),bitmap), bitmap.getWidth(), bitmap.getHeight(), AFD_FSDKEngine.CP_PAF_NV21, result);
		Log.d("com.arcsoft", "AFD_FSDK_StillImageFaceDetection =" + err.getCode());
		Log.d("com.arcsoft", "Face=" + result.size());
		for (AFD_FSDKFace face : result) {
			Log.d("com.arcsoft", "Face:" + face.toString());
			info.add(face.clone());
		}
	}
	private void FR_FACE() {
		// TODO Auto-generated method stub
		Log.d("com.arcsoft", info.get(0).getRect() + "," + info.get(0).getDegree());
		error = AFR_FSDKEngine.AFR_FSDK_ExtractFRFeature(ImageUtils.getNV21(obmp0.getWidth(),obmp0.getHeight(),obmp0), obmp0.getWidth(),obmp0.getHeight(), AFR_FSDKEngine.CP_PAF_NV21, info.get(0).getRect(),info.get(0).getDegree(), face1);
		Log.d("com.arcsoft", "Face=" + face1.getFeatureData()[0]+ "," + face1.getFeatureData()[1] + "," + face1.getFeatureData()[2] + "," + error.getCode());
		Log.d("com.arcsoft", info.get(1).getRect() + "," + info.get(1).getDegree());
		error = AFR_FSDKEngine.AFR_FSDK_ExtractFRFeature(ImageUtils.getNV21(obmp1.getWidth(),obmp1.getHeight(),obmp1), obmp1.getWidth(), obmp1.getHeight(), AFR_FSDKEngine.CP_PAF_NV21, info.get(1).getRect(),info.get(1).getDegree(), face2);
		Log.d("com.arcsoft", "Face=" + face2.getFeatureData()[0]+ "," + face2.getFeatureData()[1] + "," + face2.getFeatureData()[2] + "," + error.getCode());
		//score用于存放人脸对比的相似度值
		AFR_FSDKMatching score = new AFR_FSDKMatching();
		error = AFR_FSDKEngine.AFR_FSDK_FacePairMatching(face1, face2, score);
		Log.d("com.arcsoft", "AFR_FSDK_FacePairMatching=" + error.getCode());
		Log.d("com.arcsoft", "Score:" + score.getScore());
		if(score.getScore()>0.5){
			obmp0.recycle();
			obmp1.recycle();
			Intent intent=new Intent(CameraActivity.this,MainActivity.class);
			startActivity(intent);
			finish();
		}else {
			showDialog("置信度"+score.getScore()+"匹配失败！请重试");
			info.remove(1);
		}
	}
	/* 显示Dialog的method */
	private void showDialog(String mess) {
		new AlertDialog.Builder(CameraActivity.this).setTitle("提示")
				.setMessage(mess)
				.setNegativeButton("确定", new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int which) {
					}
				}).show();
	}
	/**
	 * 得到公私钥
	 */
	public void GetKey() throws Exception {
		String s= RSAUtils.CreateKey();
		String[] key = s.split("#");
		String String_publicKey = key[0];
		String String_privateKey = key[1];
		cipher= Cipher.getInstance("RSA/None/PKCS1Padding");
		//通过密钥字符串得到密钥
		Key1 = RSAUtils.getPublicKey(String_publicKey);
		Key2 = RSAUtils.getPrivateKey(String_privateKey);
	}
}