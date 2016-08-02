package com.dynamsoft.noFakes;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.Display;
import android.widget.FrameLayout;

import java.util.List;

import database.manager.DatabaseHandler;
import database.manager.DatabaseHelper;
import product.manager.Product;

public class BarcodeReader extends Activity {
    private CameraPreview mPreview;
    private CameraManager mCameraManager;
    private HoverView mHoverView;
    private DatabaseHandler db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        /*database class handler class
         initialization
         */
        db = new DatabaseHandler(this);
        db.addAllProducts();

        Display display = getWindowManager().getDefaultDisplay();
        mHoverView = (HoverView) findViewById(R.id.hover_view);
        mHoverView.update(display.getWidth(), display.getHeight());
        mCameraManager = new CameraManager(this);
        mPreview = new CameraPreview(this, mCameraManager.getCamera());
        mPreview.setArea(mHoverView.getHoverLeft(), mHoverView.getHoverTop(), mHoverView.getHoverAreaWidth(), display.getWidth());
        FrameLayout preview = (FrameLayout) findViewById(R.id.camera_preview);
        preview.addView(mPreview);
       // getActionBar().hide();
    }

    @Override
    protected void onPause() {
        super.onPause();
        mPreview.onPause();
        mCameraManager.onPause();
    }

    @Override
    protected void onResume() {
        super.onResume();
        mCameraManager.onResume();
        mPreview.setCamera(mCameraManager.getCamera());
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
}
