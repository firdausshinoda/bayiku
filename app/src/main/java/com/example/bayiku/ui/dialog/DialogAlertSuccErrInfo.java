package com.example.bayiku.ui.dialog;

import android.content.Context;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.fragment.app.DialogFragment;
import androidx.vectordrawable.graphics.drawable.VectorDrawableCompat;

import com.example.bayiku.R;
import com.example.bayiku.utils.Config;
import com.google.android.material.button.MaterialButton;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class DialogAlertSuccErrInfo extends DialogFragment {

    private View v;
    private Context context;
    private String deskripsi, judul, type;
    public OnDialogClickPositive onDialogClickPositive;

    @BindView(R.id.ln_icon) LinearLayout ln_icon;
    @BindView(R.id.imv) ImageView imv;
    @BindView(R.id.tv_judul) TextView tv_judul;
    @BindView(R.id.tv_deskripsi) TextView tv_deskripsi;
    @BindView(R.id.btn_ok) MaterialButton btn_ok;

    public static DialogAlertSuccErrInfo newInstance(String type, String judul, String deskripsi) {
        Bundle args = new Bundle();
        args.putString("type", type);
        args.putString("judul", judul);
        args.putString("deskripsi", deskripsi);

        DialogAlertSuccErrInfo fragment = new DialogAlertSuccErrInfo();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            type = getArguments().getString("type");
            judul = getArguments().getString("judul");
            deskripsi = getArguments().getString("deskripsi");
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        v = inflater.inflate(R.layout.dialog_alert_succerr_info, container, false);
        ButterKnife.bind(this,v);

        context = getContext();
        if (type.equals("SUCCESS")){
            ln_icon.setBackgroundColor(getResources().getColor(R.color.green_700));
            imv.setImageDrawable(VectorDrawableCompat.create(getResources(), R.drawable.ic_round_check_24, null));

            btn_ok.setStrokeWidth((int) Config.dpTOpx(1));
            btn_ok.setStrokeColor(ColorStateList.valueOf(getResources().getColor(R.color.green_700)));
            btn_ok.setTextColor(getResources().getColor(R.color.green_700));
        } else if (type.equals("DANGER")){
            ln_icon.setBackgroundColor(getResources().getColor(R.color.red_700));
            imv.setImageDrawable(VectorDrawableCompat.create(getResources(), R.drawable.ic_round_close_24, null));

            btn_ok.setStrokeWidth((int)Config.dpTOpx(1));
            btn_ok.setStrokeColor(ColorStateList.valueOf(getResources().getColor(R.color.red_700)));
            btn_ok.setTextColor(getResources().getColor(R.color.red_700));
        } else if (type.equals("INFO")){
            ln_icon.setBackgroundColor(getResources().getColor(R.color.blue_700));
            imv.setImageDrawable(VectorDrawableCompat.create(getResources(), R.drawable.ic_outline_info_24, null));

            btn_ok.setStrokeWidth((int)Config.dpTOpx(1));
            btn_ok.setStrokeColor(ColorStateList.valueOf(getResources().getColor(R.color.blue_700)));
            btn_ok.setTextColor(getResources().getColor(R.color.blue_700));
        }
        tv_judul.setText(judul);
        tv_deskripsi.setText(deskripsi);

        return v;
    }

    @OnClick(R.id.btn_ok) void OnClick_ok(){
        if (onDialogClickPositive!=null) {
            onDialogClickPositive.onDialogClickPositive();
        }
        dismiss();
    }

    @Override
    public void onStart() {
        super.onStart();
        getDialog().setCancelable(false);
        getDialog().setCanceledOnTouchOutside(false);
        getDialog().getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        getDialog().getWindow().setLayout(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
    }

    public interface OnDialogClickPositive {
        void onDialogClickPositive();
    }

    public void setPositiveButton(final OnDialogClickPositive onDialogClickPositive) {
        this.onDialogClickPositive = onDialogClickPositive;
    }
}
