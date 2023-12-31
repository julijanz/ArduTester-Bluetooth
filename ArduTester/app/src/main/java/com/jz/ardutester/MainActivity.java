package com.jz.ardutester;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;

import android.text.Html;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.jz.ardutester.bt.BluetoothDeviceListDialog;
import com.jz.ardutester.bt.BluetoothSerial;
import com.jz.ardutester.bt.BluetoothSerialListener;
import com.jz.ardutester.db.DaoSettings;
import com.jz.ardutester.utils.CircularProgressView;
import com.jz.ardutester.utils.Common;
import com.jz.ardutester.utils.DialogMsg;

import java.util.ArrayList;
import java.util.List;

import pub.devrel.easypermissions.AfterPermissionGranted;
import pub.devrel.easypermissions.AppSettingsDialog;
import pub.devrel.easypermissions.EasyPermissions;

public class MainActivity extends AppCompatActivity implements BluetoothSerialListener, BluetoothDeviceListDialog.OnDeviceSelectedListener,
        EasyPermissions.PermissionCallbacks, EasyPermissions.RationaleCallbacks
{
    private static final String TAG = "MainActivity";
    private static final String[] BLUETOOTH_ALL = {Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.BLUETOOTH, Manifest.permission.BLUETOOTH_SCAN, Manifest.permission.BLUETOOTH_CONNECT};

    private static final int RC_BLUETOOTH_ALL = 124;

    public static final int STATE_NOT_ENABLED = -1;

    public static enum ImagePng
    {
        unknown, triac, transistor_pnp, transistor_npn, thyristor, resistor, potentiometer,
        mosfet_pch_em, mosfet_pch_dm, mosfet_nch_em, mosfet_nch_dm, jfet_pch, jfet_nch, inductor, capacitor, diode_left, diode_right
    };

    private MenuItem menuItemConnect;
    private MenuItem menuItemDisconnect;
    private boolean isValidElement;

    private BluetoothSerial bluetoothSerial;
    private int requestCode;
    private int resultCode;
    private Intent data;

    private TextView tv1;
    private TextView tv2;
    private TextView tv3;
    private TextView tv4;
    private TextView tv5;
    private TextView pin1;
    private TextView pin2;
    private TextView pin3;
    private TextView pin4;
    private TextView pin5;
    private TextView pin6;
    private ImageView iv_component;
    private TextView tvInfo;
    private TextView tvMeritev;
    private ScrollView svInfo;
    CircularProgressView cpv;

    private String appName;
    private String appVersion;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_activity);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        appName = getResources().getString(R.string.app_name);
        appVersion = String.valueOf(BuildConfig.VERSION_NAME);

        this.setTitle(appName + " " + appVersion);

        ApplicationArduTester.ReadSettings();

        cpv = (CircularProgressView) findViewById(R.id.cpv);

        svInfo = (ScrollView) findViewById(R.id.svInfo);

        tvInfo = (TextView) findViewById(R.id.tvInfo);
        tvInfo.setText("");
        tvInfo.setOnLongClickListener(new View.OnLongClickListener()
        {
            @Override
            public boolean onLongClick(View v)
            {
                // TODO Auto-generated method stub
                tvInfo.setText("");
                return false;
            }
        });

        tvMeritev = (TextView) findViewById(R.id.tvMeasure);
        tv1 = (TextView) findViewById(R.id.tv1);
        tv2 = (TextView) findViewById(R.id.tv2);
        tv3 = (TextView) findViewById(R.id.tv3);
        tv4 = (TextView) findViewById(R.id.tv4);
        tv5 = (TextView) findViewById(R.id.tv5);
        pin1 = (TextView) findViewById(R.id.pin1);
        pin2 = (TextView) findViewById(R.id.pin2);
        pin3 = (TextView) findViewById(R.id.pin3);
        pin4 = (TextView) findViewById(R.id.pin4);
        pin5 = (TextView) findViewById(R.id.pin5);
        pin6 = (TextView) findViewById(R.id.pin6);
        pin1.setText("");
        pin2.setText("");
        pin3.setText("");
        pin4.setText("");
        pin5.setText("");
        pin6.setText("");

        iv_component = (ImageView) findViewById(R.id.iv_component);

        HideAll(false);

        // Create a new instance of BluetoothSerial
        bluetoothSerial = null;
        bluetoothAllTask();

        tv1.setText(appName);
        tv2.setText(appVersion);

    }

    private void HideAll(boolean progressVisible)
    {
        isValidElement = false;
        tvMeritev.setText("");
        tv1.setText("");
        tv2.setText("");
        tv3.setText("");
        tv4.setText("");
        tv5.setText("");
        tvInfo.setText("");
        pin1.setVisibility(View.INVISIBLE);
        pin2.setVisibility(View.INVISIBLE);
        pin3.setVisibility(View.INVISIBLE);
        pin4.setVisibility(View.INVISIBLE);
        pin5.setVisibility(View.INVISIBLE);
        pin6.setVisibility(View.INVISIBLE);
        if (progressVisible)
            cpv.setVisibility(View.VISIBLE);
        else
            cpv.setVisibility(View.INVISIBLE);
        iv_component.setVisibility(View.INVISIBLE);
    }

    private void ShowPng(ImagePng png)
    {
        cpv.setVisibility(View.INVISIBLE);
        String imageName;
        switch(png)
        {
            case capacitor: imageName = "el_capacitor"; break;
            case diode_left: imageName = "el_diode_left"; break;
            case diode_right: imageName = "el_diode_right"; break;
            case inductor: imageName = "el_inductor"; break;
            case jfet_nch: imageName = "el_jfet_nch"; break;
            case jfet_pch: imageName = "el_jfet_pch"; break;
            case mosfet_nch_dm: imageName = "el_mosfet_nch_dm"; break;
            case mosfet_nch_em: imageName = "el_mosfet_nch_em"; break;
            case mosfet_pch_dm: imageName = "el_mosfet_pch_dm"; break;
            case mosfet_pch_em: imageName = "el_mosfet_pch_em"; break;
            case potentiometer: imageName = "el_potentiometer"; break;
            case resistor: imageName = "el_resistor"; break;
            case thyristor: imageName = "el_thyristor"; break;
            case transistor_npn: imageName = "el_transistor_npn"; break;
            case transistor_pnp: imageName = "el_transistor_pnp"; break;
            case triac: imageName = "el_triac"; break;
            default: imageName = "el_unknown";
        }
        //@SuppressLint("DiscouragedApi")
        int res = getResources().getIdentifier(imageName, "drawable", this.getPackageName());
        iv_component.setImageResource(res);
        iv_component.setVisibility(View.VISIBLE);
        if (png == ImagePng.unknown)
            ApplicationArduTester.BeepError();
        else
            ApplicationArduTester.Beep();
    }

    private void OpenBluetooth()
    {
        // Use this check to determine whether Bluetooth classic is supported on the device.
        // Then you can selectively disable BLE-related features.
        boolean bluetoothAvailable = getPackageManager().hasSystemFeature(PackageManager.FEATURE_BLUETOOTH);
        if (bluetoothAvailable)
        {
            bluetoothSerial = new BluetoothSerial(MainActivity.this, MainActivity.this);
            if (!bluetoothSerial.isBluetoothEnabled())
            {
                EnableBluetooth();
            }
            else
            {
                AutoBluetoohConnect();
            }
        }
    }

    private boolean hasBluetoothAllPermissions()
    {
        return EasyPermissions.hasPermissions(this,  BLUETOOTH_ALL);
    }

    @AfterPermissionGranted(RC_BLUETOOTH_ALL)
    public void bluetoothAllTask()
    {
        if (hasBluetoothAllPermissions())
        {
            //Have permissions, do the thing!
            OpenBluetooth();
        } else
        {
            // Ask for both permissions
            EasyPermissions.requestPermissions(this, "Bluetooth permissions", RC_BLUETOOTH_ALL, BLUETOOTH_ALL);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults)
    {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        // EasyPermissions handles the request result.
        EasyPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults, this);
    }

    @Override
    public void onPermissionsGranted(int requestCode, @NonNull List<String> perms)
    {
        //Log.d(TAG, "onPermissionsGranted:" + requestCode + ":" + perms.size());
    }

    @Override
    public void onPermissionsDenied(int requestCode, @NonNull List<String> perms)
    {
        //Log.d(TAG, "onPermissionsDenied:" + requestCode + ":" + perms.size());
        // (Optional) Check whether the user denied any permissions and checked "NEVER ASK AGAIN."
        // This will display a dialog directing them to enable the permission in app settings.
        if (EasyPermissions.somePermissionPermanentlyDenied(this, perms)) {
            new AppSettingsDialog.Builder(this).build().show();
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK)
        {
            //
        }
    }

    @Override
    public void onRationaleAccepted(int requestCode)
    {
        //Log.d(TAG, "onRationaleAccepted:" + requestCode);
    }

    @Override
    public void onRationaleDenied(int requestCode)
    {
        //Log.d(TAG, "onRationaleDenied:" + requestCode);
    }

    private void AutoBluetoohConnect()
    {
        if (bluetoothSerial == null)
        {
            return;
        }
        bluetoothSerial.setup();
        bluetoothSerial.start();
        String macAddress = new DaoSettings().getMacAddress();
        if (!macAddress.trim().equals(""))
        {
            try
            {
                bluetoothSerial.connect(macAddress);
            } catch (Exception e)
            {
                Toast.makeText(MainActivity.this, getResources().getString(R.string.bluetooth_select_device), Toast.LENGTH_SHORT).show();
            }
        }
    }

    public void EnableBluetooth()
    {
        Intent enableBluetooth = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
        // Always return true for SDK < M, let the system deal with the permissions
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M)
        {
            if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.BLUETOOTH_CONNECT) != PackageManager.PERMISSION_GRANTED)
            {
                return;
            }
        }
        startActivityForResult(enableBluetooth, Common.REQUEST_ENABLE_BLUETOOTH);
    }

    private void showDeviceListDialog()
    {
        if (bluetoothSerial == null)
        {
            return;
        }
        // Display dialog for selecting a remote Bluetooth device
        BluetoothDeviceListDialog dialog = new BluetoothDeviceListDialog(this);
        dialog.setOnDeviceSelectedListener(this);
        dialog.setTitle(getResources().getString(R.string.bluetooth_select_device));
        dialog.setDevices(bluetoothSerial.getPairedDevices());
        dialog.showAddress(true);
        dialog.show();
    }

    @Override
    public void onBluetoothNotSupported()
    {
        new AlertDialog.Builder(this)
                .setCancelable(false)
                .setTitle(getResources().getString(R.string.bluetooth_warning))
                .setMessage(getResources().getString(R.string.bluetooth_missing))
                .setPositiveButton( getResources().getString(R.string.btn_close) , new DialogInterface.OnClickListener()
                {
                    @Override
                    public void onClick(DialogInterface dialog, int which)
                    {
                        finish();
                    }
                }).setCancelable(false).show();
    }

    @Override
    public void onBluetoothDisabled()
    {
        UpdateBluetoothState();
    }

    @Override
    public void onBluetoothDeviceDisconnected()
    {
        UpdateBluetoothState();
    }

    @Override
    public void onConnectingBluetoothDevice()
    {
        UpdateBluetoothState();
    }

    @Override
    public void onBluetoothDeviceConnected(String name, String address)
    {
        UpdateBluetoothState();
        new DaoSettings().setMacAddress(address);
        ApplicationArduTester.UpdateMacAddress();
    }

    @Override
    public void onBluetoothDeviceSelected(BluetoothDevice device)
    {
        if (bluetoothSerial == null)
        {
            return;
        }
        bluetoothSerial.connect(device);
    }

    @Override
    public void onBluetoothSerialWrite(String message)
    {
        //Print the outgoing message on the terminal screen
    }

    @Override
    protected void onDestroy()
    {
        if (bluetoothSerial == null)
        {
            return;
        }
        bluetoothSerial.stop();
        super.onDestroy();
    }

    @Override
    protected void onStart()
    {
        super.onStart();
    }

    @Override
    protected void onResume()
    {
        super.onResume();
    }

    @Override
    protected void onStop()
    {
        super.onStop();
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        menuItemConnect = menu.findItem(R.id.action_connect);
        menuItemDisconnect = menu.findItem(R.id.action_disconnect);
        UpdateBluetoothState();
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        if (bluetoothSerial == null)
        {
            return false;
        }
        int id = item.getItemId();
        if (id == R.id.action_connect)
        {
            //connect bluetooth
            if (!bluetoothSerial.isBluetoothEnabled())
            {
                EnableBluetooth();
            }
            else
            {
                showDeviceListDialog();
            }
            return true;
        }
        else if (id == R.id.action_disconnect)
        {
            bluetoothSerial.stop();
            UpdateBluetoothState();
            return true;
        }
        else if (id == R.id.action_info)
        {
            DialogMsg.Info(MainActivity.this, getResources().getString(R.string.str_bluetooth_info)  +" " + getResources().getString(R.string.btn_connect) + ".");
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void invalidateOptionsMenu()
    {
        //
    }

    public void setMenuItems(boolean actConnect, boolean actDisconect)
    {
        if (menuItemConnect != null)
        {
            menuItemConnect.setVisible(actConnect);
        }
        if (menuItemDisconnect != null)
        {
            menuItemDisconnect.setVisible(actDisconect);
        }
    }

    public void UpdateBluetoothState()
    {
        boolean lBluetoothEnabled = (bluetoothSerial != null) && (bluetoothSerial.isBluetoothEnabled());
        // Get the current Bluetooth state
        final int state;
        if (!lBluetoothEnabled)
            state = STATE_NOT_ENABLED;
        else if (bluetoothSerial != null)
            state = bluetoothSerial.getState();
        else
            state = BluetoothSerial.STATE_DISCONNECTED;

        // Display the current state on the app bar as the subtitle
        CharSequence subtitle;
        switch (state)
        {
            case STATE_NOT_ENABLED:
                cpv.setVisibility(View.INVISIBLE);
                subtitle = getResources().getString(R.string.bluetooth_not_enabled);
                subtitle = Html.fromHtml("<font color='#E3E3E3'><b>" + subtitle + "</b></font>");
                setMenuItems(true, false);
                break;
            case BluetoothSerial.STATE_CONNECTING:
                HideAll(true);
                tv1.setText(appName);
                tv2.setText(appVersion);
                subtitle = getResources().getString(R.string.bluetooth_connecting);
                subtitle = Html.fromHtml("<font color='#FF9800'><b>" + subtitle + "</b></font>");
                setMenuItems(false, false);
                break;
            case BluetoothSerial.STATE_CONNECTED:
                cpv.setVisibility(View.INVISIBLE);
                subtitle = getResources().getString(R.string.bluetooth_connected) + " " + bluetoothSerial.getConnectedDeviceName();
                subtitle = Html.fromHtml("<font color='#96FF1D'><b>" + subtitle + "</b></font>");
                setMenuItems(false, true);
                break;
            default:
                cpv.setVisibility(View.INVISIBLE);
                subtitle = getResources().getString(R.string.bluetooth_not_connected);
                subtitle = Html.fromHtml("<font color='#FF0000'><b>" + subtitle + "</b></font>");
                setMenuItems(true, false);
                break;
        }
        if (getSupportActionBar() != null)
        {
            getSupportActionBar().setSubtitle(subtitle);
        }
    }

    //RECEIVED
    @Override
    public void onBluetoothSerialRead(String message)
    {
        if (!(tv1.getText().toString().trim().equals("")))
        {
            isValidElement = false;
            HideAll(true);
        }
        boolean isEnd = message.contains("#");
        message = message.replace("#", " ");
        message = message.replace("�", " ");
        message = message.replace("^M", " ");
        tvInfo.setText(tvInfo.getText().toString().concat(message));
        svInfo.fullScroll(View.FOCUS_DOWN);
        tvMeritev.setText(tvMeritev.getText().toString().concat(message));
        if (isEnd)
        {
            scrollDown();
            ShowMeritev();
            UpdateBluetoothState();
        }
    }

    private void ShowMeritev()
    {
        String line2 = "";
        String line3 = "";
        String line4 = "";
        String line5 = "";
        int tvi = 1;
        List<CharSequence> lines = new ArrayList<>();
        int count = tvMeritev.getLineCount();
        for (int line = 0; line < count; line++)
        {
            int start = tvMeritev.getLayout().getLineStart(line);
            int end = tvMeritev.getLayout().getLineEnd(line);
            String s = tvMeritev.getText().subSequence(start, end).toString().trim();
            if (s.trim().equals(""))
                continue;
            if (s.trim().contains("Testing"))
                continue;
            tvi = tvi + 1;
            if (tvi == 2)
            {
                line2 = s;
                tv2.setText(s);
            }
            if (tvi == 3)
            {
                line3 = s;
                tv3.setText(s);
            }
            if (tvi == 4)
            {
                line4 = s;
                tv4.setText(s);
            }
            if (tvi == 5)
            {
                line5 = s;
                tv5.setText(s);
            }
            if (!isValidElement)
                CheckUnknown(s);
            if (!isValidElement)
                CheckDiode(s);
            if (!isValidElement)
                CheckInductor(s);
            if (!isValidElement)
                CheckPotentiometer(s);
            if (!isValidElement)
                CheckResistor(s);
            if (!isValidElement)
                CheckTransistor(s, line2);
            if (!isValidElement)
                CheckCapacitor(s);
            if (!isValidElement)
                CheckTriac(s, line2);
            if (!isValidElement)
                CheckMosfet(s, line2);
            if (!isValidElement)
                CheckThyristor(s, line2);
            if (!isValidElement)
                CheckJFet(s, line2);
        }
        if ((!isValidElement) && tv1.getText().toString().trim().equals(""))
        {
            cpv.setVisibility(View.INVISIBLE);
            tv1.setText("-");
            ApplicationArduTester.BeepError();
        }
    }

    private void CheckUnknown(String s)
    {
        isValidElement = s.contains("unknown") || s.contains("damaged");
        if (!isValidElement)
            return;
        tv1.setText(getResources().getString(R.string.str_error));
        ShowPng(ImagePng.unknown);
    }

    private void CheckDiode(String s)
    {
        boolean diode_right = s.contains("->|-");
        boolean diode_left = s.contains("-|<-");
        isValidElement = diode_right || diode_left;
        if (!isValidElement)
            return;
        tv1.setText(getResources().getString(R.string.str_diode));
        if (diode_right)
            ShowPng(ImagePng.diode_right);
        if (diode_left)
            ShowPng(ImagePng.diode_left);
        pin2.setText(Common.Copy(s, 1, 1));
        pin2.setVisibility(View.VISIBLE);
        pin5.setText(Common.Copy(s, 6, 1));
        pin5.setVisibility(View.VISIBLE);
    }

    private void CheckInductor(String s)
    {
        isValidElement = s.contains("-RR-WW-");
        if (!isValidElement)
            return;
        tv1.setText(getResources().getString(R.string.str_inductor));
        ShowPng(ImagePng.inductor);
        pin2.setText(Common.Copy(s, 1, 1));
        pin2.setVisibility(View.VISIBLE);
        pin5.setText(Common.Copy(s, 9, 1));
        pin5.setVisibility(View.VISIBLE);
    }

    private void CheckPotentiometer(String s)
    {
        isValidElement = s.contains("-RR-1-RR-") || s.contains("-RR-2-RR-") || s.contains("-RR-3-RR-");
        if (!isValidElement)
            return;
        tv1.setText(getResources().getString(R.string.str_potentiometer));
        ShowPng(ImagePng.potentiometer);
        pin2.setText(Common.Copy(s, 1, 1));
        pin2.setVisibility(View.VISIBLE);
        pin5.setText(Common.Copy(s, 11, 1));
        pin5.setVisibility(View.VISIBLE);
        pin3.setText(Common.Copy(s, 6, 1)) ;
        pin3.setVisibility(View.VISIBLE);
    }

    private void CheckResistor(String s)
    {
        isValidElement = s.contains("-RR-");
        if (!isValidElement)
            return;
        tv1.setText(getResources().getString(R.string.str_resistor));
        ShowPng(ImagePng.resistor);
        pin2.setText(Common.Copy(s, 1, 1));
        pin2.setVisibility(View.VISIBLE);
        pin5.setText(Common.Copy(s, 6, 1));
        pin5.setVisibility(View.VISIBLE);
    }

    private void CheckTransistor(String s, String line2)
    {
        boolean isPNP = line2.contains("PNP") && s.contains("123");
        boolean isNPN = line2.contains("NPN") && s.contains("123");
        isValidElement = isPNP | isNPN;
        if (!isValidElement)
            return;
        tv1.setText(getResources().getString(R.string.str_transisotr));
        if (isPNP)
            ShowPng(ImagePng.transistor_pnp);
        if (isNPN)
            ShowPng(ImagePng.transistor_npn);
        s = s.trim();
        if (Common.Copy(s, 5, 1).equalsIgnoreCase("B")){pin2.setText("1");}
        if (Common.Copy(s, 5, 1).equalsIgnoreCase("C")){pin4.setText("1");}
        if (Common.Copy(s, 5, 1).equalsIgnoreCase("E")){pin6.setText("1");}
        if (Common.Copy(s, 6, 1).equalsIgnoreCase("B")){pin2.setText("2");}
        if (Common.Copy(s, 6, 1).equalsIgnoreCase("C")){pin4.setText("2");}
        if (Common.Copy(s, 6, 1).equalsIgnoreCase("E")){pin6.setText("2");}
        if (Common.Copy(s, 7, 1).equalsIgnoreCase("B")){pin2.setText("3");}
        if (Common.Copy(s, 7, 1).equalsIgnoreCase("C")){pin4.setText("3");}
        if (Common.Copy(s, 7, 1).equalsIgnoreCase("E")){pin6.setText("3");}
        pin2.setVisibility(View.VISIBLE);
        pin4.setVisibility(View.VISIBLE);
        pin6.setVisibility(View.VISIBLE);
    }

    private void CheckCapacitor(String s)
    {
        isValidElement = s.contains("-||-");
        if (!isValidElement)
            return;
        tv1.setText(getResources().getString(R.string.str_capacitor));
        ShowPng(ImagePng.capacitor);
        pin2.setText(Common.Copy(s, 1, 1));
        pin2.setVisibility(View.VISIBLE);
        pin5.setText(Common.Copy(s, 6, 1));
        pin5.setVisibility(View.VISIBLE);
    }

    private void CheckTriac(String s, String line2)
    {
        s = s.trim();
        isValidElement = line2.contains("Triac") && s.contains("123");
        if (!isValidElement)
            return;
        tv1.setText(getResources().getString(R.string.str_triac));
        ShowPng(ImagePng.triac);
        if (Common.Copy(s, 5, 1).equalsIgnoreCase("C")){pin5.setText("1");}
        if (Common.Copy(s, 5, 1).equalsIgnoreCase("G")){pin6.setText("1");}
        if (Common.Copy(s, 5, 1).equalsIgnoreCase("A")){pin2.setText("1");}
        if (Common.Copy(s, 6, 1).equalsIgnoreCase("C")){pin5.setText("2");}
        if (Common.Copy(s, 6, 1).equalsIgnoreCase("G")){pin6.setText("2");}
        if (Common.Copy(s, 6, 1).equalsIgnoreCase("A")){pin2.setText("2");}
        if (Common.Copy(s, 7, 1).equalsIgnoreCase("C")){pin5.setText("3");}
        if (Common.Copy(s, 7, 1).equalsIgnoreCase("G")){pin6.setText("3");}
        if (Common.Copy(s, 7, 1).equalsIgnoreCase("A")){pin2.setText("3");}
        pin2.setVisibility(View.VISIBLE);
        pin5.setVisibility(View.VISIBLE);
        pin6.setVisibility(View.VISIBLE);
    }

    private void CheckMosfet(String s, String line2)
    {
        boolean lNEMos = line2.contains("N-E-MOS") && s.contains("123");
        boolean lPEMos = line2.contains("P-E-MOS") && s.contains("123");
        boolean lNDMos = line2.contains("N-D-MOS") && s.contains("123");
        //P-D-MOS ni v dokumentaciji

        isValidElement = lNEMos || lPEMos || lNDMos;
        if (!isValidElement)
            return;
        if (lNEMos)
        {
            tv1.setText(getResources().getString(R.string.str_n_e_mosfet));
            ShowPng(ImagePng.mosfet_nch_em);
        }
        if (lPEMos)
        {
            tv1.setText(getResources().getString(R.string.str_p_e_mosfet));
            ShowPng(ImagePng.mosfet_pch_em);
        }
        if (lNDMos)
        {
            tv1.setText(getResources().getString(R.string.str_n_d_mosfet));
            ShowPng(ImagePng.mosfet_nch_dm);
        }
        s = s.trim();
        if (Common.Copy(s, 5, 1).equalsIgnoreCase("G")){pin2.setText("1");}
        if (Common.Copy(s, 5, 1).equalsIgnoreCase("D")){pin4.setText("1");}
        if (Common.Copy(s, 5, 1).equalsIgnoreCase("S")){pin6.setText("1");}
        if (Common.Copy(s, 6, 1).equalsIgnoreCase("G")){pin2.setText("2");}
        if (Common.Copy(s, 6, 1).equalsIgnoreCase("D")){pin4.setText("2");}
        if (Common.Copy(s, 6, 1).equalsIgnoreCase("S")){pin6.setText("2");}
        if (Common.Copy(s, 7, 1).equalsIgnoreCase("G")){pin2.setText("3");}
        if (Common.Copy(s, 7, 1).equalsIgnoreCase("D")){pin4.setText("3");}
        if (Common.Copy(s, 7, 1).equalsIgnoreCase("S")){pin6.setText("3");}
        pin2.setVisibility(View.VISIBLE);
        pin4.setVisibility(View.VISIBLE);
        pin6.setVisibility(View.VISIBLE);
    }

    private void CheckThyristor(String s, String line2)
    {
        s = s.trim();
        isValidElement = line2.contains("Thyristor") && s.contains("123");
        if (!isValidElement)
            return;
        tv1.setText(getResources().getString(R.string.str_thyristor));
        ShowPng(ImagePng.thyristor);
        if (Common.Copy(s, 5, 1).equalsIgnoreCase("A")){pin2.setText("1");}
        if (Common.Copy(s, 5, 1).equalsIgnoreCase("G")){pin4.setText("1");}
        if (Common.Copy(s, 5, 1).equalsIgnoreCase("C")){pin5.setText("1");}
        if (Common.Copy(s, 6, 1).equalsIgnoreCase("A")){pin2.setText("2");}
        if (Common.Copy(s, 6, 1).equalsIgnoreCase("G")){pin4.setText("2");}
        if (Common.Copy(s, 6, 1).equalsIgnoreCase("C")){pin5.setText("2");}
        if (Common.Copy(s, 7, 1).equalsIgnoreCase("A")){pin2.setText("3");}
        if (Common.Copy(s, 7, 1).equalsIgnoreCase("G")){pin4.setText("3");}
        if (Common.Copy(s, 7, 1).equalsIgnoreCase("C")){pin5.setText("3");}
        pin2.setVisibility(View.VISIBLE);
        pin4.setVisibility(View.VISIBLE);
        pin5.setVisibility(View.VISIBLE);
    }

    private void CheckJFet(String s, String line2)
    {
        boolean lNJFet = line2.contains("N-JFET") && s.contains("123");
        boolean lPJFet = line2.contains("P-JFET") && s.contains("123");
        isValidElement = lNJFet || lPJFet;
        if (!isValidElement)
            return;
        if (lNJFet)
        {
            tv1.setText(getResources().getString(R.string.str_n_jfet));
            ShowPng(ImagePng.jfet_nch);
        }
        if (lPJFet)
        {
            tv1.setText(getResources().getString(R.string.str_p_jfet));
            ShowPng(ImagePng.jfet_pch);
        }
        s = s.trim();
        if (Common.Copy(s, 5, 1).equalsIgnoreCase("G")){pin2.setText("1");}
        if (Common.Copy(s, 5, 1).equalsIgnoreCase("D")){pin4.setText("1");}
        if (Common.Copy(s, 5, 1).equalsIgnoreCase("S")){pin6.setText("1");}
        if (Common.Copy(s, 6, 1).equalsIgnoreCase("G")){pin2.setText("2");}
        if (Common.Copy(s, 6, 1).equalsIgnoreCase("D")){pin4.setText("2");}
        if (Common.Copy(s, 6, 1).equalsIgnoreCase("S")){pin6.setText("2");}
        if (Common.Copy(s, 7, 1).equalsIgnoreCase("G")){pin2.setText("3");}
        if (Common.Copy(s, 7, 1).equalsIgnoreCase("D")){pin4.setText("3");}
        if (Common.Copy(s, 7, 1).equalsIgnoreCase("S")){pin6.setText("3");}
        pin2.setVisibility(View.VISIBLE);
        pin4.setVisibility(View.VISIBLE);
        pin6.setVisibility(View.VISIBLE);
    }

    private void scrollDown()
    {
        Thread scrollThread = new Thread()
        {
            public void run()
            {
                try
                {
                    sleep(200);
                    MainActivity.this.runOnUiThread(new Runnable()
                    {
                        public void run()
                        {
                            svInfo.fullScroll(View.FOCUS_DOWN);
                        }
                    });
                } catch (Exception e)
                {
                    e.printStackTrace();
                }
            }
        };
        scrollThread.start();
    }

}