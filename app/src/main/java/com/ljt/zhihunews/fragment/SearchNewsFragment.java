package com.ljt.zhihunews.fragment;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.eowise.recyclerview.stickyheaders.StickyHeadersAdapter;
import com.eowise.recyclerview.stickyheaders.StickyHeadersBuilder;
import com.eowise.recyclerview.stickyheaders.StickyHeadersItemDecoration;
import com.ljt.zhihunews.R;
import com.ljt.zhihunews.adapter.DateHeaderAdapter;
import com.ljt.zhihunews.adapter.NewsAdapter;
import com.ljt.zhihunews.bean.DailyNews;

import java.util.ArrayList;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 */
public class SearchNewsFragment extends Fragment {
    private List<DailyNews> newsList = new ArrayList<>();

    private NewsAdapter mAdapter;
    private DateHeaderAdapter mHeaderAdapter;

    public SearchNewsFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_search_news, container, false);
        assert  view !=null;

        RecyclerView mRecyclerView = (RecyclerView) view.findViewById(R.id.search_result_list);
        mRecyclerView.setHasFixedSize(true);
        LinearLayoutManager llm = new LinearLayoutManager(getActivity());

        llm.setOrientation(LinearLayout.VERTICAL);
        mRecyclerView.setLayoutManager(llm);
        mAdapter=new NewsAdapter(newsList);
        mHeaderAdapter=new DateHeaderAdapter(newsList);

        StickyHeadersItemDecoration header = new StickyHeadersBuilder()
                .setAdapter(mAdapter)
                .setRecyclerView(mRecyclerView)
                .setStickyHeadersAdapter(mHeaderAdapter)
                .build();
        mRecyclerView.setAdapter(mAdapter);
        mRecyclerView.addItemDecoration(header);

        return view;
    }
    public void updateContent(List<DailyNews> newsList){
        mHeaderAdapter.setNewsList(newsList);
        mAdapter.updateNewsList(newsList);
    }

}
