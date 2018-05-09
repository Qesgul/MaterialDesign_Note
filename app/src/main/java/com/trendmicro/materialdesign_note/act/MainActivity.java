package com.trendmicro.materialdesign_note.act;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.RelativeLayout;
import com.google.gson.Gson;
import com.trendmicro.materialdesign_note.Adapter.MsgAdapter;
import com.trendmicro.materialdesign_note.Adapter.MyDatabaseHelper;
import com.trendmicro.materialdesign_note.R;
import com.trendmicro.materialdesign_note.bean.Message;
import com.yanzhenjie.recyclerview.swipe.SwipeMenu;
import com.yanzhenjie.recyclerview.swipe.SwipeMenuBridge;
import com.yanzhenjie.recyclerview.swipe.SwipeMenuCreator;
import com.yanzhenjie.recyclerview.swipe.SwipeMenuItem;
import com.yanzhenjie.recyclerview.swipe.SwipeMenuItemClickListener;
import com.yanzhenjie.recyclerview.swipe.SwipeMenuRecyclerView;
import java.util.ArrayList;
import java.util.List;

import static android.view.ViewGroup.LayoutParams.MATCH_PARENT;

public class MainActivity extends BaseActivity
        implements NavigationView.OnNavigationItemSelectedListener {
    private MyDatabaseHelper dbHelper;
    SQLiteDatabase db;
    private List<Message> MsgList = new ArrayList<Message>();
    public SwipeMenuRecyclerView recyclerView;
    public RelativeLayout relativeLayout;
    private MsgAdapter msgAdapter;
    private String stringEditInfo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        init();
        initMessage();
        initRecycle();
    }

    public void init() {

        dbHelper = new MyDatabaseHelper(this, "Safe.db", null, 1);
        db = dbHelper.getWritableDatabase();
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        recyclerView = findViewById(R.id.recyclerView);
        relativeLayout = findViewById(R.id.exception);
        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new FloatOnClickListener());

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open,
                R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        relativeLayout.setVisibility(View.VISIBLE);
        recyclerView.setVisibility(View.GONE);
    }

    private void initMessage() {
        Cursor cursor = db.query("PasswodSafe", null, null, null, null, null, null);
        int i = 0;
        if (cursor.moveToFirst()) {
            do {
                // 遍历Cursor对象，取出数据并打印
                Message message = new Message();
                message.setTitle(cursor.getString(cursor.getColumnIndex("title")));
                message.setPassword(cursor.getString(cursor.getColumnIndex("password")));
                message.setMemoInfo(cursor.getString(cursor.getColumnIndex("memoInfo")));
                message.setTime(cursor.getString(cursor.getColumnIndex("time")));
                MsgList.add(message);
                i = 1;
            } while (cursor.moveToNext());
        }
        if (i == 1) {
            relativeLayout.setVisibility(View.GONE);
            recyclerView.setVisibility(View.VISIBLE);
        }
        cursor.close();
    }

    public void initRecycle() {
        SwipeMenuCreator mSwipeMenuCreator = new SwipeMenuCreator() {
            @Override
            public void onCreateMenu(SwipeMenu leftMenu, SwipeMenu rightMenu, int viewType) {
                SwipeMenuItem deleteItem = new SwipeMenuItem(getApplicationContext());
                deleteItem.setText("删除");
                deleteItem.setTextColor(getResources().getColor(R.color.white));
                deleteItem.setWidth(280);
                deleteItem.setHeight(MATCH_PARENT);
                deleteItem.setBackgroundColorResource(R.color.red);
                rightMenu.addMenuItem(deleteItem); // 在Item右侧添加一个菜单。
            }
        };
        recyclerView.setSwipeMenuCreator(mSwipeMenuCreator);
        SwipeMenuItemClickListener mMenuItemClickListener = new SwipeMenuItemClickListener() {
            @Override
            public void onItemClick(SwipeMenuBridge menuBridge) {
                // 任何操作必须先关闭菜单，否则可能出现Item菜单打开状态错乱。
                menuBridge.closeMenu();
                int adapterPosition =
                        menuBridge.getAdapterPosition(); // RecyclerView的Item的position。
                db.delete("PasswodSafe", "time = ?",
                        new String[] {MsgList.get(adapterPosition).getTime()});
                MsgList.remove(adapterPosition);
                msgAdapter.notifyItemChanged(adapterPosition);
            }
        };
        recyclerView.setSwipeMenuItemClickListener(mMenuItemClickListener);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        msgAdapter = new MsgAdapter(MsgList);
        recyclerView.setAdapter(msgAdapter);
    }

    class FloatOnClickListener implements View.OnClickListener {

        @Override
        public void onClick(View v) {
            // TODO Auto-generated method stub
            EditActivity.startEditActivityForResult(MainActivity.this, null);
        }

    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_pic) {
            Intent intent = new Intent(this, PhotoViewActivity.class);
            startActivity(intent);
        } else if (id == R.id.nav_send) {

        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    protected void onResume() {
        super.onResume();

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent intent) {
        super.onActivityResult(requestCode, resultCode, intent);
        try {
            stringEditInfo = intent.getStringExtra("edit_return");// 取出传过来的Diary对象
            Message temp = new Gson().fromJson(stringEditInfo, Message.class);
            if (stringEditInfo != null) {
                if (resultCode == 200) {
                    relativeLayout.setVisibility(View.GONE);
                    recyclerView.setVisibility(View.VISIBLE);
                    MsgList.add(temp);
                    msgAdapter.notifyItemInserted(msgAdapter.getItemCount() + 1);
                } else {
                    MsgList.remove(temp.getPosition());
                    MsgList.add(temp.getPosition(), temp);
                    msgAdapter.notifyItemChanged(temp.getPosition());
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }
}
