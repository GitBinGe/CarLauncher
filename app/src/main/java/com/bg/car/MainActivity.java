package com.bg.car;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.bg.library.UI.Dialog.Prompt;
import com.bg.library.Utils.Localize.Saver;
import com.bg.library.Utils.Log.LogUtils;

import java.util.List;

public class MainActivity extends AppCompatActivity implements View.OnLongClickListener, View.OnClickListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initView(R.id.navigation, R.mipmap.maps, "导航");
        initView(R.id.music, R.mipmap.player, "音乐");
        initView(R.id.radio, R.mipmap.fmradio, "FM");
        initView(R.id.setting, R.mipmap.settings, "设置");

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

    @Override
    public void onClick(View view) {
        if (view.getTag() != null) {
            doStartApplicationWithPackageName(view.getTag().toString());
        } else {
            Prompt.show(this, "请长按选择APP");
        }
    }

    private void doStartApplicationWithPackageName(String packageName) {

        // 通过包名获取此APP详细信息，包括Activities、services、versioncode、name等等
        PackageInfo packageinfo = null;
        try {
            packageinfo = getPackageManager().getPackageInfo(packageName, 0);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        if (packageinfo == null) {
            return;
        }

        // 创建一个类别为CATEGORY_LAUNCHER的该包名的Intent
        Intent resolveIntent = new Intent(Intent.ACTION_MAIN, null);
        resolveIntent.addCategory(Intent.CATEGORY_LAUNCHER);
        resolveIntent.setPackage(packageinfo.packageName);

        // 通过getPackageManager()的queryIntentActivities方法遍历
        List<ResolveInfo> resolveinfoList = getPackageManager()
                .queryIntentActivities(resolveIntent, 0);

        ResolveInfo resolveinfo = resolveinfoList.iterator().next();
        if (resolveinfo != null) {
            // 这个就是我们要找的该APP的LAUNCHER的Activity[组织形式：packagename.mainActivityname]
            String className = resolveinfo.activityInfo.name;
            // LAUNCHER Intent
            Intent intent = new Intent(Intent.ACTION_MAIN);
            intent.addCategory(Intent.CATEGORY_LAUNCHER);

            // 设置ComponentName参数1:packagename参数2:MainActivity路径
            ComponentName cn = new ComponentName(packageName, className);

            intent.setComponent(cn);
            startActivity(intent);
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
            int id = data.getIntExtra("id", 0);
            String packageName = data.getStringExtra("package_name");
            if (id > 0 && packageName != null) {
                Saver.set("" + id, packageName);
            }
            LogUtils.d(packageName + ":" + id);
        }
    }
}
