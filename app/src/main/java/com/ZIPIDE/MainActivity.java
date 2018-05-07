package com.ZIPIDE;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.*;
import android.support.design.widget.*;
import android.widget.*;
import android.support.v7.app.*;
import android.*;
import android.os.*;
import android.support.v4.content.*;
import android.content.pm.*;
import android.support.v4.app.*;
import android.support.annotation.*;
import android.content.*;
import android.provider.*;
import android.net.*;
import java.io.*;

public class MainActivity extends AppCompatActivity {
// 要申请的权限
    private String[] permissions = {Manifest.permission.WRITE_EXTERNAL_STORAGE};
    private AlertDialog dialog;

	private EditText progectName;

	private String[] filelist;
	
	ListView mLv;
	TextView mNoProject;
	
	
	
	
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_activity);
		startPermission();
		
		mLv = (ListView)findViewById(R.id.mainactivityListView1);
		mNoProject = (TextView)findViewById(R.id.mainactivityTextView1);
		
		RefreshProject();
    }
	
	
	
	//刷新首页项目列表
	public void RefreshProject(){
		File sceneFile = new File("/sdcard/ZIPIDE/Projects");
		File[] files = sceneFile.listFiles();
		filelist = new String[files.length];
		if(files != null){
			
			for(int i=0;i<=files.length-1;i++){
				filelist[i] = files[i].getName();
			}
			if(filelist.length>0){
				mNoProject.setVisibility(8);
			}
			ArrayAdapter<String> adap = new ArrayAdapter<String>(MainActivity.this,android.R.layout.simple_list_item_1,filelist);
			//实例化列表对象,并set列表数据
			mLv.setAdapter(adap);	
			
			
			mLv.setOnItemClickListener(new AdapterView.OnItemClickListener(){ 
					@Override
					public void onItemClick(AdapterView<?> parent, View view, int position, long id)
					{
		
						Intent intent = new Intent(MainActivity.this, ProjectActivity.class);  
						intent.putExtra("项目名",filelist[position]);  
						startActivityForResult(intent,1);  
						
					}}); 
		}
			
	}
	
	//新建项目
	public void newProject(){
		isFolderExists("/sdcard/ZIPIDE");
		LayoutInflater infl = LayoutInflater.from(getApplicationContext());
		View vie = infl.inflate(R.layout.dialog_newproject, null);
		new AlertDialog.Builder(this)
			.setView(vie)
			.setPositiveButton("新建",
			new DialogInterface.OnClickListener() {
				@Override
				public void onClick(DialogInterface dialogInterface, int i) {
					String proName = "/sdcard/ZIPIDE/Projects/"+progectName.getText().toString();
					
					if(!isFolderExists(proName)){
						try
						{
							isFolderExists(proName+"/META-INF");
							isFolderExists(proName+"/META-INF/com");
							isFolderExists(proName+"/META-INF/com/google");
							isFolderExists(proName+"/META-INF/com/google/android");
							
							copyAssets("update-binary", proName+"/META-INF/com/google/android" + "/update-binary");
							copyAssets("updater-script", proName+"/META-INF/com/google/android" + "/updater-script");
							
							snackbar("创建完成");
							RefreshProject();
						}
						catch (IOException e)
						{snackbar("创建失败");}	
					}
					else{
						snackbar("请不要重复创建同一命名项目");
					}
				}
			}) 
			.setNeutralButton("取消", null)
			.show();

		progectName = (EditText)vie.findViewById(R.id.dialognewprojectEditText1);
		
	}
	
	//assets文件复制
	public void copyAssets(String assetsFileName, String OutFileName) throws IOException 
	{
        File f = new File(OutFileName);
        if (f.exists())
            f.delete();
        f = new File(OutFileName);
        f.createNewFile();
        InputStream I = getAssets().open(assetsFileName);
        OutputStream O = new FileOutputStream(OutFileName);
        byte[] b = new byte[1024];
        int l = I.read(b);
        while (l > 0) 
		{
            O.write(b, 0, l);
            l = I.read(b);
        }
        O.flush();
        I.close();
        O.close();
    }

	//检查文件夹存在,不存在则创建
	public boolean isFolderExists(String strFolder)
    {
        File file = new File(strFolder);
        if (!file.exists())
        {
            if (file.mkdir())
            {snackbar("目录创建成功");}
            else{snackbar("目录创建失败,可能是没有权限");}
			return false;
        }
		else{
			return true;
		}	
    }
	//右上角菜单 
	@Override
	public boolean onCreateOptionsMenu(Menu menu)
	{
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.main, menu);
		return super.onCreateOptionsMenu(menu);
	}
	@Override
    public boolean onOptionsItemSelected(MenuItem item)
	{
		switch (item.getItemId())
		{
			case R.id.newProject:
					newProject();		
				break;
        }
        return super.onOptionsItemSelected(item);
    }
	//工具方法
	public void alert(String text){
		new AlertDialog.Builder(this)
			.setMessage(text)
			.show();
	}
	public void snackbar(String text){
		Snackbar.make(getWindow().getDecorView(),text,Snackbar.LENGTH_SHORT).show();
	}
	public void toast(String text){
		Toast.makeText(getApplicationContext(),text,Toast.LENGTH_SHORT).show();
	}
	public void startPermission(){
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M)
		{
            // 检查该权限是否已经获取
            int i = ContextCompat.checkSelfPermission(this, permissions[0]);
            // 权限是否已经 授权 GRANTED---授权  DINIED---拒绝
            if (i != PackageManager.PERMISSION_GRANTED)
			{
                // 如果没有授予该权限，就去提示用户请求
                startRequestPermission();
            }	
	}}
	// 开始提交请求权限
    private void startRequestPermission()
	{
        ActivityCompat.requestPermissions(this, permissions, 321);
    }

    // 用户权限 申请 的回调方法
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults)
	{
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == 321)
		{
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M)
			{
                if (grantResults[0] != PackageManager.PERMISSION_GRANTED)
				{
                    // 判断用户是否 点击了不再提醒。(检测该权限是否还可以申请)
                    boolean b = shouldShowRequestPermissionRationale(permissions[0]);
                    if (!b)
					{
                        // 用户还是想用我的 APP 的
                        // 提示用户去应用设置界面手动开启权限
                        showDialogTipUserGoToAppSettting();
                    }
					else
                        finish();
                }
				else
				{
                    Toast.makeText(this, "权限获取成功", Toast.LENGTH_SHORT).show();
                }
            }
        }
    }



    // 提示用户去应用设置界面手动开启权限

    private void showDialogTipUserGoToAppSettting()
	{

        dialog = new AlertDialog.Builder(this)
			.setTitle("存储权限不可用")
			.setMessage("请在-应用设置-权限-中，允许使用存储权限来保存数据")
			.setPositiveButton("立即开启", new DialogInterface.OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog, int which)
				{
					// 跳转到应用设置界面
					goToAppSetting();
				}
			})
			.setNegativeButton("取消", new DialogInterface.OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog, int which)
				{
					finish();
				}
			}).setCancelable(false).show();
    }



    // 跳转到当前应用的设置界面
    private void goToAppSetting()
	{
        Intent intent = new Intent();

        intent.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
        Uri uri = Uri.fromParts("package", getPackageName(), null);
        intent.setData(uri);

        startActivityForResult(intent, 123);
    }

    //回调
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data)
	{
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 123)
		{

            if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.M)
			{
                // 检查该权限是否已经获取
                int i = ContextCompat.checkSelfPermission(this, permissions[0]);
                // 权限是否已经 授权 GRANTED---授权  DINIED---拒绝
                if (i != PackageManager.PERMISSION_GRANTED)
				{
                    // 提示用户应该去应用设置界面手动开启权限
                    showDialogTipUserGoToAppSettting();
                }
				else
				{

                    if (dialog != null && dialog.isShowing())
					{

                        dialog.dismiss();
                    }
                   	Toast.makeText(this, "权限获取成功", Toast.LENGTH_SHORT).show();
				}
            }
        }
    }
	
}
