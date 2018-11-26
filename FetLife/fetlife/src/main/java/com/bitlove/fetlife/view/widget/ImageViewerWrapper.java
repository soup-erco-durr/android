package com.bitlove.fetlife.view.widget;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;

import com.bitlove.fetlife.R;
import com.bitlove.fetlife.model.pojos.fetlife.dbjson.Picture;
import com.bitlove.fetlife.util.PictureUtil;
import com.crashlytics.android.Crashlytics;
import com.stfalcon.frescoimageviewer.ImageViewer;

import java.util.List;

public class ImageViewerWrapper {

    private boolean isDisplayed = false;
    private int currentPosition = 0;
    private List<Picture> pictures;
    private PictureUtil.OnPictureOverlayClickListener onPictureOverlayClickListener;

    public void onConfigurationChanged(Context context) {
        if (isDisplayed) {
            show(context, pictures, currentPosition, onPictureOverlayClickListener);
        }
    }

    public void show(Context context, final List<Picture> pictures, int startPosition, final PictureUtil.OnPictureOverlayClickListener onPictureOverlayClickListener) {
        if (pictures == null || startPosition < 0 || startPosition >= pictures.size()) {
            Crashlytics.logException(new Exception("Incorrect values"));
            return;
        }

        isDisplayed = true;
        this.currentPosition = startPosition;
        this.pictures = pictures;
        this.onPictureOverlayClickListener = onPictureOverlayClickListener;

        String[] imageUrls = new String[pictures.size()];
        int i = 0;
        for (Picture picture : pictures) {
            imageUrls[i++] = picture.getVariants().getHugeUrl();
        }
        LayoutInflater inflater = LayoutInflater.from(context);
        final View overlay = inflater.inflate(R.layout.overlay_feed_imageswipe, null);
        PictureUtil.setOverlayContent(overlay, pictures.get(currentPosition), onPictureOverlayClickListener);
        new ImageViewer.Builder(context, imageUrls).setOverlayView(overlay).setStartPosition(startPosition).setImageChangeListener(new ImageViewer.OnImageChangeListener() {
            @Override
            public void onImageChange(int position) {
                PictureUtil.setOverlayContent(overlay, pictures.get(position), onPictureOverlayClickListener);
            }
        }).setOnDismissListener(new ImageViewer.OnDismissListener() {
            @Override
            public void onDismiss() {
                ImageViewerWrapper.this.currentPosition = 0;
                ImageViewerWrapper.this.pictures = null;
                ImageViewerWrapper.this.onPictureOverlayClickListener = null;
                ImageViewerWrapper.this.isDisplayed = false;
            }
        }).show();
    }
}