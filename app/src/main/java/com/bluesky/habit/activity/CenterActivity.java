package com.bluesky.habit.activity;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.FileProvider;

import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.bluesky.habit.R;
import com.bluesky.habit.entity.HabitUser;
import com.bluesky.habit.util.CommonMethod;
import com.bluesky.habit.util.LogUtils;
import com.bluesky.habit.view.CustomDialog;
import com.google.android.material.snackbar.Snackbar;

import java.io.File;
import java.io.IOException;

import cn.bmob.v3.BmobUser;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.UpdateListener;

import static com.bluesky.habit.util.CommonMethod.createImageFile;
import static com.bluesky.habit.util.CommonMethod.setPic;

public class CenterActivity extends AppCompatActivity implements View.OnClickListener {

    private static final String TAG = CenterActivity.class.getSimpleName();
    EditText mEtNickname;
    EditText mEtSex;
    EditText mEtAge;
    EditText mEtDesc;
    Button mBtnUpdate;
    Button mBtnLogout;
    ImageView mIvAvatar;
    private Button mBtnCamera;
    private Button mBtnAlbum;
    private Button mBtnCancel;
    private CustomDialog mDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_center);

/*        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);*/
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        initView();
        initEvent();
        initData();
    }

    private void initData() {
        HabitUser user = BmobUser.getCurrentUser(HabitUser.class);
        LogUtils.i(TAG, "当前登录的user信息:" + user.toString());
        mEtNickname.setText(user.getNickname());
        mEtSex.setText(user.getGender() ? "男" : "女");
        mEtAge.setText(user.getAge() + "");
        mEtDesc.setText(user.getDesc());
    }

    private void initView() {
        mEtNickname = findViewById(R.id.et_info_nickname);
        mEtSex = findViewById(R.id.et_info_sex);
        mEtAge = findViewById(R.id.et_info_age);
        mEtDesc = findViewById(R.id.et_info_desc);
        mBtnUpdate = findViewById(R.id.btn_info_update);
        mBtnLogout = findViewById(R.id.btn_info_exit);
        mIvAvatar = findViewById(R.id.iv_avatar);

        mDialog = new CustomDialog(this, WindowManager.LayoutParams.MATCH_PARENT
                , WindowManager.LayoutParams.MATCH_PARENT
                , R.layout.dialog_avatar, R.style.dialog_custom, Gravity.BOTTOM);

        mBtnCamera = mDialog.findViewById(R.id.btn_camera);
        mBtnAlbum = mDialog.findViewById(R.id.btn_album);
        mBtnCancel = mDialog.findViewById(R.id.btn_cancel);
    }

    private void initEvent() {
        mBtnUpdate.setOnClickListener(this);
        mBtnLogout.setOnClickListener(this);
        mIvAvatar.setOnClickListener(this);

        mBtnCamera.setOnClickListener(this);
        mBtnAlbum.setOnClickListener(this);
        mBtnCancel.setOnClickListener(this);
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                break;

            default:
        }
        return true;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_info_exit:
                BmobUser.logOut();
                finish();
                break;
            case R.id.btn_info_update:
                onUserInfoUpdate();
                break;
            case R.id.iv_avatar:
                mDialog.show();

                break;
            case R.id.btn_camera:
                currentPhotoPath = CommonMethod.dispatchTakePictureIntent(this, REQUEST_TAKE_PHOTO_FROM_CAMERA);
                mDialog.dismiss();
                break;
            case R.id.btn_album:
                openAlbum();
                mDialog.dismiss();
                break;
            case R.id.btn_cancel:
                mDialog.dismiss();
                break;
            default:

        }
    }

    static String currentPhotoPath = "";
    static String corpImagePath = "";
    static final int REQUEST_TAKE_PHOTO_FROM_CAMERA = 100;
    static final int REQUEST_CHOICE_IMAGE_FROM_GALLERY = 101;
    static final int REQUEST_CROP_IMAGE = 102;

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if (resultCode != RESULT_CANCELED) {
            switch (requestCode) {
                case REQUEST_TAKE_PHOTO_FROM_CAMERA:
                    //拍照回调
//                    CommonMethod.setPic(mIvAvatar, currentPhotoPath);
                    cropImage(Uri.fromFile(new File(currentPhotoPath)));
                    break;
                case REQUEST_CHOICE_IMAGE_FROM_GALLERY:
                    //相册回调
                    cropImage(data.getData());
                    break;
                case REQUEST_CROP_IMAGE:
                    //裁剪回调
                    //设置头像到imageview
                    setPic(mIvAvatar, corpImagePath);
                    File file = new File(currentPhotoPath);
                    if (file != null) {
                        file.delete();
                    }
                    break;
                default:

            }
        }
    }

    private void cropImage(Uri uri) {
        if (uri == null) {
            return;
        } else {
            try {
                File file = CommonMethod.createImageFile(this);
                corpImagePath = file.getAbsolutePath();
                CommonMethod.cropImageUri(this, uri, Uri.fromFile(file), 1, 1, 320, 320, REQUEST_CROP_IMAGE);
            } catch (IOException e) {
                e.printStackTrace();
            }

        }
    }

    /**
     * 检查设备是否存在SDCard的工具方法
     */
    public static boolean hasSdcard() {
        String state = Environment.getExternalStorageState();
        return state.equals(Environment.MEDIA_MOUNTED);
    }


    /**
     * 打开系统相册
     */
    private void openAlbum() {
//        Intent pickIntent=new Intent(Intent.ACTION_PICK,MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setDataAndType(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, "image/*");
        startActivityForResult(intent, REQUEST_CHOICE_IMAGE_FROM_GALLERY);

    }


    private void onUserInfoUpdate() {
        String nickname = mEtNickname.getText().toString().trim();
        String sex = mEtSex.getText().toString().trim();
        String age = mEtAge.getText().toString().trim();
        String desc = mEtDesc.getText().toString().trim();
        if (TextUtils.isEmpty(nickname) || TextUtils.isEmpty(sex) || TextUtils.isEmpty(age) || TextUtils.isEmpty(desc)) {
            Snackbar.make(getWindow().getDecorView(), "任何一项不能为空", Snackbar.LENGTH_SHORT);
        } else {
            HabitUser user = BmobUser.getCurrentUser(HabitUser.class);
            user.setNickname(nickname);
            user.setAge(Integer.parseInt(age));
            user.setDesc(desc);
            user.setGender(sex.equals("男") ? true : false);
            user.update(new UpdateListener() {
                @Override
                public void done(BmobException e) {
                    if (e == null) {
                        Snackbar.make(CenterActivity.this.getWindow().getDecorView(), "更新用户信息成功!", Snackbar.LENGTH_SHORT);

                    } else {
                        Snackbar.make(CenterActivity.this.getWindow().getDecorView(), "更新用户信息失败!", Snackbar.LENGTH_SHORT);

                    }
                }
            });
        }

    }
}
