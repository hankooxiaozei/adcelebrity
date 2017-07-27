package com.yundian.celebrity.ui.main.activity;

import android.app.Activity;
import android.content.Intent;
import android.text.Editable;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.netease.nim.uikit.common.media.picker.PickImageHelper;
import com.netease.nim.uikit.session.constant.Extras;
import com.qiniu.android.http.ResponseInfo;
import com.qiniu.android.storage.UpCompletionHandler;
import com.qiniu.android.storage.UploadManager;
import com.yundian.celebrity.R;
import com.yundian.celebrity.app.Constant;
import com.yundian.celebrity.base.BaseActivity;
import com.yundian.celebrity.bean.EventBusMessage;
import com.yundian.celebrity.bean.QiNiuImageToken;
import com.yundian.celebrity.bean.RequestResultBean;
import com.yundian.celebrity.listener.OnAPIListener;
import com.yundian.celebrity.networkapi.NetworkAPIFactoryImpl;
import com.yundian.celebrity.ui.view.ValidationWatcher;
import com.yundian.celebrity.utils.DisplayUtil;
import com.yundian.celebrity.utils.FormatUtil;
import com.yundian.celebrity.utils.HttpUtils;
import com.yundian.celebrity.utils.ImageLoaderUtils;
import com.yundian.celebrity.utils.LogUtils;
import com.yundian.celebrity.utils.SharePrefUtil;
import com.yundian.celebrity.utils.ToastUtils;
import com.yundian.celebrity.widget.NormalTitleBar;

import org.greenrobot.eventbus.EventBus;
import org.json.JSONObject;

import java.io.File;

import butterknife.Bind;
import butterknife.OnClick;

/**
 * 发布状态
 * Created by sll on 2017/7/13.
 */

public class PublishStateActivity extends BaseActivity {

    @Bind(R.id.nt_title)
    NormalTitleBar ntTitle;
    @Bind(R.id.feedbackContent)
    EditText feedbackContent;
    @Bind(R.id.feedbackContentNumber)
    TextView feedbackContentNumber;  //指示器
    @Bind(R.id.iv_add_pic)
    ImageView ivAddPic;
    @Bind(R.id.iv_clear)
    ImageView ivClear;
    private String pathUrl = "";


    @Override
    public int getLayoutId() {
        return R.layout.activity_publish_state;
    }

    @Override
    public void initPresenter() {

    }

    @Override
    public void initView() {
        ntTitle.setRightTitleVisibility(true);
        ntTitle.setRightTitle("发布");
        ntTitle.setRightTitleColor(R.color.color_8C0808);

        ntTitle.setLeftTitle("取消");
        ntTitle.setTvLeftVisiable(true);
        ntTitle.setLeftImagSrc(0);  //去掉返回图标

        initListener();

        feedbackContent.addTextChangedListener(mValidationWatcher);
    }

    private void initListener() {
        ntTitle.setOnRightTextListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (TextUtils.isEmpty(feedbackContent.getText().toString().trim()) &&
                        TextUtils.isEmpty(pathUrl)) {
                    ToastUtils.showShort("发布内容不能为空");
                } else {
                    publishState();//发布状态
                }
            }
        });

    }

    private UploadManager uploadManager = new UploadManager();

    private void publishState() {
        HttpUtils.doGetAsyn(Constant.QI_NIU_TOKEN_URL, new HttpUtils.CallBack() {
            @Override
            public void onRequestComplete(String result) {
                LogUtils.loge("请求到的http数据:" + result);
                final QiNiuImageToken tokenEntity = JSON.parseObject(result, QiNiuImageToken.class);
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        LogUtils.loge("生成的图片名称是:" + FormatUtil.createImageName());
                        uploadManager.put(pathUrl, FormatUtil.createImageName(), tokenEntity.getImageToken(),
                                new UpCompletionHandler() {
                                    @Override
                                    public void complete(String key, ResponseInfo info, JSONObject response) {
                                        //res包含hash、key等信息，具体字段取决于上传策略的设置
                                        if (info.isOK()) {
                                            Log.i("qiniu", "Upload Success");

                                            //拿到上传的图片地址,请求自己的服务器
                                            String imageUrl = Constant.QI_NIU_BASE_URL + key;
                                            LogUtils.loge("获取的图片地址:" + imageUrl);
                                            doSendContent(imageUrl);
                                        } else {
                                            Log.i("qiniu", "Upload Fail");
                                            //如果失败，这里可以把info信息上报自己的服务器，便于后面分析上传错误原因
                                        }
                                        Log.i("qiniu", key + ",\r\n " + info + ",\r\n " + response);

                                    }
                                }, null);
                    }
                });
            }
        });

    }
    //上传到自己的服务器里
    private void doSendContent(String imageUrl) {
        String s = feedbackContent.getText().toString();  //内容
        String starCode = SharePrefUtil.getInstance().getStarcode();
        NetworkAPIFactoryImpl.getDealAPI().publishState(s, imageUrl, starCode, new OnAPIListener<RequestResultBean>() {
            @Override
            public void onError(Throwable ex) {
                LogUtils.loge("发布失败");
            }

            @Override
            public void onSuccess(RequestResultBean requestResultBean) {

                if (requestResultBean.getCircle_id() != -1) {
                    EventBus.getDefault().post(new EventBusMessage(-65));
                    finish();
                }
            }
        });
    }

    private ValidationWatcher mValidationWatcher = new ValidationWatcher() {
        @Override
        public void afterTextChanged(Editable editable) {
            String importAbilityWords = String.valueOf(200 - editable.toString().trim().length());
            feedbackContentNumber.setText(String.format(getString(R.string.remainder_input_count), importAbilityWords));
        }
    };

    @OnClick({R.id.iv_add_pic, R.id.iv_clear})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.iv_add_pic:
                ToastUtils.showShort("添加图片...");
                showSelector(R.string.add_pic, 4);
                break;
            case R.id.iv_clear:
                ivAddPic.setImageResource(R.drawable.about_logo);
                ivClear.setVisibility(View.GONE);
                pathUrl = "";
                break;
        }
    }

    /**
     * 打开图片选择器
     */
    private void showSelector(int titleId, final int requestCode) {
        PickImageHelper.PickImageOption option = new PickImageHelper.PickImageOption();
        option.titleResId = titleId;
        option.multiSelect = false;  //是否多选
        option.crop = true;  //是否裁剪
        option.cropOutputImageWidth = DisplayUtil.getScreenWidth(this);
        option.cropOutputImageHeight = DisplayUtil.getScreenHeight(this);
//        option.outputPath = outPath;
        PickImageHelper.pickImage(PublishStateActivity.this, requestCode, option);

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        LogUtils.logd("jieshoudao 拍摄的回调:" + requestCode);
        if (resultCode == Activity.RESULT_OK && requestCode == 4) {
            String path = data.getStringExtra(Extras.EXTRA_FILE_PATH);
            updatePic(path);
        }
    }

    /**
     * 更新头像
     */
    private void updatePic(final String path) {
        if (TextUtils.isEmpty(path)) {
            return;
        }

        File file = new File(path);
        if (file == null) {
            return;
        }
        LogUtils.loge("获取到上传的图片的地址:" + path);
        ImageLoaderUtils.display(mContext, ivAddPic, path);
        pathUrl = path;
        ivClear.setVisibility(View.VISIBLE);
    }

}
