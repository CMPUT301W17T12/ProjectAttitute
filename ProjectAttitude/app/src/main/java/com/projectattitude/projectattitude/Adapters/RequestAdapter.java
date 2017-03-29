package com.projectattitude.projectattitude.Adapters;

import android.content.Context;
import android.support.annotation.NonNull;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.TextView;

import com.projectattitude.projectattitude.Controllers.ElasticSearchRequestController;
import com.projectattitude.projectattitude.Controllers.ElasticSearchUserController;
import com.projectattitude.projectattitude.Objects.FollowRequest;
import com.projectattitude.projectattitude.Objects.User;
import com.projectattitude.projectattitude.R;

import java.util.ArrayList;

import static java.util.logging.Logger.global;

/**
 * Created by henrywei on 3/28/17.
 */

public class RequestAdapter extends ArrayAdapter<FollowRequest> {

    private FollowRequest request;
    private RequestAdapter adapter = this;

    public RequestAdapter(Context context, ArrayList<FollowRequest> requests){
        super(context, 0, requests);
    }

    @NonNull
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        request = getItem(position);

        Log.d("Error", "Following Request"+request.toString());

        if (convertView == null){
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.notification_item, parent, false);
        }

        TextView requesterText = (TextView) convertView.findViewById(R.id.notificationTextView);
        Button acceptButton = (Button) convertView.findViewById(R.id.acceptButton);
        Button denyButton = (Button) convertView.findViewById(R.id.denyButton);

        //set text to be user who requested a follow to this user
        requesterText.setText(request.getRequester());

        //accept/deny on item click
        acceptButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Find requester
                User user = null;
                try{
                    ElasticSearchUserController.GetUserTask getUserTask = new ElasticSearchUserController.GetUserTask();
                    user = getUserTask.execute(request.getRequester()).get();
                }
                catch(Exception e){
                    e.printStackTrace();
                }
                if(user != null){ //If user found, update its following list with requestee
                    user.addFollow(request.getRequestee());
                    try{//Update user in database
                        ElasticSearchUserController.UpdateUserRequestTask updateUserRequestTask = new ElasticSearchUserController.UpdateUserRequestTask();
                        updateUserRequestTask.execute(user);
                        //Now, delete request since request has been accepted
                        ElasticSearchRequestController.DeleteRequestTask deleteRequestTask = new ElasticSearchRequestController.DeleteRequestTask();
                        deleteRequestTask.execute(request);
                    }
                    catch(Exception e){
                        Log.d("error", "Could not add delete request");
                        ArrayList<String> followList = user.getFollowList();
                        followList.remove(request.getRequestee());
                        e.printStackTrace();
                    }

                }else{
                    Log.d("error", "Requester not found!");
                }
                adapter.remove(request);
                adapter.notifyDataSetChanged();
            }
        });

        denyButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //if denied, just delete request
                ElasticSearchRequestController.DeleteRequestTask deleteRequestTask = new ElasticSearchRequestController.DeleteRequestTask();
                deleteRequestTask.execute(request);
                adapter.remove(request);
                adapter.notifyDataSetChanged();
            }
        });

        return convertView;
    }
}