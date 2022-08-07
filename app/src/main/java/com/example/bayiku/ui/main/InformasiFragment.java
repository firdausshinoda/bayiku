package com.example.bayiku.ui.main;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.example.bayiku.R;
import com.example.bayiku.ui.MainActivity;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class InformasiFragment extends Fragment {
    private Context context;
    private String nama_device, mac_address;
    private boolean stt_koneksi = false;

    @BindView(R.id.tv_mac_address) TextView tv_mac_address;
    @BindView(R.id.tv_nama) TextView tv_nama;
    @BindView(R.id.tv_stt_konek) TextView tv_stt_konek;
    @BindView(R.id.btn_unbind) Button btn_unbind;
    @BindView(R.id.btn_cari) Button btn_cari;

    public InformasiFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_informasi, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        ButterKnife.bind(this, view);
        context = getContext();

        LocalBroadcastManager.getInstance(context).registerReceiver(mMessageReceiver, new IntentFilter("BcKoneksi"));
    }

    private BroadcastReceiver mMessageReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            nama_device = intent.getStringExtra("nama_device");
            mac_address = intent.getStringExtra("mac_address");
            stt_koneksi = intent.getBooleanExtra("stt_koneksi", false);

            tv_stt_konek.setText(stt_koneksi ? "TERKONEKSI" : "TERPUTUS");
            if (stt_koneksi) {
                btn_cari.setVisibility(View.GONE);
                btn_unbind.setVisibility(View.VISIBLE);
                tv_mac_address.setText(mac_address);
                tv_nama.setText(nama_device);
            } else {
                btn_cari.setVisibility(View.VISIBLE);
                btn_unbind.setVisibility(View.GONE);
                tv_mac_address.setText("");
                tv_nama.setText("");
            }
        }
    };

    @OnClick(R.id.btn_cari) void OnClick_btn_cari() {
        ((MainActivity)getActivity()).searchBLE();
    }

    @OnClick(R.id.btn_unbind) void OnClick_btn_unbind() {
        ((MainActivity)getActivity()).unBindBLE();
    }

    @Override
    public void onResume() {
        super.onResume();
        mac_address = ((MainActivity)getActivity()).getKoneksiAddress();
        nama_device = ((MainActivity)getActivity()).getKoneksiNama();
        stt_koneksi = ((MainActivity)getActivity()).getKoneksiStatus();

        tv_stt_konek.setText(stt_koneksi ? "TERKONEKSI" : "TERPUTUS");
        if (stt_koneksi) {
            btn_cari.setVisibility(View.GONE);
            btn_unbind.setVisibility(View.VISIBLE);
            tv_mac_address.setText(mac_address);
            tv_nama.setText(nama_device);
        } else {
            btn_cari.setVisibility(View.VISIBLE);
            btn_unbind.setVisibility(View.GONE);
            tv_mac_address.setText("");
            tv_nama.setText("");
        }
    }
}