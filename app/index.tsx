import React, { useEffect, useRef, useState } from 'react';
import Constants from "expo-constants"
import { WebView, WebViewNavigation } from 'react-native-webview';
import { BackHandler } from 'react-native';

const home = "https://inforestudante.ipc.pt";

export default function Index() {
    const webview = useRef<WebView>(null);
    const [canGoBack, setCanGoBack] = useState(true);

    const onNavigationStateChange = (event: WebViewNavigation) => {
        if (!event.url) return;

        if (!event.url.startsWith(home)) {
            webview.current?.stopLoading();
            webview.current?.injectJavaScript(`window.location.href = "${home}";`);
        }
        setCanGoBack(event.canGoBack);
    }

    useEffect(() => {
        const backAction = () => {
            if (webview.current && canGoBack) { webview.current.goBack(); return true; }
            return false;
        };
        const backHandler = BackHandler.addEventListener('hardwareBackPress', backAction);
        return () => backHandler.remove();
    }, [canGoBack]);

    return (
        <WebView
            ref={webview}
            style={{ flex: 1, marginTop: Constants.statusBarHeight }}
            source={{ uri: home }}
            onNavigationStateChange={onNavigationStateChange}
            onShouldStartLoadWithRequest={(request) => request.url.startsWith(home)}
        />
    );
}
