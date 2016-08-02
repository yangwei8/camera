package com.leautolink.leautocamera.upgrade;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
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

public class UpgradeDialog extends Dialog implements
		View.OnClickListener {

	public final static int PROGRESS_SET_MAX = 0;
	public final static int PROGRESS_SET_PROGRESS = 1;
	public final static int UPDATE_FAIL_SERVER = 2;
	public final static int UPDATE_FAIL_CLIENT = 3;
	public final static int CURRENT_DIALOG_DISMISS = 100;

	private UpdateData updateInfo;
	private IUpgradeDialogCallBack listener;

	private Context mContext;
	private RelativeLayout mContentView;
	private View mOK;
	private View mCancel;
	private TextView mMessage;
	private ProgressBar mProgressBar;
	private TextView mOKDesc;
	private TextView mCancelDesc;
	private String tag = "cancel";
	private boolean mDownloading = false;

	public UpgradeDialog(Context context, int theme) {
		super(context, theme);
		mContext = context;
		init();
	}

	public UpgradeDialog(Context context, int theme, UpdateData updateInfo) {
		super(context, theme);
		mContext = context;
		this.updateInfo = updateInfo;
		init();
	}

	public static int convertDipToPx(Context context, int dip) {
		final float scale = context.getResources().getDisplayMetrics().density;
		return (int) (dip * scale + 0.5f);
	}

	private Handler myUpgradeDialogHandler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			switch (msg.what) {
			case PROGRESS_SET_MAX:
				mProgressBar.setMax(msg.arg2);
				break;
			case PROGRESS_SET_PROGRESS:
				mProgressBar.setProgress(msg.arg1);
				int percentage = (int) (100.0f * msg.arg1 / msg.arg2);
				// textPercentage.setText(percentage + "%");
				break;
			case UPDATE_FAIL_SERVER:
				Toast.makeText(getContext(), R.string.update_fail_server,
						Toast.LENGTH_SHORT).show();
				break;
			case UPDATE_FAIL_CLIENT:
				Toast.makeText(getContext(), R.string.update_fail_client,
						Toast.LENGTH_SHORT).show();
				break;
			case CURRENT_DIALOG_DISMISS:
				dismiss();
			default:
				break;
			}
		}
	};

	private void init() {
		LayoutInflater inflater = (LayoutInflater) mContext
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		mContentView = (RelativeLayout) inflater.inflate(
				R.layout.upgrade_dialog_layout, null);
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
		mProgressBar = (ProgressBar) mContentView.findViewById(R.id.bar);
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

	public void setListener(IUpgradeDialogCallBack listener) {
		this.listener = listener;
	}

	public interface IUpgradeDialogCallBack {
		public void onConfirmClick(UpgradeDialog currentDialog);

		public void onCancelClick(UpgradeDialog currentDialog);

		public void onCancelDownload(UpgradeDialog currentDialog);
	}

	@Override
	public void onClick(View v) {
		if (v.getId() == R.id.ok) {
			mDownloading = true;
			listener.onConfirmClick(this);
			mProgressBar.setVisibility(View.VISIBLE);
			mMessage.setVisibility(View.GONE);
		} else if (v.getId() == R.id.cancel) {
			mDownloading = false;
			listener.onCancelClick(this);
		}
	}

	public Handler getMyUpgradeDialogHandler() {
		return myUpgradeDialogHandler;
	}

	public void setMyUpgradeDialogHandler(Handler myUpgradeDialogHandler) {
		this.myUpgradeDialogHandler = myUpgradeDialogHandler;
	}
}
