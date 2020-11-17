package com.lianliankan;

import android.app.Activity;
import android.os.Bundle;
import android.widget.TextView;

public class HelpActivity extends Activity {
	private TextView helpTextView;
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_help);
		helpTextView = (TextView)findViewById(R.id.help_text);
		helpTextView.setText(R.string.score_rules_content);
	}
}
