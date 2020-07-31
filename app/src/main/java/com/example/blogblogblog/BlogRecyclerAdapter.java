package com.example.blogblogblog;

import android.content.Context;
import android.net.Uri;
import android.text.InputFilter;
import android.text.format.DateFormat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.squareup.picasso.Picasso;

import java.util.Date;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class BlogRecyclerAdapter extends RecyclerView.Adapter<BlogRecyclerAdapter.ViewHolder> {
    private List<BlogPost> blog_list;
   private FirebaseFirestore firestore;
   private Context context;


    @NonNull
    @Override

    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(parent.getContext()).inflate(R.layout.blog_item_preview,parent,false);
        firestore=FirebaseFirestore.getInstance();
        context=view.getContext();
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, int position) {
        String titleData=blog_list.get(position).getTitle();
        holder.setPreviewTitle(titleData);
        String userId=blog_list.get(position).getUserId();
    //    String profileUrl=blog_list.get(position).getImage_url();
     //   if(profileUrl==null){

    //    }
   //     else {
            firestore.collection("Users").document(userId).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                    if (task.isSuccessful()) {
                        String username = task.getResult().getString("name");
                        String profilePic = task.getResult().getString("image");
                        holder.setUserData(username,profilePic);
                    }
                }
            });
 //       }
        long milliseconds =blog_list.get(position).getTimestamp().getTime();
        String dateString= DateFormat.format("MM/dd/yyyy", new Date(milliseconds)).toString();
        holder.setTime(dateString);
    }

    @Override
    public int getItemCount() {
        return blog_list.size();
    }

    public BlogRecyclerAdapter(List<BlogPost> blog_list) {
        this.blog_list = blog_list;

    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private TextView previewTitle,blogDate,username;
        private View mView;
        CircleImageView profileImg;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            mView = itemView;
        }

        public void setPreviewTitle(String text) {
            previewTitle = mView.findViewById(R.id.post_preview_title);
            previewTitle.setText(text);
        }
        public void setUserData(String username1,String url){
            profileImg=mView.findViewById(R.id.post_preview_profile_img);
            username=mView.findViewById(R.id.post_preview_username);
            username.setText(username1);
            RequestOptions options=new RequestOptions();
            options.placeholder(R.drawable.ic_cloud_download_black_24dp);
            Glide.with(context)
                    .applyDefaultRequestOptions(options)
                    .load(Uri.parse(url))
                    .into(profileImg);
        }
        public void setTime(String date){
            blogDate=mView.findViewById(R.id.post_preview_date);
            blogDate.setText(date);


        }


    }
}
