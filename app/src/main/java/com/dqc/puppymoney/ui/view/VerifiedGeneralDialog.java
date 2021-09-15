package com.dqc.puppymoney.ui.view;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.DrawableRes;
import androidx.annotation.NonNull;

import com.dqc.puppymoney.R;


public class VerifiedGeneralDialog extends Dialog {

    public VerifiedGeneralDialog(@NonNull Context context, int themeResId) {
        super(context, themeResId);
    }

    public static class Builder {
        private Context mContext;
        private String mTitle;
        private String mMessage;
        private String mPositiveButtonText;
        private String mNegativeButtonText;
        private View mContentView;
        private TextView mMessageView;
        private TextView mPositiveView;
        private TextView mNegativeView;
        private TextView mTitleView;
        private OnClickListener mPostiviteButtonClickLisener;
        private OnClickListener mNegativaButtonClickListener;
        private int mLayoutId;
        private @DrawableRes int mLeftDrawableId;

        public int getmLayoutId() {
            return mLayoutId;
        }

        public void setmLayoutId(int mLayoutId) {
            this.mLayoutId = mLayoutId;
        }

        public Builder(Context context) {
            this.mContext = context;
        }

        public TextView getMessageView() {
            return mMessageView;
        }

        public TextView getPositiveView() {
            return mPositiveView;
        }

        public TextView getNegativeView() {
            return mNegativeView;
        }

        public TextView getTitleView() {
            return mTitleView;
        }

        public Builder setMessage(String message) {
            this.mMessage = message;
            return this;
        }

        public Builder setTitle(String title) {
            this.mTitle = title;
            return this;
        }

        public Builder setTitle(String title, @DrawableRes int leftDrawableId) {
            this.mTitle = title;
            this.mLeftDrawableId = leftDrawableId;
            return this;
        }

        public Builder setContentView(View view) {
            this.mContentView = view;
            return this;
        }

        public Builder setPositiveButton(String positiveButtonText
                , OnClickListener listener) {
            this.mPositiveButtonText = positiveButtonText;
            this.mPostiviteButtonClickLisener = listener;
            return this;
        }

        public Builder setNegativeButton(String negativeButtonText
                , OnClickListener listener) {
            this.mNegativeButtonText = negativeButtonText;
            this.mNegativaButtonClickListener = listener;
            return this;
        }

        public VerifiedGeneralDialog create() {
            LayoutInflater inflater = (LayoutInflater) mContext
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            final VerifiedGeneralDialog dialog = new VerifiedGeneralDialog(mContext, R.style.VerifiedDialog);
            dialog.setCancelable(false);
            View layout = inflater.inflate(mLayoutId != 0 ? mLayoutId : R.layout.verified_dialog_normal_layout, null);
            dialog.addContentView(layout, new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.FILL_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT));
            mMessageView = (TextView)(layout.findViewById(R.id.message));
            mPositiveView = (TextView)(layout.findViewById(R.id.positive_btn));
            mNegativeView = (TextView) (layout.findViewById(R.id.negative_btn));
            mTitleView = (TextView) layout.findViewById(R.id.title);

            ((TextView) layout.findViewById(R.id.title)).setText(mTitle);
            if (mPositiveButtonText != null) {
                ((TextView)(layout.findViewById(R.id.positive_btn)))
                        .setText(mPositiveButtonText);
                if (mPostiviteButtonClickLisener != null) {
                    ((TextView)(layout.findViewById(R.id.positive_btn)))
                            .setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    mPostiviteButtonClickLisener.onClick(dialog,
                                            DialogInterface.BUTTON_POSITIVE);
                                }
                            });
                }
            } else {
                layout.findViewById(R.id.positive_btn).setVisibility(View.GONE);
            }

            if (mNegativeButtonText != null) {
                ((TextView) (layout.findViewById(R.id.negative_btn)))
                        .setText(mNegativeButtonText);
                if (mNegativaButtonClickListener != null) {
                    ((TextView) (layout.findViewById(R.id.negative_btn)))
                            .setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    mNegativaButtonClickListener.onClick(dialog,
                                            DialogInterface.BUTTON_NEGATIVE);
                                }
                            });
                }
            } else {
                layout.findViewById(R.id.negative_btn)
                        .setVisibility(View.GONE);
            }

            if (mMessage != null) {
                ((TextView) (layout.findViewById(R.id.message)))
                        .setText(mMessage);
            } else  if (mContentView != null) {
                ((LinearLayout) (layout.findViewById(R.id.content)))
                        .removeAllViews();
                ((LinearLayout) (layout.findViewById(R.id.content)))
                        .addView(mContentView
                                , new LinearLayout.LayoutParams(LinearLayout.LayoutParams.FILL_PARENT, LinearLayout.LayoutParams.FILL_PARENT));
            } else {
                ((TextView) (layout.findViewById(R.id.message)))
                        .setVisibility(View.GONE);
            }
            dialog.setContentView(layout);
            return dialog;
        }

    }

}
