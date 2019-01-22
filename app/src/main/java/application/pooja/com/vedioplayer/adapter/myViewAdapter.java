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

public class myViewAdapter extends RecyclerView.Adapter<myViewAdapter.MyViewHolder> {

    ArrayList<Example> data;
    ArrayList<Example> myData;
    String id;

    private ItemClickListener mClickListener;
    Context context;

    public myViewAdapter(ArrayList<Example> data, Context context,String id) {
        this.data = data;
        this.context = context;
        this.id = id;
        Log.d("VT","value of id in myViewAdapter "+id);
        myData = new ArrayList<Example>();


        for (int i = 0;i<data.size();i++)
        {
            if((data.get(i).getId()).equals(id))
            {
                Log.d("VT","id inside if "+data.get(i).getId());

            }
            else if(!(data.get(i).getId()).equals(id))
            {
                Log.d("VT","id inside else "+data.get(i).getId());
                myData.add(data.get(i));
            }
        }
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.my_view_adapter, parent, false);

        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, final int position) {
        Log.d("VT","value of id  inside myViewAdapter 1 : "+id);
        RequestOptions options = new RequestOptions();
        options.centerCrop();

        int mId = Integer.parseInt(data.get(position).getId());
        Log.d("VT","value of id  inside myViewAdapter 2: "+mId);

            String url = myData.get(position).getThumb();

            Glide.with(context)
                    .load(url)
                    .apply(options)
                    .into(holder.mVideo);

            holder.mTitle.setText(myData.get(position).getTitle());
            holder.mDiscription.setText(myData.get(position).getDescription());
            holder.mVideo.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (mClickListener != null)
                        mClickListener.onItemClick(view, myData.get(position).getId());

                    Log.d("VT", "inside viewHolder id" + myData.get(position).getId());
                    Log.d("VT", "inside viewHolder  title" + myData.get(position).getTitle());
                }
            });
            holder.mDiscription.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (mClickListener != null)
                        mClickListener.onItemClick(view, myData.get(position).getId());

                    Log.d("VT", "inside viewHolder id" + myData.get(position).getId());
                    Log.d("VT", "inside viewHolder  title" + myData.get(position).getTitle());

                }
            });
            holder.mTitle.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (mClickListener != null)
                        mClickListener.onItemClick(view, myData.get(position).getId());

                    Log.d("VT", "inside viewHolder  id" + myData.get(position).getId());
                    Log.d("VT", "inside viewHolder  title" + myData.get(position).getTitle());
                }
            });

    }


    @Override
    public int getItemCount() {
        return myData.size();
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
        void onItemClick(View view, String id);
    }

    // allows clicks events to be caught
    public void setClickListener(ItemClickListener itemClickListener) {
        this.mClickListener = itemClickListener;
    }
}

