package com.leautolink.leautocamera.ui.view.customview;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.WindowManager;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.leautolink.leautocamera.R;
import com.leautolink.leautocamera.upgrade.UpdateData;

public class CallDialog extends Dialog implements
		View.OnClickListener {

//	public final static int PROGRESS_SET_MAX = 0;
//	public final static int PROGRESS_SET_PROGRESS = 1;
//	public final static int UPDATE_FAIL_SERVER = 2;
//	public final static int UPDATE_FAIL_CLIENT = 3;
//	public final static int CURRENT_DIALOG_DISMISS = 100;

//	private UpdateData updateInfo;
	private ICallDialogCallBack listener;

	private Context mContext;
	private RelativeLayout mContentView;
	private View mOK;
	private View mCancel;
	private TextView mMessage;
//	private ProgressBar mProgressBar;
	private TextView mOKDesc;
	private TextView mCancelDesc;
//	private String tag = "cancel";
//	private boolean mDownloading = false;

	public CallDialog(Context context, int theme) {
		super(context, theme);
		mContext = context;
		init();
	}

//	public CallDialog(Context context, int theme, UpdateData updateInfo) {
//		super(context, theme);
//		mContext = context;
////		this.updateInfo = updateInfo;
//		init();
//	}

	public static int convertDipToPx(Context context, int dip) {
		final float scale = context.getResources().getDisplayMetrics().density;
		return (int) (dip * scale + 0.5f);
	}

//	private Handler myUpgradeDialogHandler = new Handler() {
//		@Override
//		public void handleMessage(Message msg) {
//			super.handleMessage(msg);
//			switch (msg.what) {
//			case PROGRESS_SET_MAX:
//				mProgressBar.setMax(msg.arg2);
//				break;
//			case PROGRESS_SET_PROGRESS:
//				mProgressBar.setProgress(msg.arg1);
//				int percentage = (int) (100.0f * msg.arg1 / msg.arg2);
//				// textPercentage.setText(percentage + "%");
//				break;
//			case UPDATE_FAIL_SERVER:
//				Toast.makeText(getContext(), R.string.update_fail_server,
//						Toast.LENGTH_SHORT).show();
//				break;
//			case UPDATE_FAIL_CLIENT:
//				Toast.makeText(getContext(), R.string.update_fail_client,
//						Toast.LENGTH_SHORT).show();
//				break;
//			case CURRENT_DIALOG_DISMISS:
//				dismiss();
//			default:
//				break;
//			}
//		}
//	};

	private void init() {
		LayoutInflater inflater = (LayoutInflater) mContext
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		mContentView = (RelativeLayout) inflater.inflate(
				R.layout.call_dialog_layout, null);
		setContentView(mContentView);
		WindowManager.LayoutParams params = getWindow().getAttributes();
		params.width = (int) mContext.getResources().getDimension(
				R.dimen.dialog_layout_width);
		params.height = (int) mContext.getResources().getDimension(
				R.dimen.dialog_layout_height);
		getWindow().setAttributes(params);
		mOK = mContentView.findViewById(R.id.ok);
		mOKDesc = (TextView) mContentView.findViewById(R.id.mOKDesc);
		mCancel = mContentView.findViewById(R.id.cancel);
		mCancelDesc= (TextView) mContentView.findViewById(R.id.mCancelDesc);
		mCancel.setOnClickListener(this);
		mMessage = (TextView) mContentView.findViewById(R.id.message);
		mOK.setOnClickListener(this);
//		mProgressBar = (ProgressBar) mContentView.findViewById(R.id.bar);
		mOK.setOnTouchListener(new OnTouchListener() {

			@Override
			public boolean onTouch(View v, MotionEvent event) {
				switch (event.getAction()) {
				case MotionEvent.ACTION_DOWN:
					mOK.setBackgroundResource(R.drawable.btn_left);
					mOKDesc.setTextColor(Color.WHITE);
					break;
				case MotionEvent.ACTION_UP:
					mOK.setBackgroundColor(Color.TRANSPARENT);
					mOKDesc.setTextColor(Color.BLACK);
					break;

				default:
					break;
				}
				return false;
			}
		});
		mCancel.setOnTouchListener(new OnTouchListener() {

			@Override
			public boolean onTouch(View v, MotionEvent event) {
				switch (event.getAction()) {
				case MotionEvent.ACTION_DOWN:
					mCancel.setBackgroundResource(R.drawable.btn_right);
					mCancelDesc.setTextColor(Color.WHITE);
					break;
				case MotionEvent.ACTION_UP:
					mCancel.setBackgroundColor(Color.TRANSPARENT);
					mCancelDesc.setTextColor(Color.BLACK);
					break;

				default:
					break;
				}
				return false;
			}
		});
	}

	public void setListener(ICallDialogCallBack listener) {
		this.listener = listener;
	}

	public interface ICallDialogCallBack {
		public void onConfirmClick(CallDialog currentDialog);

		public void onCancelClick(CallDialog currentDialog);

		public void onCancelDownload(CallDialog currentDialog);
	}

	@Override
	public void onClick(View v) {
		if (v.getId() == R.id.ok) {
			Intent intentPhone = new Intent(Intent.ACTION_CALL, Uri.parse("tel:" + "01010109000"));
			mContext.startActivity(intentPhone);
			dismiss();
//			listener.onCancelClick(this);

		} else if (v.getId() == R.id.cancel) {
			dismiss();
//			listener.onCancelClick(this);

		}
	}

//	public Handler getMyUpgradeDialogHandler() {
//		return myUpgradeDialogHandler;
//	}

//	public void setMyUpgradeDialogHandler(Handler myUpgradeDialogHandler) {
//		this.myUpgradeDialogHandler = myUpgradeDialogHandler;
//	}
}
