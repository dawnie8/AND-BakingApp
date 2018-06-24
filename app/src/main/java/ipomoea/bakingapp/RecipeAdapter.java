package ipomoea.bakingapp;

import android.content.Context;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.List;

import ipomoea.bakingapp.model.Recipe;

public class RecipeAdapter extends RecyclerView.Adapter<RecipeAdapter.RecipeViewHolder> {

    private List<Recipe> recipes;

    final private RecipeItemClickListener onRecipeClickListener;

    public interface RecipeItemClickListener {
        void onListItemClick(int clickedItemIndex);
    }

    public RecipeAdapter(List<Recipe> recipes, RecipeItemClickListener listener) {
        this.recipes = recipes;
        this.onRecipeClickListener = listener;
    }

    @Override
    public RecipeViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Context context = parent.getContext();

        View itemView = LayoutInflater.from(context)
                .inflate(R.layout.recipe_list_item, parent, false);

        return new RecipeViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(RecipeViewHolder holder, int position) {
        Recipe recipe = recipes.get(position);
        String recipeName = recipe.getName();
        holder.recipeTextView.setText(recipeName);

        if (TextUtils.isEmpty(recipe.getImage())) {
            holder.imageView.setBackgroundResource(R.drawable.image);
        } else {
            Picasso.with(holder.imageView.getContext())
                    .load(recipe.getImage())
                    .into(holder.imageView);
        }

    }

    @Override
    public int getItemCount() {
        return recipes.size();
    }

    class RecipeViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        CardView cardView;
        TextView recipeTextView;
        ImageView imageView;

        RecipeViewHolder(View itemView) {
            super(itemView);
            cardView = itemView.findViewById(R.id.recipe_cardView);
            recipeTextView = itemView.findViewById(R.id.recipe_item_textView);
            imageView = itemView.findViewById(R.id.recipe_item_image);

            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            int clickedPosition = getAdapterPosition();
            onRecipeClickListener.onListItemClick(clickedPosition);
        }
    }
}