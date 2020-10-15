package com.ly.rshypoc.util;

import android.content.Context;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.Priority;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.ly.rshypoc.R;

import jp.wasabeef.glide.transformations.RoundedCornersTransformation;

/**
 * @author 郑山
 * @date 2019/8/22
 */

public class GlideUtil {

    public static void with(Context context, Object url, ImageView imageView) {
        Glide.with(context)
                .load(url)
                //设置加载失败后的图片显示
//                .error(R.drawable.default_avatar)
//                .fitCenter()
                //默认淡入淡出动画
//                .crossFade()
                //缓存策略,跳过内存缓存【此处应该设置为false，否则列表刷新时会闪一下】
//                .skipMemoryCache(false)
                //缓存策略,硬盘缓存-仅仅缓存最终的图像，即降低分辨率后的（或者是转换后的）
//                .diskCacheStrategy(DiskCacheStrategy.RESULT)
                //设置图片加载的优先级
//                .priority(Priority.HIGH)
                .into(imageView);
    }

    public static void withHead(Context context, Object url, ImageView imageView) {

        Glide.with(context)
                .load(url)
//                .crossFade()
//                .error(R.drawable.default_avatar)
//                .bitmapTransform(new RoundedCornersTransformation(context, dip2px(context, 8),0,
//                        RoundedCornersTransformation.CornerType.TOP))
                .into(imageView);
    }
    public static int dip2px(Context context, float dpValue) {
        float scale = context.getResources().getDisplayMetrics().density;
        return (int)((dpValue * scale) + 0.5f);
    }


}
