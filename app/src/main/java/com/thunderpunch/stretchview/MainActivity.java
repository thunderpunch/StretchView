package com.thunderpunch.stretchview;

import android.content.Intent;
import android.graphics.Color;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import com.thunderpunch.stretchview.sample.RightSampleActivity;
import com.thunderpunch.stretchview.sample.RcvDecoration;
import com.thunderpunch.stretchview.view.StretchView;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        final int startColor = ContextCompat.getColor(MainActivity.this, R.color.colorPrimary);
        final int endColor = ContextCompat.getColor(MainActivity.this, R.color.colorTeal_400);
        final StretchView sv = (StretchView) findViewById(R.id.sv);
        sv.setOnStretchListener(new StretchView.OnStretchListener() {
            @Override
            public void onTranslation(int trans) {
                if (sv.getDirection() == StretchView.BOTTOM || sv.getDirection() == StretchView.RIGHT) {
                    trans = -trans;
                }
                if (trans > 0) {
                    if (trans >= sv.getContentSpace()) {
                        //拉伸状态改变底色
                        final float percent = (trans - sv.getContentSpace()) * 1.0f / sv.getStretchSize() * 0.8f;
                        sv.setBackgroundColor(getGradientColor(startColor, endColor, percent));
                    } else {
                        //非拉伸状态使用初始颜色
                        sv.setBackgroundColor(startColor);
                    }
                }
            }
        });

        final RecyclerView rcv = (RecyclerView) findViewById(R.id.rcv);
        rcv.addItemDecoration(new RcvDecoration((int) getResources().getDimension(R.dimen.divider_vertical), 0));
        rcv.setLayoutManager(new LinearLayoutManager(MainActivity.this, LinearLayoutManager.VERTICAL, false));
        rcv.setAdapter(new RecyclerView.Adapter() {
            @Override
            public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
                return new VH(LayoutInflater.from(MainActivity.this).inflate(R.layout.item_vertical, parent, false));
            }

            @Override
            public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {

            }

            @Override
            public int getItemCount() {
                return 10;
            }
        });

    }

    public void click(View v) {

    }

    class VH extends RecyclerView.ViewHolder {
        public VH(View itemView) {
            super(itemView);
        }
    }

    private static int getGradientColor(int startColor, int endColor, float percent) {
        int sr = (startColor & 0xff0000) >> 0x10;
        int sg = (startColor & 0xff00) >> 0x8;
        int sb = (startColor & 0xff);

        int er = (endColor & 0xff0000) >> 0x10;
        int eg = (endColor & 0xff00) >> 0x8;
        int eb = (endColor & 0xff);

        int cr = (int) (sr * (1 - percent) + er * percent);
        int cg = (int) (sg * (1 - percent) + eg * percent);
        int cb = (int) (sb * (1 - percent) + eb * percent);
        return Color.argb(0xff, cr, cg, cb);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item_vertical clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_right) {
            startActivity(new Intent(MainActivity.this, RightSampleActivity.class));
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
