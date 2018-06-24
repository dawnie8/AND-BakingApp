package ipomoea.bakingapp;

import android.appwidget.AppWidgetManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import java.util.List;

import ipomoea.bakingapp.model.Ingredient;
import ipomoea.bakingapp.model.Recipe;
import ipomoea.bakingapp.model.Step;

public class MasterListFragment extends Fragment implements StepAdapter.ListItemClickListener {

    Recipe recipe;
    TextView ingredientsTextView;
    TextView servingsTextView;
    Button addToWidgetButton;
    List<Step> steps;
    boolean twoPane;

    RecyclerView recyclerView;
    LinearLayoutManager linearLayoutManager;
    StepAdapter stepAdapter;

    public MasterListFragment() {
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_master_list, container, false);
        ingredientsTextView = rootView.findViewById(R.id.ingredients_textView);
        servingsTextView = rootView.findViewById(R.id.servings_textView);
        addToWidgetButton = rootView.findViewById(R.id.addToWidget_button);
        recyclerView = rootView.findViewById(R.id.steps_recyclerView);

        addToWidgetButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                addToWidget();
            }
        });

        return rootView;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        twoPane = RecipeDetailActivity.twoPane;

        recipe = RecipeDetailActivity.recipe;

        servingsTextView.setText(recipe.getServings() + " servings");

        List<Ingredient> ingredients = recipe.getIngredients();

        for (int i = 0; i < ingredients.size(); i++) {
            Ingredient item = ingredients.get(i);
            ingredientsTextView.append("\u2022 " + item.getQuantity() +
                    item.getMeasure() + " " +
                    item.getIngredient() +
                    "\n");
        }

        steps = recipe.getSteps();

        linearLayoutManager = new LinearLayoutManager(getActivity());
        linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(linearLayoutManager);

        stepAdapter = new StepAdapter(steps, this);
        recyclerView.setAdapter(stepAdapter);

        if (twoPane) {

            Step stepToShow;

            if (getActivity().getIntent().hasExtra("clickedStep")) {
                stepToShow = getActivity().getIntent().getParcelableExtra("clickedStep");

            } else {
                stepToShow = steps.get(0);
            }

            DescriptionFragment descriptionFragment = DescriptionFragment.newInstance(stepToShow);
            FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
            fragmentManager.beginTransaction()
                    .replace(R.id.fragment_container, descriptionFragment)
                    .commit();
        }
    }

    @Override
    public void onListItemClick(int clickedItem) {

        Step clickedStep = steps.get(clickedItem);

        if (twoPane) {
            DescriptionFragment descriptionFragment = DescriptionFragment.newInstance(clickedStep);
            FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
            fragmentManager.beginTransaction()
                    .replace(R.id.fragment_container, descriptionFragment)
                    .commit();

        } else {

            Intent intent = new Intent(getActivity(), StepDetailActivity.class);
            intent.putExtra("clickedStep", clickedStep);
            startActivity(intent);

        }

    }

    public void addToWidget() {

        String ingredientsString = ingredientsTextView.getText().toString();

        SharedPreferences sharedPref = getActivity().getSharedPreferences("selected_ingredients", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putString("ingredientsToWidget", ingredientsString);
        editor.putString("recipeName", recipe.getName());
        editor.apply();

        Snackbar.make(getActivity().findViewById(android.R.id.content), "The ingredients list is added to the homescreen widget.", Snackbar.LENGTH_LONG).show();

        Intent intent = new Intent(getActivity(), RecipeWidgetProvider.class);
        intent.setAction(AppWidgetManager.ACTION_APPWIDGET_UPDATE);

        int[] ids = AppWidgetManager.getInstance(getActivity().getApplication())
                .getAppWidgetIds(new ComponentName(getActivity().getApplication(), RecipeWidgetProvider.class));
        intent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_IDS, ids);
        getActivity().sendBroadcast(intent);
    }
}
