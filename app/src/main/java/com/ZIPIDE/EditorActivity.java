package com.ZIPIDE;
import android.support.v7.app.*;
import android.os.*;
import android.widget.*;
import java.io.*;
import org.apache.http.util.*;
import android.view.*;
import android.content.*;

public class EditorActivity extends AppCompatActivity
{
	EditText ed;
	String ProjectName;

	@Override
    protected void onCreate(Bundle savedInstanceState)
	{
        super.onCreate(savedInstanceState);
        setContentView(R.layout.editor_activity);

		String FileName = getIntent().getStringExtra("文件名");
		String FilePath = getIntent().getStringExtra("路径");
		ProjectName = getIntent().getStringExtra("项目名");


		getSupportActionBar().setTitle("脚本编辑器");

		ed = (EditText)findViewById(R.id.editor_activityEditText);


		try
		{
			ed.setText(readSDFile("/sdcard/ZIPIDE/Projects/" + ProjectName + "/META-INF/com/google/android/updater-script"));
		}
		catch (IOException e)
		{}


    }



	//菜单 
	@Override
	public boolean onCreateOptionsMenu(Menu menu)
	{
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.editor, menu);
		return super.onCreateOptionsMenu(menu);
	}
	@Override
    public boolean onOptionsItemSelected(MenuItem item)
	{
		switch (item.getItemId())
		{
			case R.id.save:
				new AlertDialog.Builder(this)
					.setTitle("提示")
					.setMessage("确定保存当前编辑框内的脚本嘛？")
					.setPositiveButton("确认",
					new DialogInterface.OnClickListener() {
						@Override
						public void onClick(DialogInterface dialogInterface, int i)
						{	
							saveSdFile("/ZIPIDE/Projects/" + ProjectName + "/META-INF/com/google/android/updater-script", ed.getText().toString());
							Toast.makeText(getApplicationContext(),"保存成功",1).show();
							finish();
						}
					}) 
					.setNeutralButton("取消", null)
					.show();
				break;
			case R.id.exit:
				new AlertDialog.Builder(this)
					.setTitle("提示")
					.setMessage("确定返回上一个界面吗？")
					.setPositiveButton("确认",
					new DialogInterface.OnClickListener() {
						@Override
						public void onClick(DialogInterface dialogInterface, int i)
						{
							finish();
						}
					}) 
					.setNeutralButton("取消", null)
					.show();
				break;
        }
        return super.onOptionsItemSelected(item);
    }



	//filename   a.txt  不加路径保存到sdcard
	public void saveSdFile(String filename, String text)
	{
		if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED))
		{	
			try
			{
				File sdCardDir = Environment.getExternalStorageDirectory();//获取SDCard目录
				File saveFile = new File(sdCardDir, filename);
				FileOutputStream outStream = new FileOutputStream(saveFile);
				outStream.write(text.getBytes());
				outStream.close();
			}
			catch (IOException ioe)
			{
			}
		}
	}
	public String readSDFile(String fileName) throws IOException
	{    

        File file = new File(fileName);    
        FileInputStream fis = new FileInputStream(file);    
        int length = fis.available();   
		byte [] buffer = new byte[length];   
		fis.read(buffer); 
		String res = EncodingUtils.getString(buffer, "UTF-8");
		fis.close();       
		return res;    
	} 

}
