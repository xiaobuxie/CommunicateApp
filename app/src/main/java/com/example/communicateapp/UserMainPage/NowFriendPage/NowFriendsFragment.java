package com.example.communicateapp.UserMainPage.NowFriendPage;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.example.communicateapp.MyApplication;
import com.example.communicateapp.R;
import com.example.communicateapp.database.FriendShip;

import java.util.ArrayList;
import java.util.List;

public class NowFriendsFragment extends Fragment {
    private  View view;
    // recyclerview
    private RecyclerView friendRecyclerView;
    private FriendAdapter friendAdapter;
    private LinearLayoutManager linearLayoutManager;
    // data
    List<FriendShip> friendList;

    // viewModel
    FriendShipViewModel friendShipViewModel;

    // view info
    EditText search_id_text;
    Button search_button;
    SwipeRefreshLayout swipeRefreshLayout;
    TextView swipeText;

    public NowFriendsFragment(){ }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){
        // 避免重复createview
//        if( view != null) return view;
        view = inflater.inflate(R.layout.now_friends_fragment,container,false);

        friendList = new ArrayList<>();

        // init recyclerview 一开始是空，后面在初始化添加
        Log.i("NowFriendsFragment" , "now friend fragemnet create view");
        initRecyclerView();

        //init viewmodel
        initViewModel();

        // init view
        search_id_text = view.findViewById(R.id.search_friendId);
        search_button = view.findViewById(R.id.search_button);
        swipeRefreshLayout = view.findViewById(R.id.swipefresh_layout);
        swipeText = view.findViewById(R.id.swipefresh_text);
        initSwipeRefresh();
        search_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String search_userId = search_id_text.getText().toString();
                if(search_userId == null){
                    Toast.makeText(getApplicationContext(),"没有输入id",Toast.LENGTH_SHORT).show();
                }
                else{
                    // 调用 viewmodel
                    friendShipViewModel.addFriendRequest(search_userId,((MyApplication)getApplication()).getNowTime() );
                }
            }
        });


        return view;
    }

    // init viewmodel
    private void initViewModel(){
        Log.i("NowFriendsFragment" , "initViewModel");
        friendShipViewModel = new ViewModelProvider(this).get(FriendShipViewModel.class);
        friendShipViewModel.setInfo(getApplicationContext(),((MyApplication)getApplication()).UserId);
//        Log.d("NowFriendsFragment",((MyApplication)getApplication()).UserId);
        // bind 获取所有friend
        friendShipViewModel.getLocalFriendsListSingal().observe(getViewLifecycleOwner(), new Observer<List<FriendShip>>() {
            @Override
            public void onChanged(List<FriendShip> friendShipList) {
                for(int i = 0; i< friendShipList.size(); ++i){
                    FriendShip friendShip = friendShipList.get(i);
                    friendAdapter.addFriendItem(friendList.size(),friendShip);
                }
            }
        });

        friendShipViewModel.getServeFriendsListSingal().observe(getViewLifecycleOwner(), new Observer<List<FriendShip>>() {
            @Override
            public void onChanged(List<FriendShip> reFriendShipList) {
                for(int i = 0; i< reFriendShipList.size(); ++i){
                    FriendShip friendShip = reFriendShipList.get(i);
                    friendAdapter.addFriendItem(friendList.size(),friendShip);
                }
                swipeText.setVisibility(View.GONE);
                swipeRefreshLayout.setRefreshing(false);
            }
        });

        friendShipViewModel.getAddFriendRequestSingal().observe(getViewLifecycleOwner(), new Observer<Boolean>() {
            @Override
            public void onChanged(Boolean aBoolean) {
                if(aBoolean){
                    Toast.makeText(getApplicationContext(),"你的请求已发送",Toast.LENGTH_SHORT).show();
                }
                else{
                    Toast.makeText(getApplicationContext(),"无此用户",Toast.LENGTH_SHORT).show();
                }
            }
        });

        friendShipViewModel.queryLocalFriends();
    }

    // init recyclerView
    private void initRecyclerView(){
        Log.i("NowFriendsFragment" , "initRecyclerVIew");
        friendList = new ArrayList<>();
        friendRecyclerView = view.findViewById(R.id.friend_recyclerview);
        linearLayoutManager = new LinearLayoutManager(getApplicationContext());
        friendRecyclerView.setLayoutManager(linearLayoutManager);

        // init adapter
        friendAdapter = new FriendAdapter(friendList,getApplicationContext());
        friendRecyclerView.setAdapter(friendAdapter);
        friendRecyclerView.setItemAnimator(new DefaultItemAnimator());
    }

    private void initSwipeRefresh(){
        Log.i("NowFriendsFragment" , "initSwipeRefresh");
        swipeText.setVisibility(View.GONE);
        swipeRefreshLayout.setEnabled(true);
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                swipeText.setText("正在刷新");
                swipeText.setVisibility(View.VISIBLE);
                friendShipViewModel.queryServeFriends();
            }
        });
    }


    private Object getApplication() {
        return getActivity().getApplication();
    }

    private Context getApplicationContext() {
        return getActivity().getApplicationContext();
    }

}
