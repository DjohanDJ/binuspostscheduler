package com.example.binuspostscheduler.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.binuspostscheduler.R;
import com.example.binuspostscheduler.activities.SetFacebookPages;
import com.example.binuspostscheduler.authentications.UserSession;
import com.example.binuspostscheduler.models.FacebookPages;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class FacebookPagesAdapter extends RecyclerView.Adapter<FacebookPagesAdapter.MyViewHolder>{

    Context ctx;
    ArrayList<FacebookPages> fp;

    public FacebookPagesAdapter(Context ctx, ArrayList<FacebookPages> fp) {
        this.ctx = ctx;
        this.fp = fp;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(ctx);
        View view = inflater.inflate(R.layout.facebook_pages_list, parent, false);

        return new FacebookPagesAdapter.MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        holder.pagetitle.setText(fp.get(position).getName());

        if(fp.get(position).getStatus().equals("active")){
            holder.checkbox.setChecked(true);
        }

        holder.checkbox.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                boolean st = holder.checkbox.isChecked();

                Map<String, Object> map = new HashMap<>();
                map.put("access_token", fp.get(position).getAccess_token());
                map.put("id", fp.get(position).getId());
                map.put("name", fp.get(position).getName());
                map.put("uid", fp.get(position).getUid());
                if(st){
                    map.put("status", "active");
                }else map.put("status", "not active");


                FirebaseFirestore db = FirebaseFirestore.getInstance();
                db.collection("users").document(UserSession.getCurrentUser().getId()).collection("accounts")
                        .document("facebook").collection("pages").document(fp.get(position).getId()).update(map);
            }
        });
    }

    @Override
    public int getItemCount() {
        return fp.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        TextView pagetitle;
        CheckBox checkbox;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            pagetitle = itemView.findViewById(R.id.pagesTitle);
            checkbox = itemView.findViewById(R.id.checkboxPages);
        }
    }
}
