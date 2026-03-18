#import "StoreRedirectPlugin.h"

@implementation StoreRedirectPlugin
+ (void)registerWithRegistrar:(NSObject<FlutterPluginRegistrar>*)registrar {
    FlutterMethodChannel* channel = [FlutterMethodChannel
                                     methodChannelWithName:@"store_redirect"
                                     binaryMessenger:[registrar messenger]];
    StoreRedirectPlugin* instance = [[StoreRedirectPlugin alloc] init];
    [registrar addMethodCallDelegate:instance channel:channel];
}

- (void)handleMethodCall:(FlutterMethodCall*)call result:(FlutterResult)result {
    if (![ @"redirect" isEqualToString:call.method ]) {
        result(FlutterMethodNotImplemented);
        return;
    }

    NSString *appId = call.arguments[@"ios_id"];
    if (appId == nil || appId.length == 0) {
        result([FlutterError errorWithCode:@"ERROR"
                                   message:@"Invalid app id"
                                   details:nil]);
        return;
    }

    NSString *iTunesLink;
    if (@available(iOS 11.0, *)) {
        iTunesLink = [NSString stringWithFormat:@"itms-apps://itunes.apple.com/app/id%@", appId];
    } else {
        iTunesLink = [NSString stringWithFormat:@"itms-apps://itunes.apple.com/WebObjects/MZStore.woa/wa/viewContentsUserReviews?type=Purple+Software&id=%@", appId];
    }

    NSURL *url = [NSURL URLWithString:iTunesLink];
    if (url == nil) {
        result([FlutterError errorWithCode:@"ERROR"
                                   message:@"Invalid App Store URL"
                                   details:nil]);
        return;
    }

    UIApplication *application = [UIApplication sharedApplication];
    if (@available(iOS 10.0, *)) {
        [application openURL:url options:@{} completionHandler:^(BOOL success) {
            if (success) {
                result(nil);
            } else {
                result([FlutterError errorWithCode:@"UNAVAILABLE"
                                           message:@"Unable to open the App Store"
                                           details:nil]);
            }
        }];
    } else {
#pragma clang diagnostic push
#pragma clang diagnostic ignored "-Wdeprecated-declarations"
        BOOL success = [application openURL:url];
#pragma clang diagnostic pop
        if (success) {
            result(nil);
        } else {
            result([FlutterError errorWithCode:@"UNAVAILABLE"
                                       message:@"Unable to open the App Store"
                                       details:nil]);
        }
    }
}

@end
