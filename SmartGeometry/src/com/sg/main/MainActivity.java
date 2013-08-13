package com.sg.main;

import com.sg.property.R;

import android.R.integer;
import android.app.Activity;
import android.app.AlertDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.HorizontalScrollView;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TableLayout;
import android.widget.Toast;
import android.widget.RadioGroup.OnCheckedChangeListener;

public class MainActivity extends Activity implements OnCheckedChangeListener {
	/** Called when the activity is first created. */

	// Message types sent from the BluetoothService Handler
	public static final int MESSAGE_STATE_CHANGE = 1;
	public static final int MESSAGE_DEVICE_NAME = 2;
	public static final int MESSAGE_TOAST = 3;

	// Key names received from the BluetoothService Handler
	public static final String DEVICE_NAME = "device_name";
	public static final String TOAST = "toast";

	// Intent request codes
	private static final int FILE_EXPLORER_REQUEST = 10;
	private static final int CONNECT_DEVICE_REQUEST = 11;

	private MainView mainView;

	private RadioGroup mRadioGroup;
	private RadioButton undo;
	private RadioButton redo;
	private RadioButton clear;
	private RadioButton save;
	private RadioButton open;
	private RadioButton bluetooth;
	private RadioButton exit;
	private HorizontalScrollView mHorizontalScrollView;// 上面的水平滚动控件

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, // 设置全屏
				WindowManager.LayoutParams.FLAG_FULLSCREEN);
		getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON); // 设置背光灯长亮
		initConfig(this);

		setContentView(R.layout.main);

		initMenu();

		mainView = (MainView) findViewById(R.id.myview);

		// 读取关联文件
		Intent intent = getIntent();
		String action = intent.getAction();
		if (Intent.ACTION_VIEW.equals(action)) {
			Uri uri = (Uri) intent.getData();
			String path = uri.getPath();
			// mainView.open(path);
		}
	}

	private void initMenu() {
		mRadioGroup = (RadioGroup) findViewById(R.id.radioGroup);
		undo = (RadioButton) findViewById(R.id.undo);
		redo = (RadioButton) findViewById(R.id.redo);
		clear = (RadioButton) findViewById(R.id.clear);
		save = (RadioButton) findViewById(R.id.save);
		open = (RadioButton) findViewById(R.id.open);
		bluetooth = (RadioButton) findViewById(R.id.bluetooth);
		exit = (RadioButton) findViewById(R.id.exit);
		mHorizontalScrollView = (HorizontalScrollView) findViewById(R.id.horizontalScrollView);
		mRadioGroup.setOnCheckedChangeListener(this);
	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		switch (requestCode) {
		case FILE_EXPLORER_REQUEST:
			if (resultCode == RESULT_OK) {
				String path = data.getStringExtra("path");
				// mainView.open(path);
			}
			break;
		case CONNECT_DEVICE_REQUEST:
			if (resultCode == RESULT_OK) {
				// connectDevice(data);
			}
			break;
		default:
			break;
		}
	}

	/*
	 * 初始化 阈值配置文件
	 */
	private void initConfig(Context context) {
		DisplayMetrics dm = getResources().getDisplayMetrics();
		// ThresholdProperty.set(dm.densityDpi / DisplayMetrics.DENSITY_MEDIUM);
	}

	// 返回键对话框
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_MENU) {
			if (mHorizontalScrollView.getVisibility() == ViewGroup.GONE) {
				mHorizontalScrollView.setVisibility(ViewGroup.VISIBLE);
			} else {
				mHorizontalScrollView.setVisibility(ViewGroup.GONE);
			}

			return true;
		}
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			exit();
			return true;
		} else {
			return super.onKeyDown(keyCode, event);
		}
	}

	private void exit() {
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setTitle("退出软件");
		builder.setMessage("确认退出？");
		builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int whichButton) {
				// mainView.clear();
				finish();
			}
		});
		builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int whichButton) {

			}
		});
		AlertDialog dialog = builder.create();
		dialog.setCanceledOnTouchOutside(true);
		dialog.show();
	}

	private void save() {
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setTitle("保存文件");
		LayoutInflater factory = LayoutInflater.from(this);
		final TableLayout saveForm = (TableLayout) factory.inflate(
				R.layout.save, null);
		builder.setView(saveForm);
		builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int whichButton) {
				EditText nameText = (EditText) saveForm
						.findViewById(R.id.editName);
				String name = nameText.getText().toString();
				// mainView.save(name);
			}
		});
		builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int whichButton) {

			}
		});
		builder.create().show();
	}

	@Override
	public void onCheckedChanged(RadioGroup group, int checkedId) {
		// TODO Auto-generated method stub
		switch (checkedId) {
		case R.id.undo:
			// mainView.Undo();
			undo.setChecked(false);
			break;
		case R.id.redo:
			// mainView.Redo();
			redo.setChecked(false);
			break;
		case R.id.clear:
			// mainView.clear();
			clear.setChecked(false);
			break;
		case R.id.save:
			save();
			save.setChecked(false);
			break;
		case R.id.open:
			open.setChecked(false);
			Intent intent = new Intent(this, FileExplorerActivity.class);
			startActivityForResult(intent, FILE_EXPLORER_REQUEST);
			break;
		case R.id.bluetooth:
			// chooseDevice();
			bluetooth.setChecked(false);
			break;
		case R.id.exit:
			exit();
			exit.setChecked(false);
			break;
		default:
			break;
		}
	}

}