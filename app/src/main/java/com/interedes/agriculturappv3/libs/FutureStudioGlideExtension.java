package com.interedes.agriculturappv3.libs;

import com.bumptech.glide.annotation.GlideExtension;
import com.bumptech.glide.annotation.GlideOption;
import com.bumptech.glide.request.RequestOptions;
import com.interedes.agriculturappv3.R;

@GlideExtension
public class FutureStudioGlideExtension {
    private FutureStudioGlideExtension() {}

    @GlideOption
    public static void profilePhoto(RequestOptions options) {
        options
                .placeholder(R.drawable.default_avata)
                .fitCenter()
                .circleCrop()
                .override(300, 300);
    }

    @GlideOption
    public static void productorPhoto(RequestOptions options) {
        options

                .placeholder(R.drawable.ic_account_box_green)
                .error(R.drawable.ic_account_box_green)
                .centerCrop()
                .fitCenter();
    }


    @GlideOption
    public static void productorPhotoCenterCrop(RequestOptions options) {
        options

                .placeholder(R.drawable.ic_account_box_green)
                .error(R.drawable.ic_account_box_green)
                .centerCrop();
    }


    @GlideOption
    public static void productoPhoto(RequestOptions options) {
        options
                .placeholder(R.drawable.ic_foto_producto)
                .error(R.drawable.ic_foto_producto)
                .centerInside()
                .fitCenter();
    }




    @GlideOption
    public static void productoPhotoCenterCrop(RequestOptions options) {
        options
                .placeholder(R.drawable.ic_foto_producto)
                .error(R.drawable.ic_foto_producto)
                .centerCrop()
                .fitCenter();
    }

    @GlideOption
    public static void insumosPhoto(RequestOptions options) {
        options
                .placeholder(R.drawable.empty_insumo)
                .centerInside()
                .fitCenter();
    }

    @GlideOption
    public static void plagasPhoto(RequestOptions options) {
        options
                .placeholder(R.drawable.emtpy_img_plaga)
                .centerCrop()
                .fitCenter();
    }
}
