package com.palmwifi.ktv.presenter;

import android.content.Context;
import android.text.TextUtils;

import com.palmwifi.http.AsyncCallback;
import com.palmwifi.http.JsonCallback;
import com.palmwifi.ktv.bean.BaseResult;
import com.palmwifi.ktv.bean.Singer;
import com.palmwifi.ktv.bean.SingerType;
import com.palmwifi.ktv.bean.Song;
import com.palmwifi.ktv.comm.ConstantUrl;
import com.palmwifi.ktv.comm.Contract;
import com.palmwifi.ktv.constact.SearchSingerContract;
import com.palmwifi.ktv.helper.FavHelper;
import com.trello.rxlifecycle.LifecycleProvider;
import com.zhy.http.okhttp.OkHttpUtils;

import java.util.List;

/**
 * <pre>
 *     author : David
 *     e-mail : 873825232@qq.com
 *     time   : 2017/04/06
 *     desc   :
 *     version: 1.0
 * </pre>
 */
public class SearchSingerPresenter implements SearchSingerContract.Presenter {
    private Context context;
    private LifecycleProvider provider;
    private SearchSingerContract.View view;
    private List<SingerType> singerTypes;
    private int  pager;
    private boolean isSearch;
    private int tabPosition;
    private String content;
    private boolean isNoMore;

    public SearchSingerPresenter(Context context, LifecycleProvider provider, SearchSingerContract.View view) {
        this.context = context;
        this.provider = provider;
        this.view = view;
        this.view.setPresenter(this);
    }

    @Override
    public void unSubscribe() {
        this.context = null;
        this.provider = null;
        this.view = null;
    }

    @Override
    public void requestSingerType() {
        OkHttpUtils.get().url(ConstantUrl.SINGER_TYPE_URL).build().execute(
                new JsonCallback<BaseResult<List<SingerType>>>(provider) {

                    @Override
                    public void onGetDataSuccess(BaseResult<List<SingerType>> result) {
                        if(TextUtils.equals(result.getResultCode(),BaseResult.SUC)){
                            singerTypes = result.getData();
                            view.getSingerType(singerTypes);
                        }
                    }

                });
    }

    @Override
    public void requestSingerList(final boolean isRefresh,int position) {
        pager = isRefresh? 0: (pager+1);
        isSearch = false;
        if(isRefresh){
            isNoMore = false;
        }
        tabPosition = position;
        String singerTypeID= singerTypes.get(position).getTid();
        OkHttpUtils.get().url(ConstantUrl.SINGER_LIST_URL).addParams("singerTypeID",singerTypeID)
                .addParams("page",pager+"").addParams("pageSize", Contract.PAGE_SIZE+"").build().execute(
                new JsonCallback<BaseResult<List<Singer>>>(provider) {

                    @Override
                    public void onGetDataSuccess(BaseResult<List<Singer>> result) {
                        if(TextUtils.equals(result.getResultCode(),BaseResult.SUC)){
                            view.getSingerListSuc(false,isRefresh,result.getData());
                            view.getTotalCount(result.getTotalCount());
                            if(result.getData() == null || result.getData().size() < Contract.PAGE_SIZE){
                                isNoMore = true;
                            }
                            pager++;
                        }


                    }

                });
    }

    public void searchSingerList(final boolean isRefresh,String content){
        pager = isRefresh? 0: (pager+1);
        if(isRefresh){
            isNoMore = false;
        }
        isSearch = true;
        this.content = content;
        OkHttpUtils.get().url(ConstantUrl.SEARCH_SINGER).addParams("singer_pingyin",content)
                .addParams("page",pager+"").addParams("pageSize", Contract.PAGE_SIZE+"").build().execute(
                new JsonCallback<BaseResult<List<Singer>>>(provider) {

                    @Override
                    public void onGetDataSuccess(BaseResult<List<Singer>> result) {
                        if(TextUtils.equals(result.getResultCode(),BaseResult.SUC)){
                            view.getSingerListSuc(true,isRefresh,result.getData());
                            view.getTotalCount(result.getTotalCount());
                            if( result.getData() == null || result.getData().size() < Contract.PAGE_SIZE){
                                isNoMore = true;
                            }
                            pager++;
                        }
                    }

                });
    }

    @Override
    public void requestMore() {
        if(isSearch){
            searchSingerList(false,content);
        }else {
            requestSingerList(false,tabPosition);
        }
    }

    @Override
    public boolean isNoMore() {
        return isNoMore;
    }

    @Override
    public boolean  getIsSearch() {
        return isSearch;
    }
}