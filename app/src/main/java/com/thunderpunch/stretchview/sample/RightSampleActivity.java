package com.thunderpunch.stretchview.sample;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.CoordinatorLayout;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.thunderpunch.stretchview.R;
import com.thunderpunch.stretchview.view.StretchBehavior;
import com.thunderpunch.stretchview.view.StretchView;

/**
 * Created by thunderpunch on 2017/3/29
 * Description:
 */

public class RightSampleActivity extends AppCompatActivity {
    private RecyclerView rcv;
    private StretchView sv;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_right_sample);

        sv = (StretchView) findViewById(R.id.sv);
        sv.setDrawHelper(new ArcDrawHelper(sv, ContextCompat.getColor(RightSampleActivity.this, R.color.colorPrimary), 40));

        rcv = (RecyclerView) findViewById(R.id.rcv);
        rcv.setLayoutManager(new LinearLayoutManager(RightSampleActivity.this, LinearLayoutManager.HORIZONTAL, false));
        rcv.addItemDecoration(new RcvDecoration(0, (int) getResources().getDimension(R.dimen.divider_horzontal), LinearLayoutCompat.HORIZONTAL));
        rcv.setAdapter(new RecyclerView.Adapter() {
            @Override
            public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
                return new VH(LayoutInflater.from(RightSampleActivity.this).inflate(R.layout.item_horizontal, parent, false));
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
        if (v.getId() == R.id.exit) {
            //获取behavior,并折叠
            final StretchBehavior b = (StretchBehavior) ((CoordinatorLayout.LayoutParams) sv.getLayoutParams()).getBehavior();
            b.setIsShow(sv, false);
        }
    }


    class VH extends RecyclerView.ViewHolder {
        public VH(View itemView) {
            super(itemView);
        }
    }
}
