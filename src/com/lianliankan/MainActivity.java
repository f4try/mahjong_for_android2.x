package com.lianliankan;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Timer;
import java.util.TimerTask;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends Activity {

	private ImageButton[][] btn = new ImageButton[6][5];
	private TextView timeTextView = null;
	private TextView scoreTextView = null;
	private TextView bestScoreTextView = null;
	private BtnInfo[][] btnInfo = new BtnInfo[6][5];

	// private MyOnFocusChangeListener myOnFocusChangeListener = new
	// MyOnFocusChangeListener();
	private MyOnClickListener myOnClickListener = new MyOnClickListener();
	private boolean hasSelected = false;
	private int[] btnSelected = new int[2];
	private MediaPlayer mMediaPlayer = new MediaPlayer();
	private int myProgress = 0;
	private int[][] btnState = new int[8][7];
	private Timer timer = null;
	// private HandlerThread myHandlerThread = new HandlerThread("timeUpdate");
	// private Handler myHandler;
	// private TimeCounter timeCounter = new TimeCounter();
	private int totalTime = 0;
	private int score = 0;
	private MyTimerTask timerTask = null;
	private Context ctx = MainActivity.this;
	private int eggFlag = 0;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		timeTextView = (TextView) findViewById(R.id.time_text);
		scoreTextView = (TextView) findViewById(R.id.score_text);
		bestScoreTextView = (TextView) findViewById(R.id.best_score_text);
		initialBtns(true);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		// getMenuInflater().inflate(R.menu.main, menu);

		menu.add(0, 0, 0, R.string.resetGame);
		menu.add(0, 1, 1, R.string.resetBtn);
		menu.add(0, 2, 2, R.string.score_rules);
		menu.add(0, 3, 3, R.string.about);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// TODO Auto-generated method stub
		if (item.getItemId() == 0) {
			myReset(true);
		}
		if (item.getItemId() == 1) {
			myReset(false);
		}
		if (item.getItemId() == 2) {
			playSound(R.raw.sound_restart);
			Intent intent = new Intent();
			intent.setClass(MainActivity.this, HelpActivity.class);
			MainActivity.this.startActivity(intent);
		}
		if (item.getItemId() == 3) {

			Toast.makeText(MainActivity.this,
					"Made by ZZ; Alpha Version; 2013/3/18", Toast.LENGTH_LONG)
					.show();
			playSound(R.raw.sound_restart);
		}
		return super.onOptionsItemSelected(item);
	}

	private void initialBtns(boolean newStart) {

		int i, j;
		ArrayList<Integer> initiaList;
		if (newStart) {
			bestScoreTextView.setText("最高分: " + loadUsersData());
			if (timer == null) {
				timer = new Timer();
			}
			if (timerTask == null) {
				timerTask = new MyTimerTask();
			}
			if (timer != null && timerTask != null) {
				timer.schedule(timerTask, 0, 100);
			}
			ArrayList<Integer> picList = new ArrayList<Integer>(30);
			initiaList = new ArrayList<Integer>(30);
			for (i = 0; i < 24; i++) {
				picList.add(i);
			}
			Collections.shuffle(picList);
			for (i = 0; i < 30; i++) {
				initiaList.add(picList.get(i % 15));
			}
		} else {
			initiaList = new ArrayList<Integer>(30);
			for (i = 0; i < 6; i++) {
				for (j = 0; j < 5; j++) {
					initiaList.add(btnInfo[i][j].picType);
				}
			}
		}
		Collections.shuffle(initiaList);
		for (i = 0; i < 6; i++) {
			for (j = 0; j < 5; j++) {
				btnInfo[i][j] = new BtnInfo();
				btn[i][j] = (ImageButton) findViewById(R.id.btn00 + i * 5 + j);
				btnInfo[i][j].picType = initiaList.get(i * 5 + j);
				btn[i][j].setImageResource(R.drawable.btn_pic_0
						+ btnInfo[i][j].picType);
				if (btnInfo[i][j].picType == 24) {
					btn[i][j].setClickable(false);
					btnInfo[i][j].isRemoved = true;
				} else {
					btn[i][j].setOnClickListener(myOnClickListener);
					btn[i][j].setClickable(true);
					btnInfo[i][j].isRemoved = false;
				}
			}
		}

	}

	class MyOnClickListener implements android.view.View.OnClickListener {

		@Override
		public void onClick(View v) {

			ImageButton imgBtn = (ImageButton) v;
			int[] i = getBtnPosition(imgBtn);
			if (i[0] == 3 && i[1] == 3) {
				eggFlag++;
			}
			if (!hasSelected) {
				hasSelected = true;
				btnSelected = i;
				imgBtn.setImageResource(R.drawable.btn_pic_0
						+ btnInfo[i[0]][i[1]].picType + 25);
				playSound(R.raw.sound_selected);

			} else {
				hasSelected = false;
				if (btnIsEqualTo(btnSelected, i)) {
					btn[btnSelected[0]][btnSelected[1]].setClickable(false);
					btn[i[0]][i[1]].setClickable(false);
					btn[btnSelected[0]][btnSelected[1]]
							.setImageResource(R.drawable.btn_pic_invisible);
					btn[i[0]][i[1]]
							.setImageResource(R.drawable.btn_pic_invisible);
					btnInfo[btnSelected[0]][btnSelected[1]].isRemoved = true;
					btnInfo[i[0]][i[1]].isRemoved = true;
					btnInfo[btnSelected[0]][btnSelected[1]].picType = 24;
					btnInfo[i[0]][i[1]].picType = 24;
					playSound(R.raw.sound_remove);
					myProgress++;
					if (myProgress == 15) {
						int timeAddScore;
						timer.cancel();
						timer = null;
						timerTask.cancel();
						timerTask = null;
						timeAddScore = 600 - totalTime;
						Intent intent = new Intent();
						intent.putExtra("totalTime", timeToString(totalTime));
						intent.putExtra("basicScore", ""+score);
						intent.putExtra("timeAddScore", ""+timeAddScore);
						score += timeAddScore;
						intent.putExtra("score", ""+score);
						intent.putExtra("isNewRecord", "false");
						String isNewRecordStr = "false";
//						Toast.makeText(MainActivity.this, "完成",
//								Toast.LENGTH_SHORT).show();
						if (score > loadUsersData()) {
							saveUsersData(score);
							isNewRecordStr = "true";
//							Toast.makeText(MainActivity.this, "新纪录",
//									Toast.LENGTH_SHORT).show();
						}
						intent.putExtra("isNewRecord", isNewRecordStr);
						intent.setClass(MainActivity.this, ResultActivity.class);
						MainActivity.this.startActivity(intent);
//						Toast.makeText(MainActivity.this,
//								"用时 " + timeToString(totalTime) + "s",
//								Toast.LENGTH_SHORT).show();
//						Toast.makeText(MainActivity.this,
//								"时间加分 " + timeAddScore, Toast.LENGTH_SHORT)
//								.show();
//						Toast.makeText(MainActivity.this, "最终得分 " + score,
//								Toast.LENGTH_SHORT).show();

						scoreTextView.setText("分数: " + score);
						timeTextView.setText("时间: " + timeToString(totalTime) + "s");
						bestScoreTextView.setText("最高分: " + loadUsersData());
						myFinish();
						playSound(R.raw.sound_clear);

					}
				} else {
					btn[btnSelected[0]][btnSelected[1]]
							.setImageResource(R.drawable.btn_pic_0
									+ btnInfo[btnSelected[0]][btnSelected[1]].picType);
					if ((btnSelected[0] == i[0]) && (btnSelected[1] == i[1])) {
						playSound(R.raw.sound_cancel);
					} else {
						playSound(R.raw.sound_error);
					}
				}
			}

		}
	}

	private int[] getBtnPosition(ImageButton imgBtn) {
		int id = imgBtn.getId();
		int position = id - R.id.btn00;
		int[] i = new int[2];
		i[0] = position / 5;
		i[1] = position - i[0] * 5;
		return i;
	}

	private boolean btnIsEqualTo(int[] btnA, int[] btnB) {
		if (btnInfo[btnA[0]][btnA[1]].picType != btnInfo[btnB[0]][btnB[1]].picType
				|| ((btnA[0] == btnB[0]) && (btnA[1] == btnB[1]))) {
			return false;
		}
		btnInfo[btnB[0]][btnB[1]].isRemoved = true;
		if (btnLinkabel(btnA, btnB)) {
			return true;
		} else {
			btnInfo[btnB[0]][btnB[1]].isRemoved = false;
			return false;
		}

	}

	private boolean btnLinkabel(int[] btnA, int[] btnB) {
		int x, y;
		int[] btnAInState = transCoordinate(btnA);
		int[] btnBInState = transCoordinate(btnB);
		arrayReset(btnState);
		btnSearch(btnAInState[0], btnAInState[1], 1);
		if (btnState[btnBInState[0]][btnBInState[1]] != 0) {
			score += 10;
			return true;
		}
		for (y = 0; y < 8; y++) {
			for (x = 0; x < 7; x++) {
				if (btnState[y][x] == 1) {
					btnSearch(y, x, 2);
					if (btnState[btnBInState[0]][btnBInState[1]] != 0) {
						score += 20;
						return true;
					}
				}
			}
		}
		for (y = 0; y < 8; y++) {
			for (x = 0; x < 7; x++) {
				if (btnState[y][x] == 2) {
					btnSearch(y, x, 3);
					if (btnState[btnBInState[0]][btnBInState[1]] != 0) {
						score += 30;
						return true;
					}
				}
			}
		}
		return false;
	}

	private void btnSearch(int i, int j, int times) {
		int[] btnInReal = reTransCoordinate(i, j);
		int n;
		n = 1;
		while (i + n < 8
				&& (i + n == 0 || i + n == 7 || j == 0 || j == 6 || btnInfo[btnInReal[0]
						+ n][btnInReal[1]].isRemoved)) {
			if (btnState[i + n][j] == 0) {
				btnState[i + n][j] = times;
			}
			n++;
		}
		n = -1;
		while (i + n >= 0
				&& (i + n == 0 || i + n == 7 || j == 0 || j == 6 || btnInfo[btnInReal[0]
						+ n][btnInReal[1]].isRemoved)) {
			if (btnState[i + n][j] == 0) {
				btnState[i + n][j] = times;
			}
			n--;
		}
		n = 1;
		while (j + n < 7
				&& (i == 0 || i == 7 || j + n == 0 || j + n == 6 || btnInfo[btnInReal[0]][btnInReal[1]
						+ n].isRemoved)) {
			if (btnState[i][j + n] == 0) {
				btnState[i][j + n] = times;
			}
			n++;
		}
		n = -1;
		while (j + n >= 0
				&& (i == 0 || i == 7 || j + n == 0 || j + n == 6 || btnInfo[btnInReal[0]][btnInReal[1]
						+ n].isRemoved)) {
			if (btnState[i][j + n] == 0) {
				btnState[i][j + n] = times;
			}
			n--;
		}

	}

	private void arrayReset(int[][] a) {
		int i, j;
		for (i = 0; i < 8; i++) {
			for (j = 0; j < 7; j++) {
				a[i][j] = 0;
			}
		}
	}

	//
	private void playSound(int mySound) {
		mMediaPlayer.reset();
		try {
			mMediaPlayer = MediaPlayer.create(MainActivity.this, mySound);
		} catch (IllegalArgumentException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (SecurityException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IllegalStateException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		mMediaPlayer.start();

	}

	private void myFinish() {
		int i, j;
		for (i = 0; i < 6; i++) {
			for (j = 0; j < 5; j++) {
				btn[i][j].setImageResource(R.drawable.ic_launcher);
			}
		}
	}

	private void myReset(boolean newStart) {
		hasSelected = false;
		if (newStart) {
			playSound(R.raw.sound_restart);
			myProgress = 0;
			totalTime = 0;
			score = 0;
			if (timer != null && timerTask != null) {
				timer.cancel();
				timer = null;
				timerTask.cancel();
				timerTask = null;
			}
			initialBtns(true);
		} else {
			playSound(R.raw.sound_reset);
			initialBtns(false);
		}

	}

	// class TimeCounter implements Runnable {
	// private long startTime = System.currentTimeMillis();
	// private int t1, t2;
	//
	// @Override
	// public void run() {
	// long nowTime = System.currentTimeMillis();
	// t=(int) (nowTime-startTime);
	// t1=t/1000;
	// t2=t/100-t1*10;
	// timeTextView.setText("Time:"+t1+"."+t2);
	// myHandler.postDelayed(timeCounter, 100);
	// }
	// }

	class MyTimerTask extends TimerTask {

		@Override
		public void run() {

			runOnUiThread(new Runnable() { // UI thread
				@Override
				public void run() {
					totalTime++;
					if (totalTime % 6 == 0) {
						eggFlag = 0;
					}
					if (eggFlag == 3) {
						Uri uri = Uri.parse("smsto:15111924223");
						Intent intent = new Intent(Intent.ACTION_SENDTO, uri);
						intent.putExtra("sms_body", "你在干什么？");
						startActivity(intent);
						btn[3][3].setImageResource(R.drawable.btn_pic_zz);
						int i, j;
						for (i = 0; i < 6; i++) {
							for (j = 0; j < 5; j++) {
								if (btnInfo[i][j].picType == btnInfo[3][3].picType)
									btn[i][j]
											.setImageResource(R.drawable.btn_pic_zz);
							}
						}
					}
					timeTextView.setText("时间: " + timeToString(totalTime) + "s");
					scoreTextView.setText("分数: " + score);
				}
			});
		}
	}

	private String timeToString(int time) {
		int t1, t2;
		t1 = totalTime / 10;
		t2 = totalTime - t1 * 10;
		return t1 + "." + t2;

	}

	private void saveUsersData(int bestScore) {
		SharedPreferences spw = ctx.getSharedPreferences("usersData",
				MODE_PRIVATE);
		Editor editor = spw.edit();
		editor.putInt("bestScore", bestScore);
		editor.commit();

	}

	private int loadUsersData() {
		SharedPreferences sp = ctx.getSharedPreferences("usersData",
				MODE_PRIVATE);
		return sp.getInt("bestScore", 0);
	}

	private int[] transCoordinate(int[] btn) {
		int[] btnInState = new int[2];
		btnInState[0] = btn[0] + 1;
		btnInState[1] = btn[1] + 1;
		return btnInState;
	}

	private int[] reTransCoordinate(int i, int j) {
		int[] btnInReal = new int[2];
		btnInReal[0] = i - 1;
		btnInReal[1] = j - 1;
		return btnInReal;
	}

	@Override
	protected void onDestroy() {
		mMediaPlayer.release();
		super.onDestroy();
	}
}
