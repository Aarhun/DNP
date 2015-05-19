package com.tacitus.dnp.scenario;

import android.support.v7.widget.RecyclerView;
import android.view.ViewGroup;

import java.util.ArrayList;

public class StepAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder>{
    private ArrayList<Step> mDataSet;

    public static class ViewHolder extends RecyclerView.ViewHolder {

        public StepView mStepView;
        public ViewHolder(StepView v) {
            super(v);
            mStepView = v;
        }
    }

    public StepAdapter(ArrayList<Step> Steps) {
        mDataSet = Steps;
    }


    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        StepView v = new StepView(viewGroup.getContext(), getItemCount());
        ViewHolder vh = new ViewHolder(v);
        return vh;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder viewHolder, int i) {
        ((ViewHolder)viewHolder).mStepView.setStep(mDataSet.get(i));
    }


    @Override
    public int getItemCount() {
        return mDataSet.size();
    }

}
