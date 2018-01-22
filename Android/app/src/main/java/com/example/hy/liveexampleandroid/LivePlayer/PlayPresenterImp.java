package com.example.hy.liveexampleandroid.LivePlayer;

import com.example.livelib.Util.IpChecker;

/**
 * Created by Hamik Young on 2018/1/19.
 */

public class PlayPresenterImp implements PlayPresenter {
    private PlayView mPlayView;
    private PlayModel mPlayModel;

    PlayPresenterImp(PlayView playView) {
        mPlayView = playView;
        mPlayModel = new PlayModelImp();
    }

    @Override
    public void startPlay(String address) {

        if (IpChecker.IsIpEmpty(address)) {
            mPlayView.showIpEmptyError();
            return;
        } else if (!IpChecker.IsIpValid(address)) {
            mPlayView.showIpInvalidError();
            return;
        }
        mPlayModel.startPlay(address);
        mPlayView.btnTextChangeToStop();
        mPlayView.showProgress();
    }

    @Override
    public void startPlay(String address, int port) {
        if (IpChecker.IsIpEmpty(address)) {
            mPlayView.showIpEmptyError();
            return;
        } else if (!IpChecker.IsIpValid(address)) {
            mPlayView.showIpInvalidError();
            return;
        }


        mPlayModel.startPlay(address, port);
        mPlayView.btnTextChangeToStop();
        mPlayView.showProgress();
    }

    @Override
    public void stopPlay() {
        mPlayModel.stopPlay();
        mPlayView.btnTextChangeToStart();
    }

    @Override
    public void takePic() {
        mPlayModel.takePic();
    }

    @Override
    public void onDestroy() {
        mPlayModel.onDestroy();
    }
}
