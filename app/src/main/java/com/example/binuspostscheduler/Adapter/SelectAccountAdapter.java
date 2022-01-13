package com.example.binuspostscheduler.Adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import com.example.binuspostscheduler.R;
import com.example.binuspostscheduler.fragments.SelectAccountFragment;
import com.example.binuspostscheduler.models.Account;

import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.List;

public class SelectAccountAdapter extends RecyclerView.Adapter<SelectAccountAdapter.ViewHolder> {

    private List<Account> accounts;
    private Context ctx;
    private HashMap<String, Account> map;
    private SelectAccountFragment fragment;
    public SelectAccountAdapter(List<Account> accounts, Context ctx, SelectAccountFragment fragment) {
        this.accounts = accounts;
        this.ctx = ctx;
        map = new HashMap();
        this.fragment = fragment;
    }

    @NonNull
    @NotNull
    @Override
    public SelectAccountAdapter.ViewHolder onCreateViewHolder(@NonNull @NotNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(ctx);
        View view = inflater.inflate(R.layout.select_account_view_layout, parent, false);

        return new SelectAccountAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull @NotNull SelectAccountAdapter.ViewHolder holder, @SuppressLint("RecyclerView") int position) {

        if(accounts.get(position).getType().equalsIgnoreCase("Twitter")){
            holder.account_icon.setImageResource(R.drawable.tw__ic_logo_default);
            holder.account_username.setText(accounts.get(position).getUsername());
            holder.account_checked_icon.setImageResource(R.drawable.ic_baseline_check_circle_outline_24_alt);
        }
        else if(accounts.get(position).getType().equalsIgnoreCase("Facebook")){
            //facebook
        }
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(accounts.get(position).isChecked()){
                    holder.account_checked_icon.setImageResource(R.drawable.ic_baseline_check_circle_outline_24_alt);
                    map.remove(accounts.get(position).getType());
                }else{
                    holder.account_checked_icon.setImageResource(R.drawable.ic_baseline_check_circle_24);
                    map.put(accounts.get(position).getType(),accounts.get(position));
                }
                accounts.get(position).setChecked(!accounts.get(position).isChecked());
                fragment.changeButton(!map.isEmpty());
            }
        });
    }

    @Override
    public int getItemCount() {
        return accounts.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{
        private ImageView account_icon;
        private ImageView account_checked_icon;
        private TextView account_username;
        public ViewHolder(@NonNull @NotNull View itemView) {
            super(itemView);
            account_icon = itemView.findViewById(R.id.account_type_icon);
            account_checked_icon = itemView.findViewById(R.id.account_checked_icon);
            account_username = itemView.findViewById(R.id.account_username);
        }
    }

    public HashMap<String, Account> getMap() {
        return map;
    }

    public void setMap(HashMap<String, Account> map) {
        this.map = map;
    }


}
