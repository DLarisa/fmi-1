package com.iprovider.dan.iprovider;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.DialogInterface;
import android.os.Vibrator;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.util.SparseArray;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.vision.CameraSource;
import com.google.android.gms.vision.Detector;
import com.google.android.gms.vision.barcode.Barcode;
import com.google.android.gms.vision.barcode.BarcodeDetector;
import com.google.android.gms.vision.text.TextBlock;
import com.google.android.gms.vision.text.TextRecognizer;
import com.google.android.gms.vision.MultiDetector;
import com.google.android.gms.vision.MultiProcessor;


import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class CameraScanActivity extends AppCompatActivity {

    private static final String TAG = "CameraScan";

    private static final int RC_HANDLE_GMS = 9001;
    private static final int RC_HANDLE_CAMERA_PERM = 2;
    final int RequestCameraPermissionID = 1001;

    SurfaceView cameraScan;
    TextView scanResult;
    Button btn_qr_ok, btn_back;
    CameraSource cameraSource = null;
    private String stringResult="";
    private String currentRead="";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_camera_scan);
        setTitle("Scan meter with camera");

        cameraScan = (SurfaceView) findViewById(R.id.camera_scan);
        scanResult = (TextView) findViewById(R.id.txt_scan_result);
        btn_qr_ok = (Button) findViewById(R.id.btn_qr_ok);
        btn_back = (Button) findViewById(R.id.btn_back_camera_scan);
        btn_qr_ok.setEnabled(false);

        int rc = ActivityCompat.checkSelfPermission(this, Manifest.permission.CAMERA);
        if (rc == PackageManager.PERMISSION_GRANTED) {
            createCameraSource();
        } else {
            requestCameraPermission();
        }

        btn_qr_ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent();



                intent.putExtra("idMeter", stringResult);
                intent.putExtra("currentRead", currentRead);
                toastMessage( "Scanned data added!");
                setResult(RESULT_OK, intent);
                finish();
            }
        });
        btn_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent();
                setResult(RESULT_CANCELED, intent);
                finish();
            }
        });
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case RequestCameraPermissionID: {
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    if (ActivityCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                        return;
                    }
                    try {
                        cameraSource.start(cameraScan.getHolder());
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
            break;
        }
        if (requestCode != RC_HANDLE_CAMERA_PERM) {
            Log.d(TAG, "Got unexpected permission result: " + requestCode);
            super.onRequestPermissionsResult(requestCode, permissions, grantResults);
            return;
        }

        if (grantResults.length != 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            Log.d(TAG, "Camera permission granted - initialize the camera source");
            // we have permission, so create the camerasource
            createCameraSource();
            return;
        }

        Log.e(TAG, "Permission not granted: results len = " + grantResults.length +
                " Result code = " + (grantResults.length > 0 ? grantResults[0] : "(empty)"));

        DialogInterface.OnClickListener listener = new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                finish();
            }
        };

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Multitracker sample")
                .setMessage("No camera permission")
                .setPositiveButton("OK", listener)
                .show();
    }
    private void requestCameraPermission() {
        Log.w(TAG, "Camera permission is not granted. Requesting permission");

        final String[] permissions = new String[]{Manifest.permission.CAMERA};

        if (!ActivityCompat.shouldShowRequestPermissionRationale(this,
                Manifest.permission.CAMERA)) {
            ActivityCompat.requestPermissions(this, permissions, RC_HANDLE_CAMERA_PERM);
            return;
        }

        final Activity thisActivity = this;
        View.OnClickListener listener = new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ActivityCompat.requestPermissions(thisActivity, permissions,
                        RC_HANDLE_CAMERA_PERM);
            }
        };
    }

    private void createCameraSource() {
        Context context = getApplicationContext();

        BarcodeDetector barcodeDetector = new BarcodeDetector.Builder(context).setBarcodeFormats(Barcode.QR_CODE).build();
        barcodeDetector.setProcessor(new Detector.Processor<Barcode>() {
            @Override
            public void release() {
            }
            @Override
            public void receiveDetections(Detector.Detections<Barcode> detections) {
                final SparseArray<Barcode> qrcodes = detections.getDetectedItems();
                if(qrcodes.size() != 0)
                {
                    scanResult.post(new Runnable() {
                        @Override
                        public void run() {
                            //Create vibrate
                            Vibrator vibrator = (Vibrator)getApplicationContext().getSystemService(Context.VIBRATOR_SERVICE);
                            vibrator.vibrate(100);
                            scanResult.setText("idMeter: " + qrcodes.valueAt(0).displayValue + " Current read: " + currentRead);
                            stringResult = qrcodes.valueAt(0).displayValue;
                            if(stringResult.length() <= 1 || currentRead.length() <=0 ){
                                stringResult = "0";
                                btn_qr_ok.setEnabled(false);
                            }else{
                                btn_qr_ok.setEnabled(true);
                            }
                        }
                    });
                }
            }
        });

        TextRecognizer textRecognizer = new TextRecognizer.Builder(context).build();
        textRecognizer.setProcessor(new Detector.Processor<TextBlock>() {
            @Override
            public void release() {

            }

            @Override
            public void receiveDetections(Detector.Detections<TextBlock> detections) {

                final SparseArray<TextBlock> items = detections.getDetectedItems();
                if(items.size() != 0)
                {
                    scanResult.post(new Runnable() {
                        @Override
                        public void run() {
                            StringBuilder stringBuilder = new StringBuilder();
                            for(int i =0;i<items.size();++i)
                            {
                                TextBlock item = items.valueAt(i);
                                stringBuilder.append(item.getValue());
                            }
                            currentRead = stringBuilder.toString();

//                            Pattern pattern = Pattern.compile("(\\d)");
//                            //   \d is for a digit
//                            Matcher matcher = pattern.matcher(currentRead);
//                            String val = "";
//                            if (matcher.find()) {
//                                val = matcher.group(1);
//                            }
//                            currentRead = val;
                            currentRead = getOnlyNumerics(currentRead);


                        }
                    });
                }
            }
        });

        MultiDetector multiDetector = new MultiDetector.Builder()
                .add(textRecognizer)
                .add(barcodeDetector)
                .build();

        if (!multiDetector.isOperational()) {
            toastMessage("Detector dependencies are not yet available");
        }
        cameraSource = new CameraSource.Builder(getApplicationContext(), multiDetector)
                .setFacing(CameraSource.CAMERA_FACING_BACK)
                .setRequestedPreviewSize(640, 480)
                .setRequestedFps(2.0f)
                .setAutoFocusEnabled(true)
                .build();

    }

    @Override
    protected void onResume() {
        super.onResume();
        startCameraSource();
    }
    @Override
    protected void onPause() {
        super.onPause();
    }
    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (cameraSource != null) {
            cameraSource.release();
        }
    }
    private void startCameraSource() {

        // check that the device has play services available.
        int code = GoogleApiAvailability.getInstance().isGooglePlayServicesAvailable(
                getApplicationContext());
        if (code != ConnectionResult.SUCCESS) {
            Dialog dlg =
                    GoogleApiAvailability.getInstance().getErrorDialog(this, code, RC_HANDLE_GMS);
            dlg.show();
        }
        if (cameraSource != null) {
            cameraScan.getHolder().addCallback(new SurfaceHolder.Callback() {
                @Override
                public void surfaceCreated(SurfaceHolder surfaceHolder) {
                    if (ActivityCompat.checkSelfPermission(getApplicationContext(), android.Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                        //Request permission
                        ActivityCompat.requestPermissions(CameraScanActivity.this,
                                new String[]{Manifest.permission.CAMERA},RequestCameraPermissionID);
                        return;
                    }
                    try {
                        cameraSource.start(cameraScan.getHolder());
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                @Override
                public void surfaceChanged(SurfaceHolder surfaceHolder, int i, int i1, int i2) {
                }
                @Override
                public void surfaceDestroyed(SurfaceHolder surfaceHolder) {
                    cameraSource.stop();
                }
            });
        }
    }
    private void toastMessage (String message){
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }
    public static String getOnlyNumerics(String str) {

        if (str == null) {
            return null;
        }

        StringBuffer strBuff = new StringBuffer();
        char c;

        for (int i = 0; i < str.length() ; i++) {
            c = str.charAt(i);
            if(c == '0' && strBuff.length() == 0){

            }else if (Character.isDigit(c)) {
                strBuff.append(c);
            }
        }


        return strBuff.toString();
    }
}
