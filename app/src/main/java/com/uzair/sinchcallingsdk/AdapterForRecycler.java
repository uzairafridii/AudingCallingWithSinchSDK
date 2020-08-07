package com.uzair.sinchcallingsdk;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class AdapterForRecycler extends RecyclerView.Adapter<AdapterForRecycler.MyViewHolder>
{
    private List<UserModel> userModelList;
    private Context context;

    public AdapterForRecycler(List<UserModel> userModelList, Context context) {
        this.userModelList = userModelList;
        this.context = context;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType)
    {
        View myView = LayoutInflater.from(parent.getContext()).inflate(R.layout.recycler_design, null);
        return new MyViewHolder(myView);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position)
    {
        UserModel userModel = userModelList.get(position);
        holder.name.setText(userModel.getUserName());




    }

    @Override
    public int getItemCount() {
        return userModelList.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder
    {
        private TextView name ;
        private Button callBtn;

        public MyViewHolder(@NonNull View itemView)
        {
            super(itemView);
            name = itemView.findViewById(R.id.nameOfUser);
            callBtn = itemView.findViewById(R.id.callBtn);

            callBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    UserModel userModel = userModelList.get(getAdapterPosition());
                   ((MainActivity)context).callingUser(userModel);

                }
            });
        }


    }

}
