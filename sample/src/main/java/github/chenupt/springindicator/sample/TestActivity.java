package github.chenupt.springindicator.sample;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

public class TestActivity extends Activity{
    SpringView mSpringView;
    float radiusMax;
    float radiusMin;
    float radiusOffset;
    private float acceleration = 0.5f;
    private float headMoveOffset = 0.6f;
    private float footMoveOffset = 1- headMoveOffset;
    float positionOffset = 0;

    Handler mHandler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what){
                case 0x11:
                    mHandler.sendEmptyMessageDelayed(0x12, 200);
                    break;

                case 0x12:
                    Log.e("TAG", "positionOffset = "+ positionOffset);
                    positionOffset = positionOffset + 0.05f;
                    if(positionOffset >= 1){
                        positionOffset = positionOffset -1;
                    }
                    go(positionOffset);
                    mHandler.sendEmptyMessageDelayed(0x12, 200);
                    break;
            }
        }
    };

    private void go(float positionOffset){
        // radius
        float radiusOffsetHead = 0.5f;
        if(positionOffset < radiusOffsetHead){
            mSpringView.getHeadPoint().setRadius(radiusMin);
        }else{
            mSpringView.getHeadPoint().setRadius(((positionOffset-radiusOffsetHead)/(1-radiusOffsetHead) * radiusOffset + radiusMin));
        }
        float radiusOffsetFoot = 0.5f;
        if(positionOffset < radiusOffsetFoot){
            mSpringView.getFootPoint().setRadius((1-positionOffset/radiusOffsetFoot) * radiusOffset + radiusMin);
        }else{
            mSpringView.getFootPoint().setRadius(radiusMin);
        }

        // x
        float headX = 1f;
        if (positionOffset < headMoveOffset){
            float positionOffsetTemp = positionOffset / headMoveOffset;
            headX = (float) ((Math.atan(positionOffsetTemp*acceleration*2 - acceleration ) + (Math.atan(acceleration))) / (2 * (Math.atan(acceleration))));
        }
        mSpringView.getHeadPoint().setX(getTabX() - headX * getPositionDistance());
        float footX = 0f;
        if (positionOffset > footMoveOffset){
            float positionOffsetTemp = (positionOffset- footMoveOffset) / (1- footMoveOffset);
            footX = (float) ((Math.atan(positionOffsetTemp*acceleration*2 - acceleration ) + (Math.atan(acceleration))) / (2 * (Math.atan(acceleration))));
        }
        mSpringView.getFootPoint().setX(getTabX() - footX * getPositionDistance());

        // reset radius
        if(positionOffset == 0){
            mSpringView.getHeadPoint().setRadius(radiusMax);
            mSpringView.getFootPoint().setRadius(radiusMax);
        }

        mSpringView.postInvalidate();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test);

        initViews();
        initDatas();
    }

    private void initDatas(){
        radiusMax = getResources().getDimension(R.dimen.c_radiu_max);
        radiusMin = getResources().getDimension(R.dimen.c_radiu_min);
        radiusOffset = radiusMax - radiusMin;
    }

    private void initViews(){
        mSpringView = (SpringView) findViewById(R.id.springview);
        mSpringView.setIndicatorColor(getResources().getColor(github.chenupt.springindicator.R.color.si_default_indicator_bg));

        mHandler.sendEmptyMessageDelayed(0x11, 1000);
    }

    private float getPositionDistance() {
        float tarX = 40*2 + 40;
        float oriX = 0;
        return oriX - tarX;
    }

    private float getTabX() {
        return 0 + 40;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if(mHandler.hasMessages(0x12)){
            Log.e("TAG", "移除消息0x12");
            mHandler.removeMessages(0x12);
        }
    }
}
