package com.ZIPIDE;
import android.support.v7.app.*;
import android.os.*;
import android.support.design.widget.*;
import android.widget.*;
import android.view.*;
import java.io.*;
import java.text.*;
import android.content.*;
import android.app.*;
import android.net.*;
import android.database.*;
import android.provider.*;
import android.annotation.*;


public class ProjectActivity extends AppCompatActivity
{
	TextView info;
	TextView filenum,scrfile;
	ListView mLv;

	String ProjectPath,ScriptPath,ProjectName;

	private String[] filelist;

	@Override
    protected void onCreate(Bundle savedInstanceState)
	{
        super.onCreate(savedInstanceState);
        setContentView(R.layout.project_activity);

		ProjectName = getIntent().getStringExtra("项目名");
		getSupportActionBar().setTitle("项目:" + ProjectName);

		ProjectPath = "/sdcard/ZIPIDE/Projects/" + ProjectName;
		ScriptPath = ProjectPath + "/META-INF/com/google/android/updater-script";

		info = (TextView)findViewById(R.id.info);
		filenum = (TextView)findViewById(R.id.project_filenum);
		scrfile = (TextView)findViewById(R.id.project_scr);
		mLv = (ListView)findViewById(R.id.pro_list);


		info.setText("项目路径:" + ProjectPath + "\n脚本路径:" + ScriptPath);
		RefreshProject();

    }

	public void isInfo(View v)
	{
		snackbar("项目是否存在:" + Exists(ProjectPath) + "\n脚本文件是否存在:" + Exists(ScriptPath));
	}


	public void ModifyScript(View v)
	{
		Intent intent = new Intent(ProjectActivity.this, EditorActivity.class);  
		intent.putExtra("项目名", ProjectName);
		intent.putExtra("文件名", "updater-script"); 
		intent.putExtra("路径", ScriptPath);
		startActivityForResult(intent, 1);  
	}

	public void ImportFile(View v)
	{
		Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
		intent.setType("*/*");//设置类型，我这里是任意类型，任意后缀的可以这样写。
		intent.addCategory(Intent.CATEGORY_OPENABLE);
		startActivityForResult(intent, 1);
	}




	String path;

	@Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data)
	{
        if (resultCode == Activity.RESULT_OK)
		{
            Uri uri = data.getData();
            if ("file".equalsIgnoreCase(uri.getScheme()))
			{//使用第三方应用打开
                path = uri.getPath();

                                return;
            }
            if (Build.VERSION.SDK_INT > Build.VERSION_CODES.KITKAT)
			{//4.4以后
                path = getPath(this, uri);
				String fn = new File(path).getName();
				
			//	snackbar(path);
				//snackbar(ProjectPath);
				copyFile(path,ProjectPath+"/"+fn);
				if(Exists(ProjectPath+"/"+fn)){snackbar("导入成功");}
				
			}
			else
			{//4.4以下下系统调用方法
                path = getRealPathFromURI(uri);
				String fn = new File(path).getName();
				copyFile(path,ProjectPath+"/"+fn);
				if(Exists(ProjectPath+"/"+fn)){snackbar("导入成功");}
				
			}
        }
    }

	/** 
     * 复制单个文件 
     * @param oldPath String 原文件路径 如：c:/fqf.txt 
     * @param newPath String 复制后路径 如：f:/fqf.txt 
     * @return boolean 
     */ 
	public void copyFile(String oldPath, String newPath) { 
		try { 
			int bytesum = 0; 
			int byteread = 0; 
			File oldfile = new File(oldPath); 
			if (oldfile.exists()) { //文件存在时 
				InputStream inStream = new FileInputStream(oldPath); //读入原文件 
				FileOutputStream fs = new FileOutputStream(newPath); 
				byte[] buffer = new byte[1444]; 
				int length; 
				while ( (byteread = inStream.read(buffer)) != -1) { 
					bytesum += byteread; //字节数 文件大小 
					System.out.println(bytesum); 
					fs.write(buffer, 0, byteread); 
				} 
				inStream.close(); 
			} 
		} 
		catch (Exception e) { 
			System.out.println("复制单个文件操作出错"); 
			e.printStackTrace(); 

		} 

	} 

	/** 
     * 复制整个文件夹内容 
     * @param oldPath String 原文件路径 如：c:/fqf 
     * @param newPath String 复制后路径 如：f:/fqf/ff 
     * @return boolean 
     */ 
	public void copyFolder(String oldPath, String newPath) { 

		try { 
			(new File(newPath)).mkdirs(); //如果文件夹不存在 则建立新文件夹 
			File a=new File(oldPath); 
			String[] file=a.list(); 
			File temp=null; 
			for (int i = 0; i < file.length; i++) { 
				if(oldPath.endsWith(File.separator)){ 
					temp=new File(oldPath+file[i]); 
				} 
				else{ 
					temp=new File(oldPath+File.separator+file[i]); 
				} 

				if(temp.isFile()){ 
					FileInputStream input = new FileInputStream(temp); 
					FileOutputStream output = new FileOutputStream(newPath + "/" + 
																   (temp.getName()).toString()); 
					byte[] b = new byte[1024 * 5]; 
					int len; 
					while ( (len = input.read(b)) != -1) { 
						output.write(b, 0, len); 
					} 
					output.flush(); 
					output.close(); 
					input.close(); 
				} 
				if(temp.isDirectory()){//如果是子文件夹 
					copyFolder(oldPath+"/"+file[i],newPath+"/"+file[i]); 
				} 
			} 
		} 
		catch (Exception e) { 
			System.out.println("复制整个文件夹内容操作出错"); 
			e.printStackTrace(); 

		} 

	}
	

	public boolean isExternalStorageDocument(Uri uri)
	{
        return "com.android.externalstorage.documents".equals(uri.getAuthority());
    }

    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is DownloadsProvider.
     */
    public boolean isDownloadsDocument(Uri uri)
	{
        return "com.android.providers.downloads.documents".equals(uri.getAuthority());
    }

    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is MediaProvider.
     */
    public boolean isMediaDocument(Uri uri)
	{
        return "com.android.providers.media.documents".equals(uri.getAuthority());
    }



	@SuppressLint("NewApi")
    public String getPath(final Context context, final Uri uri)
	{

        final boolean isKitKat = Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT;

        // DocumentProvider
        if (isKitKat && DocumentsContract.isDocumentUri(context, uri))
		{
            // ExternalStorageProvider
            if (isExternalStorageDocument(uri))
			{
                final String docId = DocumentsContract.getDocumentId(uri);
                final String[] split = docId.split(":");
                final String type = split[0];

                if ("primary".equalsIgnoreCase(type))
				{
                    return Environment.getExternalStorageDirectory() + "/" + split[1];
                }
            }
            // DownloadsProvider
            else if (isDownloadsDocument(uri))
			{

                final String id = DocumentsContract.getDocumentId(uri);
                final Uri contentUri = ContentUris.withAppendedId(
					Uri.parse("content://downloads/public_downloads"), Long.valueOf(id));

                return getDataColumn(context, contentUri, null, null);
            }
            // MediaProvider
            else if (isMediaDocument(uri))
			{
                final String docId = DocumentsContract.getDocumentId(uri);
                final String[] split = docId.split(":");
                final String type = split[0];

                Uri contentUri = null;
                if ("image".equals(type))
				{
                    contentUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
                }
				else if ("video".equals(type))
				{
                    contentUri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI;
                }
				else if ("audio".equals(type))
				{
                    contentUri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
                }

                final String selection = "_id=?";
                final String[] selectionArgs = new String[]{split[1]};

                return getDataColumn(context, contentUri, selection, selectionArgs);
            }
        }
        // MediaStore (and general)
        else if ("content".equalsIgnoreCase(uri.getScheme()))
		{
            return getDataColumn(context, uri, null, null);
        }
        // File
        else if ("file".equalsIgnoreCase(uri.getScheme()))
		{
            return uri.getPath();
        }
        return null;
    }
	public String getDataColumn(Context context, Uri uri, String selection,
                                String[] selectionArgs)
	{

        Cursor cursor = null;
        final String column = "_data";
        final String[] projection = {column};

        try
		{
            cursor = context.getContentResolver().query(uri, projection, selection, selectionArgs,
														null);
            if (cursor != null && cursor.moveToFirst())
			{
                final int column_index = cursor.getColumnIndexOrThrow(column);
                return cursor.getString(column_index);
            }
        }
		finally
		{
            if (cursor != null)
                cursor.close();
        }
        return null;
    }


	public String getRealPathFromURI(Uri contentUri)
	{
        String res = null;
        String[] proj = { MediaStore.Images.Media.DATA };
        Cursor cursor = getContentResolver().query(contentUri, proj, null, null, null);
        if (null != cursor && cursor.moveToFirst())
		{;
            int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
            res = cursor.getString(column_index);
            cursor.close();
        }
        return res;
    }


	public void Refresh(View v)
	{
		RefreshProject();
	}



	public void RefreshProject()
	{

		File f=new File(ScriptPath);
		long time=f.lastModified();
		SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");  
		String result=formatter.format(time);
		scrfile.setText("updater-script" + "(最后修改于:" + result + ")");

		File sceneFile = new File(ProjectPath);
		File[] files = sceneFile.listFiles();
		filelist = new String[files.length];

		if (files != null)
		{
			filelist = new String[files.length];
			for (int i=0;i <= files.length - 1;i++)
			{

				if (!files[i].getName().equals("META-INF"))
				{
					filelist[i] = files[i].getName();
				}
				else
				{
					filelist[i] = "签名文件";
				}

			}
			filenum.setText("文件个数:" + filelist.length);

			ArrayAdapter<String> adap = new ArrayAdapter<String>(ProjectActivity.this, android.R.layout.simple_list_item_1, filelist);
			//实例化列表对象,并set列表数据
			mLv.setAdapter(adap);	
			mLv.setOnItemClickListener(new AdapterView.OnItemClickListener(){ 
					@Override
					public void onItemClick(AdapterView<?> parent, View view, int position, long id)
					{

					}}); 
		}

	}



	//工具方法
	//文件是否存在
	public boolean Exists(String fi)
	{
		try
		{
			File f=new File(fi);
			if (!f.exists())
			{
				return false;
			}

		}
		catch (Exception e)
		{

			return false;
		}
		return true;
	}

	public void snackbar(String text)
	{
		Snackbar.make(getWindow().getDecorView(), text, Snackbar.LENGTH_SHORT).show();
	}
	public void toast(String text)
	{
		Toast.makeText(getApplicationContext(), text, Toast.LENGTH_SHORT).show();
	}

}
