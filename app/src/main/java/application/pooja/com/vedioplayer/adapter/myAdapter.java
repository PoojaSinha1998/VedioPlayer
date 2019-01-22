package application.pooja.com.vedioplayer.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;

import java.util.ArrayList;

import application.pooja.com.vedioplayer.R;
import application.pooja.com.vedioplayer.model.Example;

public class myAdapter extends RecyclerView.Adapter<myAdapter.MyViewHolder> {

    ArrayList<Example> data;
    int id;

    private ItemClickListener mClickListener;
    Context context;

    public myAdapter(ArrayList<Example> data, Context context) {
        this.data = data;
        this.context = context;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.my_adapter, parent, false);

        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, final int position) {
        RequestOptions options = new RequestOptions();
        options.centerCrop();
        String url = data.get(position).getThumb();

        Glide.with(context)
                .load(url)
                .apply(options)
                .into(holder.mVideo);

        holder.mTitle.setText(data.get(position).getTitle());
        holder.mDiscription.setText(data.get(position).getDescription());
        holder.mVideo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mClickListener != null) mClickListener.onItemClick(view,data.get(position).getId());

                Log.d("VT","inside viewHolder id" +data.get(position).getId());
                Log.d("VT","inside viewHolder  title" +data.get(position).getTitle());
            }
        });
        holder.mDiscription.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mClickListener != null) mClickListener.onItemClick(view,data.get(position).getId());

                Log.d("VT","inside viewHolder id" +data.get(position).getId());
                Log.d("VT","inside viewHolder  title" +data.get(position).getTitle());

            }
        });
        holder.mTitle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mClickListener != null) mClickListener.onItemClick(view,data.get(position).getId());

                Log.d("VT","inside viewHolder  id" +data.get(position).getId());
                Log.d("VT","inside viewHolder  title" +data.get(position).getTitle());
            }
        });
    }


    @Override
    public int getItemCount() {
        return data.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        public ImageView mVideo;
        TextView mTitle, mDiscription;

        public MyViewHolder(View view) {
            super(view);
            mVideo = view.findViewById(R.id.vVideo);
            mTitle = view.findViewById(R.id.tTitle);
            mDiscription = view.findViewById(R.id.tDiscription);

            Log.d("VT","inside viewHolder");

        }


    }
    // parent activity will implement this method to respond to click events
    public interface ItemClickListener {
        void onItemClick(View view,String id);
    }

    // allows clicks events to be caught
    public void setClickListener(ItemClickListener itemClickListener) {
        this.mClickListener = itemClickListener;
    }
}

