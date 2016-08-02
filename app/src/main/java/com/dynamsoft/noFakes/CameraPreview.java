package com.dynamsoft.noFakes;

import java.io.IOException;
import java.util.List;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.hardware.Camera;
import android.hardware.Camera.Parameters;
import android.hardware.Camera.PreviewCallback;
import android.os.Bundle;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import com.google.zxing.BinaryBitmap;
import com.google.zxing.LuminanceSource;
import com.google.zxing.MultiFormatReader;
import com.google.zxing.NotFoundException;
import com.google.zxing.PlanarYUVLuminanceSource;
import com.google.zxing.Result;
import com.google.zxing.common.HybridBinarizer;

import database.manager.DatabaseHandler;
import database.manager.DatabaseHelper;
import product.manager.Product;

public class CameraPreview extends SurfaceView implements SurfaceHolder.Callback {
    private SurfaceHolder mHolder;
    private Camera mCamera;
    private static final String TAG = "camera";
    private int mWidth, mHeight;
    private Context mContext;
    private MultiFormatReader mMultiFormatReader;
    private AlertDialog mDialog;
    private int mLeft, mTop, mAreaWidth, mAreaHeight;
    final DatabaseHandler db;
    List<Product> results;

    public CameraPreview(Context context, Camera camera) {
        super(context);
        mCamera = camera;
        mContext = context;
        mHolder = getHolder();
        mHolder.addCallback(this);
        db = new DatabaseHandler(mContext);
        // deprecated setting, but required on Android versions prior to 3.0
        mHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
        Parameters params = mCamera.getParameters();

        mWidth = 640;
        mHeight = 480;

        params.setPreviewSize(mWidth, mHeight);
        mCamera.setParameters(params);
        mMultiFormatReader = new MultiFormatReader();
        mDialog = new AlertDialog.Builder(mContext).create();
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        try {
            mCamera.setPreviewDisplay(holder);
            mCamera.startPreview();
        } catch (IOException e) {
            Log.d(TAG, "Error setting camera preview: " + e.getMessage());
        }
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {

    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int w, int h) {

        if (mHolder.getSurface() == null) {
            return;
        }
        try {
            //mCamera.stopPreview();
        } catch (Exception e) {
            Log.d(TAG, "Error starting camera preview: " + e.getMessage());
        }
        try {
            mCamera.setPreviewCallback(mPreviewCallback);
            mCamera.setPreviewDisplay(mHolder);
            mCamera.startPreview();
        } catch (Exception e) {
            Log.d(TAG, "Error starting camera preview: " + e.getMessage());
        }
    }

    public void setCamera(Camera camera) {
        mCamera = camera;
    }

    public void onPause() {
        if (mCamera != null) {
            mCamera.setPreviewCallback(null);
            mCamera.stopPreview();
        }
    }

    private Camera.PreviewCallback mPreviewCallback = new PreviewCallback() {

        @Override
        public void onPreviewFrame(byte[] data, Camera camera) {
            if (mDialog.isShowing())
                return;
            LuminanceSource source = new PlanarYUVLuminanceSource(data, mWidth, mHeight, mLeft, mTop, mAreaWidth, mAreaHeight, false);
            BinaryBitmap bitmap = new BinaryBitmap(new HybridBinarizer(
                    source));
            Result result;
            try {
                result = mMultiFormatReader.decode(bitmap, null);
                if (result != null) {
                    /*get the result scanned and store
                      it to a string
                     */
                    String prd_code = result.getText();
                    Log.d("NoFakes", "ProductCode scanned: " + prd_code);
                    if (validateOutput(prd_code) == true) {
                        Log.d("NoFakes", "ProductCode scanned: ----->true");
                        List<Product> productResults = db.getListProducts(prd_code);
                        if (!productResults.isEmpty()) {
                            Log.d("NoFakes", "ProductId:-> " + String.valueOf(productResults.get(0).getProductId()) + " | ProductCode: " +
                                    productResults.get(0).getProductCode() + " | DateCreated: " + productResults.get(0).getDateCreated() +
                                    " | ExpiryDate: " + productResults.get(0).getExpiryDate());
                            Log.d("NoFakes", "ProductResults was not empty: " + productResults);
                            String productId = String.valueOf(productResults.get(0).getProductId());
                            String productCode = productResults.get(0).getProductCode();
                            String dateCreated = productResults.get(0).getDateCreated();
                            String expiryDate = productResults.get(0).getExpiryDate();
                            String description = productResults.get(0).getProductDescription();
                            /*call the method to push prod details to
                             the displayActivity
                            */
                            bundleMessage(productId, productCode, dateCreated, expiryDate, description);
                        } else {
                            Log.d("NoFakes", "Product not found in the database");
                            String displayMessage = prd_code + " is not listed as a genuine product";
                            mDialog.setTitle("NoFakes Results");
                            mDialog.setMessage(displayMessage);
                            mDialog.show();
                        }
                    } else {
                        String results = "Could not resolve " + prd_code + "\nPlease scan a number";
                        mDialog.setTitle("NoFakes Results");
                        mDialog.setMessage(results);
                        mDialog.show();
                    }
                } else {
                    String results = "Could not scan barcode/qr code \nKindly retry";
                    mDialog.setTitle("NoFakes Results");
                    mDialog.setMessage(results);
                    mDialog.show();
                }
            } catch (NotFoundException e) {
                Log.d("NoFakes", "Exception caught" + e.getMessage());
                e.printStackTrace();
            }
        }
    };

    public void setArea(int left, int top, int areaWidth, int width) {
        double ratio = width / mWidth;
        mLeft = (int) (left / (ratio + 1));
        mTop = (int) (top / (ratio + 1));
        mAreaHeight = mAreaWidth = mWidth - mLeft * 2;
    }

    /*
       Method bundleMessage
       @Param prodId,prodCode,prodDate,prodExpiry
       loads the next intent to display product details
    */
    public void bundleMessage(String prodId, String prodCode, String prodDate,
                              String prodExpiry, String desc) {
        //bundle data to pass to the next intent
        Bundle bundle = new Bundle();
        bundle.putString("id", prodId);
        bundle.putString("code", prodCode);
        bundle.putString("date", prodDate);
        bundle.putString("expiry", prodExpiry);
        bundle.putString("desc", desc);
        //load the intent to display the data
        Intent displayIntent = new Intent(mContext, DisplayActivity.class);
        displayIntent.putExtras(bundle);
        mContext.startActivity(displayIntent);
    }

    /*
     Method validateOutput
     @Param String outPut
     return boolean
     */
    public boolean validateOutput(String outPut) {
        char c = outPut.charAt(0);
        char h = 'h';
        //check if the firstLetter is h for http
        if (c == h) {
            return false;
        } else {
            return true;
        }
    }

}
