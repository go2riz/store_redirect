package com.danieldallos.storeredirect;

import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;

import androidx.annotation.NonNull;

import io.flutter.embedding.engine.plugins.FlutterPlugin;
import io.flutter.embedding.engine.plugins.activity.ActivityAware;
import io.flutter.embedding.engine.plugins.activity.ActivityPluginBinding;
import io.flutter.plugin.common.MethodCall;
import io.flutter.plugin.common.MethodChannel;
import io.flutter.plugin.common.MethodChannel.MethodCallHandler;
import io.flutter.plugin.common.MethodChannel.Result;

/** StoreRedirectPlugin */
public class StoreRedirectPlugin implements MethodCallHandler, FlutterPlugin, ActivityAware {

  private Context applicationContext;
  private Activity activity;
  private MethodChannel methodChannel;

  @Override
  public void onAttachedToEngine(@NonNull FlutterPluginBinding binding) {
    applicationContext = binding.getApplicationContext();
    methodChannel = new MethodChannel(binding.getBinaryMessenger(), "store_redirect");
    methodChannel.setMethodCallHandler(this);
  }

  @Override
  public void onDetachedFromEngine(@NonNull FlutterPluginBinding binding) {
    if (methodChannel != null) {
      methodChannel.setMethodCallHandler(null);
      methodChannel = null;
    }
    applicationContext = null;
  }

  @Override
  public void onMethodCall(@NonNull MethodCall call, @NonNull Result result) {
    if (!"redirect".equals(call.method)) {
      result.notImplemented();
      return;
    }

    final String requestedAppId = call.argument("android_id");
    final String appPackageName = requestedAppId != null && !requestedAppId.isEmpty()
        ? requestedAppId
        : (activity != null ? activity.getPackageName() : applicationContext != null ? applicationContext.getPackageName() : null);

    if (appPackageName == null || appPackageName.isEmpty()) {
      result.error("UNAVAILABLE", "Could not determine Android application id.", null);
      return;
    }

    final Context launchContext = activity != null ? activity : applicationContext;
    if (launchContext == null) {
      result.error("UNAVAILABLE", "Store redirect is not attached to an Android context.", null);
      return;
    }

    final Intent marketIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" + appPackageName));
    marketIntent.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY
        | Intent.FLAG_ACTIVITY_NEW_DOCUMENT
        | Intent.FLAG_ACTIVITY_MULTIPLE_TASK);

    if (!(launchContext instanceof Activity)) {
      marketIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
    }

    try {
      launchContext.startActivity(marketIntent);
      result.success(null);
    } catch (ActivityNotFoundException e) {
      final Intent webIntent = new Intent(Intent.ACTION_VIEW,
          Uri.parse("https://play.google.com/store/apps/details?id=" + appPackageName));
      if (!(launchContext instanceof Activity)) {
        webIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
      }
      try {
        launchContext.startActivity(webIntent);
        result.success(null);
      } catch (Exception inner) {
        result.error("UNAVAILABLE", "Unable to open the Play Store or browser.", null);
      }
    } catch (Exception e) {
      result.error("UNAVAILABLE", "Unable to launch store redirect.", null);
    }
  }

  @Override
  public void onAttachedToActivity(@NonNull ActivityPluginBinding binding) {
    activity = binding.getActivity();
  }

  @Override
  public void onDetachedFromActivityForConfigChanges() {
    activity = null;
  }

  @Override
  public void onReattachedToActivityForConfigChanges(@NonNull ActivityPluginBinding binding) {
    activity = binding.getActivity();
  }

  @Override
  public void onDetachedFromActivity() {
    activity = null;
  }
}
