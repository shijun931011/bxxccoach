package com.jgkj.bxxccoach;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.TextView;

/**
 * Created by Administrator on 2017/1/20.
 */

public class WebViewActivity extends Activity implements View.OnClickListener{
    private TextView title;
    private Button back;
    private WebView webView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.webview);
        initView();
    }

    private void initView() {
        title = (TextView) findViewById(R.id.text_title);
        back = (Button) findViewById(R.id.button_backward);
        back.setVisibility(View.VISIBLE);
        back.setOnClickListener(this);

        webView = (WebView) findViewById(R.id.webView);

    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.button_backward:
                finish();
                break;

        }

    }
}
