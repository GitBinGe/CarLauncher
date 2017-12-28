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

        initView(R.id.navigation, R.mipmap.icon_maps, "Maps");
        initView(R.id.music, R.mipmap.icon_music, "Music");
        initView(R.id.radio, R.mipmap.icon_fm, "FM");
        initView(R.id.setting, R.mipmap.icon_app, "Apps");
        View view = findViewById(R.id.setting);
        view.setOnClickListener(this);
        view.setOnLongClickListener(null);
        view.setTag(null);
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
            int id = data.getIntExtra("id", 0);
            String packageName = data.getStringExtra("package_name");
            if (id > 0 && packageName != null) {
                Saver.set("" + id, packageName);
            }
            LogUtils.d(packageName + ":" + id);

            View view = findViewById(id);
            view.setTag(packageName);
        }
    }
}
