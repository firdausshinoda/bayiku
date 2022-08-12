package com.example.bayiku.ui.main;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.example.bayiku.R;
import com.example.bayiku.item.ItemBayi;
import com.example.bayiku.ui.dialog.DialogAlertSuccErrInfo;
import com.example.bayiku.ui.dialog.DialogKonfirmasi;
import com.example.bayiku.utils.Config;
import com.example.bayiku.utils.UserAPIServices;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import okhttp3.MultipartBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class HomeFragment extends Fragment implements TextWatcher, AdapterView.OnItemSelectedListener, AdapterView.OnItemClickListener {
    private Context context;
    private ArrayList<String> list_posyandu = new ArrayList<>();
    private ArrayList<String> list_posyandu_id = new ArrayList<>();
    private ArrayList<ItemBayi> itemBayis = new ArrayList<>();
    private ArrayList<String> listNama = new ArrayList<>();
    private ArrayAdapter<String> cariAdapter;
    private String id, posyandu_id, jk, tgl_lahir, berat = "9", tinggi = "10", keterangan, berat_satuan, tinggi_satuan;
    private boolean stt_show = false;

    @BindView(R.id.tv_berat) TextView tv_berat;
    @BindView(R.id.tv_tinggi) TextView tv_tinggi;
    @BindView(R.id.tv_tgl_lahir) TextView tv_tgl_lahir;
    @BindView(R.id.tv_jk) TextView tv_jk;
    @BindView(R.id.et_keterangan) EditText et_keterangan;
    @BindView(R.id.at_cari) AutoCompleteTextView at_cari;
    @BindView(R.id.sp_posyandu) Spinner sp_posyandu;
    @BindView(R.id.progress_cari) ProgressBar progress_cari;
    @BindView(R.id.toolbar) Toolbar toolbar;

    public HomeFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_home, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        ButterKnife.bind(this, view);
        context = getContext();

        ((AppCompatActivity)getActivity()).setSupportActionBar(toolbar);
        ((AppCompatActivity)getActivity()).getSupportActionBar().setTitle("Bayiku");

        at_cari.addTextChangedListener(this);
        at_cari.setOnItemClickListener(this);
        at_cari.setThreshold(2);

        list_posyandu.add("Kenanga");
        list_posyandu.add("Bougenfil");
        list_posyandu.add("Anggrek");

        list_posyandu_id.add("1");
        list_posyandu_id.add("2");
        list_posyandu_id.add("3");

        sp_posyandu.setAdapter(new ArrayAdapter<String>(context, android.R.layout.simple_spinner_item, list_posyandu));
        sp_posyandu.setOnItemSelectedListener(this);
        LocalBroadcastManager.getInstance(context).registerReceiver(mMessageReceiver, new IntentFilter("BcHasil"));
    }

    private BroadcastReceiver mMessageReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            berat = intent.getStringExtra("berat");
            berat_satuan = intent.getStringExtra("berat_satuan");
            tinggi = intent.getStringExtra("tinggi");
            tinggi_satuan = intent.getStringExtra("tinggi_satuan");
            tv_berat.setText(berat+" "+berat_satuan);
            tv_tinggi.setText(tinggi+" "+tinggi_satuan);
        }
    };

    @Override
    public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

    }

    @Override
    public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

    }

    @Override
    public void afterTextChanged(Editable editable) {
        if (editable.toString().length() > 1) {
            searchName(editable.toString());
        }
    }

    private void searchName(String cari) {
        progress_cari.setVisibility(View.VISIBLE);
        MultipartBody.Builder builder = new MultipartBody.Builder();
        builder.setType(MultipartBody.FORM);
        builder.addFormDataPart("nama",cari.split("\n")[0]);
        builder.addFormDataPart("posyandu_id",posyandu_id);
        MultipartBody requestBody = builder.build();

        UserAPIServices api = Config.getRetrofit(Config.BASE_URL).create(UserAPIServices.class);
        Call<ResponseBody> post = api.search_nama(requestBody);
        post.enqueue(new Callback<ResponseBody>(){
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                progress_cari.setVisibility(View.GONE);
                try {
                    if (response.isSuccessful()) {
                        if (response.body() != null) {
                            String json  = response.body().string();
                            if (!json.equals("null")){
                                JSONObject jsonObj = new JSONObject(json);
                                String status = jsonObj.getString("status");
                                String message = jsonObj.getString("message");
                                if (status.equals("success")){
                                    JSONObject jsonObject = jsonObj.getJSONObject("result");
                                    JSONArray jsonObjJSONArray = jsonObject.getJSONArray("datas");
                                    listNama.clear();
                                    itemBayis.clear();
                                    for(int i=0; i < jsonObjJSONArray.length(); i++) {
                                        JSONObject c = jsonObjJSONArray.getJSONObject(i);
                                        String id = c.getString("id");
                                        String nama = c.getString("nama");
                                        String tanggal_lahir = c.getString("tanggal_lahir");
                                        String jk = c.getString("jk");
                                        listNama.add(nama+"\n"+Config.setTglIndo(tanggal_lahir));
                                        itemBayis.add(new ItemBayi(id, nama, tanggal_lahir, jk));
                                    }
                                    cariAdapter = new ArrayAdapter<>(context, android.R.layout.simple_list_item_1, listNama);
                                    at_cari.setAdapter(cariAdapter);
                                } else if (status.equals("warning")){
                                    DialogAlertSuccErrInfo.newInstance("DANGER","Peringatan", message).show(getChildFragmentManager(),null);
                                } else {
                                    DialogAlertSuccErrInfo.newInstance("DANGER","Gagal", message).show(getChildFragmentManager(),null);
                                }
                            }
                        }
                    } else {
                        if (response.errorBody() != null) {
                            String json  = response.errorBody().string();
                            if (!json.equals("null")){
                                JSONObject jsonObj = new JSONObject(json);
                                String status = jsonObj.getString("status");
                                String message = jsonObj.getString("message");
                                if (status.equals("warning")){
                                    if (!stt_show) {
                                        DialogAlertSuccErrInfo dialog = DialogAlertSuccErrInfo.newInstance("DANGER","Peringatan", message);
                                        dialog.setPositiveButton(() -> stt_show = false);
                                        dialog.show(getChildFragmentManager(),null);
                                        stt_show = true;
                                    }
                                } else {
                                    DialogAlertSuccErrInfo.newInstance("DANGER","Peringatan", message).show(getChildFragmentManager(),null);
                                }
                            }
                        }
                    }

                } catch (IOException | JSONException e) {
                    Toast.makeText(getContext(),"TIDAK DAPAT MENGAMBIL DATA", Toast.LENGTH_LONG).show();
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                progress_cari.setVisibility(View.GONE);
                Toast.makeText(getContext(),"TIDAK DAPAT MENGAMBIL DATA", Toast.LENGTH_LONG).show();
            }
        });
    }

    @OnClick(R.id.btn_simpan) void OnClick_btn_simpan() {
        tv_jk.setError(null);
        tv_tgl_lahir.setError(null);
        tv_berat.setError(null);
        tv_tinggi.setError(null);
        et_keterangan.setError(null);

        jk = tv_jk.getText().toString();
        tgl_lahir = tv_tgl_lahir.getText().toString();
        String beratKomplit = tv_berat.getText().toString();
        String tinggiKomplit = tv_tinggi.getText().toString();
        keterangan = et_keterangan.getText().toString();

        boolean cancel = false;
        View focusView = null;

        if (TextUtils.isEmpty(jk)) {
            tv_jk.setError("Silahkan cari terlebih dahulu...");
            focusView = tv_jk;
            cancel = true;
        }
        if (TextUtils.isEmpty(tgl_lahir)) {
            tv_tgl_lahir.setError("Silahkan cari terlebih dahulu...");
            focusView = tv_tgl_lahir;
            cancel = true;
        }
        if (TextUtils.isEmpty(beratKomplit)) {
            tv_berat.setError("Silahkan sambungkan ke alat onemed...");
            focusView = tv_berat;
            cancel = true;
        }
        if (TextUtils.isEmpty(tinggiKomplit)) {
            tv_tinggi.setError("Silahkan sambungkan ke alat onemed...");
            focusView = tv_tinggi;
            cancel = true;
        }
        if (TextUtils.isEmpty(keterangan)) {
            et_keterangan.setError("Silahkan diisi...");
            focusView = et_keterangan;
            cancel = true;
        }
        if (cancel) {
            focusView.requestFocus();
        } else {
            DialogKonfirmasi dialogKonfirmasi = DialogKonfirmasi.newInstance("Info", "Apakah anda yakin akan menyimpan data?");
            dialogKonfirmasi.show(getChildFragmentManager(),null);
            dialogKonfirmasi.setPositiveButton(() -> sendData());
            dialogKonfirmasi.setNegativeButton(() -> {
                dialogKonfirmasi.dismiss();
            });
        }
    }

    private void sendData() {
        final ProgressDialog pDialog = new ProgressDialog(context);
        pDialog.setMessage("Tunggu sebentar...");
        pDialog.setIndeterminate(false);
        pDialog.setCancelable(false);
        pDialog.show();

        MultipartBody.Builder builder = new MultipartBody.Builder();
        builder.setType(MultipartBody.FORM);
        builder.addFormDataPart("bayi_id",id);
        builder.addFormDataPart("posyandu_id",posyandu_id);
        builder.addFormDataPart("berat",berat);
        builder.addFormDataPart("tinggi",tinggi);
        builder.addFormDataPart("keterangan",keterangan);
        builder.addFormDataPart("tgl_timbang",Config.getDate());
        MultipartBody requestBody = builder.build();

        UserAPIServices api = Config.getRetrofit(Config.BASE_URL).create(UserAPIServices.class);
        Call<ResponseBody> post = api.saveBobot(requestBody);
        post.enqueue(new Callback<ResponseBody>(){
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                pDialog.dismiss();
                try {
                    String json  = response.body().string();
                    if (!json.equals("null")){
                        JSONObject jsonObj = new JSONObject(json);
                        String status = jsonObj.getString("status");
                        String message = jsonObj.getString("message");
                        if (status.equals("success")){
                            DialogAlertSuccErrInfo dialogAlertSuccErrInfo = DialogAlertSuccErrInfo.newInstance("SUCCESS","INFO", message);
                            dialogAlertSuccErrInfo.show(getChildFragmentManager(),null);
                            dialogAlertSuccErrInfo.setPositiveButton(() -> {
                                at_cari.setText("");
                                clearForm();
                            });
                        } else if (status.equals("warning")){
                            clearForm();
                            DialogAlertSuccErrInfo.newInstance("DANGER","Peringatan", message).show(getChildFragmentManager(),null);
                        } else {
                            DialogAlertSuccErrInfo.newInstance("DANGER","Gagal", message).show(getChildFragmentManager(),null);
                            clearForm();
                        }
                    }
                } catch (IOException | JSONException e) {
                    DialogAlertSuccErrInfo.newInstance("DANGER","Gagal", "Terjadi kesalahan pada server.").show(getChildFragmentManager(),null);
                    e.printStackTrace();
                    clearForm();
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                pDialog.dismiss();
                DialogAlertSuccErrInfo.newInstance("DANGER","Gagal", "Tidak dapat mengirim data.").show(getChildFragmentManager(),null);
                clearForm();
            }
        });
    }

    private void clearForm() {
        tv_jk.setText("");
        tv_tgl_lahir.setText("");
        tv_berat.setText("");
        tv_tinggi.setText("");
        et_keterangan.setText("");
    }

    @Override
    public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
        this.posyandu_id = list_posyandu_id.get(i);
    }

    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {
        this.posyandu_id = list_posyandu_id.get(0);
    }

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
        at_cari.setText(itemBayis.get(i).getNama());
        tv_tgl_lahir.setText(Config.setTglIndo(itemBayis.get(i).getTgl_lahir()));
        tv_jk.setText(itemBayis.get(i).getJk().toUpperCase(Locale.ROOT));
        id = itemBayis.get(i).getId();
    }
}