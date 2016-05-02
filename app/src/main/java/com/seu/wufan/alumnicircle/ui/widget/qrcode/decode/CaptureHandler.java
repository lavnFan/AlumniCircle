package com.seu.wufan.alumnicircle.ui.widget.qrcode.decode;

import android.os.Handler;
import android.os.Message;

import com.seu.wufan.alumnicircle.ui.widget.qrcode.camera.CameraManager;
import com.seu.wufan.alumnicircle.ui.widget.qrcode.dwj.ScanActivity;


public final class CaptureHandler extends Handler {

    private final ScanActivity activity;
    private final DecodeThread decodeThread;
    private final CameraManager cameraManager;
    private State state;

    private enum State {
        PREVIEW,
        SUCCESS,
        DONE
    }

    public CaptureHandler(ScanActivity activity, CameraManager cameraManager) {
        this.activity = activity;
        decodeThread = new DecodeThread(activity);
        decodeThread.start();
        state = State.SUCCESS;

        this.cameraManager = cameraManager;
        cameraManager.startPreview();
        restartPreviewAndDecode();
    }

    @Override
    public void handleMessage(Message message) {
        switch (message.what) {
            case Constants.ID_RESTART_PREVIEW:
                restartPreviewAndDecode();
                break;
            case Constants.ID_DECODE_SUCCESS:
                state = State.SUCCESS;
                activity.handleDecode((String) message.obj, message.getData());
                break;
            case Constants.ID_DECODE_FAILED:
                state = State.PREVIEW;
                cameraManager.requestPreviewFrame(decodeThread.getHandler(), Constants.ID_DECODE);
                break;
        }
    }

    public void quitSynchronously() {
        state = State.DONE;
        cameraManager.stopPreview();
        Message quit = Message.obtain(decodeThread.getHandler(), Constants.ID_QUIT);
        quit.sendToTarget();
        try {
            decodeThread.join(500L);
        } catch (InterruptedException e) {
            // continue
        }

        removeMessages(Constants.ID_DECODE_SUCCESS);
        removeMessages(Constants.ID_DECODE_FAILED);
    }

    private void restartPreviewAndDecode() {
        if (state == State.SUCCESS) {
            state = State.PREVIEW;
            cameraManager.requestPreviewFrame(decodeThread.getHandler(), Constants.ID_DECODE);
        }
    }

}
