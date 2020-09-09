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
import com.bumptech.glide.request.RequestOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.mikhaellopez.circularimageview.CircularImageView;

import org.w3c.dom.Text;

import java.lang.reflect.Field;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class BlogRecyclerAdapter extends RecyclerView.Adapter<BlogRecyclerAdapter.ViewHolder> {

    public List<BlogPost> blog_list;
    public Context context;

    private FirebaseFirestore firebaseFirestore;
    private FirebaseAuth firebaseAuth;

    public BlogRecyclerAdapter(List<BlogPost> blog_list) {

        this.blog_list = blog_list;

    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.blog_list_item, parent, false);
        context = parent.getContext();
        firebaseFirestore = FirebaseFirestore.getInstance();
        firebaseAuth = FirebaseAuth.getInstance();
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, int position) {

        final String blogPostId = blog_list.get(position).BlogPostId;
        final String currentUserId = firebaseAuth.getCurrentUser().getUid();

        String desc_data = blog_list.get(position).getTitle();
        holder.setDescText(desc_data);

        String image_url = blog_list.get(position).getImage_uri();
        String thumbUri = blog_list.get(position).getThumb();
        holder.setBlogImage(image_url, thumbUri);

        String user_id = blog_list.get(position).getUser_id();

        //User Data will be retirived here...
        firebaseFirestore.collection("Users").document(user_id).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {

                if (task.isSuccessful()) {

                    String userName = task.getResult().getString("name");
                    String userImage = task.getResult().getString("image");

                    holder.setUserData(userName, userImage);

                } else {
                    //Firebase Exception
                }

            }
        });
        long milliseconds = blog_list.get(position).getTimestamp().getTime();
        String dateString = DateFormat.format("yyyy/MM/dd", new Date(milliseconds)).toString();

        holder.setTime(dateString);

        //Like Feature
        holder.blogLikeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Map<String, Object> likesMap = new HashMap<>();
                likesMap.put("timestamp", FieldValue.serverTimestamp());

                firebaseFirestore.collection("Posts/" + blogPostId + "/Likes")
                        .document(currentUserId).set(likesMap);

            }
        });
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

        private TextView blogUserName;
        private CircularImageView blogUserImage;

        private ImageView blogLikeBtn;
        private TextView blogLikeCount;

        public ViewHolder(View itemView) {
            super(itemView);
            mView = itemView;

            blogLikeBtn = mView.findViewById(R.id.blog_like_btn);
        }

        public void setDescText(String desc) {

            descView = mView.findViewById(R.id.blog_desc);
            descView.setText(desc);
        }

        public void setBlogImage(String downloadUri, String thumbUri) {

            blogImageView = mView.findViewById(R.id.blog_image);

            RequestOptions requestOptions = new RequestOptions();
            requestOptions.placeholder(R.drawable.plus_image);

            Glide.with(context).applyDefaultRequestOptions(requestOptions)
                    .load(downloadUri).thumbnail().into(blogImageView);

        }

        public void setTime(String date) {

            blogDate = mView.findViewById(R.id.blog_date);
            blogDate.setText(date);
        }

        public void setUserData(String name, String image) {

            blogUserImage = mView.findViewById(R.id.blog_user_image);
            blogUserName = mView.findViewById(R.id.blog_user_name);

            blogUserName.setText(name);

            RequestOptions placeholderOption = new RequestOptions();
            placeholderOption.placeholder(R.drawable.profile);

            Glide.with(context).applyDefaultRequestOptions(placeholderOption).load(image).into(blogUserImage);
        }
    }
}
