package ipomoea.bakingapp;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

import ipomoea.bakingapp.model.Step;

public class StepAdapter extends RecyclerView.Adapter<StepAdapter.StepViewHolder> {

    private List<Step> steps;

    final private ListItemClickListener onClickListener;

    public interface ListItemClickListener {
        void onListItemClick(int clickedItem);
    }

    StepAdapter(List<Step> list, ListItemClickListener listener) {
        this.steps = list;
        this.onClickListener = listener;
    }

    @Override
    public StepViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Context context = parent.getContext();

        View itemView = LayoutInflater.from(context)
                .inflate(R.layout.steps_list_item, parent, false);

        return new StepViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(StepViewHolder holder, int position) {
        Step step = steps.get(position);
        String shortDescription = step.getShortDescription();
        holder.itemTextView.setText(shortDescription);
    }

    @Override
    public int getItemCount() {
        if (steps == null) return 0;
        return steps.size();
    }

    class StepViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        TextView itemTextView;

        StepViewHolder(View itemView) {
            super(itemView);
            itemTextView = itemView.findViewById(R.id.steps_item_textView);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            int clickedPosition = getAdapterPosition();
            onClickListener.onListItemClick(clickedPosition);
        }
    }
}