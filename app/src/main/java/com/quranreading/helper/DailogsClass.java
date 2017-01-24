package com.quranreading.helper;

import com.quranreading.listeners.OnDailogButtonSelectionListner;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnCancelListener;
import android.content.DialogInterface.OnDismissListener;
import android.support.v7.app.AlertDialog;
import android.view.KeyEvent;

public class DailogsClass {

	String title;
	String[] options = null;
	Context context;
	int selectedIndex = 0;
	OnDailogButtonSelectionListner listner;
	String textPositive = "";
	String textNegative = "";
	String textMessage = "";
	int customViewId = 0;

	public DailogsClass(Context context, String title, String textMessage, String[] options, int selectedValue, OnDailogButtonSelectionListner listner, String btnPositiveText, String btnNegativeText) {
		// TODO Auto-generated constructor stub
		this.title = title;
		this.options = options;
		this.context = context;
		this.selectedIndex = selectedValue - 1;
		this.listner = listner;
		this.textPositive = btnPositiveText;
		this.textNegative = btnNegativeText;
		this.textMessage = textMessage;
	}

	public DailogsClass(Context context, String title, int customViewId, OnDailogButtonSelectionListner listner) {
		this.context = context;
		this.title = title;
		this.listner = listner;
		this.customViewId = customViewId;
	}

	public DailogsClass(Context context, String title, String textMessage, OnDailogButtonSelectionListner listner, String btnPositiveText, String btnNegativeText) {
		// TODO Auto-generated constructor stub
		this.title = title;
		this.context = context;
		this.listner = listner;
		this.textPositive = btnPositiveText;
		this.textNegative = btnNegativeText;
		this.textMessage = textMessage;
	}

	public DailogsClass(Context context, String title, String textMessage, OnDailogButtonSelectionListner listner, String btnPositiveText) {
		// TODO Auto-generated constructor stub
		this.title = title;
		this.context = context;
		this.listner = listner;
		this.textPositive = btnPositiveText;
		this.textMessage = textMessage;
	}

	public void showOptionsDialogTwoButton() {
		if(options != null)
		{
			AlertDialog.Builder builder = new AlertDialog.Builder(context);
			builder.setCancelable(true).setTitle(title).setSingleChoiceItems(options, selectedIndex, new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int which) {
					selectedIndex = which;

				}
			}).setPositiveButton(textPositive, new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int id) {
					// User clicked OK button
					listner.onDailogButtonSelectionListner(title, selectedIndex, true);
				}
			}).setNegativeButton(textNegative, new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int id) {
					// User cancelled the dialog
					listner.onDailogButtonSelectionListner(title, selectedIndex, false);
				}
			})

			.setOnCancelListener(new OnCancelListener() {

				@Override
				public void onCancel(DialogInterface dialog) {
					// TODO Auto-generated method stub
					listner.onDailogButtonSelectionListner(title, selectedIndex, false);
				}
			});

			builder.show();
		}
	}

	public void showOptionsDialogOneButton() {
		if(options != null)
		{
			AlertDialog.Builder builder = new AlertDialog.Builder(context);
			builder.setCancelable(false).setTitle(title).setSingleChoiceItems(options, selectedIndex, new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int which) {
					selectedIndex = which;

				}
			}).setPositiveButton(textPositive, new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int id) {
					// User clicked OK button
					listner.onDailogButtonSelectionListner(title, selectedIndex, true);
				}
			});

			builder.show();
		}
	}

	public void showTwoButtonDialog() {
		AlertDialog.Builder builder = new AlertDialog.Builder(context);
		builder.setCancelable(true).setTitle(title).setMessage(textMessage).setPositiveButton(textPositive, new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int id) {
				// User clicked OK button
				listner.onDailogButtonSelectionListner(title, selectedIndex, true);
			}
		}).setNegativeButton(textNegative, new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int id) {
				// User cancelled the dialog
				listner.onDailogButtonSelectionListner(title, selectedIndex, false);
			}
		})

		.setOnCancelListener(new OnCancelListener() {

			@Override
			public void onCancel(DialogInterface dialog) {
				// TODO Auto-generated method stub
				listner.onDailogButtonSelectionListner(title, selectedIndex, false);

			}
		});

		builder.show();
	}

	public void showOneButtonDialog() {
		AlertDialog.Builder builder = new AlertDialog.Builder(context);
		builder.setCancelable(true).setTitle(title).setMessage(textMessage).setPositiveButton(textPositive, new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int id) {
				// User clicked OK button
				listner.onDailogButtonSelectionListner(title, selectedIndex, true);
			}
		})

		.setOnCancelListener(new OnCancelListener() {

			@Override
			public void onCancel(DialogInterface dialog) {
				// TODO Auto-generated method stub
				listner.onDailogButtonSelectionListner(title, selectedIndex, false);
			}
		});

		builder.show();
	}

	public void showCustomDailog() {
		if(customViewId > 0)
		{
			AlertDialog alertDialog = null;
			AlertDialog.Builder builder = new AlertDialog.Builder(context).setTitle(title);
			builder.setOnKeyListener(new Dialog.OnKeyListener() {
				@Override
				public boolean onKey(DialogInterface arg0, int keyCode, KeyEvent event) {
					// TODO Auto-generated method stub
					if(keyCode == KeyEvent.KEYCODE_BACK)
					{
						listner.onDailogButtonSelectionListner(title, selectedIndex, false);

					}
					return false;
				}
			});

			alertDialog = builder.create();
			alertDialog.setOnDismissListener(new OnDismissListener() {

				@Override
				public void onDismiss(DialogInterface dialog) {
					// TODO Auto-generated method stub
					listner.onDailogButtonSelectionListner(title, selectedIndex, false);
				}
			});
			alertDialog.setView(alertDialog.getLayoutInflater().inflate(customViewId, null));
			alertDialog.show();

		}
	}

}
