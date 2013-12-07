package com.example.boogieboogie;

import java.util.ArrayList;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.StrictMode;
import android.util.Log;
import android.view.Menu;
import android.widget.AbsListView;
import android.widget.ListView;
import android.widget.Toast;

import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

public class FindIsbnActivity extends Activity {
	
	private ProgressDialog dialog;
	private IsbnSearchParser isbnSearchParser;
	protected ListView myList;
	protected CustomAdapter adapter;
	
	ArrayList<BookData> data;
	
	private final Handler handler = new Handler() {
		@Override
		public void handleMessage(final Message msg) {
			dialog.dismiss();
			adapter = new CustomAdapter(FindIsbnActivity.this,
					R.layout.find_book_listview_item, data);
			myList.setAdapter(adapter);
			myList.setChoiceMode(AbsListView.CHOICE_MODE_SINGLE);
		}
	};
	
	public void getBookFromIsbn(final String queryIsbn) {
		dialog = ProgressDialog.show(this, "Loading...", "Loading Book...",
				true, false);
		new Thread() {
			@Override
			public void run() {
				data = isbnSearchParser.getBookData(queryIsbn);
				handler.sendEmptyMessage(0);
			}
		}.start();
	}
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		switch (requestCode) {
			case IntentIntegrator.REQUEST_CODE: {
				if (resultCode != RESULT_CANCELED) {
					IntentResult scanResult = IntentIntegrator
							.parseActivityResult(requestCode, resultCode, data);
					String result = "";
					if (scanResult != null) {
						result = scanResult.getContents();
					} else
						return;
					
					result = result.trim();
					Log.i("DEBUG", result);
					// Check to see if it is a ISBN (checks if it is numeric)
					if (result.matches("[0-9]*")) {
						Log.i("DEBUG", "before getbookfromisbn");
						getBookFromIsbn(result);
						Log.i("DEBUG", "after getbookfromisbn");
						// Intent intent = new Intent(FindIsbnActivity.this,
						// BarcodeResult.class);
						//
						// // Pass the ISBN to the BarcodeResult form
						// Bundle bundle = new Bundle();
						// bundle.putString("ISBN", scanResult.getContents());
						// intent.putExtras(bundle);
						//
						// startActivity(intent);
					} else {
						Toast.makeText(this, "No search result try again",
								Toast.LENGTH_LONG).show();
						// Intent intent = new Intent(FindIsbnActivity.this,
						// (Activity)FindActivity.class);
						// this.startActivity(intent);
						break;
					}
					// Assume that it must be a string
					// else {
					// AlertBox.show("Found Message", result, this);
					// }
				}
			}
				break;
		}
	}
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.find_book_listview);
		
		StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder()
				.permitAll().build();
		StrictMode.setThreadPolicy(policy);
		myList = (ListView) findViewById(R.id.find_listview);
		isbnSearchParser = new IsbnSearchParser();
		// if(!DEBUGMODE){
		try {
			IntentIntegrator integrator = new IntentIntegrator(this);
			integrator.initiateScan();
			// IntentIntegrator
			// .initiateScan(
			// this
			// ,
			// "Install Barcode Scanner?",
			// "This application requires you to install 'Barcode Scanner'. Would you like to install it?",
			// "Yes", "No"
			// );
			Log.i("DEBUG", "initiateScan");
		} catch (Exception e) {
			// This error is usually handled in IntentIntegrator
			// AlertBox.show("Error", "Could not access Market",
			// FindActivity.this);
			e.printStackTrace();
		}
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.find_isbn, menu);
		return true;
	}
}
