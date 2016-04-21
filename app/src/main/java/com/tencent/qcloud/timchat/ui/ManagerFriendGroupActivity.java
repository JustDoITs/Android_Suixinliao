package com.tencent.qcloud.timchat.ui;

import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.tencent.TIMCallBack;
import com.tencent.TIMFriendResult;
import com.tencent.TIMValueCallBack;
import com.tencent.qcloud.presentation.event.FriendshipEvent;
import com.tencent.qcloud.presentation.event.GroupEvent;
import com.tencent.qcloud.presentation.presenter.FriendshipManagerPresenter;
import com.tencent.qcloud.presentation.presenter.GetFriendGroupsPresenter;
import com.tencent.qcloud.presentation.viewfeatures.ManagerGroupView;
import com.tencent.qcloud.timchat.R;
import com.tencent.qcloud.timchat.adapters.GroupListAdapter;
import com.tencent.qcloud.timchat.model.FriendshipInfo;
import com.tencent.qcloud.timchat.ui.customview.NotifyDialog;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * 好友分组管理
 */
public class ManagerFriendGroupActivity extends FragmentActivity implements ManagerGroupView, View.OnClickListener {

    private final String TAG = ManagerFriendGroupActivity.class.getSimpleName();

    GetFriendGroupsPresenter mGetFriendGroupsPresenter;
    private ListView mMyGroupList;
    private GroupListAdapter mGroupListAdapter;
    private LinearLayout mAddGroup;
    private List<String> groups = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.manager_group);
        mMyGroupList = (ListView) findViewById(R.id.group_list);
        mAddGroup = (LinearLayout) findViewById(R.id.add_group);
        mAddGroup.setOnClickListener(this);
        groups.addAll(FriendshipInfo.getInstance().getGroups());
        mGroupListAdapter = new GroupListAdapter(this, groups, this);
        mMyGroupList.setAdapter(mGroupListAdapter);


    }


    @Override
    public void notifyGroupListChange() {
        mGetFriendGroupsPresenter.getFriendGroupList();
    }




    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.add_group) {
            addDialog();
        }
    }


    private Dialog addGroupDialog;
    private void addDialog() {
        addGroupDialog = new Dialog(this, R.style.dialog);
        addGroupDialog.setContentView(R.layout.dialog_addgroup);
        TextView btnYes = (TextView) addGroupDialog.findViewById(R.id.confirm_btn);
        TextView btnNo = (TextView) addGroupDialog.findViewById(R.id.cancel_btn);
        final EditText inputView = (EditText) addGroupDialog.findViewById(R.id.input_group_name);
        btnNo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                addGroupDialog.dismiss();
            }
        });

        btnYes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final String groupname = inputView.getText().toString();
                if (groupname.equals("")) {
                    Toast.makeText(ManagerFriendGroupActivity.this, getString(R.string.add_dialog_null), Toast.LENGTH_SHORT).show();
                } else {
                    FriendshipManagerPresenter.createFriendGroup(groupname, new TIMValueCallBack<List<TIMFriendResult>>() {
                        @Override
                        public void onError(int i, String s) {
                            Log.e(TAG, "onError code " + i + " msg " + s);
                        }

                        @Override
                        public void onSuccess(List<TIMFriendResult> timFriendResults) {
                            Toast.makeText(ManagerFriendGroupActivity.this, getString(R.string.add_group_succ), Toast.LENGTH_SHORT).show();
                            FriendshipEvent.getInstance().OnAddFriendGroups(null);
                            groups.add(groupname);
                            mGroupListAdapter.notifyDataSetChanged();
                            FriendshipEvent.getInstance().OnAddFriendGroups(null);
                        }
                    });
                }
                addGroupDialog.dismiss();
            }
        });
        Window window = addGroupDialog.getWindow();
        window.setGravity(Gravity.TOP);
        WindowManager.LayoutParams lp = window.getAttributes();
        window.setAttributes(lp);
        addGroupDialog.show();
    }

    public void deleteGroup(int position) {
        deleteDialog(position);
    }


    private void deleteDialog(final int position) {
        NotifyDialog dialog = new NotifyDialog();
        dialog.show(getString(R.string.delete_dialog_subtitle)+groups.get(position)+getString(R.string.delete_dialog_subtitle_sur), ManagerFriendGroupActivity.this.getSupportFragmentManager(), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                FriendshipManagerPresenter.delFriendGroup(groups.get(position), new TIMCallBack() {
                    @Override
                    public void onError(int i, String s) {
                        Log.e(TAG, "onError code " + i + " msg " + s);

                    }

                    @Override
                    public void onSuccess() {
                        Toast.makeText(ManagerFriendGroupActivity.this, getString(R.string.delete_group_succ), Toast.LENGTH_SHORT).show();
                        FriendshipEvent.getInstance().OnDelFriendGroups(Collections.singletonList(groups.get(position)));
                        groups.remove(position);
                        mGroupListAdapter.notifyDataSetChanged();
                    }
                });
            }
        });
//        final TIMFriendGroup groupinfo = mMyListTitle.get(position);
//        deleteGroupDialog = new Dialog(this, R.style.dialog);
//        deleteGroupDialog.setContentView(R.layout.dialog_delete_group);
//        TextView btnYes = (TextView) deleteGroupDialog.findViewById(R.id.confirm_btn);
//        TextView btnNo = (TextView) deleteGroupDialog.findViewById(R.id.cancel_btn);
//        TextView deleteGroup = (TextView) deleteGroupDialog.findViewById(R.id.select_delete_group);
//        deleteGroup.setText(groupinfo.getGroupName());
//        btnNo.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                deleteGroupDialog.dismiss();
//            }
//        });
//
//        btnYes.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                mManagerMyGroupPresenter.deleteFriendGroup(groupinfo.getGroupName());
//                deleteGroupDialog.dismiss();
//            }
//        });
//        deleteGroupDialog.show();
    }


}
