package com.bg.car;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.media.RemoteControlClient;
import android.media.RemoteController;
import android.os.IBinder;
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
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MainActivity extends AppCompatActivity implements View.OnLongClickListener, View.OnClickListener, ServiceConnection, TimeService.DateCallback {

    private Map<Integer, View> map = new HashMap<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ViewPager bg = (ViewPager) findViewById(R.id.bgs);
        bg.setAdapter(new BGAdapter(this));
        bg.setCurrentItem(Saver.getInteger("current_page", 0), false);
        bg.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                Saver.set("current_page", position);
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });

        initView(1001, 0xff48515a, R.id.navigation, R.mipmap.icon_map, "MAP");
        initView(1002, 0xffb3e1ee, R.id.music, R.mipmap.icon_music, "MUSIC");
        initView(1003, 0xffffd55d, R.id.radio, R.mipmap.icon_fm, "FM");
        initView(1004, 0xfff49070, R.id.setting, R.mipmap.icon_app, "APP");
        View view = findViewById(R.id.setting);
        view.setOnClickListener(this);
        view.setOnLongClickListener(null);
        view.setTag(null);

        initShortcut();
    }

    @Override
    protected void onStart() {
        super.onStart();
        Intent intent = new Intent(this, TimeService.class);
        bindService(intent, this, BIND_AUTO_CREATE);
        onDateChange();
    }

    private void initView(final int appid, int background, int id, int icon, String name) {
        View view = findViewById(id);
        view.setOnClickListener(this);
        String appId = appid + "";
        String packageName = Saver.getString(appId, null);
        view.setTag(packageName);
        map.put(appid, view);
        view.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                Intent intent = new Intent(MainActivity.this, AppSelectActivity.class);
                intent.putExtra("id", appid);
                startActivityForResult(intent, 0);
                overridePendingTransition(0, 0);
                return true;
            }
        });

        ImageView iv = view.findViewById(R.id.image);
        view.setBackgroundColor(0xffffffff & background);
        iv.setImageResource(icon);
        TextView tv = view.findViewById(R.id.text);
        tv.setText(name);
    }

    private void initShortcut() {
        LinearLayout vg = (LinearLayout) findViewById(R.id.apps);
        for (int i = 0; i < 4; i++) {
            LinearLayout item = (LinearLayout) vg.getChildAt(i);
            ImageView iv = item.findViewById(R.id.image);
            TextView tv = item.findViewById(R.id.text);

            final int index = i + 1;
            String packageName = Saver.getString("shortcut_" + index, null);
            if (TextUtils.isEmpty(packageName)) {
                iv.setImageBitmap(null);
                tv.setText("");
                item.setTag(null);
                item.setOnClickListener(null);
                item.setOnLongClickListener(new View.OnLongClickListener() {
                    @Override
                    public boolean onLongClick(View view) {
                        Intent intent = new Intent(MainActivity.this, AppSelectActivity.class);
                        intent.putExtra("id", index);
                        startActivityForResult(intent, 1);
                        overridePendingTransition(0, 0);
                        return true;
                    }
                });
            } else {
                tv.setText(Util.getAppName(this, packageName));
                item.setTag(packageName);
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
                item.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Util.startApp(MainActivity.this, view.getTag().toString());
                    }
                });
                item.setOnLongClickListener(new View.OnLongClickListener() {
                    @Override
                    public boolean onLongClick(final View view) {

                        ActionSheet as = new ActionSheet(view.getContext());
                        as.addItem("修改图标");
                        as.addItem("删除应用");
                        as.show();
                        as.setOnItemClickListener(new ActionSheet.OnItemClickListener() {
                            @Override
                            public void onItemClick(int i) {
                                if (i == 0) {
                                    Intent intent = new Intent(MainActivity.this, IconSelectActivity.class);
                                    intent.putExtra("package_name", view.getTag().toString());
                                    startActivityForResult(intent, 2);
                                    overridePendingTransition(0, 0);
                                } else {
                                    Saver.set("shortcut_" + index, "");
                                    initShortcut();
                                }
                            }
                        });
                        return true;
                    }
                });
            }
        }
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
                    View view = map.get(id);
                    view.setTag(packageName);
                }
            } else if (requestCode == 1) {
                String packageName = data.getStringExtra("package_name");
                int id = data.getIntExtra("id", 0);
                Saver.set("shortcut_" + id, packageName);
                initShortcut();
            } else if (requestCode == 2) {
                String packageName = data.getStringExtra("package_name");
                String path = data.getStringExtra("icon");
                Saver.set(packageName, path);
                initShortcut();
            }
        }
    }

    @Override
    public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
        LogUtils.d("TimeService:连接成功");
        TimeService.Binder binder = (TimeService.Binder) iBinder;
        TimeService service = binder.getService();
        service.setDateCallback(this);
    }

    @Override
    public void onServiceDisconnected(ComponentName componentName) {

    }

    private SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm");
    private SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd E");

    @Override
    public void onDateChange() {
        LogUtils.d("TimeService:刷新时间");

        TextView time = (TextView) findViewById(R.id.time);
        time.setText(timeFormat.format(new Date()));

        TextView date = (TextView) findViewById(R.id.date);
        date.setText(dateFormat.format(new Date()));
    }

    @Override
    public boolean onKeyUp(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            return true;
        }
        return super.onKeyUp(keyCode, event);
    }
}
