package com.bg.car;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.media.RemoteControlClient;
import android.media.RemoteController;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bg.library.Base.os.SystemInfo;
import com.bg.library.UI.Dialog.ActionSheet;
import com.bg.library.UI.Dialog.Prompt;
import com.bg.library.Utils.Localize.Saver;
import com.bg.library.Utils.Log.LogUtils;

import org.w3c.dom.Text;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements View.OnLongClickListener, View.OnClickListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ViewPager bg = (ViewPager) findViewById(R.id.bgs);
        bg.setAdapter(new BGAdapter(this));

        findViewById(R.id.root).setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                Intent intent = new Intent(MainActivity.this, AppSelectActivity.class);
                intent.putExtra("id", 999);
                startActivityForResult(intent, 1);
                overridePendingTransition(0, 0);
                return true;
            }
        });

        initView(R.id.navigation, R.mipmap.icon_maps, "MAP");
        initView(R.id.music, R.mipmap.icon_music, "MUSIC");
        initView(R.id.radio, R.mipmap.icon_fm, "FM");
        initView(R.id.setting, R.mipmap.icon_app, "APP");
        View view = findViewById(R.id.setting);
        view.setOnClickListener(this);
        view.setOnLongClickListener(null);
        view.setTag(null);

        initShortcut();

        initMediaButtons();
    }

    private void initView(int id, int icon, String name) {
        View view = findViewById(id);
        view.setOnClickListener(this);
        view.setOnLongClickListener(this);
        String packageName = Saver.getString("" + id, null);
        view.setTag(packageName);

        ImageView iv = view.findViewById(R.id.image);
        iv.setImageResource(icon);
        TextView tv = view.findViewById(R.id.text);
        tv.setText(name);
    }

    private void initShortcut() {
        LinearLayout vg = (LinearLayout) findViewById(R.id.apps);
        vg.removeAllViews();

        String[] apps = Saver.getString("apps", "").split(",");
        List<String> appList = new ArrayList<>();
        if (apps != null && apps.length > 0) {
            for (int i = 0; i < apps.length; i++) {
                String packageName = apps[i];
                if (!TextUtils.isEmpty(packageName)) {
                    appList.add(packageName);
                }
            }
        }

        if (appList.size() > 4) {
            appList = appList.subList(0, 4);
        }
        int padding = SystemInfo.Screen.dip2px(30);
        for (String packageName : appList) {
            ImageView iv = new ImageView(this);
            iv.setBackgroundResource(R.drawable.item_selector);
//            iv.setBackgroundColor(Color.RED);
            iv.setPadding(padding, padding / 3, padding, padding / 3);
            iv.setScaleType(ImageView.ScaleType.FIT_CENTER);
            iv.setTag(packageName);
            String path = Saver.getString(packageName, null);
            if (path != null) {
                try {
                    iv.setImageBitmap(BitmapFactory.decodeStream(getAssets().open(path)));
                } catch (IOException e) {
                    iv.setImageDrawable(Util.getAppIcon(this, packageName));
                }
            } else {
                iv.setImageDrawable(Util.getAppIcon(this, packageName));
            }
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT, 1);
            vg.addView(iv, params);
//            params.rightMargin = padding;
            iv.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Util.startApp(MainActivity.this, view.getTag().toString());
                }
            });
            iv.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View view) {
                    Intent intent = new Intent(MainActivity.this, IconSelectActivity.class);
                    intent.putExtra("package_name", view.getTag().toString());
                    startActivityForResult(intent, 2);
                    overridePendingTransition(0, 0);
                    return true;
                }
            });
        }
    }

    private void initMediaButtons() {
        findViewById(R.id.next).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });
    }

    public boolean sendMusicKeyEvent(int keyCode) {
        return false;
    }

    @Override
    public void onClick(View view) {

        if (view.getTag() != null && Util.startApp(this, view.getTag().toString())) {

        } else if (view.getId() == R.id.setting) {
            Intent intent = new Intent(this, AppSelectActivity.class);
            startActivity(intent);
            overridePendingTransition(0, 0);
        } else {
            Prompt.show(this, "请长按选择APP");
        }
    }

    @Override
    public boolean onLongClick(View view) {
        Intent intent = new Intent(this, AppSelectActivity.class);
        intent.putExtra("id", view.getId());
        startActivityForResult(intent, 0);
        overridePendingTransition(0, 0);
        return true;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == 0) {
                int id = data.getIntExtra("id", 0);
                String packageName = data.getStringExtra("package_name");
                if (id > 0 && packageName != null) {
                    Saver.set("" + id, packageName);
                    View view = findViewById(id);
                    view.setTag(packageName);
                }
            } else if (requestCode == 1) {
                StringBuffer apps = new StringBuffer(Saver.getString("apps", ""));
                String packageName = data.getStringExtra("package_name");
                if (!apps.toString().contains(packageName)) {
                    apps.append("," + packageName);
                    Saver.set("apps", apps);
                    LogUtils.d("apps : " + apps);
                }
                initShortcut();
            } else if (requestCode == 2) {
                String packageName = data.getStringExtra("package_name");
                String path = data.getStringExtra("icon");
                Saver.set(packageName, path);
                initShortcut();
            }
        }
    }
}
