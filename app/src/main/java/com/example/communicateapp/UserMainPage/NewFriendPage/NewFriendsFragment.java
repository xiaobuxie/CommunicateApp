package com.example.communicateapp.UserMainPage.NewFriendPage;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;
import androidx.core.util.Pair;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import com.example.communicateapp.MyApplication;
import com.example.communicateapp.R;
import com.example.communicateapp.database.FriendRequest;

import java.util.ArrayList;
import java.util.List;

public class NewFriendsFragment extends Fragment {
    // view
    View view;
    SwipeRefreshLayout swipeRefreshLayout;
    TextView swipeText;
    // recyclerview
    RecyclerView requestRecyclerView;
    RequestFriendAdapter requestFriendAdapter;

    // data
    List<FriendRequest> requestList;

    String UserId;
    // view model
    RequestFriendViewModel requestFriendViewModel;

    public NewFriendsFragment(){}
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){
        // 避免重复createview
//        if( view != null) return view;

        view = inflater.inflate(R.layout.new_friend_fragment,container,false);
        // init view
        swipeRefreshLayout = view.findViewById(R.id.request_swiperefresh_layout);
        swipeText = view.findViewById(R.id.request_swiperefresh_text);
        requestRecyclerView = view.findViewById(R.id.request_recyclerview);
        UserId = ((MyApplication)getActivity().getApplication()).UserId;

        // 初始化流程：加载本地request，再加载服务端的
        initViewModel();
        initRecyclerView();
        initSwipeFreshLayout();

        return view;
    }

    private void initViewModel(){
        requestFriendViewModel = new ViewModelProvider(this).get(RequestFriendViewModel.class);
        requestFriendViewModel.setInfo(getContext(),UserId);

        requestFriendViewModel.getLocalListSingal().observe(getViewLifecycleOwner(), new Observer<List<FriendRequest> >() {
            @Override
            public void onChanged(List<FriendRequest> friendRequests) {
                Log.d("NewFriendsFragment"," local size : " + friendRequests.size());
                for(int i=  0;i<friendRequests.size();++i){
                    FriendRequest friendRequest = friendRequests.get(i);
                    requestFriendAdapter.addRequestFriend(requestList.size(),friendRequest);
                }
            }
        });

        requestFriendViewModel.getServeListSingal().observe(getViewLifecycleOwner(), new Observer<List<FriendRequest>>() {
            @Override
            public void onChanged(List<FriendRequest> friendRequests) {
                for(int i = 0;i<friendRequests.size();++i){
                    FriendRequest friendRequest = friendRequests.get(i);
                    requestFriendAdapter.addRequestFriend(requestList.size(),friendRequest);
                }
                swipeText.setVisibility(View.GONE);
                Toast.makeText(getContext(),"刷新成功",Toast.LENGTH_SHORT).show();
                swipeRefreshLayout.setRefreshing(false);
            }
        });
    }

    private void initRecyclerView(){
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
        requestRecyclerView.setLayoutManager(linearLayoutManager);

        requestList = new ArrayList<>();
        requestFriendAdapter = new RequestFriendAdapter(requestList,getContext());
        requestRecyclerView.setAdapter(requestFriendAdapter);

        requestRecyclerView.setItemAnimator(new DefaultItemAnimator());
        //加载本地数据
        requestFriendViewModel.queryLocalRequestList();
    }

    private void initSwipeFreshLayout(){
        swipeText.setVisibility(View.GONE);
        swipeRefreshLayout.setEnabled(true);
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                swipeText.setVisibility(View.VISIBLE);
                swipeText.setText("正在刷新");
                requestFriendViewModel.queryServeRequestList(MyApplication.getMyApplicationInstance().getNowTime());
            }
        });
    }

}
