package com.jiacw.t03mynews.activity;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.graphics.Palette;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.jiacw.t03mynews.R;

/**
 * Created by Jiacw on 15:22 29/2/2016.
 * Email: 313133710@qq.com
 * Function:
 */
public class CollapseActivity extends AppCompatActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.collapse_layout);
        final CollapsingToolbarLayout ctl = (CollapsingToolbarLayout) findViewById(R.id.cl_ctl);
        ctl.setTitle("Test UI");
        Bitmap bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.anima);
        Palette.generateAsync(bitmap, new Palette.PaletteAsyncListener() {
            @Override
            public void onGenerated(Palette palette) {
                Palette.Swatch swatch = palette.getDarkMutedSwatch();
                ctl.setContentScrimColor(swatch.getRgb());
            }
        });
        RecyclerView recyclerView = (RecyclerView) findViewById(R.id.cl_rv);
        recyclerView.setLayoutManager(new LinearLayoutManager(this,LinearLayoutManager.VERTICAL,false));
        recyclerView.setAdapter(new RecyclerView.Adapter() {
            @Override
            public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
                return new Viewholder(new TextView(parent.getContext()));
            }

            @Override
            public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
                if (holder instanceof Viewholder){
                    ((Viewholder) holder).mTextView.setText("text here");
                }
            }

            @Override
            public int getItemCount() {
                return 20;
            }
        });
    }

    class Viewholder extends RecyclerView.ViewHolder {
        TextView mTextView;

        public Viewholder(View itemView) {
            super(itemView);
            mTextView = (TextView) itemView;
        }
    }
}
