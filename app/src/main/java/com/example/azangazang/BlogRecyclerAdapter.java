package com.example.azangazang;

import android.content.Context;
import android.text.format.DateFormat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.util.Date;
import java.util.List;

public class BlogRecyclerAdapter extends RecyclerView.Adapter<BlogRecyclerAdapter.ViewHolder> {

    public List<BlogPost> blog_list;
    public Context context;

    public BlogRecyclerAdapter(List<BlogPost> blog_list) {

        this.blog_list = blog_list;

    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.blog_list_item, parent, false);
        context = parent.getContext();
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

        String desc_data = blog_list.get(position).getTitle();
        holder.setDescText(desc_data);

        String image_url = blog_list.get(position).getImage_uri();
        holder.setBlogImage(image_url);

        String user_id = blog_list.get(position).getUser_id();

        //User Data will be retirived here...
        long milliseconds = blog_list.get(position).getTimestamp().getTime();
        String dateString = DateFormat.format("MM/dd/yyyy", new Date(milliseconds)).toString();

        holder.setTime(dateString);
    }

    @Override
    public int getItemCount() {
        return blog_list.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        private View mView;

        private TextView descView;
        private ImageView blogImageView;
        private TextView blogDate;

        public ViewHolder(View itemView) {
            super(itemView);
            mView = itemView;
        }

        public void setDescText(String desc) {

            descView = mView.findViewById(R.id.blog_desc);
            descView.setText(desc);
        }

        public void setBlogImage(String downloadUri) {

            blogImageView = mView.findViewById(R.id.blog_image);
            Glide.with(context).load(downloadUri).into(blogImageView);

        }

        public void setTime(String date) {

            blogDate = mView.findViewById(R.id.blog_date);
            blogDate.setText(date);
        }

    }
}
