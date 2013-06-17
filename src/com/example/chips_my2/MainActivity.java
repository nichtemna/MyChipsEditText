package com.example.chips_my2;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.example.chips_my2.actions.ActionManager;
import com.example.chips_my2.actions.MyAction;
import com.example.chips_my2.model.Friend;
import com.example.chips_my2.view.MyAutoCompleteTextView;

public class MainActivity extends Activity {
	public static final String UNCKECK_ITEM_ACTION = "com.example.chips.main.uncheckitem";
	public static final String FILTER_ITEM_ACTION = "com.example.chips.main.filteritem";
	public static final String FRIEND = "friend";
	public static final String FILTER_TEXT = "filter_text";
	private ListView listview;
	private MyAutoCompleteTextView editText;
	private MyAdapter myAdapter;
	private UncheckItemAction unckeckItemAction;
	private FilterItemAction filterItemAction;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Log.d("tag", "MainActivity");
		setContentView(R.layout.activity_main);
		filterItemAction = new FilterItemAction();
		unckeckItemAction = new UncheckItemAction();

		editText = (MyAutoCompleteTextView) findViewById(R.id.editText1);

		if (myAdapter == null) {
			myAdapter = new MyAdapter(this);
		}
		listview = (ListView) findViewById(R.id.listView1);
		listview.setAdapter(myAdapter);
		listview.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				if (((Friend) myAdapter.getItem(position)).getArrayOfEmails()
						.size() > 1
						&& !((Friend) myAdapter.getItem(position)).isChecked()) {
					showChooseEmailDialog(position);
				} else {
					checkItem(position);
				}
			}
		});
	}

	private void checkItem(int position) {
		myAdapter.checkItem(position);
		editText.setItem((Friend) myAdapter.getItem(position));
		myAdapter.notifyDataSetChanged();
	}

	private void showChooseEmailDialog(final int position) {
		final Friend friend = (Friend) myAdapter.getItem(position);
		AlertDialog.Builder adb = new AlertDialog.Builder(this);
		adb.setTitle("Choose one email");
		ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
				android.R.layout.select_dialog_item, friend.getArrayOfEmails());
		adb.setAdapter(adapter, new OnClickListener() {
			public void onClick(DialogInterface dialog, int which) {
				checkItem(position);
				myAdapter.setChosenEmail(friend,
						friend.getArrayOfEmails().get(which));
			}
		});
		AlertDialog dialog = adb.create();
		dialog.show();
	}

	@Override
	public void onResume() {
		super.onResume();
		ActionManager.registrateAction(this, null, unckeckItemAction);
		ActionManager.registrateAction(this, null, filterItemAction);
	}

	@Override
	public void onPause() {
		super.onPause();
		ActionManager.unregistrateAction(this, unckeckItemAction);
		ActionManager.unregistrateAction(this, filterItemAction);
	}

	class UncheckItemAction extends MyAction {

		@Override
		public void onReceive(Context context, Intent intent) {
			Friend friend = (Friend) intent.getSerializableExtra(FRIEND);
			myAdapter.checkItem(friend, false);
			myAdapter.notifyDataSetChanged();
		}

		@Override
		protected void onRegistrate(Activity activity, Fragment fragment) {
			super.onRegistrate(activity, fragment);
		}

		@Override
		protected IntentFilter init() {
			IntentFilter intentFilter = new IntentFilter(UNCKECK_ITEM_ACTION);
			return intentFilter;
		}
	}

	class FilterItemAction extends MyAction {

		@Override
		public void onReceive(Context context, Intent intent) {
			String s = intent.getStringExtra(FILTER_TEXT);
			myAdapter.getFilter().filter(s);
		}

		@Override
		protected void onRegistrate(Activity activity, Fragment fragment) {
			super.onRegistrate(activity, fragment);
		}

		@Override
		protected IntentFilter init() {
			IntentFilter intentFilter = new IntentFilter(FILTER_ITEM_ACTION);
			return intentFilter;
		}
	}

}
