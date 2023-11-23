package com.fabiel.weather.core.util;

import android.content.Context;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.fabiel.weather.R;

/** Utility methods related to images. */
public final class ImageUtil {

  // ImageView.setImageURI(Uri) loads images from a local Uri only.
  // To display images from a remote Uri:
  // https://developer.android.com/develop/ui/views/graphics
  // https://github.com/bumptech/glide

  private ImageUtil() {
  }

  /**
   * Loads the image from {@code imageUrl} to {@code imageView}. A place holder image is displayed
   * in {@code imageView} while the actual image is being downloaded.
   *
   * @param context context
   * @param imageUrl image URL
   * @param imageView vie where the image will be loaded to
   */
  public static void loadImageFromUrl(Context context, String imageUrl, ImageView imageView) {
    Glide
        .with(context)
        .load(imageUrl)
        .placeholder(R.drawable.image_loading_indicator)
        .into(imageView);
  }

  /**
   * Loads the image from {@code imageUrl} to {@code imageView}. A place holder image is displayed
   * in {@code imageView} while the actual image is being downloaded.
   *
   * @param imageUrl image URL
   * @param imageView vie where the image will be loaded to
   */
  public static void loadImageFromUrl(String imageUrl, ImageView imageView) {
    Glide
        .with(imageView)
        .load(imageUrl)
        .placeholder(R.drawable.image_loading_indicator)
        .into(imageView);
  }
}
