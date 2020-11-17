package com.lianliankan;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;

public class ResultActivity extends Activity {
	private TextView resultTextView;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_result);
		resultTextView = (TextView)findViewById(R.id.result_text);
		Intent intent = getIntent();
		String totalTimeStr = intent.getStringExtra("totalTime");
		String basicScoreStr = intent.getStringExtra("basicScore");
		String timeAddScoreStr = intent.getStringExtra("timeAddScore");
		String scoreStr= intent.getStringExtra("score");
		String isNewRecordStr= intent.getStringExtra("isNewRecord");
		if(isNewRecordStr.equals("true")){
			this.setTitle(R.string.new_record);			
		}
		resultTextView.setText("��ʱ : " + totalTimeStr + "s\n"
		+"������: " + basicScoreStr + "\n"
				+"ʱ��ӷ�: " + timeAddScoreStr + "\n"+"���յ÷�: " + scoreStr + "\n");

	}
	
}
