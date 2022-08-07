package com.example.bayiku.ui.dialog;

import android.content.Context;
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

import com.example.bayiku.R;
import com.google.android.material.button.MaterialButton;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class DialogKonfirmasi extends DialogFragment {

    private View v;
    private Context context;
    private String deskripsi, judul;
    public OnDialogClickPositive onDialogClickPositive;
    public OnDialogClickNegative onDialogClickNegative;

    @BindView(R.id.ln_icon) LinearLayout ln_icon;
    @BindView(R.id.imv) ImageView imv;
    @BindView(R.id.tv_judul) TextView tv_judul;
    @BindView(R.id.tv_deskripsi) TextView tv_deskripsi;
    @BindView(R.id.btn_ya) MaterialButton btn_ya;
    @BindView(R.id.btn_tidak) MaterialButton btn_tidak;

    public static DialogKonfirmasi newInstance(String judul, String deskripsi) {
        Bundle args = new Bundle();
        args.putString("judul", judul);
        args.putString("deskripsi", deskripsi);

        DialogKonfirmasi fragment = new DialogKonfirmasi();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            judul = getArguments().getString("judul");
            deskripsi = getArguments().getString("deskripsi");
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        v = inflater.inflate(R.layout.dialog_konfirmasi, container, false);
        ButterKnife.bind(this,v);

        context = getContext();
        tv_judul.setText(judul);
        tv_deskripsi.setText(deskripsi);

        return v;
    }

    @OnClick(R.id.btn_ya) void btn_ya(){
        if (onDialogClickPositive!=null) {
            onDialogClickPositive.onDialogClickPositive();
        }
        dismiss();
    }

    @OnClick(R.id.btn_tidak) void btn_tidak(){
        if (onDialogClickNegative!=null) {
            onDialogClickNegative.onDialogClickNegative();
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

    public interface OnDialogClickNegative {
        void onDialogClickNegative();
    }

    public void setPositiveButton(final OnDialogClickPositive onDialogClickPositive) {
        this.onDialogClickPositive = onDialogClickPositive;
    }

    public void setNegativeButton(final OnDialogClickNegative onDialogClickNegative) {
        this.onDialogClickNegative = onDialogClickNegative;
    }
}
