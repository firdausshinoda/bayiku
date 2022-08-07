package com.example.bayiku.ui;

import android.Manifest;
import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothManager;
import android.bluetooth.le.BluetoothLeScanner;
import android.bluetooth.le.ScanCallback;
import android.bluetooth.le.ScanResult;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;

import com.example.bayiku.R;
import com.example.bayiku.databinding.ActivityMainBinding;
import com.example.bayiku.easyble.BleAdvertisedData;
import com.example.bayiku.easyble.BleDevice;
import com.example.bayiku.easyble.BleManager;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.view.ViewCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.example.bayiku.easyble.BleUtil;
import com.example.bayiku.easyble.Logger;
import com.example.bayiku.easyble.gatt.bean.ServiceInfo;
import com.example.bayiku.easyble.gatt.callback.BleConnectCallback;
import com.example.bayiku.easyble.gatt.callback.BleNotifyCallback;
import com.example.bayiku.ui.main.HomeFragment;
import com.example.bayiku.ui.main.InformasiFragment;
import com.example.bayiku.ui.main.WebFragment;
import com.example.bayiku.utils.ByteUtils;
import com.example.bayiku.utils.Config;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;
import com.google.android.material.shape.CornerFamily;
import com.google.android.material.shape.MaterialShapeDrawable;
import com.google.android.material.shape.ShapeAppearanceModel;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.MultiplePermissionsReport;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.multi.MultiplePermissionsListener;

import android.os.Handler;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MainActivity extends AppCompatActivity implements FragmentManager.OnBackStackChangedListener, NavigationBarView.OnItemSelectedListener {
    private ActivityMainBinding binding;
    private boolean mScanning;
    private Handler mHandler;
    private static final int REQUEST_ENABLE_BT = 1;
    private static final long SCAN_PERIOD = 10000;
    private BluetoothAdapter bluetoothAdapter;
    private BluetoothLeScanner bluetoothLeScanner = null;
    private BluetoothManager bluetoothManager = null;
    private BleManager bleManager;
    private BleDevice bleDevice;
    private ServiceInfo curService;

    private String macAddress = null , deviceName = null;
    private boolean stt_koneksi = false;
    private Menu menu;
    private Context context;
    private HomeFragment homeFragment;
    private WebFragment webFragment;
    private InformasiFragment informasiFragment;
    private FragmentManager fragmentManager;

    @BindView(R.id.nav_view)
    BottomNavigationView nav_view;
    @BindView(R.id.cardNavBottom)
    CardView cardNavBottom;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        setSupportActionBar(binding.toolbar);

        ButterKnife.bind(this);
        context = this;
        if (savedInstanceState == null) {
            homeFragment = new HomeFragment();
            webFragment = new WebFragment();
            informasiFragment = new InformasiFragment();
        }
        initVitew();
        permission();
    }

    private void permission() {
        String[] permission = null;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.S) {
            permission = new String[]{
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_COARSE_LOCATION,
                    Manifest.permission.BLUETOOTH,
                    Manifest.permission.BLUETOOTH_CONNECT,
                    Manifest.permission.BLUETOOTH_SCAN
            };
        } else {
            permission = new String[]{
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_COARSE_LOCATION,
                    Manifest.permission.BLUETOOTH
            };
        }

        Dexter.withContext(context).withPermissions(permission)
                .withListener(new MultiplePermissionsListener() {
            @Override
            public void onPermissionsChecked(MultiplePermissionsReport report) {
                if (report.areAllPermissionsGranted()) {
                    initBLE();
//                    Toast.makeText(context, "Permission terkonfirmasi...", Toast.LENGTH_SHORT).show();
                }
                if (report.isAnyPermissionPermanentlyDenied()) {
                    Log.d("CATATAN","ERROR : "+report.getDeniedPermissionResponses());
//                    Toast.makeText(context, "Silahkan untuk mengkonfirmasi permission...", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onPermissionRationaleShouldBeShown(List<PermissionRequest> permissions, PermissionToken token) {
                token.continuePermissionRequest();
            }
        }).withErrorListener(error -> Toast.makeText(context, "Error occurred! ", Toast.LENGTH_SHORT).show()).onSameThread().check();
    }

    private void initVitew() {
        getSupportActionBar().setDisplayHomeAsUpEnabled(false);
        fragmentManager = getSupportFragmentManager();
        fragmentManager.addOnBackStackChangedListener(this);
        replaceFragment(homeFragment);

        nav_view = findViewById(R.id.nav_view);

        MaterialShapeDrawable materialShapeDrawableCard = new MaterialShapeDrawable();
        ShapeAppearanceModel shapeAppearanceModelCard = materialShapeDrawableCard.getShapeAppearanceModel().toBuilder()
                .setTopLeftCorner(CornerFamily.ROUNDED, Config.dpTOpx(20))
                .setTopRightCorner(CornerFamily.ROUNDED, Config.dpTOpx(20))
                .build();
        materialShapeDrawableCard.setTint(getResources().getColor(R.color.white));
        materialShapeDrawableCard.setShapeAppearanceModel(shapeAppearanceModelCard);
        ViewCompat.setBackground(cardNavBottom, materialShapeDrawableCard);

        MaterialShapeDrawable materialShapeDrawableNav = new MaterialShapeDrawable();
        ShapeAppearanceModel shapeAppearanceModelNav = materialShapeDrawableNav.getShapeAppearanceModel().toBuilder()
                .setTopLeftCorner(CornerFamily.ROUNDED, Config.dpTOpx(20))
                .setTopRightCorner(CornerFamily.ROUNDED, Config.dpTOpx(20))
                .build();
        materialShapeDrawableNav.setTint(getResources().getColor(R.color.white));
        materialShapeDrawableNav.setShapeAppearanceModel(shapeAppearanceModelNav);
        ViewCompat.setBackground(nav_view, materialShapeDrawableNav);
        cardNavBottom.setCardElevation(Config.dpTOpx(10));

        nav_view.setOnItemSelectedListener(this);
    }

    private void initBLE() {
        mHandler = new Handler();
        if (!getPackageManager().hasSystemFeature(PackageManager.FEATURE_BLUETOOTH_LE)) {
            Toast.makeText(this, R.string.ble_not_supported, Toast.LENGTH_SHORT).show();
        } else {
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {
                bluetoothManager = getSystemService(BluetoothManager.class);
            } else {
                bluetoothManager = (BluetoothManager) getSystemService(Context.BLUETOOTH_SERVICE);
            }
            bluetoothAdapter = bluetoothManager.getAdapter();
            if (bluetoothAdapter == null) {
                Toast.makeText(this, R.string.error_bluetooth_not_supported, Toast.LENGTH_SHORT).show();
            } else {
                if (!bluetoothAdapter.isEnabled()) {
                    Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                    btLauncher.launch(enableBtIntent);
                } else {
                    setBLE();
                }
            }
        }
    }

    private void setBLE() {
        bluetoothLeScanner = bluetoothAdapter.getBluetoothLeScanner();
        BleManager.ScanOptions scanOptions = BleManager.ScanOptions.newInstance().scanPeriod(8000)
                .scanDeviceName(null);
        BleManager.ConnectOptions connectOptions = BleManager.ConnectOptions.newInstance()
                .connectTimeout(12000);
        bleManager = BleManager.getInstance().setScanOptions(scanOptions).setConnectionOptions(connectOptions)
                .setLog(true, "EasyBle")
                .init(this.getApplication());
        bleDevice = null;
        scaneBLT();
    }

    @Override
    public boolean onCreateOptionsMenu(@NonNull Menu menu) {
        getMenuInflater().inflate(R.menu.toolbar_main, menu);
        return true;
    }

    @Override
    public boolean onPrepareOptionsMenu(@NonNull Menu menu) {
        this.menu = menu;
        return super.onPrepareOptionsMenu(menu);
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.navigation_home) {
            replaceFragment(homeFragment);
        } else if (id == R.id.navigation_web) {
            replaceFragment(webFragment);
        } else if (id == R.id.navigation_informasi) {
            replaceFragment(informasiFragment);
        }
        return true;
    }

    private void replaceFragment(Fragment fragment) {
        String backStateName = fragment.getClass().getName();
        String fragmentTag = backStateName;

        boolean fragmentPopped = fragmentManager.popBackStackImmediate(backStateName, 0);
        if (!fragmentPopped && fragmentManager.findFragmentByTag(fragmentTag) == null) {
            FragmentTransaction ft = fragmentManager.beginTransaction();
            if (fragment == homeFragment) {
                ft.replace(R.id.nav_host_fragment, fragment, fragmentTag);
            } else {
                ft.add(R.id.nav_host_fragment, fragment, fragmentTag);
            }
            ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
            ft.addToBackStack(backStateName);
            ft.commit();
        }
    }

    @Override
    public void onBackStackChanged() {
        Fragment f = getSupportFragmentManager().findFragmentById(R.id.nav_host_fragment);
        assert f != null;
        if (f.getClass().getName().equals(homeFragment.getClass().getName())) {
            nav_view.getMenu().findItem(R.id.navigation_home).setChecked(true);
            nav_view.getMenu().findItem(R.id.navigation_home).setIcon(R.drawable.ic_round_home_24);
            nav_view.getMenu().findItem(R.id.navigation_web).setIcon(R.drawable.ic_round_web_24);
            nav_view.getMenu().findItem(R.id.navigation_informasi).setIcon(R.drawable.ic_outline_info_24);
        } else if (f.getClass().getName().equals(webFragment.getClass().getName())) {
            nav_view.getMenu().findItem(R.id.navigation_web).setChecked(true);
            nav_view.getMenu().findItem(R.id.navigation_home).setIcon(R.drawable.ic_outline_home_24);
            nav_view.getMenu().findItem(R.id.navigation_web).setIcon(R.drawable.ic_round_web_24);
            nav_view.getMenu().findItem(R.id.navigation_informasi).setIcon(R.drawable.ic_outline_info_24);
        } else if (f.getClass().getName().equals(informasiFragment.getClass().getName())) {
            nav_view.getMenu().findItem(R.id.navigation_informasi).setChecked(true);
            nav_view.getMenu().findItem(R.id.navigation_home).setIcon(R.drawable.ic_outline_home_24);
            nav_view.getMenu().findItem(R.id.navigation_web).setIcon(R.drawable.ic_round_web_24);
            nav_view.getMenu().findItem(R.id.navigation_informasi).setIcon(R.drawable.ic_round_info_24);
        }
    }

    @Override
    public void onBackPressed() {
        if (getSupportFragmentManager().getBackStackEntryCount() == 1) {
            finish();
        } else {
            super.onBackPressed();
        }
    }

    private void scaneBLT() {
        scanLeDevice(true);
    }

    private void scanLeDevice(final boolean enable) {
        if (enable) {
            mHandler.postDelayed(() -> {
                mScanning = false;
                if (ActivityCompat.checkSelfPermission(context, Manifest.permission.BLUETOOTH_SCAN) != PackageManager.PERMISSION_GRANTED) {
                    permission();
                }
                bluetoothLeScanner.stopScan(leScanCallback);
            }, SCAN_PERIOD);

            mScanning = true;
            bluetoothLeScanner.startScan(leScanCallback);
        } else {
            mScanning = false;
            bluetoothLeScanner.stopScan(leScanCallback);
        }
    }

    ActivityResultLauncher<Intent> btLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(), result -> {
                if (result.getResultCode() == Activity.RESULT_OK) {
                    setBLE();
                }
            });

    private ScanCallback leScanCallback = new ScanCallback() {
        @Override
        public void onScanResult(int callbackType, ScanResult result) {
            super.onScanResult(callbackType, result);
            final BleAdvertisedData badata = BleUtil.parseAdertisedData(result.getScanRecord().getBytes());
            String namaDevice = result.getDevice().getName();
            if( namaDevice == null ){
                namaDevice = badata.getName();
            }
            if (namaDevice != null && namaDevice.equals("SENSSUN Growth")) {
//            if (namaDevice != null && namaDevice.equals("LUNAR")) {
                deviceName = namaDevice;
                macAddress = result.getDevice().getAddress();
                BleManager.getInstance().connect(macAddress, connectCallback);
            }
        }
    };

    private BleConnectCallback connectCallback = new BleConnectCallback() {
        @Override
        public void onStart(boolean startConnectSuccess, String info, BleDevice device) {
            Logger.e("start connecting:" + startConnectSuccess + "    info=" + info);
            MainActivity.this.bleDevice = device;
            updateConnectionStateUi(false);
            if (!startConnectSuccess) {
                updateConnectionStateUi(false);
            }
        }

        @Override
        public void onConnected(BleDevice device) {
            updateConnectionStateUi(true);
        }

        @Override
        public void onDisconnected(String info, int status, BleDevice device) {
            updateConnectionStateUi(false);
        }

        @Override
        public void onFailure(int failCode, String info, BleDevice device) {
            Logger.e("connect fail:" + info);
            updateConnectionStateUi(false);
        }
    };

    private void updateConnectionStateUi(boolean connected) {
        if (bleDevice.connected || bleDevice.connecting || connected) {
            Log.d("CATATAN","KONEKSI SUKSES");
            stt_koneksi = true;
            if (this.menu.getItem(0) != null) {
                this.menu.getItem(0).setIcon(ContextCompat.getDrawable(this,R.drawable.ic_round_bluetooth_connected_24));
            }
            BleManager.getInstance().notify(bleDevice, "0000fff0-0000-1000-8000-00805f9b34fb", "0000fff1-0000-1000-8000-00805f9b34fb", notifyCallback);
        } else {
            stt_koneksi = false;
            Log.d("CATATAN","KONEKSI TIDAK");
            if (this.menu.getItem(0) != null) {
                this.menu.getItem(0).setIcon(ContextCompat.getDrawable(this,R.drawable.ic_round_autorenew_24));
            }
            scaneBLT();
        }
        sendBroadcast(connected);
    }

    public void unBindBLE() {
        if (bleDevice != null) {
            BleManager.getInstance().disconnect(macAddress);
        }
    }

    public void searchBLE() {
        scaneBLT();
    }

    public boolean getKoneksiStatus() {
        return stt_koneksi;
    }

    public String getKoneksiNama() {
        return deviceName;
    }

    public String getKoneksiAddress() {
        return macAddress;
    }

    private void sendBroadcast(boolean stt) {
        Intent pushNotification = new Intent("BcKoneksi");
        pushNotification.putExtra("nama_device", deviceName);
        pushNotification.putExtra("mac_address", macAddress);
        pushNotification.putExtra("stt_koneksi", stt);
        LocalBroadcastManager.getInstance(this).sendBroadcast(pushNotification);
    }

    private BleNotifyCallback notifyCallback = new BleNotifyCallback() {
        @Override
        public void onCharacteristicChanged(byte[] data, BleDevice device) {
            String s = ByteUtils.bytes2HexStr(data);
            setDataNotif(s);
        }

        @Override
        public void onNotifySuccess(String notifySuccessUuid, BleDevice device) {
            Logger.e("notify success uuid:" + notifySuccessUuid);
        }

        @Override
        public void onFailure(int failCode, String info, BleDevice device) {
            Logger.e("notify fail:" + info);
        }
    };

    private void setDataNotif(String s) {
        String[] strData = s.split("-");
        if (strData[1].equals("A5")){
            String tmpNum= strData[2]+strData[3];
            String WeightNum = String.valueOf(Integer.valueOf(tmpNum,16));

            tmpNum= strData[6]+strData[7];
            String cmHeightNum = String.valueOf(Integer.valueOf(tmpNum,16));

            String satuan_berat = null, satuan_tinggi = null;

            if ("01-02-03-04-05-06-07-08-09".contains(strData[8])){
                if("02-04".contains(strData[8])){
                    satuan_berat = "Lb";
                    satuan_tinggi = "Inch";
                } else if("06-08".contains(strData[8])){
                    satuan_berat = "Oz";
                    satuan_tinggi = "Inch";
                } else if("07-09".contains(strData[8])){
                    satuan_berat = "Lb Oz";
                    satuan_tinggi = "Inch";
                } else {
                    satuan_berat = "Kg";
                    satuan_tinggi = "Cm";
                }
            }
            if (WeightNum.length() == 4) {
                WeightNum = WeightNum.substring(0,1)+"."+WeightNum.substring(1,4);
            } else if (WeightNum.length() == 5){
                WeightNum = WeightNum.substring(0,2)+"."+WeightNum.substring(2,5);
            } else {
                WeightNum = "0."+WeightNum;
            }
            if (cmHeightNum.length() == 3) {
                cmHeightNum = cmHeightNum.substring(0,2)+"."+cmHeightNum.substring(2,3);
            } else if (cmHeightNum.length() == 2){
                cmHeightNum = cmHeightNum.substring(0,1)+"."+cmHeightNum.substring(1,2);
            } else {
                cmHeightNum = "0."+cmHeightNum;
            }
            Intent pushNotification = new Intent("BcHasil");
            pushNotification.putExtra("berat", WeightNum);
            pushNotification.putExtra("berat_satuan", satuan_berat);
            pushNotification.putExtra("tinggi", cmHeightNum);
            pushNotification.putExtra("tinggi_satuan", satuan_tinggi);
            LocalBroadcastManager.getInstance(this).sendBroadcast(pushNotification);
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (isFinishing() && bleDevice != null) {
            BleManager.getInstance().disconnect(macAddress);
        }
    }
}