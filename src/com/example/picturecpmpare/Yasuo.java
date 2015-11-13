package com.example.picturecpmpare;

import java.io.FileInputStream;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.Toast;

public class Yasuo extends Activity implements OnClickListener{

	private Button selectpic;
	private Button upload;
	private String path;
	private static final int STEP1 = 1;  
    private static final int STEP2 = 2;  
    private static final int STEP3 = 3; 
    private String base64;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.yasuo);
		selectpic = (Button) findViewById(R.id.selectpic);
		upload = (Button) findViewById(R.id.upload_pic);
		selectpic.setOnClickListener(this);
		upload.setOnClickListener(this);
	}
	@Override
	public void onClick(View v) {
		switch(v.getId())
		{
			case R.id.selectpic:
				//����ѡ����Ƭ
				Intent intent = new Intent(Intent.ACTION_PICK,null);
				//�õ�ϵͳ����ͼƬ        image����ͼƬ����
				intent.setDataAndType(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, "image/*");
				//�ӻ1�����2 �����2����ʱ���ص��1 ������onActivityResult
				startActivityForResult(intent,1);
			break;		
		}
		
	}

	@SuppressWarnings("deprecation")
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		switch(resultCode)
		{
			case RESULT_OK :
				String [] projection = {MediaStore.Images.Media.DATA};
				//��ȡ�α�
				Cursor cursor = managedQuery(data.getData(), projection, null, null, null);
				//��ȡ����
				int columnIndex = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
				//���һֱ�ڿ�ͷ
				cursor.moveToFirst();
				//��ȡͼƬ·��
				path = cursor.getString(columnIndex);
				//תΪ���ļ�
			try {
				FileInputStream filein = new FileInputStream(path);
				Bitmap bitmap = BitmapFactory.decodeStream(filein);
				base64 = Base64Util.BitmapToBase64(bitmap);
				JSONObject json = new JSONObject();
				json.put("imgurl", base64);
				final String param = json.toString();
				new Thread(new Runnable() {
					
					@Override
					public void run() {
						String request = HttpUtil.sendRequest(param);
						try {
							String flag = new JSONObject(request).getString("flag");
							if("1".equals(flag))
							{
								Message msg = new Message();
								msg.what =1;
								handler.sendMessage(msg);
							}
							if("0".equals(flag))
							{
								Message msg = new Message();
								msg.what =0;
								handler.sendMessage(msg);
							}
						} catch (JSONException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					}
				}).start();
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			break;
		}
	}
	
	Handler handler = new Handler(){

		@Override
		public void handleMessage(Message msg) {
			// TODO Auto-generated method stub
			switch(msg.what)
			{
				case 1:
					Toast.makeText(Yasuo.this, "�ɹ�", Toast.LENGTH_SHORT).show();
					break;
				case 0:
					Toast.makeText(Yasuo.this, "ʧ��", Toast.LENGTH_SHORT).show();
					break;
			}
			
		}
		
	};
	
	
	
}
